package com.katsuna.messages.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.katsuna.commons.entities.KatsunaConstants;
import com.katsuna.commons.ui.KatsunaActivity;
import com.katsuna.messages.R;
import com.katsuna.messages.domain.Message;
import com.katsuna.messages.providers.SmsProvider;
import com.katsuna.messages.receivers.BaseBroadcastReceiver;
import com.katsuna.messages.ui.adapters.MessagesAdapter;
import com.katsuna.messages.utils.Constants;
import com.katsuna.messages.utils.Device;

import java.util.ArrayList;
import java.util.List;

public class ConversationActivity extends KatsunaActivity {

    private static final String TAG = "ConversationActivity";
    private final String SENT = "SMS_SENT";
    private final String DELIVERED = "SMS_DELIVERED";
    private RecyclerView mRecyclerView;
    private EditText mNewMessage;
    private BaseBroadcastReceiver sendBroadcastReceiver;
    private BaseBroadcastReceiver deliveryBroadcastReceiver;
    private String message;
    private long conversationId;
    private String conversationNumber;
    private long savedMessageId = -1;
    private String mDisplayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        initControls();
    }

    private void initToolbarLocal() {
        initToolbar();
        mToolbar.setTitleTextAppearance(this, R.style.CommonRobotoBold);
    }

    public static String conversationNumberActive = "";

    @Override
    protected void onResume() {
        super.onResume();

        ConversationIntentData intentData = getConversationIntentData(getIntent());

        // set value from intent data to member variables.
        if (intentData.conversationId == Constants.NOT_FOUND_CONVERSATION_ID) {
            // Try to find conversation id from conversation number.
            conversationId = getConversationId(intentData.conversationNumber);
        } else {
            conversationId = intentData.conversationId;
        }

        mDisplayName = intentData.conversationDisplayName;
        conversationNumber = intentData.conversationNumber;

        if (intentData.message != null) {
            mNewMessage.setText(intentData.message);
        }

        if (conversationId != Constants.NOT_FOUND_CONVERSATION_ID) {
            loadMessages();
        } else {
            if (mDisplayName != null && mDisplayName.length() > 0) {
                setTitle(mDisplayName);
            } else {
                setTitle(conversationNumber);
            }
        }

        registerReceivers();

        // open keyboard
        mNewMessage.requestFocus();
    }

    private long getConversationId(String number) {
        long conversationId = Constants.NOT_FOUND_CONVERSATION_ID;
        if (number != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS},
                        Constants.REQUEST_CODE_READ_CONV_ID);
            } else {
                SmsProvider dao = new SmsProvider(this);
                conversationId = dao.getConversationId(number);
            }
        }
        return conversationId;
    }

    @Override
    protected void showPopup(boolean b) {
        // no op here
    }

    private void registerReceivers() {
        deliveryBroadcastReceiver.register(this, new IntentFilter(DELIVERED));
        sendBroadcastReceiver.register(this, new IntentFilter(SENT));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.conversation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_call) {
            //Log.e(TAG, mNewMessage.getText().toString());
            callContact(conversationNumber);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void callContact(String address) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, Constants.REQUEST_CODE_ASK_CALL_PERMISSION);
            return;
        }

        Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + address));
        startActivity(i);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_CODE_ASK_CALL_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Log.e(TAG, "call contact permission granted");
                    callContact(conversationNumber);
                }
                break;
            case Constants.REQUEST_CODE_READ_SMS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Log.e(TAG, "read sms permission granted");
                    loadMessages();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void loadMessages() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, Constants.REQUEST_CODE_READ_SMS);
            return;
        }

        SmsProvider dao = new SmsProvider(this);
        //dao.showRows(Uris.PHONES);
        if (conversationId == Constants.NOT_FOUND_CONVERSATION_ID) {
            conversationId = dao.getConversationId(conversationNumber);
        }
        List<Message> messages = dao.getConversationMessages(conversationId);
        MessagesAdapter adapter = new MessagesAdapter(messages, null, null, mUserProfileContainer);
        mRecyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        linearLayoutManager.setReverseLayout(true);

        if (messages.size() > 0) {
            conversationNumber = messages.get(0).getAddress();
            conversationNumberActive = conversationNumber;
            setTitle(messages.get(0).getDisplayName());
            markRead(messages);
        }
    }

    private void markRead(List<Message> messages) {
        SmsProvider dao = new SmsProvider(this);
        for (Message message : messages) {
            dao.markRead(message.getId());
        }
    }

    private void initControls() {
        initToolbarLocal();

        mRecyclerView = findViewById(R.id.messagesRecyclerView);
        mNewMessage = findViewById(R.id.new_message);

        sendBroadcastReceiver = new BaseBroadcastReceiver() {

            public void onReceive(Context arg0, Intent arg1) {
                Log.e(TAG, "SENT RESULT: " + getResultCode());

                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        //save new message
                        SmsProvider smsProvider = new SmsProvider(ConversationActivity.this);
                        if (savedMessageId == -1) {
                            savedMessageId = smsProvider.sendMessage(conversationNumber, message);
                        }

                        loadMessages();
                        mNewMessage.setText(null);
                        Toast.makeText(getBaseContext(), R.string.message_sent, Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), R.string.generic_failure, Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), R.string.no_service, Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), R.string.generic_failure, Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), R.string.radio_off, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        deliveryBroadcastReceiver = new BaseBroadcastReceiver() {
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), R.string.delivered, Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), R.string.not_delivered, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        Button mSendButton = findViewById(R.id.send_button);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mNewMessage.getText().toString();
                if (TextUtils.isEmpty(message)) {
                    Toast.makeText(ConversationActivity.this, R.string.empty_message,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                sendSMS(message);
                hideKeyboard();
            }
        });
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    @Override
    protected void onStop() {
        sendBroadcastReceiver.unregister(this);
        deliveryBroadcastReceiver.unregister(this);
        super.onStop();
    }

    private ConversationIntentData getConversationIntentData(Intent i) {
        if (i == null) return null;

        // Find conversationId
        long convId = Constants.NOT_FOUND_CONVERSATION_ID;
        if (i.getExtras() != null) {
            convId = i.getExtras().getLong(Constants.EXTRA_CONVERASTION_ID);
        }

        // Find conversation number.
        String conversationNumber = null;
        String message = null;
        if (i.getAction() != null &&
                (i.getAction().equals(Intent.ACTION_VIEW) ||
                        i.getAction().equals(Intent.ACTION_SENDTO))) {
            if (i.getData() != null) {
                conversationNumber = i.getData().getSchemeSpecificPart();
            }
        } else {
            if (i.getExtras() != null) {
                conversationNumber = i.getExtras().getString(KatsunaConstants.EXTRA_NUMBER);
                message = i.getExtras().getString(KatsunaConstants.MESSAGE);
            }
        }

        // Find display name.
        String conversationDisplayName = null;
        if (i.getExtras() != null) {
            conversationDisplayName = i.getExtras().getString(KatsunaConstants.EXTRA_DISPLAY_NAME);
        }

        return new ConversationIntentData(convId, conversationNumber, conversationDisplayName,
                message);
    }

    private class ConversationIntentData {
        final long conversationId;
        final String conversationNumber;
        final String conversationDisplayName;
        final String message;

        ConversationIntentData(long conversationId, String conversationNumber,
                               String conversationDisplayName, String message) {
            this.conversationId = conversationId;
            this.conversationNumber = conversationNumber;
            this.conversationDisplayName = conversationDisplayName;
            this.message = message;
        }
    }

    //---sends an SMS message to another device---
    private void sendSMS(final String message) {
        if (!simIsReady()) {
            Toast.makeText(ConversationActivity.this, R.string.not_active_sim,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Device.isDefaultApp(this)) {
            Toast.makeText(this, R.string.app_is_not_the_default_sms_handler, Toast.LENGTH_SHORT)
                    .show();
            Device.makeDefaultApp(this, 0);
            return;
        }

        this.message = message;

        savedMessageId = -1;

        PendingIntent sentPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> smsBodyParts = smsManager.divideMessage(message);
        ArrayList<PendingIntent> sentPendingIntents = new ArrayList<>();
        ArrayList<PendingIntent> deliveredPendingIntents = new ArrayList<>();

        Log.e(TAG, "sms parts: " + smsBodyParts.size());

        for (int i = 0; i < smsBodyParts.size(); i++) {
            sentPendingIntents.add(sentPendingIntent);
            deliveredPendingIntents.add(deliveredPendingIntent);
        }

        // Send a text based SMS
        smsManager.sendMultipartTextMessage(conversationNumber, null, smsBodyParts, sentPendingIntents, deliveredPendingIntents);
    }

    private boolean simIsReady() {
        TelephonyManager telMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (telMgr != null) {
            int simState = telMgr.getSimState();
            return simState == TelephonyManager.SIM_STATE_READY;
        }
        return false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent != null) {
            boolean reload = intent.getBooleanExtra("reload", false);
            if (reload) {
                loadMessages();
            }
        }
    }

}
