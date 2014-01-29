package org.headzoo.irc.bots.coin.commands;

import org.headzoo.irc.bots.coin.Action;
import org.headzoo.irc.bots.coin.Event;

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
     * Returns the command description
     *
     * @param sender Name of the user asking for the description
     * @param hostname The hostname of the user
     */
    public String getDescription(String sender, String hostname);

    /**
     * Returns whether the command is enabled
     *
     * @return Is the command enabled?
     */
    public Boolean getIsEnabled();

    /**
     * Sets whether the command is enabled
     */
    public void setIsEnabled(Boolean is_enabled);

    /**
     * Returns whether the command can only be used by admins
     *
     * @return For admins only?
     */
    public Boolean getIsAdminOnly();

    /**
     * Sets whether the command can only be used by admins
     *
     * @param is_admin Is the command admin only?
     */
    public void setIsAdminOnly(Boolean is_admin);

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
