package org.headzoo.irc.bots.coin.commands;

import org.headzoo.irc.bots.coin.Action;
import org.headzoo.irc.bots.coin.CoinBot;
import org.headzoo.irc.bots.coin.Event;

import java.util.Map;

/**
 * Help command
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
public class Help
    extends AbstractCommand
{
    /**
     * Constructor
     *
     * @param bot     Instance of the main bot
     * @param trigger The command trigger
     */
    public Help(CoinBot bot, String trigger)
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
            "Get a list of all bot commands.",
            "help"
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action onMessage(Event event)
    {
        Map<String, ICommand> commands = bot.getCommands();
        Action action = new Action();
        String buffer = "";
        String prefix = bot.getPrefix();
        Boolean is_admin = bot.isAdmin(event.getSender(), event.getHostname());

        for(String trig: commands.keySet()) {
            ICommand command = commands.get(trig);
            if (command.getIsEnabled() && (!command.getIsAdminOnly() || (command.getIsAdminOnly() && is_admin))) {
                String description = command.getDescription(event.getSender(), event.getHostname());
                if (null != description) {
                    buffer += description + "\n";
                }
            }
        }
        if (!buffer.isEmpty()) {
            buffer = bot.getNick() + " Help\n" + buffer;
            action
                .setMessage(buffer)
                .setTarget(event.getSender());
        }

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
