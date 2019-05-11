package com.sc;

import interfaces.heweather.com.interfacesmodule.bean.Lang;
import interfaces.heweather.com.interfacesmodule.view.HeConfig;

/**
 * 公共的配置
 */
public class SysConfig {

    /**
     * 一些常用的状态码
     */
    public final static int SUCCESS=200;//请求成功
    public final static int ERROR=500;//服务器未响应，错误
    public final static int WARING=400;// 错误
    public final static int INITERROR=300;// 初始化失败


    /**
     * 获取城市的路径
     */
    public final static String PROVINCE_URL="http://guolin.tech/api/china";

    /**
     * 获取天气的url
     */
    public final static String WEATHRE_URL="http://guolin.tech/api/weather?key=bc0418b57b2d4918819d3974ac1285d9&cityid=";


    /**
     * 获取每日一个图片的路径
     */
    public final static String WEATHER_IMG="http://guolin.tech/api/bing_pic";


    /**
     *和风天气的url
     */

      public final static String WEATHER_NEW_URL="https://free-api.heweather.net/s6/weather/forecast?key=bc0418b57b2d4918819d3974ac1285d9&lang="+ Lang.CHINESE_SIMPLIFIED+"&location=";


}
