package com.example.sunnyweather.util;

import com.example.sunnyweather.gson.PlaceResponse;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class Utility {

    public static PlaceResponse handlePlaceResponse (String response) {
        return new Gson().fromJson(response,PlaceResponse.class);
    }
}
