package com.ajay.floow.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.ajay.floow.R;
import com.ajay.floow.adapter.JourneyAdapter;
import com.ajay.floow.database.DatabaseHandler;
import com.ajay.floow.model.Journey;

import java.util.ArrayList;
import java.util.List;

public class ListJourney extends AppCompatActivity {
    private RecyclerView recyclerView;
    private JourneyAdapter mAdapter;

    private List<Journey> journeyList = new ArrayList<>();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_journey);
        getSupportActionBar().setTitle("Trip History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadData();
        recyclerView=(RecyclerView)findViewById(R.id.recycler_view) ;
        mAdapter = new JourneyAdapter(journeyList,this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);



    }
/*
*
* getting all data from local db
*
* */
    void loadData(){
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());

        journeyList = db.getAllJourneyLIst();

    }
}
