package org.headzoo.irc.bots.coin.commands;

import org.headzoo.irc.bots.coin.Action;
import org.headzoo.irc.bots.coin.CoinBot;
import org.headzoo.irc.bots.coin.Event;
import org.headzoo.irc.bots.coin.models.Channel;
import org.headzoo.irc.bots.coin.models.Pool;

import java.util.List;

/**
 * Class description
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
public class Pools
    extends AbstractCommand
{
    /**
     * Constructor
     *
     * @param bot     Instance of the main bot
     * @param trigger The command trigger
     */
    public Pools(CoinBot bot, String trigger)
    {
        super(bot, trigger);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription(String sender, String hostname)
    {
        String prefix = bot.getPrefix();
        String options     = new String();
        String example     = new String();
        String description = new String();
        if (bot.isAdmin(sender, hostname)) {
            options     = "(add|remove) (pool_url)";
            example     = " OR " + prefix + trigger + " add mypool.net OR " + prefix + trigger + " remove mypool.net";
            description = "Lists, adds, or removes from the list of mining pools.";
        } else {
            example     = "";
            options     = "";
            description = "List the current mining pools.";
        }
        return buildDescription(
            options,
            description,
            trigger + example
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
        Action action    = new Action();
        String message   = event.getMessage();
        String target    = event.getSender();
        Channel channel  = event.getChannel();
        Boolean is_admin = bot.isAdmin(target, event.getHostname());
        if (null != channel) {
            target = channel.getName();
        }
        action.setTarget(target);

        String[] split = message.split(" ", 2);
        if (0 == split.length || split[0].isEmpty()) {
            List<Pool> pools = Pool.findAll();
            StringBuilder sb = new StringBuilder();
            String delim = "";
            for (Pool pool : pools) {
                sb.append(delim).append(pool.getString("url"));
                delim = ", ";
            }
            action.setMessage("Mining Pools: " + sb.toString());
        } else if (split[0].equals("add") && 2 == split.length && !split[1].isEmpty() && is_admin) {
            split[1] = formatUrl(split[1]);
            Pool pool = Pool.findFirst("url = ?", split[1]);
            if (null != pool) {
                action.setMessage("Pool '" + split[1] + "' already exists in the database.");
            } else {
                pool = new Pool();
                pool.setString("url", split[1]);
                pool.saveIt();
                action.setMessage("The pool '" + split[1] + "' has been added to the database.");
            }
        } else if (split[0].equals("remove") && 2 == split.length && !split[1].isEmpty() && is_admin) {
            split[1] = formatUrl(split[1]);
            Pool pool = Pool.findFirst("url = ?", split[1]);
            if (null == pool) {
                action.setMessage("No pool found in the database with url '" + split[1] + "'.");
            } else {
                pool.delete();
                action.setMessage("The pool '" + split[1] + "' has been removed the database.");
            }
        } else {
            action.setMessage("I did not understand your command.");
        }

        return action;
    }

    /**
     * Formats pool urls
     *
     * Ensures the url starts with at least "http://", and does not end with a slash.
     *
     * @param url The url to format
     * @return The formatted url
     */
    protected String formatUrl(String url)
    {
        if (!url.startsWith("http")) {
            url = "http://" + url;
        }
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        return url;
    }
}
