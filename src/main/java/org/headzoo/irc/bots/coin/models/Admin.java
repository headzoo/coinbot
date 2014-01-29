package org.headzoo.irc.bots.coin.models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

/**
 * Model for the "admins" table
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
@Table(value = "admins")
public class Admin
    extends Model
{
    /**
     * Returns the admin id
     *
     * @return The id
     */
    public Integer getId()
    {
        return getInteger("id");
    }

    /**
     * Returns the name of the admin
     *
     * @return The name
     */
    public String getNick()
    {
        return getString("nick");
    }

    /**
     * Returns the name admin host name
     *
     * @return The host name
     */
    public String getHostname()
    {
        return getString("hostname");
    }
}
