package org.headzoo.irc.bots.coin.commands;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.headzoo.irc.bots.coin.Action;
import org.headzoo.irc.bots.coin.CoinBot;
import org.headzoo.irc.bots.coin.Event;
import org.headzoo.irc.bots.coin.models.Channel;

import java.io.IOException;

/**
 * Faucet command
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
public class Faucet
    extends AbstractCommand
{
    /**
     * Constructor
     *
     * @param bot     Instance of the main bot
     * @param trigger The command trigger
     */
    public Faucet(CoinBot bot, String trigger)
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
            "<mincoin_address>",
            "Submit an address to the MinCoin faucet, and get free coin!",
            trigger + " MGW9WikecePFjjjQFqX7J1qDEJMCGqRNXX"
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
        action.setTarget(target);

        String message = event.getMessage();
        if (message.trim().isEmpty()) {
            action.setMessage(event.getSender() + ": You didn't give me your MinCoin address!");
        } else {
            CloseableHttpClient client = HttpClients.createDefault();
            try {
                ResponseHandler<String> handler = new ResponseHandler<String>()
                {
                    @Override
                    public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException
                    {
                        int status = response.getStatusLine().getStatusCode();
                        if (status >= 200 && status < 300) {
                            HttpEntity entity = response.getEntity();
                            return entity != null ? EntityUtils.toString(entity) : null;
                        } else {
                            throw new ClientProtocolException("Unexpected response status: " + status);
                        }
                    }
                };

                HttpGet get = new HttpGet("https://mincoin.io/faucet?api=1&mnc_address=" + message);
                String response = client.execute(get, handler);
                action.setMessage(event.getSender() + ": " + response);
            } catch (Exception e) {
                action.setMessage(event.getSender() + ": Oops! Something broke! Try again in a minute.");
            } finally {
                try {
                    client.close();
                } catch (IOException e) {
                    bot.err(e);
                }
            }

        }

        return action;
    }
}
