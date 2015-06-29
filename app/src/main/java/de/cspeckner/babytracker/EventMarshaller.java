package de.cspeckner.babytracker;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventMarshaller {

    public static class InvalidMessageException extends Exception {}

    protected static Event.Type decodeType(byte value) throws InvalidMessageException {
        switch (value) {
            case 0:
                return Event.Type.FEED;

            case 1:
                return Event.Type.DIAPER;

            case 2:
                return Event.Type.SLEEP_START;

            case 3:
                return Event.Type.SLEEP_STOP;

            default:
                throw new InvalidMessageException();
        }
    }

    public static List<Event> unmarshalEvents(byte[] buffer) throws InvalidMessageException {
        byte eventCount = buffer[0];

        if (buffer.length != 1 + eventCount * 5)
            throw new InvalidMessageException();

        ArrayList<Event> events = new ArrayList<>(buffer[0]);

        for (byte i = 1; i < buffer.length; i += 5) {
            long timestamp = (buffer[i+1] & 0xFF) | ((buffer[i+2] & 0xFF)<< 8) |
                    ((buffer[i+3] & 0xFF) << 16) | ((buffer[i+4] & 0xFF) << 24);

            Event event = new Event();
            event
                    .setType(decodeType(buffer[i]))
                    .setTime(new Date(1000 * timestamp));

            events.add(event);
        }

        return events;
    }

}