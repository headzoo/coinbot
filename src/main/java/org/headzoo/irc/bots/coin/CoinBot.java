package org.headzoo.irc.bots.coin;

import org.headzoo.irc.bots.coin.commands.Action;
import org.headzoo.irc.bots.coin.commands.Command;
import org.headzoo.irc.bots.coin.commands.Event;
import org.headzoo.irc.bots.coin.commands.ICommand;
import org.headzoo.irc.bots.coin.models.Admin;
import org.headzoo.irc.bots.coin.models.Channel;
import org.javalite.activejdbc.Base;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * The main bot class
 *
 * Created by Sean Hickey <sean@mincoin.io> on 1/27/14.
 *
 * The MIT License (MIT)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 */
public class CoinBot
    extends PircBot
    implements ICommand
{
    /**
     * For connecting to the database
     */
    protected DataSource data_source = null;

    /**
     * Are we in the process of disconnecting?
     */
    protected Boolean disconnecting = false;

    /**
     * The server configuration
     */
    protected Server server = null;

    /**
     * The server we are connected to
     */
    protected org.headzoo.irc.bots.coin.models.Server model_server = null;

    /**
     * The name of the bot
     */
    protected String nick = null;

    /**
     * Identify with NickServ using this password
     */
    protected String password = null;

    /**
     * Whether to output messages
     */
    protected Boolean verbose = false;

    /**
     * The character that begins bot commands
     */
    protected String trigger_char = "!";

    /**
     * The channels to join
     */
    protected List<String> channels = new ArrayList<String>();

    /**
     * The channels the bot is actually in
     */
    protected Map<String, Channel> joined = new HashMap<String, Channel>();

    /**
     * The loaded commands
     */
    protected Map<String, ICommand> commands = new HashMap<String, ICommand>();

    /**
     * The bot admins
     */
    protected List<Admin> admins = new ArrayList<Admin>();

    /**
     * Constructor
     *
     * @param server The server configuration
     * @param data_source For connecting to the database
     */
    public CoinBot(Server server, DataSource data_source)
    {
        setServer(server);
        setDataSource(data_source);
    }

    /**
     * Sets the data source required to connect to the database
     *
     * @param data_source The data source
     */
    public void setDataSource(DataSource data_source)
    {
        this.data_source = data_source;
    }

    /**
     * Sets the server configuration
     *
     * @param server The server configuration
     * @return CoinBot
     */
    public CoinBot setServer(Server server)
    {
        this.server = server;
        return this;
    }

    /**
     * Sets the name of the bot
     *
     * @param nick The name of the bot
     * @return CoinBot
     */
    public CoinBot setNick(String nick)
    {
        this.nick = nick;
        if (isConnected()) {
            setName(nick);
        }

        return this;
    }

    /**
     * Identify with NickServ using this password
     *
     * @param password The password
     * @return CoinBot
     */
    public CoinBot setPassword(String password)
    {
        this.password = password;
        return this;
    }

    /**
     * Sets the channels the bot should join
     *
     * @param channels List of channel names
     * @return CoinBot
     */
    public CoinBot setChannels(List<String> channels)
    {
        if (null != channels) {
            for(String channel: channels) {
                addChannel(channel);
            }
        }
        return this;
    }

    /**
     * Adds a channel the bot should join
     *
     * @param channel Name of the channel
     * @return CoinBot
     */
    public CoinBot addChannel(String channel)
    {
        if (!channel.startsWith("#")) {
            channel = "#" + channel;
        }
        channels.add(channel);
        return this;
    }

    /**
     * Sets whether to use verbose output
     *
     * @param verbose Use verbose output?
     * @return CoinBot
     */
    public CoinBot setVerbose(Boolean verbose)
    {
        this.verbose = verbose;
        return this;
    }

    /**
     * Sets the character that begins bot commands
     *
     * @param trigger_char The trigger character
     * @return CoinBot
     */
    public CoinBot setTriggerChar(String trigger_char)
    {
        this.trigger_char = trigger_char;
        return this;
    }

    /**
     * Connects to the irc server
     */
    public void connect() throws IrcException, IOException
    {
        if (channels.isEmpty()) {
            err("No channels have been set to join. Quiting.");
            return;
        }

        super.setName(nick);
        super.setVerbose(verbose);
        int connection_attempts = 0;
        while(!isConnected()) {
            try {
                connection_attempts++;
                connect(server.getHost(), server.getPort(), server.getPassword());
            } catch (NickAlreadyInUseException e) {
                if (connection_attempts > 10) {
                    throw e;
                }
                nick = nick + connection_attempts;
                super.setName(nick);
                connect(server.getHost(), server.getPort(), server.getPassword());
            }
        }
    }

    /**
     * This method is called once the bot has successfully connected to the IRC server
     */
    @Override
    public void onConnect()
    {
        try {
            initDatabaseThread();
            model_server = org.headzoo.irc.bots.coin.models.Server.findFirst("`host` = ?", server.getHost());
            if (null == model_server) {
                model_server = new org.headzoo.irc.bots.coin.models.Server();
                model_server.setString("host", server.getHost());
                model_server.setInteger("port", server.getPort());
            }
            model_server.setTimestamp("date_last_connect", Calendar.getInstance().getTime());
            model_server.saveIt();
            loadCommands();
            loadAdmins();
        } catch (Exception e) {
            err(e);
        }

        if (null != password) {
            identify(password);
        }
        for(String channel: channels) {
            joinChannel(channel);
        }
    }

    /**
     * This method carries out the actions to be performed when the bot gets disconnected
     */
    @Override
    public void onDisconnect()
    {
        if (!disconnecting) {
            err("Disconnected from server!");
            while(!isConnected() && !disconnecting) {
                try {
                    out("Attempting to reconnect");
                    reconnect();
                } catch (IOException e) {
                    err(e);
                } catch (IrcException e) {
                    err(e);
                }

                try {
                    out("Sleeping for 10 seconds.");
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    err(e);
                }
            }
        } else {
            for(ICommand command: commands.values()) {
                command.triggerShutdown();
            }
            System.exit(Application.EXIT_OKAY);
        }
    }

    /**
     * This method is called whenever someone (possibly us) joins a channel which we are on
     *
     * @param channel The channel which somebody joined
     * @param sender The nick of the user who joined the channel
     * @param login The login of the user who joined the channel
     * @param hostname The hostname of the user who joined the channel
     */
    @Override
    public void onJoin(String channel, String sender, String login, String hostname)
    {
        if (sender.equals(nick)) {
            if (!joined.containsKey(channel)) {
                try {
                    initDatabaseThread();
                    org.headzoo.irc.bots.coin.models.Channel c
                        = org.headzoo.irc.bots.coin.models.Channel.findFirst(
                        "`server_id` = ? AND `name` = ?", model_server.getInteger("id"),
                        channel
                    );
                    if (null == c) {
                        c = new org.headzoo.irc.bots.coin.models.Channel();
                        c.setInteger("server_id", model_server.getInteger("id"));
                        c.setString("name", channel);
                    }
                    c.setTimestamp("date_last_joined", Calendar.getInstance().getTime());
                    c.saveIt();
                    joined.put(channel, c);
                } catch (Exception e) {
                    err(e);
                }
            }
        }
    }

    /**
     * This method is called whenever someone (possibly us) parts a channel which we are on
     *
     * @param channel The channel which somebody parted from
     * @param sender The nick of the user who parted from the channel
     * @param login The login of the user who parted from the channel
     * @param hostname The hostname of the user who parted from the channel
     */
    @Override
    public void onPart(String channel, String sender, String login, String hostname)
    {
        if (sender.equals(nick) && joined.containsKey(channel)) {
            joined.remove(channel);
        }
    }

    /**
     * This method is called whenever someone (possibly us) changes nick on any of the channels that we are on
     *
     * @param old_nick The old nick
     * @param login The login of the user
     * @param hostname The hostname of the user
     * @param new_nick The new nick
     */
    @Override
    public void onNickChange(String old_nick, String login, String hostname, String new_nick)
    {
        if (old_nick.equals(nick)) {
            nick = new_nick;
        }
    }

    /**
     * This method is called whenever a message is sent to a channel.
     *
     * @param channel The channel to which the message was sent
     * @param sender The nick of the person who sent the message
     * @param login The login of the person who sent the message
     * @param hostname The hostname of the person who sent the message
     * @param message The actual message sent to the channel
     */
    @Override
    public void onMessage(String channel, String sender, String login, String hostname, String message)
    {
        if (!sender.equals(nick) && message.startsWith(trigger_char)) {
            String[] parts = splitTriggerAndMessage(message);
            Event event = new Event("message", parts[0], nick);
            event
                .setChannel(joined.get(channel))
                .setSender(sender)
                .setLogin(login)
                .setHostname(hostname)
                .setMessage(parts[1]);
            trigger(event);
        }
    }

    /**
     * This method is called whenever we receive a notice.
     *
     * @param sender The nick of the user that sent the notice
     * @param login The login of the user that sent the notice
     * @param hostname The hostname of the user that sent the notice
     * @param target The target of the notice, be it our nick or a channel name
     * @param notice The notice message
     */
    public void onNotice(String sender, String login, String hostname, String target, String notice)
    {
        if (!sender.equals(nick) && notice.startsWith(trigger_char)) {
            String[] parts = splitTriggerAndMessage(notice);
            Event event = new Event("notice", parts[0], nick);
            event
                .setSender(sender)
                .setTarget(target)
                .setLogin(login)
                .setHostname(hostname)
                .setMessage(parts[1]);
            trigger(event);
        }
    }

    /**
     * This method is called whenever a private message is sent to the bot
     *
     * @param sender The nick of the person who sent the private message
     * @param login The login of the person who sent the private message
     * @param hostname The hostname of the person who sent the private message
     * @param message The actual message
     */
    public void onPrivateMessage(String sender, String login, String hostname, String message)
    {
        if (!sender.equals(nick) && message.startsWith(trigger_char)) {
            String[] parts = splitTriggerAndMessage(message);
            Event event = new Event("privateMessage", parts[0], nick);
            event
                .setSender(sender)
                .setLogin(login)
                .setHostname(hostname)
                .setMessage(parts[1]);
            trigger(event);
        }
    }

    /**
     * Disconnects from the server
     */
    public void shutdown()
    {
        disconnecting = true;
        disconnect();
    }

    /**
     * Loads the commands from the database
     */
    protected void loadCommands()
    {
        for(ICommand command: commands.values()) {
            command.triggerShutdown();
        }
        commands = new HashMap<String, ICommand>();
        commands.put("shutdown", this);
        commands.put("part",     this);
        commands.put("join",     this);
        commands.put("reload",   this);
        commands.put("help",     this);
        commands.put("send",     this);
        commands.put("enable",   this);
        commands.put("disable",  this);

        List<org.headzoo.irc.bots.coin.models.Command> db_commands = org.headzoo.irc.bots.coin.models.Command.where("is_enabled = 1");
        for(org.headzoo.irc.bots.coin.models.Command db_command: db_commands) {
            String class_name = db_command.getString("class");
            String trigger    = db_command.getString("trigger");

            out("Loading command " + class_name + " with trigger " + trigger);
            ICommand c = Command.factory(class_name);
            c.setTrigger(trigger);
            c.setDataSource(data_source);
            commands.put(trigger, c);
        }

        for(ICommand command: commands.values()) {
            command.triggerStartup();
        }
    }

    /**
     * Loads the admin users from the database
     */
    protected void loadAdmins()
    {
        initDatabaseThread();
        admins = Admin.findAll();
        for(Admin admin: admins) {
            out("Loaded admin " + admin.getString("nick") + "@" + admin.getString("hostname"));
        }
    }

    /**
     * Triggers a command event
     *
     * @param event The event information
     */
    protected void trigger(Event event)
    {
        // The event name, like "message", becomes "triggerMessage".
        String event_name   = event.getName();
        String method_name  = "trigger" + event_name.substring(0, 1).toUpperCase() + event_name.substring(1);
        Boolean is_admin    = isAdmin(event.getSender(), event.getHostname());
        for(String trigger: commands.keySet()) {
            if (trigger.equals(event.getTrigger())) {
                ICommand command = commands.get(trigger);
                if (!command.isAdminOnly() || (command.isAdminOnly() && is_admin)) {
                    try {
                        out("Triggering " + method_name + "(name = '" + event.getName() + "', trigger = '" + event.getTrigger() + "')");
                        //CommandHistory cmd_history = new CommandHistory();
                        //cmd_history.setInteger("channel_id", event.getChannel().getId());
                        //cmd_history.setInteger("command_id", 2);
                        //cmd_history.setString("text", event.getMessage());
                        //cmd_history.setBoolean("is_private", event_name == "privateMessage");
                        //cmd_history.saveIt();

                        Method method = command.getClass().getMethod(method_name, Event.class);
                        Action action = (Action)method.invoke(command, event);
                        if (null != action) {
                            execAction(action);
                        }

                        out("Triggering triggerAny(name = '" + event.getName() + "', trigger = '" + event.getTrigger() + "')");
                        action = command.triggerAny(event);
                        if (null != action) {
                            execAction(action);
                        }
                    } catch (Exception e) {
                        err(e);
                    }
                }
            }
        }
    }

    /**
     * Executes a command action
     *
     * @param action The action to execute
     */
    protected void execAction(Action action)
    {
        String message = action.getMessage();
        String target  = action.getTarget();
        if (null != action && null != message && null != target) {
            String[] parts = message.split("\n");
            if (action.getIsNotice()) {
                out("Executing action sendNotice('" + target + "', '" + message + "')");
                for(String line: parts) {
                    sendNotice(target, line);
                }
            } else {
                out("Executing action sendMessage('" + target + "', '" + message + "')");
                for(String line: parts) {
                    sendMessage(target, line);
                }
            }
        }
    }

    /**
     * Splits the trigger from the rest of the message
     *
     * @param message The message received by the sender
     * @return The trigger in index 0 and the message in index 1
     */
    protected String[] splitTriggerAndMessage(String message)
    {
        String[] split = new String[2];
        String[] parts = message.substring(1).split(" ", 2);
        split[0] = parts[0];
        split[1] = parts.length > 1 ? parts[1] : "";

        return split;
    }

    /**
     * Output the message to stdout if the verbose option is turned on
     *
     * @param message The message to output
     */
    protected void out(String message)
    {
        if (verbose) {
            System.out.println(message);
        }
    }

    /**
     * Output the message to stderr if the verbose option is turned on
     *
     * @param message The message to output
     */
    protected void err(String message)
    {
        if (verbose) {
            System.err.println(message);
        }
    }

    /**
     * Outputs the exception stack trace if the verbose option is turned on
     *
     * @param e The exception
     */
    protected void err(Exception e)
    {
        if (verbose) {
            e.printStackTrace();
        }
    }

    /**
     * Ensures this thread has a database connection
     */
    protected void initDatabaseThread()
    {
        if (!Base.hasConnection()) {
            Base.open(data_source);
        }
    }

    /**
     * Returns whether the user is an admin
     *
     * @param sender The name of the user
     * @param hostname The user hostname
     * @return Whether the user is an admin
     */
    protected Boolean isAdmin(String sender, String hostname)
    {
        Boolean is_admin = false;
        if (null != sender && null != hostname) {
            for(Admin admin: admins) {
                if (sender.equals(admin.getString("nick")) && hostname.equals(admin.getString("hostname"))) {
                    is_admin = true;
                    break;
                }
            }
        }

        return is_admin;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTrigger(String trigger) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription(String sender, String hostname, String trigger_char, String trigger)
    {
        String message = null;
        if (isAdmin(sender, hostname)) {
            if (trigger.equals("shutdown")) {
                message = DescriptionBuilder.build(
                    trigger_char,                    trigger,
                    "",
                    "Immediately shutdown the bot.",
                    "shutdown"
                );
            } else if (trigger.equals("reload")) {
                message = DescriptionBuilder.build(
                    trigger_char,
                    trigger,
                    "<commands|admins>",
                    "Reload the commands or admins from the database",
                    "reload admins"
                );
            } else if (trigger.equals("part")) {
                message = DescriptionBuilder.build(
                    trigger_char,
                    trigger,
                    "(channel)",
                    "Part the specified channel, or part the channel the command was sent to.",
                    "part #mincoin-dev"
                );
            } else if (trigger.equals("join")) {
                message = DescriptionBuilder.build(
                    trigger_char,
                    trigger,
                    "<channel>",
                    "Join the specified channel.",
                    "join #mincoin-dev"
                );
            } else if (trigger.equals("send")) {
                message = DescriptionBuilder.build(
                    trigger_char,
                    trigger,
                    "<command>",
                    "Have the bot send the command tot he server.",
                    "send /list"
                );
            } else if (trigger.equals("enable")) {
                message = DescriptionBuilder.build(
                    trigger_char,
                    trigger,
                    "<command>",
                    "Enable the bot command.",
                    "enable memo"
                );
            } else if (trigger.equals("disable")) {
                message = DescriptionBuilder.build(
                    trigger_char,
                    trigger,
                    "<command>",
                    "Disable a bot command.",
                    "disable memo"
                );
            }
        }

        return message;
    }

    /**
     * {@inheritDoc}
     */
    public Boolean isAdminOnly()
    {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void triggerStartup() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void triggerShutdown() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public Action triggerMessage(Event event)
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action triggerNotice(Event event)
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action triggerPrivateMessage(Event event)
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action triggerAny(Event event)
    {
        String sender   = event.getSender();
        String hostname = event.getHostname();
        String trigger  = event.getTrigger();
        String message  = event.getMessage().trim();
        Channel channel  = event.getChannel();
        Action action   = new Action();

        System.out.println(channel.getId());
        System.out.println(channel.getName());
        System.out.println(channel.getDateLastJoined());
        System.out.println(channel.getServer());

        out("Got command from admin " + sender + "@" + hostname + " " + trigger_char + trigger + " " + message);
        if (trigger.equals("shutdown")) {
            shutdown();
        } else if (trigger.equals("part")) {
            if (!message.isEmpty()) {
                partChannel(message);
            } else if (null != channel) {
                partChannel(channel.getName());
            } else {
                action
                    .setMessage("No channel specified.")
                    .setTarget(sender);
            }
        } else if (trigger.equals("join")) {
            if (message.isEmpty() || !message.startsWith("#")) {
                action
                    .setMessage("No channel or invalid channel given.")
                    .setTarget(sender);
            } else {
                joinChannel(message);
            }
        } else if (trigger.equals("reload")) {
            if (message.equals("commands")) {
                loadCommands();
                action
                    .setMessage("Commands will be reloaded.")
                    .setTarget(sender);
            } else if (message.equals("admins")) {
                loadAdmins();
                action
                    .setMessage("Admins will be reloaded.")
                    .setTarget(sender);
            } else {
                action
                    .setMessage("Nothing to load.")
                    .setTarget(sender);
            }
        } else if (trigger.equals("send")) {
            if (!message.isEmpty()) {
                if (message.startsWith("/")) {
                    message = message.substring(1);
                }
                sendRawLine(message);
            } else {
                action
                    .setMessage("Nothing to send.")
                    .setTarget(sender);
            }
        } else if (trigger.equals("enable")) {

        } else if (trigger.equals("disable")) {

        } else if (trigger.equals("help")) {
            String buffer = "";
            for(String trig: commands.keySet()) {
                ICommand command   = commands.get(trig);
                String description = command.getDescription(sender, hostname, trigger_char, trig);
                if (null != description) {
                    buffer += description + "\n";
                }
            }
            if (!buffer.isEmpty()) {
                buffer = nick + " Help\n" + buffer;
                action
                    .setMessage(buffer)
                    .setTarget(sender);
            }
        }

        return action;
    }
}
