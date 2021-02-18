package com.example.tabatatimer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

public class Database {
    SQLiteDatabase db;
    DatabaseHelper dbHelper;
    Cursor cursor;

    public Database(Context context){
        dbHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public Database open(){
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        dbHelper.close();
    }

    public ArrayList<Timer> getTimers() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE , null);
        ArrayList<Timer> timerList = new ArrayList<>();
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ID_COLUMN));
                    String title = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TITLE));
                    int prepTime = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.PREP_TIME));
                    int workTime = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.WORK_TIME));
                    int restTime = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.REST_TIME));
                    int cyclesAmount = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CYCLES_AMOUNT));
                    int setsAmount = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.SETS_AMOUNT));
                    int rest_sets = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.REST_SETS));
                    int color = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLOR));
                    timerList.add(new Timer(id, title, prepTime, workTime, restTime, cyclesAmount, setsAmount, rest_sets, color));
                }
            }

        }
        cursor.close();
        return timerList;
    }

    public Timer getTimer(int id){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Timer timer = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE + " WHERE " +
                DatabaseHelper.ID_COLUMN + " = ?",  new String[]{ String.valueOf(id)});
        if(cursor.moveToFirst()){
            String title = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TITLE));
            int prepTime = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.PREP_TIME));
            int workTime = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.WORK_TIME));
            int restTime = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.REST_TIME));
            int cyclesAmount = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CYCLES_AMOUNT));
            int setsAmount = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.SETS_AMOUNT));
            int rest_sets = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.REST_SETS));
            int color = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLOR));
            timer = new Timer(id, title, prepTime, workTime, restTime, cyclesAmount, setsAmount, rest_sets, color);
        }
        cursor.close();
        return  timer;
    }

    public long getCount(){
        return DatabaseUtils.queryNumEntries(db, DatabaseHelper.TABLE);
    }

    public long add(Timer timer){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.TITLE, timer.getTitle());
        cv.put(DatabaseHelper.PREP_TIME, timer.getPrepTime());
        cv.put(DatabaseHelper.WORK_TIME, timer.getWorkTime());
        cv.put(DatabaseHelper.REST_TIME, timer.getRestTime());
        cv.put(DatabaseHelper.CYCLES_AMOUNT, timer.getCyclesAmount());
        cv.put(DatabaseHelper.SETS_AMOUNT, timer.getSetsAmount());
        cv.put(DatabaseHelper.REST_SETS, timer.getRest_sets());
        cv.put(DatabaseHelper.COLOR, timer.getColor());

        return db.insert(DatabaseHelper.TABLE, null, cv);
    }

    public long update(Timer timer){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.TITLE, timer.getTitle());
        cv.put(DatabaseHelper.PREP_TIME, timer.getPrepTime());
        cv.put(DatabaseHelper.WORK_TIME, timer.getWorkTime());
        cv.put(DatabaseHelper.REST_TIME, timer.getRestTime());
        cv.put(DatabaseHelper.CYCLES_AMOUNT, timer.getCyclesAmount());
        cv.put(DatabaseHelper.SETS_AMOUNT, timer.getSetsAmount());
        cv.put(DatabaseHelper.REST_SETS, timer.getRest_sets());
        cv.put(DatabaseHelper.COLOR, timer.getColor());
        return db.update(DatabaseHelper.TABLE, cv, DatabaseHelper.ID_COLUMN + "=" + timer.getId(), null);
    }

    public void delete(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("DELETE  FROM " + DatabaseHelper.TABLE + " WHERE " +
                DatabaseHelper.ID_COLUMN + " = " + id, null);
        db.delete(DatabaseHelper.TABLE, DatabaseHelper.ID_COLUMN + " = " + id, null);
        cursor.close();
    }

    public void deleteAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("DELETE  FROM " + DatabaseHelper.TABLE , null);
        db.delete(DatabaseHelper.TABLE, null,null);
        cursor.close();
    }


}