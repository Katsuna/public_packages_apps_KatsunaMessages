package gr.crystalogic.sms.dao;

import android.net.Uri;

public class Uris {

    public static final Uri CONVERSATIONS = Uri.parse("content://mms-sms/conversations");
    public static final Uri CONVERSATIONS_SIMPLE = Uri.parse("content://mms-sms/conversations")
            .buildUpon().appendQueryParameter("simple", "true").build();
}
