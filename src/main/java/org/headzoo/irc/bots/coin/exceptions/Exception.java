package org.headzoo.irc.bots.coin.exceptions;

/**
 * The base exception class
 *
 * Created by Sean <sean@mincoin.io> on 1/29/14.
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
public class Exception
    extends java.lang.Exception
{
    public Exception()
    {
        super();
    }

    public Exception(String message)
    {
        super(message);
    }

    public Exception(String message, Throwable cause)
    {
        super(message, cause);
    }

    public Exception(Throwable cause)
    {
        super(cause);
    }
}
