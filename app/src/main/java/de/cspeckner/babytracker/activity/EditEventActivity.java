package de.cspeckner.babytracker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import de.cspeckner.babytracker.Constants;
import de.cspeckner.babytracker.R;
import de.cspeckner.babytracker.persistence.Event;

public class EditEventActivity extends AppCompatActivity {

    private static final String BUNDLE_KEY_EVENT = "de.cspeckner.babytracker.EVENT";

    private Event event;

    private Spinner eventTypeSpinner;

    private boolean deletePossible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        Intent intent = getIntent();

        if (!intent.hasExtra(Constants.EXTRA_EVENT)) {
                throw new RuntimeException("no event in intent - cannot happen");
        }

        event = intent.getParcelableExtra(Constants.EXTRA_EVENT);
        deletePossible = intent.getBooleanExtra(Constants.EXTRA_DELETE_POSSIBLE, false);

        eventTypeSpinner = configureEventTypeSpinner();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_event, menu);

        menu.findItem(R.id.action_delete).setVisible(deletePossible);

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(BUNDLE_KEY_EVENT, event);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        event = savedInstanceState.getParcelable(BUNDLE_KEY_EVENT);
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
        eventTypeSpinner.setSelection(event.getType().toInt());

        eventTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                event.setType(Event.Type.fromInt(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        return  eventTypeSpinner;
    }

    private Intent createResultIntent(int action) {
        Intent intent = new Intent();
        intent.putExtra(Constants.EXTRA_ACTION, action);
        intent.putExtra(Constants.EXTRA_EVENT, event);

        return intent;
    }

    public void onSaveEventClick(MenuItem item) {
        setResult(RESULT_OK, createResultIntent(Constants.ACTION_SAVE));

        finish();
    }

    public void onDeleteClick(MenuItem item) {
        setResult(RESULT_OK, createResultIntent(Constants.ACTION_DELETE));

        finish();
    }
}
