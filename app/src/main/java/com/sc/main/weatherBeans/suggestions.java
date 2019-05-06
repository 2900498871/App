package com.sc.main.weatherBeans;

import com.google.gson.annotations.SerializedName;

/**
 * 天气建议
 */
public class suggestions {

    @SerializedName("cw")
    public cw cw;

    @SerializedName("sport")
    public sport sport;

    @SerializedName("comf")
    public comf comf;


    private  class cw{
        @SerializedName("type")
        public String type;
        @SerializedName("brf")
        public String brf;
        @SerializedName("txt")
        public String txt;
    }

    private class sport{
        @SerializedName("type")
        public String type;
        @SerializedName("brf")
        public String brf;
        @SerializedName("txt")
        public String txt;
    }

    public class comf{
        @SerializedName("type")
        public String type;
        @SerializedName("brf")
        public String brf;
        @SerializedName("txt")
        public String txt;
    }
}
