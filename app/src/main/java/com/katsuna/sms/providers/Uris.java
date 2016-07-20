package com.katsuna.sms.providers;

import android.net.Uri;
import android.provider.Telephony;

class Uris {

    public static final Uri THREADS_URI = Telephony.Threads.CONTENT_URI;
    public static final Uri THREADS_URI_SIMPLE = THREADS_URI.buildUpon().appendQueryParameter("simple", "true").build();

    public static final Uri CANONICAL_ADDRESS = Uri.parse("content://mms-sms/canonical-address");

    public static final Uri URI_SMS = Uri.parse("content://sms");

    public static final Uri URI_SENT = Uri.parse("content://sms/sent");

    public static final Uri URI_INBOX = Uri.parse("content://sms/inbox");

}
