package org.headzoo.irc.bots.coin.commands;

import org.headzoo.irc.bots.coin.Action;
import org.headzoo.irc.bots.coin.CoinBot;
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
     * The bot trigger
     */
    protected String trigger = null;

    /**
     * Whether the command is enabled
     */
    protected Boolean is_enabled = true;

    /**
     * Whether the command can only be used by admins
     */
    protected Boolean is_admin = false;

    /**
     * Constructor
     *
     * @param bot Instance of the main bot
     * @param trigger The command trigger
     */
    public AbstractCommand(CoinBot bot, String trigger)
    {
        this.bot = bot;
        this.trigger = trigger;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean getIsEnabled()
    {
        return is_enabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIsEnabled(Boolean is_enabled)
    {
        this.is_enabled = is_enabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean getIsAdminOnly()
    {
        return is_admin;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIsAdminOnly(Boolean is_admin)
    {
        this.is_admin = is_admin;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStartup() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void onShutdown() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public Action onMessage(Event event)
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action onNotice(Event event)
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action onPrivate(Event event)
    {
        return null;
    }

    /**
     * Build a description
     *
     * @param options The command options
     * @param description The command description
     * @param example The command example
     * @return The description
     */
    public String buildDescription(String options, String description, String example)
    {
        String prefix = bot.getPrefix();
        StringBuilder builder = new StringBuilder(prefix);
        builder.append(trigger);

        Integer len = 12 - trigger.length();
        if (len < 0) len = 0;
        builder.append(new String(new char[len]).replace("\0", " "));
        builder.append(options);

        len = 25 - options.length();
        if (len < 0) len = 0;
        builder.append(new String(new char[len]).replace("\0", " "));
        builder.append(description);
        if (!example.isEmpty()) {
            builder.append(" Example '");
            builder.append(prefix);
            builder.append(example);
            builder.append("'.");
        }

        return builder.toString();
    }

    /**
     * Initializes a new bot command from the given class name
     *
     * @param class_name The bot class name
     * @param bot Instance of the main bot
     * @param trigger The command trigger
     * @return The command
     */
    public static ICommand factory(String class_name, CoinBot bot, String trigger)
    {
        ICommand command = null;
        try {
            Object[] args          = { bot, trigger };
            Class<?>[] types       = { CoinBot.class, String.class };
            Class<?> command_class = Class.forName(class_name);
            Constructor<?> ctor    = command_class.getConstructor(types);
            command = (ICommand)ctor.newInstance(args);
        } catch (Exception e) {
            bot.err(e);
        }

        return command;
    }
}
