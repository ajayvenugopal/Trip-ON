package com.ajay.floow.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ajay.floow.model.Journey;
import com.google.android.gms.maps.model.LatLng;


import java.util.ArrayList;
import java.util.List;


public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "journey";


    private static final String TABLE_JOURNEY = "journey";

    private static final String TABLE_LOCATION = "location";


    private static final String KEY_ID = "id";


    private static final String JOURNEY_id = "journey_id";
    private static final String JOURNEY_NAME = "journey_name";
    private static final String JOURNEY_LOCATION = "location";
    private static final String START_TIME = "start_time";
    private static final String END_TIME = "end_time";
    private static final String LAT = "lat";
    private static final String LON = "lon";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {


        String CREATE_JOURNEY_TABLE = "CREATE TABLE " + TABLE_JOURNEY + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + JOURNEY_NAME + " TEXT,"
                + START_TIME + " INTEGER,"
                + END_TIME + " INTEGER" +

                ")";

        String CREATE_LOCATOIN_TABLE = "CREATE TABLE " + TABLE_LOCATION + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + JOURNEY_id + " INTEGER,"
                + LAT + " TEXT,"
                + LON + " TEXT" +


                ")";


        db.execSQL(CREATE_JOURNEY_TABLE);
        db.execSQL(CREATE_LOCATOIN_TABLE);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JOURNEY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
        // Create tables again
        onCreate(db);
    }


    public long updateJourney(String id, Journey item) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues statement = new ContentValues();
        try {

            statement.put(END_TIME, (item.end_time));


        } catch (Exception e) {
            e.printStackTrace();
        }
        long count = database.update(TABLE_JOURNEY, statement, "id=" + id, null);

        return count;

    }


    public long insertLocatoin(String id, String lat, String log) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues statement = new ContentValues();
        try {
            statement.put(JOURNEY_id, (id));

            statement.put(LAT, (lat));

            statement.put(LON, (log));


        } catch (Exception e) {
            e.printStackTrace();
        }
        long count = database.insert(TABLE_LOCATION, null, statement);

        return count;

    }


    public long insertJourney(Journey item) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues statement = new ContentValues();
        try {
            statement.put(JOURNEY_NAME, (item.jname));

            statement.put(START_TIME, (item.start_time));


        } catch (Exception e) {
            e.printStackTrace();
        }
        long count = database.insert(TABLE_JOURNEY, null, statement);

        return count;

    }

    public List getAllJourneyLIst() {


        List<Journey> journeyList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_JOURNEY;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Journey journey = new Journey();
                try {
                    journey.setId((cursor.getString(0)));
                    journey.setName((cursor.getString(1)));
                    journey.setStart_time((cursor.getString(2)));

                    journey.setEnd_time((cursor.getString(3)));


                    journeyList.add(journey);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } while (cursor.moveToNext());
        }
        return journeyList;
    }

    public Journey getJourney(String id) {

        Journey journey = new Journey();

        String selectQuery = "SELECT  * FROM " + TABLE_JOURNEY + " WHERE id=" + id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {


            try {
                journey.setId((cursor.getString(0)));
                journey.setName((cursor.getString(1)));
                journey.setStart_time((cursor.getString(2)));

                journey.setEnd_time((cursor.getString(3)));


            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        return journey;
    }

    public ArrayList getLocations(String id) {


        ArrayList<LatLng> latLngs = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_LOCATION + " WHERE journey_id=" + id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                try {


                    LatLng latLng = new LatLng(Double.parseDouble((cursor.getString(2))), Double.parseDouble((cursor.getString(3))));
                    latLngs.add(latLng);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } while (cursor.moveToNext());
        }
        return latLngs;
    }


    public void deleteALL() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_JOURNEY, null,
                null);
        db.close();
    }

}