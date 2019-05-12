package com.sc.main;

import android.Manifest;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bravin.btoast.BToast;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.sc.SysConfig;
import com.sc.main.beans.location;
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
import pub.devrel.easypermissions.EasyPermissions;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView scrollView;

    private TextView titleCity;


    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private TextView aqiText;

    private TextView pm25Text;

    private TextView comfortText;

    private TextView carWashText;

    private TextView sportText;

    private ImageView backgroundImg;

    private Button locationButton;

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

    /**
     * 实现定位
     */
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            location location = new location();
            location.setLatitude(aMapLocation.getLatitude());
            location.setLongitude(aMapLocation.getLongitude());
            location.setAddress(aMapLocation.getAddress());
            location.setCountry(aMapLocation.getCountry());
            location.setCity(aMapLocation.getCity());
            location.setDistrict(aMapLocation.getDistrict());
            location.setStreet(aMapLocation.getStreet());
            location.setStreetNum(aMapLocation.getStreetNum());
            location.setCityCode(aMapLocation.getCityCode());
            location.setAdCode(aMapLocation.getAdCode());
            location.setPoiName(aMapLocation.getPoiName());
            location.setAoiName(aMapLocation.getAoiName());
            location.setErrorCode(aMapLocation.getErrorCode());
            Gson gson = new Gson();
            String json = gson.toJson(location);
            getWeather(location.getLongitude()+","+location.getLatitude());
        }
    };
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    //定位的城市id
    public String cityId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        //初始化各种控件
        scrollView=findViewById(R.id.weather_layout);
        titleCity=findViewById(R.id.title_city);
        //  titleUptime=findViewById(R.id.title_update_time);
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
        //定位代码
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout) ;
        navButton=(Button)findViewById(R.id.nav_button);
        locationButton=findViewById(R.id.location_button);

        //申请权限
        EasyPermissions.requestPermissions(
                WeatherActivity.this,
                "申请权限",
                0,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION);

        String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EasyPermissions.hasPermissions(WeatherActivity.this, perms)) {
                    // 已经申请过权限，做想做的事
                    getPositioning();
                } else {
                    ActivityCompat.requestPermissions(WeatherActivity.this,perms,1);
                }
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

        //刷新
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                requestWeather(weath.basic.weatherId);
                refreshlayout.finishRefresh(true);
            }
        });

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
        if(weatherStr==null||"".equals(weatherStr)){//初次进入程序(定位)
                scrollView.setVisibility(View.INVISIBLE);
                requestWeather("CN101010100");
        }else{//获取缓存数据
            //若没有数据把控件隐藏起来
            weather we= Utils.handleWeatherResponse(weatherStr);
            weath=we;
            scrollView.setVisibility(View.INVISIBLE);
            showWeatherInfo(we);
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
                    weath=weather;
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
                List sunnyList =this.getList();
                int num=random.nextInt(sunnyList.size());
                Glide.with(this).load(sunnyList.get(num)).into(backgroundImg);
        }

        String cityName=weather.basic.cityName;
        String uptime=weather.basic.update.updateTime;
        titleCity.setText(cityName);
      //  titleUptime.setText(uptime);

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
           }else if(msg.what==100){
               requestWeather(weatherId);
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

    public List getList(){
        List sunnyList = new ArrayList();

            sunnyList.add(R.drawable.pic1);
            sunnyList.add(R.drawable.pic2);
            sunnyList.add(R.drawable.pic3);
            sunnyList.add(R.drawable.pic4);
            sunnyList.add(R.drawable.pic5);
            sunnyList.add(R.drawable.pic6);
            sunnyList.add(R.drawable.pic7);
            sunnyList.add(R.drawable.pic8);
            sunnyList.add(R.drawable.pic9);
            sunnyList.add(R.drawable.pic10);
            sunnyList.add(R.drawable.pic11);
            sunnyList.add(R.drawable.pic12);
            sunnyList.add(R.drawable.pic13);
            sunnyList.add(R.drawable.pic14);
            sunnyList.add(R.drawable.pic15);
            sunnyList.add(R.drawable.pic16);
            sunnyList.add(R.drawable.pic17);
            sunnyList.add(R.drawable.pic18);
            sunnyList.add(R.drawable.pic19);
            sunnyList.add(R.drawable.pic20);
            sunnyList.add(R.drawable.pic21);
            sunnyList.add(R.drawable.pic22);
            sunnyList.add(R.drawable.pic23);
            sunnyList.add(R.drawable.pic24);
            sunnyList.add(R.drawable.pic25);
            sunnyList.add(R.drawable.pic26);
            sunnyList.add(R.drawable.pic27);
            sunnyList.add(R.drawable.pic28);
            sunnyList.add(R.drawable.pic29);
            sunnyList.add(R.drawable.pic30);
            sunnyList.add(R.drawable.pic31);
            sunnyList.add(R.drawable.pic32);
            sunnyList.add(R.drawable.pic33);
            sunnyList.add(R.drawable.pic34);
            sunnyList.add(R.drawable.pic35);
            sunnyList.add(R.drawable.pic36);
            sunnyList.add(R.drawable.pic37);
            sunnyList.add(R.drawable.pic38);
            sunnyList.add(R.drawable.pic39);
            sunnyList.add(R.drawable.pic40);
            sunnyList.add(R.drawable.pic41);
            sunnyList.add(R.drawable.pic42);
            sunnyList.add(R.drawable.pic43);
            sunnyList.add(R.drawable.pic44);
            sunnyList.add(R.drawable.pic45);
            sunnyList.add(R.drawable.pic46);
            sunnyList.add(R.drawable.pic47);
            sunnyList.add(R.drawable.pic48);
            sunnyList.add(R.drawable.pic49);
            sunnyList.add(R.drawable.pic50);
        return sunnyList;

    }

    // 高德定位,获取地理位置
    public void getPositioning() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);
        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否允许模拟位置,默认为true，允许模拟位置
        mLocationOption.setMockEnable(true);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
       // mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //启动定位
        mLocationClient.startLocation();
    }

/**
 * 实况天气
 * 实况天气即为当前时间点的天气状况以及温湿风压等气象指数，具体包含的数据：体感温度、
 * 实测温度、天气状况、风力、风速、风向、相对湿度、大气压强、降水量、能见度等。
 *
 */
public void getWeather(String location){
       OkHttpClient client=new OkHttpClient();
       Request request=new Request.Builder().url(SysConfig.WEATHER_NEW_URL+location).build();
       client.newCall(request).enqueue(new Callback() {
           @Override
           public void onFailure(Call call, IOException e) {
               BToast.error(WeatherActivity.this).text("无法连接服务器,请检查网络！").show();
           }

           @Override
           public void onResponse(Call call, Response response) throws IOException {
               String responseStr=response.body().string();
               weather weathe= Utils.handleRealWeatherResponse(responseStr);
               String WeatherId=weathe.basic.weatherId;
               String st=WeatherId.substring(0,WeatherId.length()-1)+"1";
               weatherId=st;
               Message msg=new Message();
               msg.what=100;
              handler.sendMessage(msg);
           }
       });
}

}
