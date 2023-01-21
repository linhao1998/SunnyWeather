package com.example.sunnyweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class DailyResponse {

    public String status;

    public Result result;

    public class Result {
        public Daily daily;
    }

    public class Daily {

        public List<Temperature> temperature;

        public List<Skycon> skycon;

        @SerializedName("life_index")
        public LifeIndex lifeIndex;
    }

    public class Temperature {
        public float max;

        public float min;
    }

    public class Skycon {
        public String value;

        public Date date;
    }

    public class LifeIndex {
        public List<LifeDescription> coldRisk;

        public List<LifeDescription> carWashing;

        public List<LifeDescription> ultraviolet;

        public List<LifeDescription> dressing;
    }

    public class LifeDescription {
        public String desc;
    }
}
