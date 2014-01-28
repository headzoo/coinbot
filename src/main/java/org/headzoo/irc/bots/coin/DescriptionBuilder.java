package org.headzoo.irc.bots.coin;

/**
 * Used to build command descriptions
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
public class DescriptionBuilder
{
    /**
     * Build a description
     *
     * @param trigger_char The trigger character
     * @param trigger The command trigger
     * @param options The command options
     * @param description The command description
     * @param example The command example
     * @return The description
     */
    public static String build(String trigger_char, String trigger, String options, String description, String example)
    {
        StringBuilder builder = new StringBuilder(trigger_char);
        builder.append(trigger);

        Integer len = 12 - trigger.length();
        if (len < 0) len = 0;
        builder.append(new String(new char[len]).replace("\0", " "));
        builder.append(options);

        len = 25 - options.length();
        if (len < 0) len = 0;
        builder.append(new String(new char[len]).replace("\0", " "));
        builder.append(description);
        if (!example.isEmpty()) {
            builder.append(" Example '");
            builder.append(trigger_char);
            builder.append(example);
            builder.append("'.");
        }

        return builder.toString();
    }
}
