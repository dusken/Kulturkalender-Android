package com.underdusken.kulturekalendar.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: pavelarteev
 * Date: 9/26/12
 * Time: 9:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "kulturkalender.db";
    private static final int DATABASE_VERSION = 2;


    // TABLE EVENTS
    public static final String TABLE_EVENTS = "events";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_EVENTS_ID = "_events_id";
    public static final String COLUMN_EVENTS_TITLE = "_title";
    public static final String COLUMN_EVENTS_CATEGORY_ID = "_category_id";
    public static final String COLUMN_EVENTS_ADDRESS = "_address";
    public static final String COLUMN_EVENTS_GEO_LAT = "_geo_lat";
    public static final String COLUMN_EVENTS_GEO_LON = "_geo_lon";
    public static final String COLUMN_EVENTS_DATE_START = "_date_start";
    public static final String COLUMN_EVENTS_PRICE = "_price";
    public static final String COLUMN_EVENTS_AGE_LIMIT = "_age_limit";

    public static final String COLUMN_EVENTS_PLACE_NAME = "_place_name";
    public static final String COLUMN_EVENTS_SHOW_DATE = "_show_date";

    public static final String COLUMN_EVENTS_FAVORITE = "_favorite";
    public static final String COLUMN_EVENTS_BEER_PRICE = "_beer_price";
    public static final String COLUMN_EVENTS_DESCRIPTION_ENG = "_desc_eng";
    public static final String COLUMN_EVENTS_DESCRIPTION_NO = "_desc_no";

    public static final String COLUMN_EVENTS_IMAGE_URL = "_image_url";
    public static final String COLUMN_EVENTS_EVENTS_URL = "_events_url";
    public static final String COLUMN_EVENTS_IS_RECOMMENDED = "_is_recommended";

    public static final String COLUMN_EVENTS_NOTIFICATION_ID = "_notification_id";


    private static final String TABLE_EVENTS_CREATE = "create table " + TABLE_EVENTS + "(" + COLUMN_ID + " " +
            "integer primary key autoincrement, " + COLUMN_EVENTS_ID + " text, " +
            "" + COLUMN_EVENTS_TITLE + " text, " + COLUMN_EVENTS_CATEGORY_ID + " text, " +
            "" + COLUMN_EVENTS_ADDRESS + " text, " + COLUMN_EVENTS_GEO_LAT + " real, " +
            "" + COLUMN_EVENTS_GEO_LON + " real, " + COLUMN_EVENTS_DATE_START + " text, " +
            "" + COLUMN_EVENTS_PRICE + " integer, " + COLUMN_EVENTS_AGE_LIMIT + " integer, " +
            "" + COLUMN_EVENTS_PLACE_NAME + " text, " + COLUMN_EVENTS_SHOW_DATE + " text, " +
            "" + COLUMN_EVENTS_FAVORITE + " integer, " + COLUMN_EVENTS_BEER_PRICE + " integer, " +
            "" + COLUMN_EVENTS_DESCRIPTION_ENG + " text, " + COLUMN_EVENTS_DESCRIPTION_NO + " text, " +
            "" + COLUMN_EVENTS_IMAGE_URL + " text, " + COLUMN_EVENTS_EVENTS_URL + " text, " +
            "" + COLUMN_EVENTS_IS_RECOMMENDED + " integer, " + COLUMN_EVENTS_NOTIFICATION_ID + " integer);";


    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_EVENTS_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to " + newVersion + ", " +
                        "which will destroy all old data");

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS_CREATE);

        onCreate(db);
    }

}
