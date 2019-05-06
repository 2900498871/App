package com.sc.main.beans;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.bravin.btoast.BToast;
import com.sc.SysConfig;
import com.sc.main.R;
import com.sc.util.Utils;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {
    private static final int LEVEL_PROVINCE=0;
    private static final int LEVEL_CITY=1;
    private static final int LEVE_COUNTRY=2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList=new ArrayList<String>();

    /**
     * 省份列表
     */
    private List<province> provinceList;

    /**
     * 城市列表
     */
    private List<city> cityList;

    /**
     * 县列表
     */
    private List<country> countryList;

    /**
     * 选中的省份
     */
    private province selectedProvince;

    /**
     * 选中的城市
     */
    private city selectedCity;

    /**
     * 选中的级别
     */
    private int currentLevel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.select_area,container,false);
        titleText=view.findViewById(R.id.title_text);
        backButton=view.findViewById(R.id.back_button);
        listView=view.findViewById(R.id.list_view);
        adapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel==LEVEL_PROVINCE){
                    selectedProvince=provinceList.get(position);
                    //获取城市
                    queryCitys();
                }else if(currentLevel==LEVEL_CITY){
                    selectedCity=cityList.get(position);
                   queryCountrys();
                }


            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentLevel==LEVE_COUNTRY){
                    queryCitys();
                }else if(currentLevel==LEVEL_CITY){
                   queryProvinces();
                }
            }
        });

        queryProvinces();
    }


    /**
     * 查询省份
     */
    private void queryProvinces(){
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList= DataSupport.findAll(province.class);
        //若从数据库查询到了数据
        if(provinceList.size()>0){
            dataList.clear();
            for(province province:provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_PROVINCE;
            closeProgressDialog();
        }else{//若没有查询到数据
            queryFromServer(SysConfig.PROVINCE_URL,"province");
        }
    }

    /**
     * 查询城市
     */
    private void queryCitys(){
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList= DataSupport.
                where("provinceId=?",String.valueOf(selectedProvince.getId()))
                .find(city.class);
           //若查询到数据了
            if(cityList.size()>0){
                dataList.clear();
                for(city city:cityList){
                    dataList.add(city.getCityName());
                }
                adapter.notifyDataSetChanged();
                listView.setSelection(0);
                currentLevel=LEVEL_CITY;
                closeProgressDialog();
            }else{//若没有查询到数据，到服务器上查询
                int provinceCode=selectedProvince.getProvinceCode();
                queryFromServer(SysConfig.PROVINCE_URL+"/"+provinceCode,"city");
            }
    }

    /**
     * 查询县的数据
     */
    private void queryCountrys(){
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countryList=DataSupport.where("cityId=?",String.valueOf(selectedCity.getId()))
                .find(country.class);
        if(countryList.size()>0){
            dataList.clear();
            for (country country:countryList){
                dataList.add(country.getCountryName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVE_COUNTRY;
            closeProgressDialog();
        }else{
            int provinceCode=selectedProvince.getProvinceCode();
            int cityCode=selectedCity.getCityCode();
            queryFromServer(SysConfig.PROVINCE_URL+"/"+provinceCode+"/"+cityCode,"country");
        }
    }


    private void queryFromServer(String address,final String type){
        showProgressDialog();
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        BToast.error(getContext()).text("数据加载失败").show();
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText=response.body().string();
                boolean result=false;
                if("province".equals(type)){//省份级别
                    result= Utils.handeProvinceResponse(responseText);
                }else if("city".equals(type)){
                    result=Utils.handleCityResponse(responseText,selectedProvince.getId());
                }else if("country".equals(type)){
                    result=Utils.hanleCountryResponse(responseText,selectedCity.getId());
                }
                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showProgressDialog();
                            if("province".equals(type)){
                                queryProvinces();
                            }else if("city".equals(type)){
                                queryCitys();
                            }else if("country".equals(type)){
                                queryCountrys();
                            }
                        }
                    });
                }
            }
        });

    }

    /**
     * 开启进度对话框
     */
    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("加载中...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭对话框
     */
    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }


}
