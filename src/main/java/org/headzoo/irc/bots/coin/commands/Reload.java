package org.headzoo.irc.bots.coin.commands;

import org.headzoo.irc.bots.coin.Action;
import org.headzoo.irc.bots.coin.CoinBot;
import org.headzoo.irc.bots.coin.Event;

/**
 * Reload command
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
public class Reload
    extends AbstractCommand
{
    /**
     * Constructor
     *
     * @param bot     Instance of the main bot
     * @param trigger The command trigger
     */
    public Reload(CoinBot bot, String trigger)
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
            "<commands|admins>",
            "Reload the commands or admins from the database",
            "reload admins"
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action onMessage(Event event)
    {
        String message = event.getMessage();
        Action action = new Action();
        if (message.equals("commands")) {
            bot.loadCommands();
            action
                .setMessage("Commands will be reloaded.")
                .setTarget(event.getSender());
        } else if (message.equals("admins")) {
            bot.loadAdmins();
            action
                .setMessage("Admins will be reloaded.")
                .setTarget(event.getSender());
        } else {
            action
                .setMessage("Nothing to load.")
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
