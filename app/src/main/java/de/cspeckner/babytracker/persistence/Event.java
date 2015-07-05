package de.cspeckner.babytracker.persistence;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Event implements Parcelable {

    public enum Type {
        FEED(0), DIAPER(1), SLEEP_START(2), SLEEP_STOP(3);

        private final int value;

        Type(int value) {
            this.value = value;
        }

        public int toInt() {
            return value;
        }

        public static Type fromInt(int value) {
            return Type.values()[value];
        }
    }

    protected Type type;
    protected Date time;
    protected Long id;

    public static final Parcelable.Creator CREATOR = new Creator() {
        @Override
        public Object createFromParcel(Parcel source) {
            return new Event(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new Event[size];
        }
    };

    public Event(Parcel source) {
        long id = source.readLong();

        if (id >= 0) {
            setId(id);
        }

        setType(Type.fromInt(source.readInt()));
        setTime(source.readLong());
    }

    public Event() {}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeLong(getId());
        dest.writeInt(type.toInt());
        dest.writeLong(time.getTime());
    }

    Event setId(long id) {
        this.id = id;

        return this;
    }

    public long getId() {
        return id == null ? -1 : id;
    }

    public boolean hasId() {
        return id != null;
    }

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

    public Event setTime(long timestamp) {
        this.time = new Date(timestamp);

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