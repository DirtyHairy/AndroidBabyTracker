package de.cspeckner.babytracker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import de.cspeckner.babytracker.Constants;
import de.cspeckner.babytracker.R;
import de.cspeckner.babytracker.persistence.Event;

public class EditEventActivity extends AppCompatActivity {

    private Event event;

    private Spinner eventTypeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        Intent intent = getIntent();

        if (!intent.hasExtra(Constants.EXTRA_EVENT)) {
                throw new RuntimeException("no event in intent - cannot happen");
        }

        event = intent.getParcelableExtra(Constants.EXTRA_EVENT);

        eventTypeSpinner = configureEventTypeSpinner();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_event, menu);
        return true;
    }

    private Spinner configureEventTypeSpinner() {
        Event.Formatter eventFormatter = new Event.Formatter(getApplicationContext());

        Event.Type[] types = Event.Type.values();
        int nValues = types.length;
        String[] values = new String[nValues];

        for (int i = 0; i < nValues; i++) {
            values[i] = eventFormatter.format(types[i]);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        eventTypeSpinner = (Spinner)findViewById(R.id.event_type_spinner);
        eventTypeSpinner.setAdapter(adapter);

        return  eventTypeSpinner;
    }

    public void onSaveEventClick(MenuItem item) {
        finish();
    }

    public void onCancelClick(MenuItem item) {
        finish();
    }
}
