package com.example.sunnyweather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunnyweather.gson.DailyResponse;
import com.example.sunnyweather.gson.RealtimeResponse;
import com.example.sunnyweather.util.HttpUtil;
import com.example.sunnyweather.util.Utility;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;

    private RelativeLayout nowLayout;

    private TextView placeName;

    private TextView currentTemp;

    private TextView currentSky;

    private TextView currentAQI;

    private LinearLayout forecastLayout;

    private TextView coldRiskText;

    private TextView dressingText;

    private TextView ultravioletText;

    private TextView carWashingText;

    public SharedPreferences pref;

    public SharedPreferences.Editor editor;

    public SwipeRefreshLayout swipeRefresh;

    public DrawerLayout drawerLayout;

    private Button navButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_weather);

        weatherLayout = (ScrollView) findViewById(R.id.weatherLayout);
        nowLayout = (RelativeLayout) findViewById(R.id.nowLayout);
        placeName = (TextView) findViewById(R.id.placeName);
        currentTemp = (TextView) findViewById(R.id.currentTemp);
        currentSky = (TextView) findViewById(R.id.currentSky);
        currentAQI = (TextView) findViewById(R.id.currentAQI);
        forecastLayout = (LinearLayout) findViewById(R.id.forecastLayout);
        coldRiskText = (TextView) findViewById(R.id.coldRiskText);
        dressingText = (TextView) findViewById(R.id.dressingText);
        ultravioletText = (TextView) findViewById(R.id.ultravioletText);
        carWashingText = (TextView) findViewById(R.id.carWashingText);

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefresh.setColorSchemeResources(R.color.purple_500);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navButton = (Button) findViewById(R.id.navBtn);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = pref.edit();
        String realtimeResponseString = pref.getString("realtimeResponse",null);
        String dailyResponseString = pref.getString("dailyResponse",null);
        if (realtimeResponseString != null && dailyResponseString != null){
            RealtimeResponse realtimeResponse = Utility.handleRealtimeResponse(realtimeResponseString);
            DailyResponse dailyResponse = Utility.handleDailyResponse(dailyResponseString);
            showRealtimeInfo(realtimeResponse);
            showDailyInfo(dailyResponse);
        } else {
            String locationLngStr = getIntent().getStringExtra("location_lng");
            String locationLatStr = getIntent().getStringExtra("location_lat");
            editor.putString("place_name",getIntent().getStringExtra("place_name"));
            editor.putString("location_lng",locationLngStr);
            editor.putString("location_lat",locationLatStr);
            editor.apply();
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(locationLngStr,locationLatStr);
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(pref.getString("location_lng",null),pref.getString("location_lat",null));
            }
        });
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {}

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {}

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(drawerView.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
            }

            @Override
            public void onDrawerStateChanged(int newState) {}
        });
    }

    /**
     * 根据城市经纬度请求天气信息
     */
    public void requestWeather(String Lng, String Lat) {
        String realtimeUrl = "https://api.caiyunapp.com/v2.5/qNTT9ci6BtlUuOiz/" + Lng + "," + Lat + "/realtime.json";
        String dailyUrl = "https://api.caiyunapp.com/v2.5/qNTT9ci6BtlUuOiz/" + Lng + "," + Lat + "/daily.json";
        HttpUtil.sendOkHttpRequest(realtimeUrl, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取实时天气信息失败",Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseText = response.body().string();
                RealtimeResponse realtimeResponse = Utility.handleRealtimeResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (realtimeResponse != null && "ok".equals(realtimeResponse.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("realtimeResponse",responseText);
                            editor.apply();
                            showRealtimeInfo(realtimeResponse);
                        } else {
                            Toast.makeText(WeatherActivity.this,"获取实时天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        HttpUtil.sendOkHttpRequest(dailyUrl, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取未来天气信息失败",Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseText = response.body().string();
                DailyResponse dailyResponse = Utility.handleDailyResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dailyResponse != null && "ok".equals(dailyResponse.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("dailyResponse",responseText);
                            editor.apply();
                            showDailyInfo(dailyResponse);
                        } else {
                            Toast.makeText(WeatherActivity.this,"获取未来天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
    }

    private void showRealtimeInfo(RealtimeResponse realtimeResponse) {
        String placeNameText = pref.getString("place_name",null);
        RealtimeResponse.Realtime realtime = realtimeResponse.result.realtime;
        String currentTempText = (int)realtime.temperature + " ℃";
        String currentSkyText = Utility.getSky(realtime.skycon).getInfo();
        String currentAQIText = "空气指数 " + (int)realtime.airQuality.aqi.chn;
        placeName.setText(placeNameText);
        currentTemp.setText(currentTempText);
        currentSky.setText(currentSkyText);
        currentAQI.setText(currentAQIText);
        nowLayout.setBackgroundResource(Utility.getSky(realtime.skycon).getBg());
        weatherLayout.setVisibility(View.VISIBLE);
    }

    private void showDailyInfo(DailyResponse dailyResponse){
        DailyResponse.Daily daily = dailyResponse.result.daily;
        // 填充forecast.xml布局中的数据
        forecastLayout.removeAllViews();
        int days = daily.skycon.size();
        for (int i = 0; i < days; i++) {
            DailyResponse.Skycon skycon = daily.skycon.get(i);
            DailyResponse.Temperature temperature = daily.temperature.get(i);
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateInfo = (TextView) view.findViewById(R.id.dateInfo);
            ImageView skyIcon = (ImageView) view.findViewById(R.id.skyIcon);
            TextView skyInfo = (TextView) view.findViewById(R.id.skyInfo);
            TextView temperatureInfo = (TextView) view.findViewById(R.id.temperatureInfo);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            dateInfo.setText(simpleDateFormat.format(skycon.date));
            skyIcon.setImageResource(Utility.getSky(skycon.value).getIcon());
            skyInfo.setText(Utility.getSky(skycon.value).getInfo());
            String tempText = (int)temperature.min + " ~ " + (int)temperature.max + " ℃";
            temperatureInfo.setText(tempText);
            forecastLayout.addView(view);
        }
        // 填充life_index.xml布局中的数据
        DailyResponse.LifeIndex lifeIndex = daily.lifeIndex;
        coldRiskText.setText(lifeIndex.coldRisk.get(0).desc);
        carWashingText.setText(lifeIndex.carWashing.get(0).desc);
        ultravioletText.setText(lifeIndex.ultraviolet.get(0).desc);
        dressingText.setText(lifeIndex.dressing.get(0).desc);
    }
}