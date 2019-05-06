package com.sc.main.weatherBeans;

import com.google.gson.annotations.SerializedName;

/**
 * 当前天气
 */
public class now {
    @SerializedName("tmp")
    public String tmp;

    @SerializedName("cloud")
    public String cloud;

    @SerializedName("wind_dir")
    public String wind_dir;

    @SerializedName("wind_sc")
    public String wind_sc;

    @SerializedName("wind_spd")
    public String wind_spd;

    @SerializedName("cond")
    public cond cond;

    private class cond{
        @SerializedName("code")
        public String code;
        @SerializedName("txt")
        public String txt;
    }
}
