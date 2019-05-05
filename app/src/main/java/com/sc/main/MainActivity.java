package com.sc.main;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.bravin.btoast.BToast;
import com.google.gson.Gson;
import com.sc.SysConfig;
import com.sc.main.beans.province;
import com.sc.util.Utils;

import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    Message message= null;
    private   Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==SysConfig.ERROR){
                BToast.error(MainActivity.this).text("请检查网络连接").show();
            }else if(msg.what==SysConfig.INITERROR){
                BToast.error(MainActivity.this).text("初始化城市列表失败").show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button= findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BToast.success(v.getContext())
                        .text("this is text")
                        .show();
            }
        });

        OkHttpClient okHttpClient= new OkHttpClient();
        getProvinces(okHttpClient);
    }


    public void getProvinces(OkHttpClient client) {
        Gson gson = null;
     /*   FormBody body = new FormBody.Builder()
                .add("name","aaa")
                .build();*/
        Request request = new Request.Builder().url(SysConfig.PROVINCE_URL).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                message= new Message();
                message.what=SysConfig.ERROR;
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
               List<province> newsList = DataSupport.findAll(province.class);
               if(newsList==null||newsList.size()==0){
                   String str=response.body().string();
                   boolean bool=  Utils.handeProvinceResponse(str);
                   if(bool==false){
                       message= new Message();
                       message.what=SysConfig.INITERROR;
                       handler.sendMessage(message);
                   }
               }
            }
        });

    }


}
