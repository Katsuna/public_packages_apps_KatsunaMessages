package com.katsuna.messages.providers.metadata;

import android.provider.BaseColumns;
import android.provider.Telephony;

public class ThreadColumns implements BaseColumns {

    public static final String DATE = Telephony.ThreadsColumns.DATE;
    public static final String TYPE = Telephony.ThreadsColumns.TYPE;
    public static final String READ = Telephony.ThreadsColumns.READ;
    public static final String RECIPIENT_IDS = Telephony.ThreadsColumns.RECIPIENT_IDS;
    public static final String SNIPPET = Telephony.ThreadsColumns.SNIPPET;
    public static final String MESSAGE_COUNT = Telephony.ThreadsColumns.MESSAGE_COUNT;

}
