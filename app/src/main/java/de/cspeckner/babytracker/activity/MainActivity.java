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
import android.widget.ListView;

import com.getpebble.android.kit.PebbleKit;

import de.cspeckner.babytracker.Constants;
import de.cspeckner.babytracker.EventCursorAdapter;
import de.cspeckner.babytracker.EventDataDbHelper;
import de.cspeckner.babytracker.EventRepository;
import de.cspeckner.babytracker.R;

public class MainActivity extends AppCompatActivity {

    private ListView eventListView;

    private Handler handler;

    private EventCursorAdapter eventListAdapter;

    private EventRepository eventRepository;

    private AlertDialog clearAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler();
        clearAlert = createClearAlert(this);

        initDb();
        collectChildViews();
        configureList();
        registerReceivers();
        updateList();
        setupPebble();
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
        eventListAdapter = new EventCursorAdapter(getApplicationContext(), cursor);

        eventListView.setAdapter(eventListAdapter);
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
