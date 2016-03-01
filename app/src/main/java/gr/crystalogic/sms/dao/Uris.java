package gr.crystalogic.sms.dao;

import android.net.Uri;
import android.provider.ContactsContract;

public class Uris {

    public static final Uri CONVERSATIONS = Uri.parse("content://mms-sms/conversations");
    public static final Uri CONVERSATIONS_SIMPLE = Uri.parse("content://mms-sms/conversations")
            .buildUpon().appendQueryParameter("simple", "true").build();

    public static final Uri CANONICAL_ADDRESS = Uri.parse("content://mms-sms/canonical-address");

    public static final Uri PHONES =  ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

    public static final Uri URI_SMS = Uri.parse("content://sms");

    public static final Uri URI_SENT = Uri.parse("content://sms/sent");

}
