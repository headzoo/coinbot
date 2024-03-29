package org.headzoo.irc.bots.coin;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ini4j.Ini;

import java.io.*;

/**
 * Main runtime class
 *
 * Created by Sean Hickey <sean@mincoin.io> on 1/27/14.
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
public class Application
{
    /**
     * Exit code, okay
     */
    public static final Integer EXIT_OKAY = 0;

    /**
     * Exit code, general error
     */
    public static final Integer EXIT_ERROR = 1;

    /**
     * Exit code, configuration error
     */
    public static final Integer EXIT_CONFIG = 2;

    /**
     * Exit code, database error
     */
    public static final Integer EXIT_DATABASE = 3;

    /**
     * Exit code, bot error
     */
    public static final Integer EXIT_BOT = 4;

    /**
     * The default configuration file
     */
    public static final String CONF_FILE = "/conf/coinbot.conf";

    /**
     * Main method
     *
     * @param args Command line arguments
     */
    public static void main(String[] args)
    {
        Ini config = null;
        try {
            config = new Ini(new File(System.getProperty("user.dir"), CONF_FILE));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(EXIT_CONFIG);
        }

        String uri = String.format(
            "jdbc:mysql://%s/%s?user=%s&password=%s",
            config.get("database", "host", String.class),
            config.get("database", "name", String.class),
            config.get("database", "user", String.class),
            config.get("database", "pass", String.class)
        );
        DataSource data_source = new DataSource(uri);

        try {
            ServerDetails server = new ServerDetails(
                config.get("irc", "host", String.class),
                config.get("irc", "port", Integer.class),
                config.get("irc", "password", String.class)
            );
            CoinBot bot = new CoinBot(server, data_source);
            bot
                .setNick(config.get("irc", "nick", String.class))
                .setAdminOps(config.get("irc", "admin_ops", Boolean.class))
                .setNickServPassword(config.get("irc", "password", String.class))
                .setVerbose(config.get("bot", "verbose", Boolean.class))
                .setPrefix(config.get("bot", "prefix", String.class))
                .setChannels(config.get("irc").getAll("join"))
                .connect();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(EXIT_BOT);
        }
    }
}
