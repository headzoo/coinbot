package org.headzoo.irc.bots.coin.models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

import java.util.Date;

/**
 * Model for the "channels" table
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
@Table(value = "channels")
public class Channel
    extends Model
{
    /**
     * Returns the channel id
     *
     * @return The id
     */
    public Integer getId()
    {
        return getInteger("id");
    }

    /**
     * Returns the server the channel belongs to
     *
     * @return The server
     */
    public Server getServer()
    {
        return (Server)get("server");
    }

    /**
     * Returns the name of the channel
     *
     * @return The name
     */
    public String getName()
    {
        return getString("name");
    }

    /**
     * Returns the date of the last join
     *
     * @return The date
     */
    public Date getDateLastJoined()
    {
        return getDate("date_last_joined");
    }
}
