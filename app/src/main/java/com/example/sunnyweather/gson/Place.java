package com.example.sunnyweather.gson;

import com.google.gson.annotations.SerializedName;

public class Place {
    public String name;

    public Location location;

    @SerializedName("formatted_address")
    public String address;

    public class Location {
        public String lat;

        public String lng;
    }
}
