package de.cspeckner.babytracker;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class EventRepository {

    protected SQLiteDatabase db;

    public EventRepository(SQLiteDatabase db) {
        this.db = db;
    }

    public void persist(Event event) {
        ContentValues values = new ContentValues();

        values.put(EventDataContract.Event.COLUMN_NAME_TYPE, event.getType().toInt());
        values.put(EventDataContract.Event.COLUMN_NAME_TIMESTAMP, event.getTime().getTime());

        db.insertWithOnConflict(EventDataContract.Event.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public Cursor getCursorForAll() {
        return db.rawQuery(String.format("SELECT %s, %s, %s FROM %s ORDER BY %s DESC",
                EventDataContract.Event.COLUMN_NAME_ID,
                EventDataContract.Event.COLUMN_NAME_TYPE,
                EventDataContract.Event.COLUMN_NAME_TIMESTAMP,
                EventDataContract.Event.TABLE_NAME,
                EventDataContract.Event.COLUMN_NAME_TIMESTAMP
        ), null);
    }

    public static Event createEvent(Cursor cursor) {
        Event event = new Event();

        int typeIdx = cursor.getColumnIndex(EventDataContract.Event.COLUMN_NAME_TYPE);
        int timestampIdx = cursor.getColumnIndex(EventDataContract.Event.COLUMN_NAME_TIMESTAMP);

        event
                .setType(Event.Type.fromInt(cursor.getInt(typeIdx)))
                .setTime(cursor.getLong(timestampIdx));

        return event;
    }
}
