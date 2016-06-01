package com.aleiacampo.oristanobus.util;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ale on 01/11/2015.
 */


public class SQLiteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "oristano_db";
    private static final String TABLE_FAVOURITES = "favourites";
    private static final String TABLE_TIMES = "times";

    private static final String ID = "id";
    private static final String ID_STOP = "id_stop";
    private static final String ID_LINE = "id_line";
    private static final String STOP_NAME = "stop_name";
    private static final String LINE_NAME = "line_name";
    private static final String STOP_TIMES = "times";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableFavourites = "CREATE TABLE " + TABLE_FAVOURITES + "(" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                ID_STOP + " INTEGER NOT NULL," +
                ID_LINE + " INTEGER NOT NULL," +
                STOP_NAME + " VARCHAR NOT NULL," +
                LINE_NAME + " VARCHAR NOT NULL" + ")";
        db.execSQL(createTableFavourites);

        String createTableTimes = "CREATE TABLE " + TABLE_TIMES + "(" +
                ID_STOP + " INTEGER PRIMARY KEY NOT NULL," +
                STOP_TIMES + " TEXT NOT NULL" + ")";
        db.execSQL(createTableTimes);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITES);
        onCreate(db);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIMES);
        onCreate(db);
    }

    public void addStop(Stop stop) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID_STOP, stop.idStop);
        values.put(ID_LINE, stop.idLine);
        values.put(STOP_NAME, stop.nameStop);
        values.put(LINE_NAME, stop.nameLine);
        db.insert(TABLE_FAVOURITES, null, values);
        db.close();
    }

    public void addTimes(int idStop, String times) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID_STOP, idStop);
        values.put(STOP_TIMES, times);
        db.insert(TABLE_TIMES, null, values);
        db.close();
    }


    public Stop getStop(int id) {
        Stop stop = new Stop();
        String selectQuery = "SELECT  * FROM " + TABLE_FAVOURITES + "WHERE id = " + id + "ORDER BY id ASC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        stop.id = Integer.parseInt(cursor.getString(0));
        stop.idStop = Integer.parseInt(cursor.getString(1));
        stop.idLine = Integer.parseInt(cursor.getString(2));
        stop.nameStop = cursor.getString(3);
        stop.nameLine = cursor.getString(4);

        return stop;
    }

    public String getTimes(int idStop) {
        String times;
        String selectQuery = "SELECT "+STOP_TIMES+" FROM " +TABLE_TIMES+ " WHERE "+ID_STOP+" = " +idStop;
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            Cursor cursor = db.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            times = cursor.getString(0);
            return times;
        }catch(Exception e){
            return null;
        }
    }



    public ArrayList<Stop> getAllStops() {
        ArrayList<Stop> stopList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_FAVOURITES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Stop stop = new Stop();
                stop.id = Integer.parseInt(cursor.getString(0));
                stop.idStop = Integer.parseInt(cursor.getString(1));
                stop.idLine = Integer.parseInt(cursor.getString(2));
                stop.nameStop = cursor.getString(3);
                stop.nameLine = cursor.getString(4);
                stopList.add(stop);
            } while (cursor.moveToNext());
        }
        return stopList;
    }

    public ArrayList<String> getStopNames() {
        ArrayList<Stop> stopsList = getAllStops();
        ArrayList<String> listNames = new ArrayList<String>();
        String[] stopsArray = new String[stopsList.size()];
        for(Stop stop : stopsList){
            listNames.add(stop.nameStop);
        }
        return listNames;
    }

    public void deleteStop(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVOURITES, ID + " = ?", new String[] { Integer.toString(id) });
        db.close();
    }

    public void deleteTimes(int idStop) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TIMES, ID_STOP + " = ?", new String[] { Integer.toString(idStop) });
        db.close();
    }
/*
    public boolean isset(int idStop) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TIMES, ID_STOP + " = ?", new String[] { Integer.toString(idStop) });
        db.close();
    }
    */
}