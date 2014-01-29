package org.headzoo.irc.bots.coin.commands;

import com.abwaters.btce.BTCE;
import com.abwaters.cryptsy.Cryptsy;
import org.headzoo.irc.bots.coin.Action;
import org.headzoo.irc.bots.coin.CoinBot;
import org.headzoo.irc.bots.coin.Event;
import org.headzoo.irc.bots.coin.models.Channel;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Price command
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
public class Price
    extends AbstractCommand
{
    /**
     * The default coin
     */
    protected String default_coin = "MNC";

    /**
     * For calling the btc-e api
     */
    protected BTCE btce = new BTCE();

    /**
     * For calling the cryptsy api
     */
    protected Cryptsy cryptsy = new Cryptsy();

    /**
     * The current prices
     */
    protected Map<String, Double> prices = new HashMap<String, Double>();

    /**
     * Constructor
     *
     * @param bot     Instance of the main bot
     * @param trigger The command trigger
     */
    public Price(CoinBot bot, String trigger)
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
            "(coin)",
            "Returns the current price of the coin in USD. Defaults to 'MNC'.",
            trigger + " MNC"
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action onMessage(Event event)
    {
        String message     = event.getMessage();
        Channel channel    = event.getChannel();
        Action action      = new Action();
        String coin        = null;
        Integer market_id  = null;
        String btce_ticker = "btc_usd";

        if (null != channel) {
            action.setTarget(channel.getName());
        } else {
            action.setTarget(event.getSender());
        }
        if (message.isEmpty()) {
            coin = default_coin;
        } else {
            coin = message.toUpperCase();
        }

        try {
            Cryptsy.Markets market = new Cryptsy.Markets();
            Class<?> cl = market.getClass();
            Field field = null;
            try {
                field = cl.getDeclaredField(coin + "_BTC");
                market_id = (Integer)field.get(market);
            } catch (NoSuchFieldException e) {
                try {
                    btce_ticker = "ltc_usd";
                    field = cl.getDeclaredField(coin + "_LTC");
                    market_id = (Integer)field.get(market);
                } catch (NoSuchFieldException ee) {
                    market_id = null;
                }
            }

            if (null == market_id) {
                action.setMessage("No market data available for " + coin + ".");
            } else {
                BTCE.Ticker tick = btce.getTicker(btce_ticker);
                Cryptsy.PublicMarket market_data = cryptsy.getPublicMarketData(market_id);
                Double price = tick.avg * market_data.lasttradeprice;

                prices.put(coin, price);
                action.setMessage(String.format("The current price of %s is $%.2f", coin, price));
            }
        } catch (Exception e) {
            action.setMessage("Unable to get market data. Please try again in a moment.");
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

    /**
     * Returns the current unix timestamp
     *
     * @return The timestamp
     */
    protected Long getUnixTimestamp()
    {
        return System.currentTimeMillis() / 1000L;
    }
}
