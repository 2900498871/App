package com.sc.main.weatherBeans;

import com.google.gson.annotations.SerializedName;

public class forecast {

    @SerializedName("date")
    public String date;

    @SerializedName("cond")
    public cond cond;

    @SerializedName("tmp")
    public tmp tmp;

    public class cond{
        @SerializedName("txt_d")
        public String txt_d;
    }

    public class tmp{
        @SerializedName("min")
        public String min;
        @SerializedName("max")
        public String max;
    }
}
