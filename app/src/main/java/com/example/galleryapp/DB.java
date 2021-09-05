package com.example.galleryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DB extends SQLiteOpenHelper {
    private static final String TAG = "SQLite";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "PHOTOSTORE";

    // Table name: Note.
    private static final String TABLE_ALBUM = "ALBUM";
    private static final String COLUMN_ALBUM_ID ="ALBUMID";
    private static final String COLUMN_ALBUM_NAME ="ALBUMNAME";

    private static final String TABLE_PHOTO = "PHOTO";
    private static final String COLUMN_PHOTO_ALBUM_ID ="ALBUMID";
    private static final String COLUMN_PHOTO_ID ="PHOTOID";
    private static final String COLUMN_PHOTO_PATH ="PHOTOPATH";
    private static final String COLUMN_PHOTO_DATE ="DATE";
    public DB(Context context)  {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.i(TAG, "DB.onCreate ... ");
        // Script.
        String script = "CREATE TABLE " + TABLE_ALBUM + "("
                + COLUMN_ALBUM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + COLUMN_ALBUM_NAME + " TEXT"+ ")";
        // Execute Script.
        sqLiteDatabase.execSQL(script);
        String script1 = "CREATE TABLE " + TABLE_PHOTO + "("
                + COLUMN_PHOTO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + COLUMN_PHOTO_ALBUM_ID + " TEXT,"
                + COLUMN_PHOTO_PATH + " TEXT,"
                + COLUMN_PHOTO_DATE + " TEXT"+ ")";
        // Execute Script.
        sqLiteDatabase.execSQL(script1);
    }
    public long addPhoto(Photo photo) {
        Log.i(TAG, "DB.addAlarm ... " + photo.toString());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PHOTO_ALBUM_ID, photo.getId());
        values.put(COLUMN_PHOTO_PATH, photo.getPath());
        values.put(COLUMN_PHOTO_DATE, photo.getDate());
        // Inserting Row
        long result=db.insert(TABLE_PHOTO, null, values);
        // Closing database connection
        db.close();
        return result;
    }
    public long addAlbum(String name) {
        Log.i(TAG, "DB.addAlarm ... " + name);

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ALBUM_NAME, name);
        // Inserting Row
        long result=db.insert(TABLE_ALBUM, null, values);
        // Closing database connection
        db.close();
        return result;
    }
    public ArrayList<String> getListAlbum() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor1 = db.query(TABLE_ALBUM, new String[] { COLUMN_ALBUM_NAME },null, null, null, null, null);
        if(cursor1.getCount()==0){
            String script2 = "INSERT INTO " + TABLE_ALBUM + "("
                    +  COLUMN_ALBUM_NAME + ") VALUES ('CAMERA')";
            // Execute Script.
            db.execSQL(script2);
        }
        ArrayList<String> list = new ArrayList<>();
        Cursor cursor = db.query(TABLE_ALBUM, new String[] { COLUMN_ALBUM_NAME },null, null, null, null, null);
        if (cursor != null && cursor.getCount()!=0){
            cursor.moveToFirst();
            do{
                list.add(cursor.getString(0));
            }
            while (cursor.moveToNext());
        }
        else list.add(null);

        // return note
        return list;
    }
    public ArrayList<String> getListAlbumTime() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> list = new ArrayList<>();
        Cursor cursor1 = db.query(TABLE_PHOTO, new String[] { COLUMN_PHOTO_DATE },null, null, COLUMN_PHOTO_DATE, null, null);
        if(cursor1.getCount()!=0){
            if (cursor1 != null && cursor1.getCount()!=0){
                cursor1.moveToFirst();
                do{
                    list.add(cursor1.getString(0));
                }
                while (cursor1.moveToNext());
            }
            // return note
            return list;
        }
        else {
            list.add("Chua co anh chup");
            return list;
        }
    }
    public ArrayList<Photo> getAllPhoto(String condition, String value) {
        Log.i(TAG, "DB.getAllAlarm ... " );

        ArrayList<Photo> alarmTimeList = new ArrayList<Photo>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_PHOTO +" WHERE "+condition +"='"+value+"'";
        System.out.println(selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Photo photo = new Photo();
                photo.setId(cursor.getString(1));
                photo.setPath(cursor.getString(2));
                photo.setDate(cursor.getString(3));
                // Adding note to list
                alarmTimeList.add(photo);
            } while (cursor.moveToNext());
        }
        // return note list
        return alarmTimeList;
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
