package org.headzoo.irc.bots.coin.commands;

import org.headzoo.irc.bots.coin.Action;
import org.headzoo.irc.bots.coin.CoinBot;
import org.headzoo.irc.bots.coin.DescriptionBuilder;
import org.headzoo.irc.bots.coin.Event;

/**
 * Class description
 * <p/>
 * Created by Sean <sean@mincoin.io> on 1/29/14.
 * <p/>
 * The MIT License (MIT)
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 */
public class Shutdown
    extends AbstractCommand
{
    /**
     * Constructor
     *
     * @param bot Instance of the main bot
     */
    public Shutdown(CoinBot bot)
    {
        super(bot);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean isAdminOnly()
    {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription(String sender, String hostname, String trigger_char, String trigger)
    {
        return DescriptionBuilder.build(
            trigger_char,
            trigger,
            "",
            "Immediately shutdown the bot.",
            "shutdown"
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action triggerAny(Event event)
    {
        bot.shutdown();
        return null;
    }
}
