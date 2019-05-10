package com.sc.main;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bravin.btoast.BToast;
import com.bumptech.glide.Glide;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.sc.SysConfig;
import com.sc.main.weatherBeans.forecast;
import com.sc.main.weatherBeans.weather;
import com.sc.util.Utils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    private String pic;

    private String picNow;

    /**
     * 服务器获取数据是自动加载
     */
    private weather weath;

    public RefreshLayout mRefreshLayout;

    public  String weatherId="";

    public DrawerLayout drawerLayout;
    public Button navButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout) ;
        navButton=(Button)findViewById(R.id.nav_button);

        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


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

        //初始化
        mRefreshLayout=findViewById(R.id.refreshLayout);

        //刷新
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                requestWeather(weatherId);
                refreshlayout.finishRefresh(true);
            }
        });
        //加载更多
       /* mRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                for(int i=0;i<30;i++){
                    mData.add("小明"+i);
                }
                mNameAdapter.notifyDataSetChanged();
                refreshlayout.finishLoadmore();
            }
        });*/


        //拿出缓存中的数据
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherStr = preferences.getString("weather",null);
        pic=preferences.getString("pic",null);
        if("".equals(pic)||pic==null){
             pic=loadImg();
            SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
            editor.putString("pic",pic);
            editor.apply();
            Glide.with(WeatherActivity.this).load(pic).into(backgroundImg);
        }else{
            String image=loadImg();
            if(!image.equals("")&&!image.equals(pic)){
                SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("pic",image);
                editor.apply();
                pic=image;
                Glide.with(WeatherActivity.this).load(image).into(backgroundImg);
            }else{
                Glide.with(WeatherActivity.this).load(pic).into(backgroundImg);
            }

        }

        if(weatherStr!=null){
            weather weather= Utils.handleWeatherResponse(weatherStr);
            weatherId=weather.basic.weatherId;
           showWeatherInfo(weather);
        }else{//从服务器中查询天气
            //若没有数据把控件隐藏起来
            weatherId=getIntent().getStringExtra("weatherId");
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

        if("".equals(pic)||pic==null){
            String weatherStatus=weather.now.cond_code;//天气状态  100 ：晴  ；100-104 多云   ；104-213  风 ； 213-399   雨  ；399-499 雪  499-999  雾
            int code=Integer.parseInt(weatherStatus);
            Random random= new Random();
            if(code==100){//晴天
                List sunnyList =this.getList("sunny");
                int num=random.nextInt(20);
                Glide.with(this).load(sunnyList.get(num)).into(backgroundImg);
            }else if(100<code&&code<=104){
                List sunnyList =this.getList("cloud");
                int num=random.nextInt(10);
                Glide.with(this).load(sunnyList.get(num)).into(backgroundImg);
            }else if(104<code&&code<=213){
                List sunnyList =this.getList("windy");
                int num=random.nextInt(10);
                Glide.with(this).load(sunnyList.get(num)).into(backgroundImg);
            }else if(213<code&&code<=399){
                List sunnyList =this.getList("rain");
                int num=random.nextInt(20);
                Glide.with(this).load(sunnyList.get(num)).into(backgroundImg);
            }else if(399<code&&code<=499){
                List sunnyList =this.getList("snow");
                int num=random.nextInt(10);
                Glide.with(this).load(sunnyList.get(num)).into(backgroundImg);
            }else if(499<code&&code<=999){
                List sunnyList =this.getList("wu");
                int num=random.nextInt(10);
                Glide.with(this).load(sunnyList.get(num)).into(backgroundImg);
            }
        }

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
        picNow="";
        OkHttpClient client= new OkHttpClient();
        Request request=new Request.Builder().url(SysConfig.WEATHER_IMG).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BToast.error(WeatherActivity.this).text("数据加载失败").show();
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                picNow=response.body().string();
            }
        });
    return picNow;
    }
    /**
     * 全透状态栏
     */
    protected void setStatusBarFullTransparent() {
        if (Build.VERSION.SDK_INT >= 21) {//21表示5.0
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= 19) {//19表示4.4
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //虚拟键盘也透明
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

    }

    /**
     * 如果需要内容紧贴着StatusBar
     * 应该在对应的xml布局文件中，设置根布局fitsSystemWindows=true。
     */
    private View contentViewGroup;

    protected void setFitSystemWindow(boolean fitSystemWindow) {
        if (contentViewGroup == null) {
            contentViewGroup = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        }
        contentViewGroup.setFitsSystemWindows(fitSystemWindow);
    }

    public List getList(String type){
        List sunnyList = new ArrayList();
        if("sunny".equals(type)){//存放晴天图片
            sunnyList.add(R.drawable.rain1);
            sunnyList.add(R.drawable.rain2);
            sunnyList.add(R.drawable.rain3);
            sunnyList.add(R.drawable.rain4);
            sunnyList.add(R.drawable.rain5);
            sunnyList.add(R.drawable.rain6);
            sunnyList.add(R.drawable.rain7);
            sunnyList.add(R.drawable.rain8);
            sunnyList.add(R.drawable.rain9);
            sunnyList.add(R.drawable.rain10);
            sunnyList.add(R.drawable.rain11);
            sunnyList.add(R.drawable.rain12);
            sunnyList.add(R.drawable.rain13);
            sunnyList.add(R.drawable.rain14);
            sunnyList.add(R.drawable.rain15);
            sunnyList.add(R.drawable.rain16);
            sunnyList.add(R.drawable.rain17);
            sunnyList.add(R.drawable.rain18);
            sunnyList.add(R.drawable.rain19);
            sunnyList.add(R.drawable.rain20);
        }else if("cloud".equals(type)){
            sunnyList.add(R.drawable.cloud1);
            sunnyList.add(R.drawable.cloud2);
            sunnyList.add(R.drawable.cloud3);
            sunnyList.add(R.drawable.cloud4);
            sunnyList.add(R.drawable.cloud5);
            sunnyList.add(R.drawable.cloud6);
            sunnyList.add(R.drawable.cloud7);
            sunnyList.add(R.drawable.cloud8);
            sunnyList.add(R.drawable.cloud9);
            sunnyList.add(R.drawable.cloud10);
        }else if("windy".equals(type)){
            sunnyList.add(R.drawable.windy1);
            sunnyList.add(R.drawable.windy2);
            sunnyList.add(R.drawable.windy3);
            sunnyList.add(R.drawable.windy4);
            sunnyList.add(R.drawable.windy5);
            sunnyList.add(R.drawable.windy6);
            sunnyList.add(R.drawable.windy7);
            sunnyList.add(R.drawable.windy8);
            sunnyList.add(R.drawable.windy9);
            sunnyList.add(R.drawable.windy10);
        }else if("rain".equals(type)){
            sunnyList.add(R.drawable.rain1);
            sunnyList.add(R.drawable.rain2);
            sunnyList.add(R.drawable.rain3);
            sunnyList.add(R.drawable.rain4);
            sunnyList.add(R.drawable.rain5);
            sunnyList.add(R.drawable.rain6);
            sunnyList.add(R.drawable.rain7);
            sunnyList.add(R.drawable.rain8);
            sunnyList.add(R.drawable.rain9);
            sunnyList.add(R.drawable.rain10);
            sunnyList.add(R.drawable.rain11);
            sunnyList.add(R.drawable.rain12);
            sunnyList.add(R.drawable.rain13);
            sunnyList.add(R.drawable.rain14);
            sunnyList.add(R.drawable.rain15);
            sunnyList.add(R.drawable.rain16);
            sunnyList.add(R.drawable.rain17);
            sunnyList.add(R.drawable.rain18);
            sunnyList.add(R.drawable.rain19);
            sunnyList.add(R.drawable.rain20);
        }else if("wu".equals(type)){
            sunnyList.add(R.drawable.wu1);
            sunnyList.add(R.drawable.wu2);
            sunnyList.add(R.drawable.wu3);
            sunnyList.add(R.drawable.wu4);
            sunnyList.add(R.drawable.wu5);
            sunnyList.add(R.drawable.wu6);
            sunnyList.add(R.drawable.wu7);
            sunnyList.add(R.drawable.wu8);
            sunnyList.add(R.drawable.wu9);
            sunnyList.add(R.drawable.wu10);
        }else if("snow".equals(type)){
            sunnyList.add(R.drawable.snowy1);
            sunnyList.add(R.drawable.snowy2);
            sunnyList.add(R.drawable.snowy3);
            sunnyList.add(R.drawable.snowy4);
            sunnyList.add(R.drawable.snowy5);
            sunnyList.add(R.drawable.snowy6);
            sunnyList.add(R.drawable.snowy7);
            sunnyList.add(R.drawable.snowy8);
            sunnyList.add(R.drawable.snowy9);
            sunnyList.add(R.drawable.snowy10);
        }
        return sunnyList;

    }


}
