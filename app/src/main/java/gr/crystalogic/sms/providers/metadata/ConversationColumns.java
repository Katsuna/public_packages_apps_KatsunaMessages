package gr.crystalogic.sms.providers.metadata;

import android.provider.BaseColumns;

public class ConversationColumns implements BaseColumns {

    public static final String BODY = "body";
    public static final String TYPE = "type";
    public static final String DATE = "date";
    public static final String DATE_SENT = "date_sent";
    public static final String READ = "read";

    public static final String THREAD_ID = "thread_id";
    public static final String ADDRESS = "address";

    //extra simple  uri columns
    public static final String UNREAD_COUNT = "unread_count";
    public static final String RECIPIENT_IDS = "recipient_ids";
    public static final String SNIPPET = "snippet";
    public static final String SNIPPET_CS = "snippet_cs";

}
