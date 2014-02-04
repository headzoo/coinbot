package org.headzoo.irc.bots.coin;

import org.headzoo.irc.bots.coin.commands.AbstractCommand;
import org.headzoo.irc.bots.coin.commands.ICommand;
import org.headzoo.irc.bots.coin.exceptions.InvalidCommandException;
import org.headzoo.irc.bots.coin.models.Admin;
import org.headzoo.irc.bots.coin.models.Channel;
import org.headzoo.irc.bots.coin.models.Command;
import org.headzoo.irc.bots.coin.models.Server;
import org.javalite.activejdbc.Base;
import org.jibble.pircbot.*;

import java.io.IOException;
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
{
    /**
     * For connecting to the database
     */
    protected DataSource data_source;

    /**
     * Are we in the process of disconnecting?
     */
    protected Boolean disconnecting;

    /**
     * The server configuration
     */
    protected ServerDetails server_details;

    /**
     * The server we are connected to
     */
    protected Server model_server;

    /**
     * Identify with NickServ using this password
     */
    protected String nickserv_password;

    /**
     * Whether to output messages
     */
    protected Boolean verbose;

    /**
     * The character that begins bot commands
     */
    protected String prefix = "!";

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
     * Whether channel ops are automatically admins
     */
    protected Boolean admin_ops;


    /**
     * Constructor
     *
     * @param server The server configuration
     * @param data_source For connecting to the database
     */
    public CoinBot(ServerDetails server, DataSource data_source)
    {
        setServerDetails(server);
        setDataSource(data_source);
        setMessageDelay(5);
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

        super.setVerbose(verbose);
        int connection_attempts = 0;
        while(!isConnected()) {
            try {
                connection_attempts++;
                connect(server_details.getHost(), server_details.getPort(), server_details.getPassword());
            } catch (NickAlreadyInUseException e) {
                if (connection_attempts > 10) {
                    throw e;
                }
                setName(getNick() + connection_attempts);
                connect(server_details.getHost(), server_details.getPort(), server_details.getPassword());
            }
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


    /********************************
     * Primary getters and setters
     *******************************/


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
     * @param server_details The server configuration
     * @return CoinBot
     */
    public CoinBot setServerDetails(ServerDetails server_details)
    {
        this.server_details = server_details;
        return this;
    }

    /**
     * Gets the server configuration
     *
     * @return The server configuration
     */
    public ServerDetails getServerDetails()
    {
        return server_details;
    }

    /**
     * Sets the name of the bot
     *
     * @param nick The nick
     * @return CoinBot
     */
    public CoinBot setNick(String nick)
    {
        setName(nick);
        return this;
    }

    /**
     * Identify with NickServ using this password
     *
     * @param nickserv_password The password
     * @return CoinBot
     */
    public CoinBot setNickServPassword(String nickserv_password)
    {
        this.nickserv_password = nickserv_password;
        return this;
    }

    /**
     * Returns the NickServ password for the bot
     *
     * @return The password
     */
    public String getNickServPassword()
    {
        return nickserv_password;
    }

    /**
     * Sets whether channel ops are automatically admins
     *
     * @param admin_ops Whether channel ops are automatically admins
     * @return CoinBot
     */
    public CoinBot setAdminOps(Boolean admin_ops)
    {
        this.admin_ops = admin_ops;
        return this;
    }

    /**
     * Returns whether channel ops are automatically admins
     *
     * @return Are ops admins?
     */
    public Boolean getAdminOps()
    {
        return admin_ops;
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
     * Returns whether verbose output is enabled
     *
     * @return Is verbose output enabled?
     */
    public Boolean getVerbose()
    {
        return verbose;
    }

    /**
     * Sets the character that begins bot commands
     *
     * @param prefix The trigger character
     * @return CoinBot
     */
    public CoinBot setPrefix(String prefix)
    {
        this.prefix = prefix;
        return this;
    }

    /**
     * Returns the trigger character
     *
     * @return The trigger character
     */
    public String getPrefix()
    {
        return prefix;
    }

    /**
     * Returns the channels the bot is in
     *
     * @return The channels
     */
    public Map<String, Channel> getJoined()
    {
        return joined;
    }

    /**
     * Returns the bot admins
     *
     * @return The admins
     */
    public List<Admin> getAdmins()
    {
        return admins;
    }

    /**
     * Enables a command
     *
     * @param trigger The command trigger
     */
    public void enableCommand(String trigger) throws InvalidCommandException
    {
        Command cmd      = Command.findFirst("trig = ?", trigger);
        ICommand command = commands.get(trigger);
        if (null != cmd) {
            cmd.setBoolean("is_enabled", true);
            cmd.saveIt();
            command.setIsEnabled(true);
        } else {
            throw new InvalidCommandException("The command '" + trigger + "' is not found.");
        }
    }

    /**
     * Disables a command
     *
     * @param trigger The command trigger
     */
    public void disableCommand(String trigger) throws InvalidCommandException
    {
        Command cmd = Command.findFirst("trig = ?", trigger);
        ICommand command = commands.get(trigger);
        if (null != cmd && null != command) {
            cmd.setBoolean("is_enabled", false);
            cmd.saveIt();
            command.setIsEnabled(false);
        } else {
            throw new InvalidCommandException("The command '" + trigger + "' is not found.");
        }
    }

    /**
     * Returns the loaded commands
     *
     * @return The commands
     */
    public Map<String, ICommand> getCommands()
    {
        return commands;
    }


    /********************************
     * IRC event callbacks
     *******************************/


    /**
     * This method is called once the bot has successfully connected to the IRC server
     */
    @Override
    public void onConnect()
    {
        try {
            initDatabaseThread();
            model_server = org.headzoo.irc.bots.coin.models.Server.findFirst("`host` = ?", server_details.getHost());
            if (null == model_server) {
                model_server = new org.headzoo.irc.bots.coin.models.Server();
                model_server.setString("host", server_details.getHost());
                model_server.setInteger("port", server_details.getPort());
            }
            model_server.setTimestamp("date_last_connect", Calendar.getInstance().getTime());
            model_server.saveIt();
            loadCommands();
            loadAdmins();
        } catch (Exception e) {
            err(e);
        }

        if (null != nickserv_password) {
            identify(nickserv_password);
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
                command.onShutdown();
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
        if (sender.equals(getNick())) {
            if (!joined.containsKey(channel)) {
                try {
                    initDatabaseThread();
                    org.headzoo.irc.bots.coin.models.Channel c
                        = org.headzoo.irc.bots.coin.models.Channel.findFirst(
                        "`server_id` = ? AND `name` = ?",
                        model_server.getInteger("id"),
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
        if (sender.equals(getNick()) && joined.containsKey(channel)) {
            joined.remove(channel);
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
        message = Colors.removeFormattingAndColors(message);
        if (!sender.equals(getNick()) && message.startsWith(prefix)) {
            String[] parts = splitTriggerAndMessage(message);
            Event event = new Event(Event.Types.MESSAGE, parts[0]);
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
        notice = Colors.removeFormattingAndColors(notice);
        if (!sender.equals(getNick()) && notice.startsWith(prefix)) {
            String[] parts = splitTriggerAndMessage(notice);
            Event event = new Event(Event.Types.NOTICE, parts[0]);
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
        message = Colors.removeFormattingAndColors(message);
        if (!sender.equals(getNick()) && message.startsWith(prefix)) {
            String[] parts = splitTriggerAndMessage(message);
            Event event = new Event(Event.Types.PRIVATE, parts[0]);
            event
                .setSender(sender)
                .setLogin(login)
                .setHostname(hostname)
                .setMessage(parts[1]);
            trigger(event);
        }
    }


    /********************************
     * Misc methods
     *******************************/


    /**
     * Triggers a command event
     *
     * @param event The event information
     */
    protected void trigger(Event event)
    {
        String sender    = event.getSender();
        Boolean is_admin = isAdmin(event.getSender(), event.getHostname());

        // The admin_ops setting controls whether channel operators are automatically admins.
        if (!is_admin && admin_ops && null != sender) {
            Channel channel = event.getChannel();
            if (null != channel) {
                User[] users = getUsers(channel.getName());
                for(User user: users) {
                    if (user.isOp() && user.getNick().equals(sender)) {
                        is_admin = true;
                        break;
                    }
                }
            }
        }

        for(String trigger: commands.keySet()) {
            if (trigger.equals(event.getTrigger())) {
                ICommand command = commands.get(trigger);
                if (command.getIsEnabled() && (!command.getIsAdminOnly() || (command.getIsAdminOnly() && is_admin))) {
                    try {
                        Action action = null;
                        switch(event.getType()) {
                            case MESSAGE:
                                action = command.onMessage(event);
                                break;
                            case NOTICE:
                                action = command.onNotice(event);
                                break;
                            case PRIVATE:
                                action = command.onPrivate(event);
                                break;
                        }
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
     * Loads the commands from the database
     */
    public void loadCommands()
    {
        for(ICommand command: commands.values()) {
            command.onShutdown();
        }

        List<org.headzoo.irc.bots.coin.models.Command> db_commands
            = org.headzoo.irc.bots.coin.models.Command.findAll();
        for(org.headzoo.irc.bots.coin.models.Command db_command: db_commands) {
            String class_name  = db_command.getCName();
            String trigger     = db_command.getTrig();
            Boolean is_enabled = db_command.getIsEnabled();
            Boolean is_admin   = db_command.getIsAdmin();

            out("Loading command " + class_name + " with trigger " + trigger + " is_enabled = " + is_enabled + " is_admin = " + is_admin);
            ICommand c = AbstractCommand.factory(class_name, this, trigger);
            c.setIsEnabled(is_enabled);
            c.setIsAdminOnly(is_admin);
            commands.put(trigger, c);
        }

        for(ICommand command: commands.values()) {
            command.onStartup();
        }
    }

    /**
     * Loads the admin users from the database
     */
    public void loadAdmins()
    {
        initDatabaseThread();
        admins = Admin.findAll();
        for(Admin admin: admins) {
            out("Loaded admin " + admin.getString("nick") + "@" + admin.getString("hostname"));
        }
    }

    /**
     * Output the message to stdout if the verbose option is turned on
     *
     * @param message The message to output
     */
    public void out(String message)
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
    public void err(String message)
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
    public void err(Exception e)
    {
        if (verbose) {
            e.printStackTrace();
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
                String[] hostnames = admin.getHostname().split("\\|");
                for(String host: hostnames) {
                    if (sender.equals(admin.getNick()) && hostname.equals(host)) {
                        is_admin = true;
                        break;
                    }
                }
            }
        }

        return is_admin;
    }
}
