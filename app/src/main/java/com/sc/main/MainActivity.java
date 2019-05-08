package com.sc.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import okhttp3.OkHttpClient;
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        OkHttpClient okHttpClient= new OkHttpClient();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //取出缓存得数据，
        SharedPreferences preferences= PreferenceManager.
                getDefaultSharedPreferences(this);
        if(preferences.getString("weather",null)!=null){
            Intent intent = new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
        }


    }


}
