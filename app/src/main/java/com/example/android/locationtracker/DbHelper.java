package com.example.android.locationtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class DbHelper extends SQLiteOpenHelper {
    static String dbName = "database_new.sqlite";
    String dbPath = "";
    SQLiteDatabase database;

    public DbHelper(Context context) {
        super(context, dbName, null, 1);
        dbPath = "/data/data/" + context.getPackageName() + "/databases";
    }

    public static synchronized DbHelper getDB(Context context) {
        return new DbHelper(context);
    }


    public boolean checkDB() {
        SQLiteDatabase sqLiteDatabase = null;

        try {
            sqLiteDatabase = SQLiteDatabase.openDatabase(dbPath + "/" + dbName, null, SQLiteDatabase.OPEN_READONLY);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sqLiteDatabase != null;

    }

    public void createDB(Context context) {
        this.getReadableDatabase();

        try {
            InputStream inputStream = context.getAssets().open(dbName);

            String outputFileName = dbPath + "/" + dbName;

            FileOutputStream fileOutputStream = new FileOutputStream(outputFileName);

            int length;

            byte[] aByte = new byte[1024];

            while ((length = inputStream.read(aByte)) > 0) {
                fileOutputStream.write(aByte, 0, length);
            }

            fileOutputStream.flush();
            fileOutputStream.close();
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void openDB() {
        try {
            database = SQLiteDatabase.openDatabase(dbPath + "/" + dbName, null, SQLiteDatabase.OPEN_READWRITE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public ArrayList<SuitCaseForDataBase> getData() {

        ArrayList<SuitCaseForDataBase> arrSuitcases = new ArrayList<>();

        Cursor cursor = database.rawQuery("select * from 'table_new'", null);

        if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                SuitCaseForDataBase suitCaseForDataBase = new SuitCaseForDataBase();

                suitCaseForDataBase.imei_no = cursor.getString(1);
                suitCaseForDataBase.lat = cursor.getString(2);
                suitCaseForDataBase.lng = cursor.getString(3);
                suitCaseForDataBase.address = cursor.getString(4);
                suitCaseForDataBase.date = cursor.getString(5);
                arrSuitcases.add(suitCaseForDataBase);
            }
        }
        return arrSuitcases;

    }

    public void insertData(SuitCaseForDataBase suitCaseForDataBase) {
        ContentValues cv = new ContentValues();


        cv.put("imei_number", suitCaseForDataBase.imei_no);
        cv.put("latitude", suitCaseForDataBase.lat);
        cv.put("longitude", suitCaseForDataBase.lng);
        cv.put("address", suitCaseForDataBase.address);
        cv.put("date", suitCaseForDataBase.date);

        database.insert("table_new", null, cv);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}




