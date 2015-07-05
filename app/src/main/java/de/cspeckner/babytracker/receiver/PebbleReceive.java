package de.cspeckner.babytracker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import org.json.JSONException;

import java.util.List;
import java.util.UUID;

import de.cspeckner.babytracker.Constants;
import de.cspeckner.babytracker.persistence.Event;
import de.cspeckner.babytracker.persistence.EventDataDbHelper;
import de.cspeckner.babytracker.persistence.EventPebbleMarshaller;
import de.cspeckner.babytracker.persistence.EventRepository;
import de.cspeckner.babytracker.persistence.InvalidEventIdException;

public class PebbleReceive extends BroadcastReceiver {

    private final String TAG = ".receiver.PebbleReceive";

    public PebbleReceive() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final UUID receivedUuid = (UUID)intent.getSerializableExtra(com.getpebble.android.kit.Constants.APP_UUID);

        if (!receivedUuid.equals(Constants.watchappUuid)) {
            return;
        }

        final int transactionId = intent.getIntExtra(com.getpebble.android.kit.Constants.TRANSACTION_ID, -1);
        final String jsonData = intent.getStringExtra(com.getpebble.android.kit.Constants.MSG_DATA);

        if (jsonData == null || jsonData.isEmpty()) {
            return;
        }

        try {
            final PebbleDictionary data = PebbleDictionary.fromJson(jsonData);
            processMessage(context, transactionId, data);
        } catch (JSONException e) {
            Log.d(TAG, "failed to unserialize message payload");
        }
    }

    protected void processMessage(Context context, int id, PebbleDictionary payload) {
        byte[] data;

        try {
            if (payload.getUnsignedIntegerAsLong(Constants.MESSAGE_KEY_MESSAGE_TYPE)
                    != Constants.MESSAGE_TYPE_EVENT_TRANSMISSION) return;

            data = payload.getBytes(Constants.MESSAGE_KEY_EVENT_BLOB);
            if (data == null) return;
        } catch (Exception e) {
            return;
        }

        List<Event> events;

        try {
            events = EventPebbleMarshaller.unmarshalEvents(data);
        } catch (EventPebbleMarshaller.InvalidMessageException e) {
            return;
        }

        PebbleKit.sendAckToPebble(context, id);

        EventDataDbHelper dbHelper = new EventDataDbHelper(context);
        EventRepository eventRepository = new EventRepository(dbHelper.getWritableDatabase());

        for (Event event : events) {
            try {
                eventRepository.persist(event);
            } catch (InvalidEventIdException e) {
                throw new RuntimeException("invalid ID during event import - cannot happen");
            }
        }

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(Constants.ACTION_NEW_EVENTS);

        localBroadcastManager.sendBroadcast(intent);
    }
}
