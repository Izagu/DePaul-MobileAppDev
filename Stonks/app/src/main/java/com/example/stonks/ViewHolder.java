package com.example.stonks;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

class ViewHolder extends RecyclerView.ViewHolder {

    TextView name;
    TextView symbol;
    TextView value;
    TextView change;
    //TextView region;
    //TextView subRegion;
//change numbers front int to string*******
    ViewHolder(View view) {
        super(view);
        name = view.findViewById(R.id.name);
        symbol = view.findViewById(R.id.symbol);
        change = view.findViewById(R.id.change);
        value = view.findViewById(R.id.value);
      //  region = view.findViewById(R.id.region);
        //subRegion = view.findViewById(R.id.subRegion);
    }

}
