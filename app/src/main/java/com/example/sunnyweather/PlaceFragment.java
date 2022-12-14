package com.example.sunnyweather;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunnyweather.gson.Place;
import com.example.sunnyweather.gson.PlaceResponse;
import com.example.sunnyweather.util.HttpUtil;
import com.example.sunnyweather.util.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PlaceFragment extends Fragment {

    private List<Place> placeList = new ArrayList<>();

    private EditText searchPlaceEdit;

    private RecyclerView recyclerView;

    private ImageView bgImageView;

    private PlaceAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place,container,false);
        searchPlaceEdit = (EditText) view.findViewById(R.id.searchPlaceEdit);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        bgImageView = (ImageView) view.findViewById(R.id.bgImageView);
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PlaceAdapter(placeList);
        recyclerView.setAdapter(adapter);
        searchPlaceEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String content = s.toString();
                if (!content.isEmpty()) {
                    searchPlaces(content);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    bgImageView.setVisibility(View.VISIBLE);
                    placeList.clear();
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void searchPlaces(String content) {
        String url = "https://api.caiyunapp.com/v2/place?query=" + content;
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(),"获取地点信息失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseText = response.body().string();
                PlaceResponse placeResponse = Utility.handlePlaceResponse(responseText);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (placeResponse != null && placeResponse.status.equals("ok")) {
                            recyclerView.setVisibility(View.VISIBLE);
                            bgImageView.setVisibility(View.GONE);
                            placeList.clear();
                            placeList.addAll(placeResponse.places);
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(),"未能查询到任何地点",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

}
