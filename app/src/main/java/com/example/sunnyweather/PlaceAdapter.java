package com.example.sunnyweather;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunnyweather.gson.Place;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {

    private List<Place> placeList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView placeName;
        TextView placeAddress;

        public ViewHolder(View view) {
            super(view);
            placeName = (TextView) view.findViewById(R.id.placeName);
            placeAddress = (TextView) view.findViewById(R.id.placeAddress);
        }
    }

    public PlaceAdapter(List<Place> placeList) {
        this.placeList = placeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
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
