package de.cspeckner.babytracker.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.List;
import java.util.UUID;

import de.cspeckner.babytracker.Event;
import de.cspeckner.babytracker.EventCursorAdapter;
import de.cspeckner.babytracker.EventDataDbHelper;
import de.cspeckner.babytracker.EventMarshaller;
import de.cspeckner.babytracker.EventRepository;
import de.cspeckner.babytracker.R;

public class MainActivity extends ActionBarActivity {

    private final static UUID watchappUuid = UUID.fromString("01a70c71-d8c1-4e33-9c12-c646168380e1");

    private TextView debugField;
    private ListView eventListView;

    private Handler handler;

    private EventCursorAdapter eventListAdapter;

    private EventRepository eventRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler();

        initDb();
        collectChildViews();
        configureList();
        setupPebble();
    }

    private void initDb() {
        EventDataDbHelper dbHelper = new EventDataDbHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        eventRepository = new EventRepository(db);
    }

    private void collectChildViews() {
        debugField = (TextView) findViewById(R.id.debugfield);
        eventListView = (ListView) findViewById(R.id.eventList);
    }

    private void configureList() {
        Cursor cursor = eventRepository.getCursorForAll();
        eventListAdapter = new EventCursorAdapter(getApplicationContext(), cursor);

        eventListView.setAdapter(eventListAdapter);
    }

    private void setupPebble() {
        Context context = getApplicationContext();

        notififyPebbleConnectionStatus(PebbleKit.isWatchConnected(context));

        PebbleKit.registerPebbleConnectedReceiver(context, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                notififyPebbleConnectionStatus(true);
            }
        });

        PebbleKit.registerPebbleDisconnectedReceiver(context, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                notififyPebbleConnectionStatus(false);
            }
        });

        PebbleKit.registerReceivedDataHandler(context, new PebbleKit.PebbleDataReceiver(watchappUuid) {
            @Override
            public void receiveData(Context context, int i, PebbleDictionary pebbleDictionary) {
                byte[] data;

                try {
                    if (pebbleDictionary.getUnsignedIntegerAsLong(0) != 1) return;

                    data = pebbleDictionary.getBytes(1);
                    if (data == null) return;
                } catch (Exception e) {
                    return;
                }

                List<Event> events;
                try {
                    events = EventMarshaller.unmarshalEvents(data);
                } catch (EventMarshaller.InvalidMessageException e) {
                    return;
                }

                PebbleKit.sendAckToPebble(context, i);

                for (Event event : events) {
                    eventRepository.persist(event);
                }

                final Cursor cursor = eventRepository.getCursorForAll();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        eventListAdapter.changeCursor(cursor);
                    }
                });

            }
        });

        PebbleKit.startAppOnPebble(context, watchappUuid);
    }

    private void notififyPebbleConnectionStatus(final boolean status) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                debugField.setText(status ? "Pebble connected" : "Pebble disconnected");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
