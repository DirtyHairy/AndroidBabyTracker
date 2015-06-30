package de.cspeckner.babytracker;

import java.util.UUID;

public final class Constants {

    public static final UUID watchappUuid = UUID.fromString("01a70c71-d8c1-4e33-9c12-c646168380e1");

    public static final int MESSAGE_TYPE_EVENT_TRANSMISSION = 1;

    public static final int MESSAGE_KEY_MESSAGE_TYPE = 0;

    public static final int MESSAGE_KEY_EVENT_BLOB = 1;

    public static final String ACTION_NEW_EVENTS = "de.cspeckner.babytracker.action.NEW_EVENTS";

    private Constants() {}

}
