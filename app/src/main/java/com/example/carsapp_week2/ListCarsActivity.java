package com.example.carsapp_week2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.carsapp_week2.provider.CarViewModel;

import java.util.ArrayList;

public class ListCarsActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    MyRecyclerViewAdapter adapter;

    private CarViewModel mCarViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_cars);

        toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        ArrayList<String> data = getIntent().getStringArrayListExtra("CARS_LIST");

        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MyRecyclerViewAdapter(this);
        recyclerView.setAdapter(adapter);

        mCarViewModel = new ViewModelProvider(this).get(CarViewModel.class);
        mCarViewModel.getAllCars().observe(this, newData -> {
            adapter.setData(newData);
            adapter.notifyDataSetChanged();
        });
    }

    public void goBack(View v){
        finish();
    }

}