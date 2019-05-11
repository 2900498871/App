package com.sc.main;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import okhttp3.OkHttpClient;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        OkHttpClient okHttpClient= new OkHttpClient();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //申请权限
        EasyPermissions.requestPermissions(
                MainActivity.this,
                "申请权限",
                0,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION);

        String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        if (!EasyPermissions.hasPermissions(MainActivity.this, perms)) {
            ActivityCompat.requestPermissions(MainActivity.this,perms,1);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        // 创建状态栏的管理实例
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // 激活状态栏设置
        tintManager.setStatusBarTintEnabled(true);
        // 激活导航栏设置
        tintManager.setNavigationBarTintEnabled(true);
        // 设置一个颜色给系统栏
        //   tintManager.setTintColor(Color.parseColor("#1AFFFFFF"));

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
