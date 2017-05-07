package com.example.tripathy.sharelocation.lib.Db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.tripathy.sharelocation.lib.Dal.MyLocation;

import java.util.LinkedList;

/**
 * Created by tripathy on 11/24/2015.
 */
//This class will execute sql inert and select query from android database to get MyLcoation object.
public class MyLocationDbSet {
    private static final String TAG = "SL: MyLocationDbSet: ";

    static FeedReaderDbHelper mDbHelper;

    public MyLocationDbSet(FeedReaderDbHelper mDbHelper) {
        this.mDbHelper = mDbHelper;
    }

    String[] projection = {
            DbContract.FeedEntry.COLUMN_NAME_LOCATION_ID,
            DbContract.FeedEntry.COLUMN_NAME_SENDER_ID,
            DbContract.FeedEntry.COLUMN_NAME_RECEIVER_ID,
            DbContract.FeedEntry.COLUMN_NAME_MESSAGE,
            DbContract.FeedEntry.COLUMN_NAME_CREATED_TIME,
            DbContract.FeedEntry.COLUMN_NAME_LATITUDE,
            DbContract.FeedEntry.COLUMN_NAME_LONGITUDE,
            DbContract.FeedEntry.COLUMN_NAME_STATUS,
            DbContract.FeedEntry.COLUMN_NAME_MOBILE_NUMBER
    };

    // How you want the results sorted in the resulting Cursor
    String sortOrder =
            DbContract.FeedEntry.COLUMN_NAME_CREATED_TIME + " DESC";

    public void Insert(MyLocation myLocation) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DbContract.FeedEntry.COLUMN_NAME_LOCATION_ID, myLocation.location_id);
        values.put(DbContract.FeedEntry.COLUMN_NAME_SENDER_ID, myLocation.sender_id);
        values.put(DbContract.FeedEntry.COLUMN_NAME_RECEIVER_ID, myLocation.receiver_id);
        values.put(DbContract.FeedEntry.COLUMN_NAME_MESSAGE, myLocation.message);
        values.put(DbContract.FeedEntry.COLUMN_NAME_CREATED_TIME, myLocation.created_time);
        values.put(DbContract.FeedEntry.COLUMN_NAME_LATITUDE, myLocation.latitude);
        values.put(DbContract.FeedEntry.COLUMN_NAME_LONGITUDE, myLocation.longitude);
        values.put(DbContract.FeedEntry.COLUMN_NAME_STATUS, myLocation.status);
        values.put(DbContract.FeedEntry.COLUMN_NAME_MOBILE_NUMBER, myLocation.mobile_number);


        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                DbContract.FeedEntry.TABLE_LOCATION,
                DbContract.FeedEntry.COLUMN_NAME_NULLABLE,
                values);
        //Save this data in ApplicationData also.
    }

    public void Delete(MyLocation myLocation) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // Delete row.
        String[] whereClause=new String[1];
        if(myLocation.location_id>0) {
            whereClause[0] = myLocation.location_id.toString();
            db.delete(
                    DbContract.FeedEntry.TABLE_LOCATION,
                    DbContract.FeedEntry.COLUMN_NAME_LOCATION_ID + " = ?",
                    whereClause);
        }
        //This case will execute when location receiver is not registered in ststem. In that case we are getting
        //0 as location id.
        else{
            whereClause[0] = myLocation.created_time.toString();
            db.delete(
                    DbContract.FeedEntry.TABLE_LOCATION,
                    DbContract.FeedEntry.COLUMN_NAME_CREATED_TIME + " = ?",
                    whereClause);
        }
        //Save this data in ApplicationData also.
    }

    public LinkedList<MyLocation> Read() {

        LinkedList<MyLocation> myLocations = new LinkedList<MyLocation>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DbContract.FeedEntry.TABLE_LOCATION,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        try {
            int location_id = cursor.getColumnIndex("location_id");
            int sender_id = cursor.getColumnIndex("sender_id");
            int receiver_id = cursor.getColumnIndex("receiver_id");
            int message = cursor.getColumnIndex("message");
            int created_time = cursor.getColumnIndex("created_time");
            int latitude = cursor.getColumnIndex("latitude");
            int longitude = cursor.getColumnIndex("longitude");
            int status = cursor.getColumnIndex("status");
            int mobile_number = cursor.getColumnIndex("mobile_number");
            while (cursor.moveToNext()) {
                myLocations.addLast(new MyLocation(cursor.getInt(location_id), cursor.getInt(sender_id),
                        cursor.getInt(receiver_id), cursor.getString(message), cursor.getString(created_time),
                        cursor.getDouble(latitude), cursor.getDouble(longitude), cursor.getInt(status),
                        cursor.getLong(mobile_number)));
            }
            return myLocations;
        } finally {
            cursor.close();
        }
    }
}
