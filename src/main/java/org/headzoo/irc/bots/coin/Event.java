package org.headzoo.irc.bots.coin;

import org.headzoo.irc.bots.coin.models.Channel;

/**
 * Describes an triggered event
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
public class Event
{
    /**
     * The types of events
     */
    public enum Types
    {
        MESSAGE,
        NOTICE,
        PRIVATE
    }

    /**
     * The name of the event
     */
    private Types name = null;

    /**
     * The trigger that was used
     */
    private String trigger = null;

    /**
     * The nick of the person who sent the message
     */
    private String sender = null;

    /**
     * The target of the notice, be it our nick or a channel name
     */
    private String target = null;

    /**
     * The channel to which the message was sent
     */
    private Channel channel = null;

    /**
     * The login of the user that sent the notice
     */
    private String login = null;

    /**
     * The hostname of the user that sent the notice
     */
    private String hostname = null;

    /**
     * The actual message without trigger
     */
    private String message = null;

    /**
     * Constructor
     *
     * @param type The type of the event
     * @param trigger The trigger that was used
     */
    public Event(Types type, String trigger)
    {
        setType(type);
        setTrigger(trigger);
    }

    /**
     * Gets the name of the event
     *
     * @return Name of the event
     */
    public Types getType()
    {
        return name;
    }

    /**
     * Sets the name of the event
     *
     *
     * @param name Name of the event
     * @return Event
     */
    public Event setType(Types name)
    {
        this.name = name;
        return this;
    }

    /**
     * Gets the trigger that was used
     *
     * @return The trigger
     */
    public String getTrigger()
    {
        return trigger;
    }

    /**
     * Sets the trigger that was used
     *
     * @param trigger The trigger
     * @return Event
     */
    public Event setTrigger(String trigger)
    {
        this.trigger = trigger;
        return this;
    }

    /**
     * Gets the nick of the person who sent the message
     *
     * @return The sender
     */
    public String getSender()
    {
        return sender;
    }

    /**
     * Sets the nick of the person who sent the message
     *
     * @param sender The sender
     * @return Event
     */
    public Event setSender(String sender)
    {
        this.sender = sender;
        return this;
    }

    /**
     * Gets the target of the notice, be it our nick or a channel name
     *
     * @return The target
     */
    public String getTarget()
    {
        return target;
    }

    /**
     * Sets the target of the notice, be it our nick or a channel name
     *
     * @param target The target
     * @return Event
     */
    public Event setTarget(String target)
    {
        this.target = target;
        return this;
    }

    /**
     * Gets the channel to which the message was sent
     *
     * @return The channel
     */
    public Channel getChannel()
    {
        return channel;
    }

    /**
     * Sets the channel to which the message was sent
     *
     * @param channel The channel
     * @return Event
     */
    public Event setChannel(Channel channel)
    {
        this.channel = channel;
        return this;
    }

    /**
     * Gets the login of the user that sent the notice
     *
     * @return The login
     */
    public String getLogin()
    {
        return login;
    }

    /**
     * Sets the login of the user that sent the notice
     *
     * @param login The login
     * @return Event
     */
    public Event setLogin(String login)
    {
        this.login = login;
        return this;
    }

    /**
     * Gets the hostname of the user that sent the notice
     *
     * @return The host name
     */
    public String getHostname()
    {
        return hostname;
    }

    /**
     * Sets the hostname of the user that sent the notice
     *
     * @param hostname The host name
     * @return Event
     */
    public Event setHostname(String hostname)
    {
        this.hostname = hostname;
        return this;
    }

    /**
     * Gets the actual message without trigger
     *
     * @return The message
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * Sets the actual message without trigger
     *
     * @param message The message
     * @return Event
     */
    public Event setMessage(String message)
    {
        this.message = message;
        return this;
    }
}
