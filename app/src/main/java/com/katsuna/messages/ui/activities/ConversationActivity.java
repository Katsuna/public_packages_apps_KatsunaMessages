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
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.katsuna.commons.ui.KatsunaActivity;
import com.katsuna.messages.R;
import com.katsuna.messages.domain.Message;
import com.katsuna.messages.providers.SmsProvider;
import com.katsuna.messages.receivers.BaseBroadcastReceiver;
import com.katsuna.messages.ui.adapters.MessagesAdapter;
import com.katsuna.messages.utils.Constants;

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

    @Override
    protected void onResume() {
        super.onResume();

        conversationId = getConversationIdFromIntent();

        if (conversationId != -1) {
            loadMessages();
        } else {
            if (mDisplayName != null && mDisplayName.length() > 0) {
                setTitle(mDisplayName);
            } else {
                setTitle(conversationNumber);
            }
        }

        //this is needed to open will screen is locked
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        registerReceivers();
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
        if (conversationId == -1) {
            conversationId = dao.getConversationId(conversationNumber);
        }
        List<Message> messages = dao.getConversationMessages(conversationId);
        MessagesAdapter adapter = new MessagesAdapter(messages, null, null, mUserProfileContainer);
        mRecyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        linearLayoutManager.setReverseLayout(true);

        if (messages.size() > 0) {
            conversationNumber = messages.get(0).getAddress();
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

        mRecyclerView = (RecyclerView) findViewById(R.id.messagesRecyclerView);
        mNewMessage = (EditText) findViewById(R.id.new_message);

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

        Button mSendButton = (Button) findViewById(R.id.send_button);
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
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onStop() {
        sendBroadcastReceiver.unregister(this);
        deliveryBroadcastReceiver.unregister(this);
        super.onStop();
    }

    private long getConversationIdFromIntent() {
        Intent i = getIntent();

        long convId = getConversationIdFromIntent(i);
        if (convId != 0) {
            return convId;
        }

        // Find conversation number.
        if (i.getAction() != null && i.getAction().equals(Intent.ACTION_VIEW)) {
            conversationNumber = i.getData().getSchemeSpecificPart();
        } else {
            if (i.getExtras() != null) {
                conversationNumber = i.getExtras().getString(Constants.EXTRA_NUMBER);
            }
        }

        if (i.getExtras() != null) {
            mDisplayName = i.getExtras().getString(Constants.EXTRA_DISPLAY_NAME);
        }

        // Try to find conversation id from conversation number.
        if (conversationNumber != null) {
            SmsProvider dao = new SmsProvider(this);
            convId = dao.getConversationId(conversationNumber);
        }

        return convId;
    }

    private long getConversationIdFromIntent(Intent i) {
        long convId = 0;
        if (i != null && i.getExtras() != null) {
            convId = i.getExtras().getLong(Constants.EXTRA_CONVERASTION_ID);
        }
        return convId;
    }

    //---sends an SMS message to another device---
    private void sendSMS(final String message) {
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

}
