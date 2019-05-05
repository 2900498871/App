package com.sc.util;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.bravin.btoast.BToast;
import com.google.gson.Gson;
import com.sc.SysConfig;

import java.io.IOException;

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



    static Message message= new Message();
    public static Gson getProvinces(String address, OkHttpClient client){
        Gson gson=null;
        FormBody body= new FormBody.Builder()
                //.add("name","aaa")
                .build();
        Request request= new Request.Builder().post(body).url(SysConfig.PROVINCE_URL).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });



        return gson;
    }

    /**
     * handler 方法
     */
    private static  Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){

            }

        }
    };

//这是弹框
  //       BToast.Config.getInstance()
//                .setAnimate() // Whether to startAnimation. default is fasle;
//                .setAnimationDuration()// Animation duration. default is 800 millisecond
//                .setAnimationGravity()// Animation entering position. default is BToast.ANIMATION_GRAVITY_TOP
//                .setDuration()// toast duration  is Either BToast.DURATION_SHORT or BToast.DURATION_LONG
//                .setTextColor()// textcolor. default is white
//                .setErrorColor()// error style background Color default is red
//                .setInfoColor()// info style background Color default is blue
//                .setSuccessColor()// success style background Color default is green
//                .setWarningColor()// waring style background Color default is orange
//                .setLayoutGravity()// whan show an toast with target, coder can assgin position relative to target. default is BToast.LAYOUT_GRAVITY_BOTTOM
//                .setLongDurationMillis()// long duration. default is 4500 millisecond
//                .setRadius()// radius. default is half of view's height. coder can assgin a positive value
//                .setRelativeGravity()// whan show an toast with target, coder can assgin position relative to toastself(like relativeLayout start end center), default is BToast.RELATIVE_GRAVITY_CENTER
//                .setSameLength()// sameLength.  whan layoutGravity is BToast.LAYOUT_GRAVITY_TOP or BToast.LAYOUT_GRAVITY_BOTTOM,sameLength mean toast's width is as same as target,otherwise is same height
//                .setShortDurationMillis()// short duration. default is 3000 millisecond
//                .setShowIcon()// show or hide icon
//                .setTextSize()// text size. sp unit
  //              .apply(this);// must call




}
