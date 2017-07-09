package com.katsuna.messages.services;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.RemoteInput;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.katsuna.messages.ui.activities.ConversationActivity;
import com.katsuna.messages.utils.Constants;

public class HeadlessSmsSendService extends IntentService {

    private static final String TAG = "HeadlessSmsSendService";

    public HeadlessSmsSendService() {
        // Class name will be the thread name.
        super(HeadlessSmsSendService.class.getName());

        // Intent should be redelivered if the process gets killed before completing the job.
        setIntentRedelivery(true);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Log.d(TAG, "HeadlessSmsSendService onHandleIntent");

        final String action = intent.getAction();
        if (!TelephonyManager.ACTION_RESPOND_VIA_MESSAGE.equals(action)) {
            Log.d(TAG, "HeadlessSmsSendService onHandleIntent wrong action: " +
                    action);

            return;
        }
        final Bundle extras = intent.getExtras();
        if (extras == null) {
            Log.e(TAG, "Called to send SMS but no extras");
            return;
        }

        // Get all possible extras from intent
        final String message = getText(intent, Intent.EXTRA_TEXT);


        final Uri intentUri = intent.getData();
        final String recipients = intentUri != null ? getSmsRecipients(intentUri) : null;

        if (TextUtils.isEmpty(recipients)) {
            Log.e(TAG, "recipient(s) cannot be empty");

            return;
        }

        Log.d(TAG, "recipients: " + recipients);
        Intent i = new Intent(this, ConversationActivity.class);
        i.putExtra(Constants.EXTRA_NUMBER, recipients);
        i.putExtra(Constants.MESSAGE, message);
        startActivity(i);
    }

    // return a semicolon separated list of phone numbers from a smsto: uri.
    private String getSmsRecipients(final Uri uri) {
        String recipients = uri.getSchemeSpecificPart();
        final int pos = recipients.indexOf('?');
        if (pos != -1) {
            recipients = recipients.substring(0, pos);
        }
        recipients = replaceUnicodeDigits(recipients).replace(',', ';');
        return recipients;
    }

    private String replaceUnicodeDigits(final String number) {
        final StringBuilder normalizedDigits = new StringBuilder(number.length());
        for (final char c : number.toCharArray()) {
            final int digit = Character.digit(c, 10);
            if (digit != -1) {
                normalizedDigits.append(digit);
            } else {
                normalizedDigits.append(c);
            }
        }
        return normalizedDigits.toString();
    }


    private String getText(final Intent intent, final String textType) {
        final String message = intent.getStringExtra(textType);
        if (message == null) {
            final Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
            if (remoteInput != null) {
                final CharSequence extra = remoteInput.getCharSequence(textType);
                if (extra != null) {
                    return extra.toString();
                }
            }
        }
        return message;
    }
}
