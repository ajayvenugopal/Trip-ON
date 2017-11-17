package com.ajay.floow.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ajay.floow.R;
import com.ajay.floow.activity.JourneyDetails;
import com.ajay.floow.model.Journey;

import java.util.List;

/**
 * Created by Ajay on 16/11/17.
 */

public class JourneyAdapter extends RecyclerView.Adapter<JourneyAdapter.MyViewHolder> {


    private List<Journey> journeyList;
    private Activity context;

    public JourneyAdapter(List<Journey> jList, Activity c) {
        this.journeyList = jList;
        this.context = c;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            cardView = (CardView) view.findViewById(R.id.cardView);
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.journey_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(JourneyAdapter.MyViewHolder holder, int position) {
        Journey journey = journeyList.get(position);
        holder.title.setText(journey.jname);

        holder.cardView.setTag(journey.jid);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent detaill = new Intent(context, JourneyDetails.class);
                detaill.putExtra("id", view.getTag().toString());
                context.startActivity(detaill);


            }
        });
    }

    @Override
    public int getItemCount() {
        return journeyList.size();
    }
}
