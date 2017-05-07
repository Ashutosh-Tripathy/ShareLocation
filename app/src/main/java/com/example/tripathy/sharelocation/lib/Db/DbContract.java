package com.example.tripathy.sharelocation.lib.Db;

import android.provider.BaseColumns;

/**
 * Created by tripathy on 11/24/2015.
 */
public final class DbContract {
    public  DbContract(){}
    public static abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_LOCATION = "location";
        public static final String COLUMN_NAME_LOCATION_ID = "location_id";
        public static final String COLUMN_NAME_SENDER_ID = "sender_id";
        public static final String COLUMN_NAME_RECEIVER_ID = "receiver_id";
        public static final String COLUMN_NAME_MESSAGE = "message";
        public static final String COLUMN_NAME_CREATED_TIME = "created_time";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
        public static final String COLUMN_NAME_STATUS= "status";
        public static final String COLUMN_NAME_MOBILE_NUMBER = "mobile_number";
        public static final String COLUMN_NAME_NULLABLE = null ;
    }
    //PRIMARY KEY NOT NULL
    protected static final String SQL_CREATE_ENTRIES="CREATE TABLE location\n" +
            "(\n" +
            "  location_id int ,\n" +
            "  sender_id integer NOT NULL,\n" +
            "  receiver_id integer NOT NULL,\n" +
            "  message varchar(50) NOT NULL,\n" +
            "  created_time timestamp without time zone NOT NULL,\n" +
            "  latitude numeric(12,9) NOT NULL,\n" +
            "  longitude numeric(12,9) NOT NULL,\n" +
            "  status int NOT NULL,\n" +
            "  mobile_number numeric(10,0)  NOT NULL\n" +
            ");";

    protected static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_LOCATION;
}
