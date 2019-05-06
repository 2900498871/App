package com.sc.main.weatherBeans;

import com.google.gson.annotations.SerializedName;

/**
 * 定义天气的实体类
 */
public class baseic {

    @SerializedName("city")
    public String cityName;

    @SerializedName("cid")
    public String weatherId;

    @SerializedName("update")
    public update update;

    private class update{
        @SerializedName("loc")
        public String updateTime;
    }


}
