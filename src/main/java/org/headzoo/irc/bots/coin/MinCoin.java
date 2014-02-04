package org.headzoo.irc.bots.coin;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Data object from the mincoin api
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
public class MinCoin
{
    /**
     * The mincoin.io api url
     */
    public static final String API_URL = "https://mincoin.io/api/stats.php";

    /**
     * The current difficulty
     */
    public Double difficulty;

    /**
     * The current block count
     */
    public Integer block_count;

    /**
     * The current network hash rate
     */
    public Integer hash_rate;

    /**
     * The current price of MinCoin in USD
     */
    public Double price_usd;

    /**
     * The current price of MinCoin in BTC
     */
    public Double price_btc;

    /**
     * The last 24 hours trading volume
     */
    public Integer trading_volume;

    /**
     * The trading volume in MNC
     */
    public Double trading_mnc;

    /**
     * The trading volume in BTC
     */
    public Double trading_btc;

    /**
     * Returns a MinCoin instance populated with data from the mincoin.io api
     *
     * @return The MinCoin instance
     * @throws IOException
     */
    public static MinCoin getInstanceFromApi() throws IOException
    {
        DefaultHttpClient client     = new DefaultHttpClient();
        HttpResponse getResponse     = client.execute(new HttpGet(API_URL));
        HttpEntity getResponseEntity = getResponse.getEntity();
        Reader reader                = new InputStreamReader(getResponseEntity.getContent());
        Gson gson                    = new Gson();
        return gson.fromJson(reader, MinCoin.class);
    }

    /**
     * Returns the current block difficulty
     *
     * @return The block difficulty
     */
    public Double getDifficulty()
    {
        return difficulty;
    }

    /**
     * Sets the block difficulty
     *
     * @param difficulty The block difficulty
     */
    public void setDifficulty(Double difficulty)
    {
        this.difficulty = difficulty;
    }

    /**
     * Returns the number of blocks
     *
     * @return The current number of blocks
     */
    public Integer getBlockCount()
    {
        return block_count;
    }

    /**
     * Sets the number of blocks
     *
     * @param block_count The current number of blocks
     */
    public void setBlockCount(Integer block_count)
    {
        this.block_count = block_count;
    }

    /**
     * Returns the network hash rate
     *
     * @return The network hash rate
     */
    public Integer getHashRate()
    {
        return hash_rate;
    }

    /**
     * Sets the network hash rate
     *
     * @param hash_rate The network hash rate
     */
    public void setHashRate(Integer hash_rate)
    {
        this.hash_rate = hash_rate;
    }

    /**
     * Returns the price of MinCoin in USD
     *
     * @return The price in USD
     */
    public Double getPriceUSD()
    {
        return price_usd;
    }

    /**
     * Sets the price of MinCoin in USD
     *
     * @param price_usd The price in USD
     */
    public void setPriceUSD(Double price_usd)
    {
        this.price_usd = price_usd;
    }

    /**
     * Returns the price of MinCoin in BTC
     *
     * @return The price in BTC
     */
    public Double getPriceBTC()
    {
        return price_btc;
    }

    /**
     * Sets the price of MinCoin in BTC
     *
     * @param price_btc The price in BTC
     */
    public void setPriceBTC(Double price_btc)
    {
        this.price_btc = price_btc;
    }

    /**
     * Returns the last 24 hours trading volume
     *
     * @return The trading volume
     */
    public Integer getTradingVolume()
    {
        return trading_volume;
    }

    /**
     * Sets the last 24 hours trading volume
     *
     * @param trading_volume The trading volume
     */
    public void setTradingVolume(Integer trading_volume)
    {
        this.trading_volume = trading_volume;
    }

    /**
     * Returns the last 24 hours trading volume in MNC
     *
     * @return The trading volume
     */
    public Double getTradingMNC()
    {
        return trading_mnc;
    }

    /**
     * Sets the last 24 hours trading volume in MNC
     *
     * @param trading_mnc The trading volume
     */
    public void setTradingMNC(Double trading_mnc)
    {
        this.trading_mnc = trading_mnc;
    }

    /**
     * Returns the last 24 hours trading volume in MNC
     *
     * @return The trading volume
     */
    public Double getTradingBTC()
    {
        return trading_btc;
    }

    /**
     * Sets the last 24 hours trading volume in MNC
     *
     * @param trading_btc The trading volume
     */
    public void setTradingBTC(Double trading_btc)
    {
        this.trading_btc = trading_btc;
    }

    /**
     * Return the data as a string
     *
     * @return The MinCoin data
     */
    @Override
    public String toString()
    {
        return String.format(
                "MinCoin { difficulty = %f, block_count = %d, hash_rate = %d, price_usd = %f, price_btc = %f, trading_volume = %d }",
                difficulty,
                block_count,
                hash_rate,
                price_usd,
                price_btc,
                trading_volume
        );
    }
}
