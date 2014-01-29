package org.headzoo.irc.bots.coin.commands;

import org.headzoo.irc.bots.coin.Action;
import org.headzoo.irc.bots.coin.CoinBot;
import org.headzoo.irc.bots.coin.Event;
import org.headzoo.irc.bots.coin.exceptions.InvalidCommandException;

/**
 * Enable command
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
public class Enable
    extends AbstractCommand
{
    /**
     * Constructor
     *
     * @param bot     Instance of the main bot
     * @param trigger The command trigger
     */
    public Enable(CoinBot bot, String trigger)
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
            "<command>",
            "Enable the bot command.",
            "enable memo"
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action onMessage(Event event)
    {
        String message = event.getMessage();
        Action action  = new Action();
        if (message.isEmpty()) {
            action
                .setMessage("No command given.")
                .setTarget(event.getSender());
        } else {
            try {
                bot.enableCommand(message);
                action
                    .setMessage("The command '" + message + "' has been enabled.")
                    .setTarget(event.getSender());
            } catch (InvalidCommandException e) {
                action
                    .setMessage(e.getMessage())
                    .setTarget(event.getSender());
            }
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
