package gr.crystalogic.sms.providers;

import android.net.Uri;

class Uris {

    public static final Uri CONVERSATIONS = Uri.parse("content://mms-sms/conversations");
    public static final Uri CONVERSATIONS_SIMPLE = Uri.parse("content://mms-sms/conversations")
            .buildUpon().appendQueryParameter("simple", "true").build();

    public static final Uri CANONICAL_ADDRESS = Uri.parse("content://mms-sms/canonical-address");

    public static final Uri URI_SMS = Uri.parse("content://sms");

    public static final Uri URI_SENT = Uri.parse("content://sms/sent");

    public static final Uri URI_INBOX = Uri.parse("content://sms/inbox");

}
