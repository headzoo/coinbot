package org.headzoo.irc.bots.coin.commands;

/**
 * Describes the action a command wants the bot to take
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
public class Action
{
    /**
     * The message the command wants to respond with
     */
    private String message = null;

    /**
     * The user/channel the command wants the message private messaged to
     */
    private String target = null;

    /**
     * Whether the message should be sent to the target/channel as a notice
     */
    private Boolean is_notice = false;

    /**
     * Gets the message the command wants to respond with
     *
     * @return The message
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * Sets the message the command wants to respond with
     *
     * @param message The message
     * @return Action
     */
    public Action setMessage(String message)
    {
        this.message = message;
        return this;
    }

    /**
     * Gets the user/channel the command wants the message private messaged to
     *
     * @return The target
     */
    public String getTarget()
    {
        return target;
    }

    /**
     * Sets the user/channel the command wants the message private messaged to
     *
     * @param target The target
     * @return Action
     */
    public Action setTarget(String target)
    {
        this.target = target;
        return this;
    }

    /**
     * Gets whether the message should be sent to the target/channel as a notice
     *
     * @return Whether this is a notice
     */
    public Boolean getIsNotice()
    {
        return is_notice;
    }

    /**
     * Sets whether the message should be sent to the target/channel as a notice
     *
     * @param is_notice Whether this is a notice
     * @return Action
     */
    public Action setIsNotice(Boolean is_notice)
    {
        this.is_notice = is_notice;
        return this;
    }
}
