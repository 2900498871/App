package com.sc.main;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bravin.btoast.BToast;
import com.bumptech.glide.Glide;
import com.sc.SysConfig;
import com.sc.main.weatherBeans.forecast;
import com.sc.main.weatherBeans.weather;
import com.sc.util.Utils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView scrollView;

    private TextView titleCity;

    private TextView titleUptime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private TextView aqiText;

    private TextView pm25Text;

    private TextView comfortText;

    private TextView carWashText;

    private TextView sportText;

    private ImageView backgroundImg;

    /**
     * 服务器获取数据是自动加载
     */
    private weather weath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        //初始化各种控件
        scrollView=findViewById(R.id.weather_layout);
        titleCity=findViewById(R.id.title_city);
        titleUptime=findViewById(R.id.title_update_time);
        degreeText=findViewById(R.id.degree_text);
        weatherInfoText=findViewById(R.id.weather_info_text);
        forecastLayout=findViewById(R.id.forecast_layout);
        aqiText=findViewById(R.id.aqi_text);
        pm25Text=findViewById(R.id.pm25_text);
        comfortText=findViewById(R.id.comfort_text);
        carWashText=findViewById(R.id.car_wash_text);
        sportText=findViewById(R.id.sport_text);
        backgroundImg=findViewById(R.id.bing_pic_img);

        //拿出缓存中的数据
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherStr = preferences.getString("weather",null);
        String pic=preferences.getString("pic",null);
        if(pic==null){
            String image=loadImg();
            SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
            editor.putString("pic",image);
            editor.apply();
            Glide.with(this).load(image).into(backgroundImg);
        }else{
            String image=loadImg();
            if(!image.equals("")&&!image.equals(pic)){
                SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("pic",image);
                editor.apply();
                Drawable drawable=Drawable.createFromPath(image);
               // backgroundImg.setBackground(drawable);
               // getWindow().getDecorView().setBackgroundDrawable(drawable);
                Glide.with(this).load(image).into(backgroundImg);
            }else{
                Drawable drawable=Drawable.createFromPath(pic);
              //  backgroundImg.setBackground(drawable);
              //  getWindow().getDecorView().setBackgroundDrawable(drawable);
                Glide.with(this).load(pic).into(backgroundImg);
            }

        }

        if(weatherStr!=null){
            weather weather= Utils.handleWeatherResponse(weatherStr);
           showWeatherInfo(weather);
        }else{//从服务器中查询天气
            String weatherId=getIntent().getStringExtra("weatherId");
            //若没有数据把控件隐藏起来
            scrollView.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
    }

    /**
     * 请求天气数据
     * @param weatherId
     */
    public void requestWeather(String weatherId){
        OkHttpClient client= new OkHttpClient();
        Request request= new Request.Builder().url(SysConfig.WEATHRE_URL+weatherId).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                BToast.error(WeatherActivity.this).text("无法连接服务器,请检查网络！").show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseStr=response.body().string();
                weather weather= Utils.handleWeatherResponse(responseStr);
                if(weather!=null&&"ok".equals(weather.status)){
                    SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                    editor.putString("weather",responseStr);
                    editor.apply();
                    //显示天气
                    Message message=new Message();
                    message.what=200;
                    weath=weather;
                    handler.sendMessage(message);

                }else{
                    BToast.error(WeatherActivity.this).text("数据加载失败").show();
                }
            }
        });
    }

    /**
     * 显示天气数据（页面赋值）
     * @param weather
     */
    public  void showWeatherInfo(weather weather){
        String cityName=weather.basic.cityName;
        String uptime=weather.basic.update.updateTime;
        titleCity.setText(cityName);
        titleUptime.setText(uptime);

        String degree=weather.now.tmp+"℃";
        String weatherInfoStr=weather.now.cond.txt;
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfoStr);

        //移除所有得内容(重新赋值)
        forecastLayout.removeAllViews();
        for(forecast forecast : weather.forecastList){
            View view= LayoutInflater.from(this).
                    inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText=view.findViewById(R.id.date_text);
            TextView infoText=view.findViewById(R.id.info_text);
            TextView maxText=view.findViewById(R.id.max_text);
            TextView minText=view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.cond.txt_d);
            maxText.setText(forecast.tmp.max);
            minText.setText(forecast.tmp.min);
            forecastLayout.addView(view);
        }

           if(weather.aqi!=null){
               String aqistr=weather.aqi.aqicity.aqi;
               String pm25Str=weather.aqi.aqicity.pm25;
                aqiText.setText(aqistr);
                pm25Text.setText(pm25Str);
            }

            String comfort="舒适度："+weather.suggestions.comf.txt;
            String carWash="洗车指数："+weather.suggestions.cw.txt;
            String sport="运动建议："+weather.suggestions.sport.txt;
            comfortText.setText(comfort);
            carWashText.setText(carWash);
            sportText.setText(sport);
           // 把有数据得页面显示出来
            scrollView.setVisibility(View.VISIBLE);

    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
           if(msg.what==200){//如果成功异步执行代码
               showWeatherInfo(weath);
           }
        }
    };

    public String loadImg(){
        final String[] str = {""};
        OkHttpClient client= new OkHttpClient();
        Request request=new Request.Builder().url(SysConfig.WEATHER_IMG).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                BToast.error(WeatherActivity.this).text("数据加载失败,请检查网络是否连接").show();

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String pic=response.body().string();
                str[0] =pic;
            }
        });
    return str[0];
    }


}
