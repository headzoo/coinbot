package org.headzoo.irc.bots.coin.commands;

import com.google.gson.Gson;
import org.headzoo.irc.bots.coin.Action;
import org.headzoo.irc.bots.coin.CoinBot;
import org.headzoo.irc.bots.coin.Event;
import org.headzoo.irc.bots.coin.models.Admin;

import java.util.ArrayList;
import java.util.List;

/**
 * Debug command
 *
 * Created by Sean <sean@mincoin.io> on 1/29/14.
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
public class Debug
    extends AbstractCommand
{
    /**
     * Constructor
     *
     * @param bot     Instance of the main bot
     * @param trigger The command trigger
     */
    public Debug(CoinBot bot, String trigger)
    {
        super(bot, trigger);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription(String sender, String hostname)
    {
        return buildDescription(
            "",
            "Dumps bot debugging information.",
            trigger
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action onMessage(Event event)
    {
        Gson gson = new Gson();
        StringBuilder builder = new StringBuilder();
        builder.append("Nick: ").append(bot.getNick()).append("\n");
        builder.append("NickServ Password: ").append(bot.getNickServPassword()).append("\n");
        builder.append("Prefix: ").append(bot.getPrefix()).append("\n");
        builder.append("Admin Ops: ").append(bot.getAdminOps()).append("\n");
        builder.append("Verbose: ").append(bot.getVerbose()).append("\n");
        builder.append("Server Details: ").append(gson.toJson(bot.getServerDetails())).append("\n");
        builder.append("Commands: ").append(gson.toJson(bot.getCommands().keySet())).append("\n");
        builder.append("Channels: ").append(gson.toJson(bot.getChannels())).append("\n");
        builder.append("Joined: ").append(gson.toJson(bot.getJoined().keySet())).append("\n");

        List<Admin> admins = bot.getAdmins();
        List<String> a = new ArrayList<String>();
        for(Admin admin: admins) {
            a.add(admin.getNick() + "@" + admin.getHostname());
        }
        builder.append("Admins: ").append(gson.toJson(a)).append("\n");

        Action action  = new Action();
        action
            .setMessage(builder.toString())
            .setTarget(event.getSender());
        return action;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action onPrivate(Event event)
    {
        return onMessage(event);
    }
}
