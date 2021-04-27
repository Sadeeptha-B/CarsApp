package com.example.carsapp_week2;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carsapp_week2.provider.Car;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    List<Car> data = new ArrayList<Car>();
    private Context context;

    public MyRecyclerViewAdapter(Context context){
        this.context = context;
    }

    public void setData(List<Car> _data){
        this.data = _data;
    }

    @NonNull
    @Override
    public MyRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(layout);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.textViewCarMaker.setText(data.get(position).getMaker());
        holder.textViewCarModel.setText(data.get(position).getModel());
        holder.textViewCarYear.setText(Integer.toString(data.get(position).getYear()));
        holder.textViewCarColor.setText(data.get(position).getColor());
        holder.textViewCarSeats.setText(Integer.toString(data.get(position).getSeats()));
        holder.textViewCarPrice.setText(Integer.toString(data.get(position).getPrice()));
        holder.textViewCarAddress.setText(data.get(position).getAddress());

        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
//                String msg = "Car No: " + position + " with name " + holder.textViewCarMaker.getText() + " and model: " + holder.textViewCarModel.getText() + " is selected";
//                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                 MainActivity.mCarViewModel.deleteCar(data.get(position).getModel());
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public View itemView;
        public TextView textViewCarMaker;
        public TextView textViewCarModel;
        public TextView textViewCarYear;
        public TextView textViewCarColor;
        public TextView textViewCarSeats;
        public TextView textViewCarPrice;
        public TextView textViewCarAddress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;

            textViewCarMaker = itemView.findViewById(R.id.textViewCarMaker);
            textViewCarModel = itemView.findViewById(R.id.textViewCarModel);
            textViewCarYear = itemView.findViewById(R.id.textViewCarYear);
            textViewCarColor = itemView.findViewById(R.id.textViewCarColor);
            textViewCarSeats = itemView.findViewById(R.id.textViewCarSeats);
            textViewCarPrice = itemView.findViewById(R.id.textViewCarPrice);
            textViewCarAddress = itemView.findViewById(R.id.textViewCarAddress);
        }
    }
}
