package org.headzoo.irc.bots.coin.commands;

import org.headzoo.irc.bots.coin.Action;
import org.headzoo.irc.bots.coin.CoinBot;
import org.headzoo.irc.bots.coin.Event;
import org.headzoo.irc.bots.coin.MinCoin;
import org.headzoo.irc.bots.coin.models.Channel;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Trading command
 *
 * Created by Sean <sean@mincoin.io> on 2/4/14.
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
public class Trading
    extends AbstractCommand
{
    /**
     * Constructor
     *
     * @param bot     Instance of the main bot
     * @param trigger The command trigger
     */
    public Trading(CoinBot bot, String trigger)
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
            "Returns MNC trading information.",
            trigger
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action onMessage(Event event)
    {
        return onAny(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action onPrivate(Event event)
    {
        return onAny(event);
    }

    /**
     * Handler for channel and private messages
     *
     * @param event The event
     * @return The action
     */
    private Action onAny(Event event)
    {
        Action action   = new Action();
        String target   = event.getSender();
        Channel channel = event.getChannel();
        if (null != channel) {
            target = channel.getName();
        }
        try {
            MinCoin mincoin = MinCoin.getInstanceFromApi();
            DecimalFormat formatter_mnc = new DecimalFormat("#,###.00000000");
            DecimalFormat formatter_btc = new DecimalFormat("#,###.00000000");
            action.setTarget(target).setMessage(
                String.format(
                    "Last 24 hour trading volume: %s MNC, %s BTC",
                    formatter_mnc.format(mincoin.getTradingMNC()),
                    formatter_btc.format(mincoin.getTradingBTC())
                )
            );

        } catch (IOException e) {
            bot.err(e);
            action.setMessage("Unable to get trading data right now. Try again later.")
                .setTarget(target);
        }

        return action;
    }
}
