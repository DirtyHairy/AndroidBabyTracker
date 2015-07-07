package de.cspeckner.babytracker.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.getpebble.android.kit.PebbleKit;

import java.util.Date;

import de.cspeckner.babytracker.Constants;
import de.cspeckner.babytracker.EventCursorAdapter;
import de.cspeckner.babytracker.persistence.Event;
import de.cspeckner.babytracker.persistence.EventDataDbHelper;
import de.cspeckner.babytracker.persistence.EventRepository;
import de.cspeckner.babytracker.R;
import de.cspeckner.babytracker.persistence.InvalidEventIdException;

public class EventListActivity extends AppCompatActivity {

    private ListView eventListView;

    private Handler handler;

    private EventCursorAdapter eventListAdapter;

    private EventRepository eventRepository;

    private AlertDialog clearAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        setTitle(R.string.activity_list_events);

        handler = new Handler();
        clearAlert = createClearAlert(this);

        initDb();
        collectChildViews();
        configureList();
        registerReceivers();
        updateList();
        setupPebble();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_list, menu);
        return true;
    }

    private void initDb() {
        EventDataDbHelper dbHelper = new EventDataDbHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        eventRepository = new EventRepository(db);
    }

    private void collectChildViews() {
        eventListView = (ListView) findViewById(R.id.eventList);
    }

    private void configureList() {
        Cursor cursor = eventRepository.getCursorForAll();
        eventListAdapter = new EventCursorAdapter(this, cursor);

        eventListView.setAdapter(eventListAdapter);

        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event;

                try {
                    event = eventRepository.getById(id);
                } catch (InvalidEventIdException e) {
                    throw new RuntimeException("list item has invalid event ID - cannot happen");
                }

                dispatchEditEvent(event);
            }
        });
    }

    private void setupPebble() {
        Context context = getApplicationContext();

        PebbleKit.startAppOnPebble(context, Constants.watchappUuid);
    }

    public void updateList() {
        eventListAdapter.changeCursor(eventRepository.getCursorForAll());
    }

    private void registerReceivers() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        IntentFilter filter = new IntentFilter(Constants.ACTION_NEW_EVENTS);

        localBroadcastManager.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateList();
                    }
                });
            }
        }, filter);
    }

    private AlertDialog createClearAlert(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(R.string.clear_alert_title);
        builder.setMessage(R.string.clear_alert_message);
        builder.setPositiveButton(R.string.clear_alert_confirm_button_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eventRepository.deleteAll();
                updateList();
            }
        });
        builder.setNegativeButton(R.string.clear_alert_cancel_button_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setCancelable(false);

        return builder.create();
    }

    public void onClearClick(MenuItem item) {
        clearAlert.show();
    }

    public void onAddClick(MenuItem item) {
        Event event = new Event();

        event.setType(Event.Type.FEED);
        event.setTime(new Date());

        dispatchEditEvent(event);
    }

    private void dispatchEditEvent(Event event) {
        Intent intent = new Intent(this, EditEventActivity.class);

        intent.putExtra(Constants.EXTRA_EVENT, event);

        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            int action = data.getIntExtra(Constants.EXTRA_ACTION, -1);

            switch (action) {
                case Constants.ACTION_SAVE:
                    try {
                        eventRepository.persist(data.<Event>getParcelableExtra(Constants.EXTRA_EVENT));
                    } catch (InvalidEventIdException e) {
                        throw new RuntimeException("invalid event ID - cannot happen");
                    }

                    updateList();
                    break;

                default:
                    throw new RuntimeException("invalid action - cannot happen");
            }
        }
    }
}
