package com.example.sunnyweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunnyweather.gson.Place;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {

    private PlaceFragment placeFragment;

    private List<Place> placeList;

    public PlaceAdapter(PlaceFragment placeFragment, List<Place> placeList) {
        this.placeFragment = placeFragment;
        this.placeList = placeList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView placeName;
        TextView placeAddress;

        public ViewHolder(View view) {
            super(view);
            placeName = (TextView) view.findViewById(R.id.placeName);
            placeAddress = (TextView) view.findViewById(R.id.placeAddress);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAbsoluteAdapterPosition();
                Place place = placeList.get(position);
                Activity activity = placeFragment.getActivity();
                if (activity instanceof MainActivity) {
                    Intent intent = new Intent(parent.getContext(),WeatherActivity.class);
                    intent.putExtra("location_lng",place.location.lng);
                    intent.putExtra("location_lat",place.location.lat);
                    intent.putExtra("place_name",place.name);
                    activity.startActivity(intent);
                    activity.finish();
                } else if (activity instanceof WeatherActivity){
                    WeatherActivity weatherActivity = (WeatherActivity) activity;
                    weatherActivity.drawerLayout.closeDrawers();
                    weatherActivity.swipeRefresh.setRefreshing(true);
                    weatherActivity.editor.putString("location_lng",place.location.lng);
                    weatherActivity.editor.putString("location_lat",place.location.lat);
                    weatherActivity.editor.putString("place_name",place.name);
                    weatherActivity.editor.apply();
                    weatherActivity.requestWeather(weatherActivity.pref.getString("location_lng",null),
                                                    weatherActivity.pref.getString("location_lat",null));
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Place place = placeList.get(position);
        holder.placeName.setText(place.name);
        holder.placeAddress.setText(place.address);
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

}
