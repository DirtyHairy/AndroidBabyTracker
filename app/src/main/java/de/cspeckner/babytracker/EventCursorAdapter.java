package de.cspeckner.babytracker;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import de.cspeckner.babytracker.persistence.Event;
import de.cspeckner.babytracker.persistence.EventRepository;

import static de.cspeckner.babytracker.persistence.Event.*;

public class EventCursorAdapter extends CursorAdapter {

    public EventCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.event_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Event.Formatter formatter = new Event.Formatter(context);
        Event event = EventRepository.createEvent(cursor);

        ((TextView) view).setText(formatter.format(event));
    }
}
