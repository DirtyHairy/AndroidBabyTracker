package de.cspeckner.babytracker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import de.cspeckner.babytracker.Constants;
import de.cspeckner.babytracker.R;
import de.cspeckner.babytracker.persistence.Event;

public class EditEventActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        Intent intent = getIntent();

        if (!intent.hasExtra(Constants.EXTRA_EVENT)) {
                throw new RuntimeException("no event in intent - cannot happen");
        }

        Event event = intent.getParcelableExtra(Constants.EXTRA_EVENT);

        ((TextView) findViewById(R.id.textView)).setText(event.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_event, menu);
        return true;
    }

    public void onSaveEventClick(MenuItem item) {
        finish();
    }

    public void onCancelClick(MenuItem item) {
        finish();
    }
}
