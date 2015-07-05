package de.cspeckner.babytracker.persistence;

public final class EventDataContract {

    private EventDataContract() {}

    public static final class Event {

        public static final String TABLE_NAME = "events";

        public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
    }
}
