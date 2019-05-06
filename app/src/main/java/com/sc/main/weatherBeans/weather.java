package com.sc.main.weatherBeans;

import com.google.gson.annotations.SerializedName;

/**
 * 天气类
 */
public class weather {

    @SerializedName("status")
    public String status;

    @SerializedName("aqi")
    public aqi aqi;

    @SerializedName("basic")
    public baseic basic;

    @SerializedName("daily_forecast")
    public forecast forecast;

    @SerializedName("now")
    public now now;

    @SerializedName("suggestion")
    public suggestions suggestions;


}
