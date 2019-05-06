package com.sc;

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



}
