package com.example.sunnyweather.util;

import static java.util.Map.entry;

import com.example.sunnyweather.R;
import com.example.sunnyweather.Sky;
import com.example.sunnyweather.gson.DailyResponse;
import com.example.sunnyweather.gson.PlaceResponse;
import com.example.sunnyweather.gson.RealtimeResponse;
import com.google.gson.Gson;

import java.util.Map;

public class Utility {
    public static Map<String, Sky> sky = Map.ofEntries(
            entry("CLEAR_DAY",new Sky("晴", R.drawable.ic_clear_day,R.drawable.bg_clear_day)),
            entry("CLEAR_NIGHT",new Sky("晴",R.drawable.ic_clear_night,R.drawable.bg_clear_night)),
            entry("PARTLY_CLOUDY_DAY",new Sky("多云",R.drawable.ic_partly_cloud_day,R.drawable.bg_partly_cloudy_day)),
            entry("PARTLY_CLOUDY_NIGHT",new Sky("多云",R.drawable.ic_partly_cloud_night,R.drawable.bg_partly_cloudy_night)),
            entry("CLOUDY",new Sky("阴",R.drawable.ic_cloudy,R.drawable.bg_cloudy)),
            entry("WIND",new Sky("大风",R.drawable.ic_cloudy,R.drawable.bg_wind)),
            entry("LIGHT_RAIN",new Sky("小雨",R.drawable.ic_light_rain,R.drawable.bg_rain)),
            entry("MODERATE_RAIN",new Sky("中雨",R.drawable.ic_moderate_rain,R.drawable.bg_rain)),
            entry("HEAVY_RAIN",new Sky("大雨",R.drawable.ic_heavy_rain,R.drawable.bg_rain)),
            entry("STORM_RAIN",new Sky("暴雨",R.drawable.ic_storm_rain,R.drawable.bg_rain)),
            entry("THUNDER_SNOW",new Sky("雷阵雨",R.drawable.ic_thunder_shower,R.drawable.bg_rain)),
            entry("SLEET",new Sky("雨夹雪",R.drawable.ic_sleet,R.drawable.bg_rain)),
            entry("LIGHT_SNOW",new Sky("小雪",R.drawable.ic_light_snow,R.drawable.bg_snow)),
            entry("MODERATE_SNOW",new Sky("中雪",R.drawable.ic_moderate_snow,R.drawable.bg_snow)),
            entry("HEAVY_SNOW",new Sky("大雪",R.drawable.ic_heavy_snow,R.drawable.bg_snow)),
            entry("STORM_SNOW",new Sky("暴雪",R.drawable.ic_storm_rain,R.drawable.bg_snow)),
            entry("HAIL",new Sky("冰雹",R.drawable.ic_hail,R.drawable.bg_snow)),
            entry("LIGHT_HAZE",new Sky("轻度雾霾",R.drawable.ic_light_haze,R.drawable.bg_fog)),
            entry("MODERATE_HAZE",new Sky("中度雾霾",R.drawable.ic_moderate_haze,R.drawable.bg_fog)),
            entry("HEAVY_HAZE",new Sky("重度雾霾",R.drawable.ic_heavy_haze,R.drawable.bg_fog)),
            entry("FOG",new Sky("雾",R.drawable.ic_fog,R.drawable.bg_fog)),
            entry("DUST",new Sky("浮尘",R.drawable.ic_fog,R.drawable.bg_fog)));


    public static PlaceResponse handlePlaceResponse (String response) {
        return new Gson().fromJson(response,PlaceResponse.class);
    }

    public static RealtimeResponse handleRealtimeResponse (String response) {
        return new Gson().fromJson(response,RealtimeResponse.class);
    }

    public static DailyResponse handleDailyResponse (String response) {
        return new Gson().fromJson(response,DailyResponse.class);
    }

    public static Sky getSky(String skycon){
        return sky.get(skycon);
    }
}
