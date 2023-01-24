package com.example.sunnyweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.sunnyweather.WeatherActivity;
import com.example.sunnyweather.gson.DailyResponse;
import com.example.sunnyweather.gson.RealtimeResponse;
import com.example.sunnyweather.util.HttpUtil;
import com.example.sunnyweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 8 * 60 * 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = null;
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pi = PendingIntent.getService(this,0,i,PendingIntent.FLAG_MUTABLE);
        } else {
            pi = PendingIntent.getService(this,0,i,PendingIntent.FLAG_ONE_SHOT);
        }
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather() {
        SharedPreferences prefs  = PreferenceManager.getDefaultSharedPreferences(this);
        String lng = prefs.getString("location_lng",null);
        String lat = prefs.getString("location_lat",null);
        if (lng != null && lat != null) {
            String realtimeUrl = "https://api.caiyunapp.com/v2.5/qNTT9ci6BtlUuOiz/" + lng + "," + lat + "/realtime.json";
            String dailyUrl = "https://api.caiyunapp.com/v2.5/qNTT9ci6BtlUuOiz/" + lng + "," + lat + "/daily.json";
            HttpUtil.sendOkHttpRequest(realtimeUrl, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String responseText = response.body().string();
                    RealtimeResponse realtimeResponse = Utility.handleRealtimeResponse(responseText);
                    if (realtimeResponse != null && "ok".equals(realtimeResponse.status)) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("realtimeResponse",responseText);
                        editor.apply();
                    }
                }
            });
            HttpUtil.sendOkHttpRequest(dailyUrl, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String responseText = response.body().string();
                    DailyResponse dailyResponse = Utility.handleDailyResponse(responseText);
                    if (dailyResponse != null && "ok".equals(dailyResponse.status)) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("dailyResponse",responseText);
                        editor.apply();
                    }
                }
            });
        }
    }
}