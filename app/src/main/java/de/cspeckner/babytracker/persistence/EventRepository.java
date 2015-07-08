package de.cspeckner.babytracker.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;

public class EventRepository {

    protected SQLiteDatabase db;

    private HashMap<Long, Event> cache = new HashMap<>();

    public EventRepository(SQLiteDatabase db) {
        this.db = db;
    }

    public void persist(Event event) throws InvalidEventIdException {
        ContentValues values = new ContentValues();

        values.put(EventDataContract.Event.COLUMN_NAME_TYPE, event.getType().toInt());
        values.put(EventDataContract.Event.COLUMN_NAME_TIMESTAMP, event.getTime().getTime());

        if (event.hasId()) {
            String[] whereArgs = {event.getId() + ""};

            int affectedRows = db.update(
                    EventDataContract.Event.TABLE_NAME,
                    values,
                    String.format("%s = ?", EventDataContract.Event.COLUMN_NAME_ID),
                    whereArgs
            );

            if (affectedRows == 0) throw new InvalidEventIdException();
        } else {
            long id = db.insertWithOnConflict(EventDataContract.Event.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            event.setId(id);
        }

        cache.put(event.getId(), event);
    }

    public void delete(Event event) throws InvalidEventIdException {
        String[] whereArgs = {event.getId() + ""};

        int affectedRows = db.delete(
                EventDataContract.Event.TABLE_NAME,
                String.format("%s = ?", EventDataContract.Event.COLUMN_NAME_ID),
                whereArgs
        );

        if (affectedRows == 0) throw new InvalidEventIdException();

        cache.remove(event.getId());

        event.clearId();
    }

    public Event getById(long id) throws InvalidEventIdException {
        Event event;

        if (!cache.containsKey(id)) {
            String sql = String.format("SELECT %s, %s, %s FROM %s WHERE %s = ?",
                    EventDataContract.Event.COLUMN_NAME_ID,
                    EventDataContract.Event.COLUMN_NAME_TYPE,
                    EventDataContract.Event.COLUMN_NAME_TIMESTAMP,
                    EventDataContract.Event.TABLE_NAME,
                    EventDataContract.Event.COLUMN_NAME_ID
            );
            String[] params = {id + ""};

            Cursor cursor = db.rawQuery(sql, params);

            if (!cursor.moveToFirst()) {
                throw new InvalidEventIdException();
            }

            event = createEvent(cursor);
            cache.put(event.getId(), event);
        } else {
            event = cache.get(id);
        }

        return event;
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

    public void deleteAll() {
        db.delete(EventDataContract.Event.TABLE_NAME, null, null);

        cache.clear();
    }

    public static Event createEvent(Cursor cursor) {
        Event event = new Event();

        int idIdx = cursor.getColumnIndex(EventDataContract.Event.COLUMN_NAME_ID);
        int typeIdx = cursor.getColumnIndex(EventDataContract.Event.COLUMN_NAME_TYPE);
        int timestampIdx = cursor.getColumnIndex(EventDataContract.Event.COLUMN_NAME_TIMESTAMP);

        event
                .setId(cursor.getInt(idIdx))
                .setType(Event.Type.fromInt(cursor.getInt(typeIdx)))
                .setTime(cursor.getLong(timestampIdx));

        return event;
    }
}
