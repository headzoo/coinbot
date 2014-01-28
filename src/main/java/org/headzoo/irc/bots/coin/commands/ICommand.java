package org.headzoo.irc.bots.coin.commands;

import org.headzoo.irc.bots.coin.DataSource;

/**
 * Interface for command classes
 *
 * Created by Sean <sean@mincoin.io> on 1/28/14.
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
public interface ICommand
{
    /**
     * Set the bot trigger
     *
     * @param trigger The trigger
     */
    public void setTrigger(String trigger);

    /**
     * Sets the database data source in case the command needs database access
     *
     * @param data_source The data source
     */
    public void setDataSource(DataSource data_source);

    /**
     * Returns the command description
     *
     * @param sender Name of the user asking for the description
     * @param hostname The hostname of the user
     * @param trigger_char The current trigger character
     * @param trigger Return the description for this trigger
     */
    public String getDescription(String sender, String hostname, String trigger_char, String trigger);

    /**
     * Returns whether the command is for bot admins only
     *
     * @return For admins only?
     */
    public Boolean isAdminOnly();

    /**
     * Called once all commands are loaded
     */
    public void triggerStartup();

    /**
     * Called when the command is being shutdown
     */
    public void triggerShutdown();

    /**
     * This method is called whenever the command trigger is sent to a channel
     *
     * @param event The trigger event information
     * @return The action to take
     */
    public Action triggerMessage(Event event);

    /**
     * This method is called whenever the command trigger is sent through a notice
     *
     * @param event The trigger event information
     * @return The action to take
     */
    public Action triggerNotice(Event event);

    /**
     * This method is called whenever the command trigger is sent through a private message
     *
     * @param event The trigger event information
     * @return The action to take
     */
    public Action triggerPrivateMessage(Event event);

    /**
     * This method is called whenever the command trigger is sent through the channel, as a private message, or
     * as a notice. This method is called in addition to the triggers for those events.
     *
     * @param event The trigger event information
     * @return The action to take
     */
    public Action triggerAny(Event event);


}
