package com.example.stonks;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Locale;

public class stockAdapter extends RecyclerView.Adapter<ViewHolder> {

    private static final String TAG = "stockAdapter";
    private List<stocks> stockList;
    private MainActivity mainAct;

    stockAdapter(List<stocks> empList, MainActivity ma) {
        this.stockList = empList;
        mainAct = ma;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: MAKING NEW");

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stock_entry, parent, false);

        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);


        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        stocks stock = stockList.get(position);

        holder.name.setText(stock.getName());
        holder.symbol.setText(stock.getSymbol());
        //holder.capital.setText(String.format("Capital: %s", country.getCapital()));
        String var = "%";
        holder.change.setText(String.format("%s (%s %s)", stock.getChange(), stock.getPercent(), var));
        holder.value.setText(String.format("%s", stock.getValue()));
        //holder.change.setText(change.getRegion());
        //holder.subRegion.setText(country.getSubRegion());

        //color change
        String x = stock.getChange();
        double y = Double.parseDouble(x);
        if(y<0){
            holder.change.setTextColor(Color.rgb(255,89,89));
            holder.symbol.setTextColor(Color.rgb(255,89,89));
            holder.name.setTextColor(Color.rgb(255,89,89));
            holder.value.setTextColor(Color.rgb(255,89,89));

        }
        else{
            holder.change.setTextColor(Color.rgb(89,255,89));
            holder.symbol.setTextColor(Color.rgb(89,255,89));
            holder.name.setTextColor(Color.rgb(89,255,89));
            holder.value.setTextColor(Color.rgb(89,255,89));
        }
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }

}