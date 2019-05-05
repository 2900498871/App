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
import com.sc.main.beans.city;
import com.sc.main.beans.country;
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
     * 获取省的list数据
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


    /**
     * 获取省份的城市列表
     * @param response
     * @param provinceId
     * @return
     */
    public static boolean handleCityResponse(String response,int provinceId){
        List<city> list=new ArrayList();
        if(!TextUtils.isEmpty(response)){
            try{
                    JSONArray citys= new JSONArray(response);
                    for(int i=0;i<citys.length();i++){
                        JSONObject jsonObject= citys.getJSONObject(i);
                        city city= new city();
                        city.setProvinceId(provinceId);
                        city.setId(jsonObject.getInt("id"));
                        city.setCityCode(jsonObject.getInt("id"));
                        city.setCityName(jsonObject.getString("name"));
                        list.add(city);
                    }
                    //查询此地区的城市是否存入到数据库中，若存入则不存，反之则存入
                    List<city> lis=DataSupport.where("provinceId=?",provinceId+"").find(city.class);
                    if(lis.size()==0){
                        DataSupport.saveAll(list);
                    }
            }catch(Exception e){
                e.printStackTrace();
                return false;
            }
            return true;
        }else{
            return false;
        }

    }


    /**
     * 获取县的数据
     * @param response
     * @param ctid
     * @return
     */
    public static boolean hanleCountryResponse(String response,int ctid){
        List<country> list=new ArrayList();
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray countrys= new JSONArray(response);
                for(int i=0;i<countrys.length();i++){
                    JSONObject jsonObject= countrys.getJSONObject(i);
                    country country=new country();
                    country.setId(jsonObject.getInt("id"));
                    country.setCityId(ctid);
                    country.setCountryName(jsonObject.getString("name"));
                    country.setWeatherId(jsonObject.getString("weather_id"));
                    list.add(country);
                }
                //查询此地区的城市是否存入到数据库中，若存入则不存，反之则存入
                List<country> lis= DataSupport.where("cityId=?",ctid+"").find(country.class);
                if(lis.size()==0){
                    DataSupport.saveAll(list);
                }

            }catch(Exception e){
                e.printStackTrace();
                return false;
            }
            return true;
        }else{
            return false;
        }

    }






}
