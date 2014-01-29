package org.headzoo.irc.bots.coin.commands;

import org.headzoo.irc.bots.coin.Action;
import org.headzoo.irc.bots.coin.CoinBot;
import org.headzoo.irc.bots.coin.Event;

/**
 * Send command
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
public class Send
    extends AbstractCommand
{
    /**
     * Constructor
     *
     * @param bot     Instance of the main bot
     * @param trigger The command trigger
     */
    public Send(CoinBot bot, String trigger)
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
            "Have the bot send the command tot he server.",
            "send /list"
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action triggerAny(Event event)
    {
        String message = event.getMessage();
        Action action = new Action();
        if (!message.isEmpty()) {
            if (message.startsWith("/")) {
                message = message.substring(1);
            }
            bot.sendRawLine(message);
        } else {
            action
                .setMessage("Nothing to send.")
                .setTarget(event.getSender());
        }

        return action;
    }
}
