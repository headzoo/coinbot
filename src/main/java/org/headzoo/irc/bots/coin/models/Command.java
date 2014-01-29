package org.headzoo.irc.bots.coin.models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

/**
 * Model for the "commands" table
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
@Table(value = "commands")
public class Command
    extends Model
{
    /**
     * Returns the command id
     *
     * @return The command id
     */
    public Integer getId()
    {
        return getInteger("id");
    }

    /**
     * Returns the command trigger
     *
     * @return The command trigger
     */
    public String getTrig()
    {
        return getString("trig");
    }

    /**
     * Returns the command class name
     *
     * @return The command class name
     */
    public String getCName()
    {
        return getString("cname");
    }

    /**
     * Returns whether the command is enabled
     *
     * @return Whether the command is enabled
     */
    public Boolean getIsEnabled()
    {
        return getBoolean("is_enabled");
    }

    /**
     * Returns whether the command is for admins only
     *
     * @return Whether the command is for admins only
     */
    public Boolean getIsAdmin()
    {
        return getBoolean("is_admin");
    }
}
