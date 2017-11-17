package com.ajay.floow.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by ajay on 15/11/17.
 */

public class Journey {


    public String jname;
    public String jid;
    private ArrayList<LatLng> latLngsPoints;

    public String start_time;

    public String end_time;

    public void setName(String name){
        this.jname=name;
    }
    public void setId(String id){
        this.jid=id;
    }
    public void setStart_time(String time){
        this.start_time=time;
    }
    public void setEnd_time(String time){
        this.end_time=time;
    }
    public void setlocations(ArrayList name){
        this.latLngsPoints=name;
    }





}
