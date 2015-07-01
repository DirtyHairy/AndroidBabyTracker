package de.cspeckner.babytracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class EventDataDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "EventData.db";

    public EventDataDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String createSql = String.format(
                "CREATE TABLE %s ( " +
                    "%s INTEGER PRIMARY KEY, " +
                    "%s INTEGER NOT NULL, " +
                    "%s INTEGER NOT NULL" +
                ");",
                EventDataContract.Event.TABLE_NAME,
                EventDataContract.Event.COLUMN_NAME_ID,
                EventDataContract.Event.COLUMN_NAME_TYPE,
                EventDataContract.Event.COLUMN_NAME_TIMESTAMP
            ),
            indexSql = String.format(
                "CREATE UNIQUE INDEX %s_%s_idx ON %s (%s, %s);",
                EventDataContract.Event.COLUMN_NAME_TYPE,
                EventDataContract.Event.COLUMN_NAME_TIMESTAMP,
                EventDataContract.Event.TABLE_NAME,
                EventDataContract.Event.COLUMN_NAME_TYPE,
                EventDataContract.Event.COLUMN_NAME_TIMESTAMP
            );

        db.execSQL(createSql);
        db.execSQL(indexSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        final String sql = String.format("DROP TABLE %s;", EventDataContract.Event.TABLE_NAME);

        db.execSQL(sql);

        onCreate(db);
    }
}
