package org.headzoo.irc.bots.coin;

/**
 * IRC server configuration
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
public class Server
{
    /**
     * The irc server host name
     */
    protected String host = null;

    /**
     * The irc server port
     */
    protected Integer port = 6667;

    /**
     * The server password
     */
    protected String password = null;

    /**
     * Constructor
     *
     * @param host The server host name
     */
    public Server(String host)
    {
        setHost(host);
    }

    /**
     * Constructor
     *
     * @param host The server host name
     * @param port The server port
     */
    public Server(String host, Integer port)
    {
        setHost(host);
        setPort(port);
    }

    /**
     * Constructor
     *
     * @param host The server host name
     * @param port The server port
     * @param password The server password
     */
    public Server(String host, Integer port, String password)
    {
        setHost(host);
        setPort(port);
        setPassword(password);
    }

    /**
     * Sets the irc server host name
     *
     * @param host Server host name
     * @return Server
     */
    public Server setHost(String host)
    {
        this.host = host;
        return this;
    }

    /**
     * Returns the server host name
     *
     * @return The host name
     */
    public String getHost()
    {
        return host;
    }

    /**
     * Sets the irc server port
     *
     * @param port A port number
     * @return Server
     */
    public Server setPort(Integer port)
    {
        this.port = port;
        return this;
    }

    /**
     * Returns the server port
     *
     * @return The port
     */
    public Integer getPort()
    {
        return port;
    }

    /**
     * Sets the server password
     *
     * @param password The server password
     * @return Server
     */
    public Server setPassword(String password)
    {
        this.password = password;
        return this;
    }

    /**
     * Returns the server password
     *
     * @return The password
     */
    public String getPassword()
    {
        return password;
    }
}
