package de.cspeckner.babytracker;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Event {

    public enum Type {
        FEED, DIAPER, SLEEP_START, SLEEP_STOP
    }

    protected Type type;
    protected Date time;

    public Event setType(Type type) {
        this.type = type;

        return this;
    }


    public Type getType() {
        return type;
    }

    public Date getTime() {
        return time;
    }

    public Event setTime(Date time) {
        this.time = time;

        return this;
    }

    protected static String typeToString(Type type) {
        switch (type) {
            case FEED:
                return "feed";

            case DIAPER:
                return "change diaper";

            case SLEEP_START:
                return "sleep start";

            case SLEEP_STOP:
                return "sleep stop";

            default:
                return "invalid";
        }
    }

    public String toString() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        return typeToString(type) + " @ " + format.format(time);
    }
}
