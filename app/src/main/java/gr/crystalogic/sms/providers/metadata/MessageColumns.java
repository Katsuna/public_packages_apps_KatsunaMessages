package gr.crystalogic.sms.providers.metadata;

import android.provider.Telephony;

public class MessageColumns implements android.provider.BaseColumns {

    public static final String ADDRESS = Telephony.TextBasedSmsColumns.ADDRESS;
    public static final String BODY = Telephony.TextBasedSmsColumns.BODY;
    public static final String DATE = Telephony.TextBasedSmsColumns.DATE;
    public static final String READ = Telephony.TextBasedSmsColumns.READ;
    public static final String TYPE = Telephony.TextBasedSmsColumns.TYPE;
    public static final String THREAD_ID = Telephony.TextBasedSmsColumns.THREAD_ID;
    public static final String DATE_SENT = Telephony.TextBasedSmsColumns.DATE_SENT;

}
