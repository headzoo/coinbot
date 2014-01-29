package org.headzoo.irc.bots.coin.commands;

import org.headzoo.irc.bots.coin.Action;
import org.headzoo.irc.bots.coin.CoinBot;
import org.headzoo.irc.bots.coin.DataSource;
import org.headzoo.irc.bots.coin.Event;

import java.lang.reflect.Constructor;

/**
 * Parent for command classes
 *
 * Created by Sean Hickey <sean@mincoin.io> on 1/28/14.
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
public abstract class AbstractCommand
    implements ICommand
{
    /**
     * Instance of CoinBot
     */
    protected CoinBot bot = null;

    /**
     * The database data source
     */
    protected DataSource data_source = null;

    /**
     * The bot trigger
     */
    protected String trigger = null;

    /**
     * Constructor
     *
     * @param bot Instance of the main bot
     */
    public AbstractCommand(CoinBot bot)
    {
        this.bot = bot;
    }

    /**
     * {@inheritDoc}
     */
    public void setTrigger(String trigger)
    {
        this.trigger = trigger;
    }

    /**
     * {@inheritDoc}
     */
    public void setDataSource(DataSource data_source)
    {
        this.data_source = data_source;
    }

    /**
     * {@inheritDoc}
     */
    public Boolean isAdminOnly()
    {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void triggerStartup() {}

    /**
     * {@inheritDoc}
     */
    public void triggerShutdown() {}

    /**
     * {@inheritDoc}
     */
    public Action triggerMessage(Event event)
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Action triggerNotice(Event event)
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Action triggerPrivateMessage(Event event)
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Action triggerAny(Event event)
    {
        return null;
    }

    /**
     * Initializes a new bot command from the given class name
     *
     * @param class_name The bot class name
     * @param bot Instance of the main bot
     * @return The command
     */
    public static ICommand factory(String class_name, CoinBot bot)
    {
        ICommand command = null;
        try {
            Object[] args = { bot };
            Class<?>[] types       = { CoinBot.class };
            Class<?> command_class = Class.forName(class_name);
            Constructor<?> ctor    = command_class.getConstructor(types);
            command = (ICommand)ctor.newInstance(args);
        } catch (Exception e) {
            bot.err(e);
        }

        return command;
    }
}
