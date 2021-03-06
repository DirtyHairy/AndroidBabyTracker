package de.cspeckner.babytracker;

import java.util.UUID;

public final class Constants {

    public static final UUID watchappUuid = UUID.fromString("cb49397f-824b-422c-bfbe-697fbe02b6ad");

    public static final int MESSAGE_TYPE_EVENT_TRANSMISSION = 1;

    public static final int MESSAGE_KEY_MESSAGE_TYPE = 0;

    public static final int MESSAGE_KEY_EVENT_BLOB = 1;

    public static final String ACTION_NEW_EVENTS = "de.cspeckner.babytracker.action.NEW_EVENTS";

    public static final String EXTRA_EVENT = "de.cspeckner.babytracker.EXTRA_EVENT";

    public static final String EXTRA_DELETE_POSSIBLE = "de.cspeckner.babytracker.EXTRA_DELETE_POSSIBLE";

    public static final String EXTRA_ACTION = "de.cspeckner.babytracker.EXTRA_ACTION";

    public static final int ACTION_SAVE = 0;
    public static final int ACTION_DELETE = 1;

    private Constants() {}

}
