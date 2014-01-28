package org.headzoo.irc.bots.coin.commands;

import org.headzoo.irc.bots.coin.DataSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

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
public abstract class Command
    implements ICommand
{
    /**
     * The database data source
     */
    protected DataSource data_source = null;

    /**
     * The bot trigger
     */
    protected String trigger = null;

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
     * @return The command
     */
    public static Command factory(String class_name)
    {
        Command command = null;
        try {
            Class command_class = Class.forName(class_name);
            Constructor ctor = command_class.getConstructor();
            command = (Command)ctor.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return command;
    }
}
