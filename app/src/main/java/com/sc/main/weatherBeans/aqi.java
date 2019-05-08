package com.sc.main.weatherBeans;

import com.google.gson.annotations.SerializedName;

/**
 * 污染指数
 */
public class aqi {

    @SerializedName("city")
    public aqicity aqicity;

    public class aqicity{
        @SerializedName("aqi")
        public String aqi;
        @SerializedName("pm25")
        public String pm25;
        @SerializedName("qlty")
        public String qlty;
    }



}
