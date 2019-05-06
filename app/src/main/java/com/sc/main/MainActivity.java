package com.sc.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import okhttp3.OkHttpClient;
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        OkHttpClient okHttpClient= new OkHttpClient();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }


}
