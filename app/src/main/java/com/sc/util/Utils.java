package com.sc.util;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.bravin.btoast.BToast;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sc.SysConfig;
import com.sc.main.beans.province;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 提供一个工具类
 */
public class Utils {


    /**
     * 获取城市的list数据
     * @param response
     * @return
     */
    public static boolean handeProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray provinces=new JSONArray(response);
                List<province> list=new ArrayList();
                for(int i=0;i<provinces.length();i++){
                    //保存数据
                    province province= new province();
                    JSONObject jsonObject=provinces.getJSONObject(i);
                    province.setId(jsonObject.getInt("id"));
                    province.setProvinceName(jsonObject.getString("name"));
                    province.setProvinceCode(jsonObject.getInt("id"));
                    list.add(province);
                }
                //保存到数据库里面,若数据库存在则不保存
                List<province> provinceList=DataSupport.findAll(province.class);
                if(provinceList==null||provinceList.size()==0){
                    DataSupport.saveAll(list);
                }
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }






}
