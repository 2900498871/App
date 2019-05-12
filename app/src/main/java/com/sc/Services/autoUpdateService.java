package com.sc.Services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.sc.SysConfig;
import com.sc.main.weatherBeans.weather;
import com.sc.util.Utils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class autoUpdateService extends Service {
    public autoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
      return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String weatherStr=preferences.getString("weather",null);
        if(weatherStr!=null&&!"".equals(weatherStr)){
          weather weather=  Utils.handleWeatherResponse(weatherStr);
            upDateWeather(weather.basic.weatherId);//更新天气

        }
        AlarmManager manager=(AlarmManager) getSystemService(ALARM_SERVICE);
        int hour=60*60*4*1000;
        Long targetTime=SystemClock.elapsedRealtimeNanos()+hour;
        Intent intent1=new Intent(this,autoUpdateService.class);
        PendingIntent pendingIntent=PendingIntent.getService(this,0,intent1,0);
        manager.cancel(pendingIntent);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,targetTime,pendingIntent);
        return super.onStartCommand(intent,flags,startId);
    }

    /**
     * 更新天气的代码
     * @param WeatherId
     */
    public void upDateWeather(String WeatherId){
        OkHttpClient client= new OkHttpClient();
        Request request=new Request.Builder().url(SysConfig.WEATHRE_URL+WeatherId).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String weatherStr=response.body().string();
                SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                editor.putString("weather",weatherStr);
                editor.apply();
            }
        });
    }

}
