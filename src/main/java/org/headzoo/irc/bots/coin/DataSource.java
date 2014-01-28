package org.headzoo.irc.bots.coin;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * Database data source
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
public class DataSource
    implements javax.sql.DataSource
{
    /**
     * The connection uri
     */
    private String uri = null;

    /**
     * Maximum time in seconds that this data source will wait while attempting to connect to a database
     */
    private int timeout = 0;

    /**
     * Constructor
     *
     * @param uri The connection uri
     */
    public DataSource(String uri)
    {
        this.uri = uri;
    }

    @Override
    public Connection getConnection() throws SQLException
    {
        return DriverManager.getConnection(uri);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException
    {
        return DriverManager.getConnection(uri, username, password);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException
    {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException
    {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException
    {
        timeout = seconds;
    }

    @Override
    public int getLoginTimeout() throws SQLException
    {
        return timeout;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException
    {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException
    {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException
    {
        return false;
    }
}
