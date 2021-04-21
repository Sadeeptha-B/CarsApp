package com.example.carsapp_week2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    ArrayList<String> data;
    private Context context;

    public MyRecyclerViewAdapter(ArrayList<String> _data, Context context){
        this.context = context;
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
        String[] carText = data.get(position).split("\\ \\|\\ ");

        for (int i=0; i < carText.length; i++){
            holder.elements.get(i).setText(carText[i]);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String msg = "Car No: " + position + " with name " + holder.elements.get(0).getText() + " and model: " + holder.elements.get(1).getText() + " is selected";
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public View itemView;
        public ArrayList<TextView> elements = new ArrayList<>();



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            elements.add(itemView.findViewById(R.id.textViewCarMaker));
            elements.add(itemView.findViewById(R.id.textViewCarModel));
            elements.add(itemView.findViewById(R.id.textViewCarYear));
            elements.add(itemView.findViewById(R.id.textViewCarColor));
            elements.add(itemView.findViewById(R.id.textViewCarSeats));
            elements.add(itemView.findViewById(R.id.textViewCarPrice));
            elements.add(itemView.findViewById(R.id.textViewCarAddress));
        }
    }
}
