package de.cspeckner.babytracker.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.cspeckner.babytracker.Event;
import de.cspeckner.babytracker.EventMarshaller;
import de.cspeckner.babytracker.R;

public class MainActivity extends ActionBarActivity {

    private final static UUID watchappUuid = UUID.fromString("01a70c71-d8c1-4e33-9c12-c646168380e1");

    private TextView debugField;
    private Handler handler;

    private ArrayList<CharSequence> eventList = new ArrayList<>();
    private ArrayAdapter<CharSequence> eventListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context context = getApplicationContext();

        debugField = (TextView)findViewById(R.id.debugfield);
        ListView eventListView = (ListView)findViewById(R.id.eventList);
        handler = new Handler();

        eventListAdapter = new ArrayAdapter<>(context, R.layout.eventlistitem, eventList);
        eventListView.setAdapter(eventListAdapter);

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
                }
                catch (EventMarshaller.InvalidMessageException e) {
                    return;
                }

                final List<Event> pickledEvents = events;

                PebbleKit.sendAckToPebble(context, i);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        for (Event event: pickledEvents) {
                            eventListAdapter.add(event.toString());
                        }
                    }
                });

            }
        });

        PebbleKit.startAppOnPebble(context, watchappUuid);
    }

    protected void notififyPebbleConnectionStatus(final boolean status) {
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
