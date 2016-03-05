package gr.crystalogic.sms.ui.activities;

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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import gr.crystalogic.sms.R;
import gr.crystalogic.sms.dao.SmsDao;
import gr.crystalogic.sms.domain.Message;
import gr.crystalogic.sms.receivers.BaseBroadcastReceiver;
import gr.crystalogic.sms.ui.adapters.MessagesAdapter;
import gr.crystalogic.sms.utils.Constants;

public class ConversationActivity extends BaseActivity {

    private static final String TAG = "ConversationActivity";


    private RecyclerView mRecyclerView;
    private EditText mNewMessage;

    private BaseBroadcastReceiver sendBroadcastReceiver;
    private BaseBroadcastReceiver deliveryBroadcastReceiver;
    private final String SENT = "SMS_SENT";
    private final String DELIVERED = "SMS_DELIVERED";
    private String address;
    private String message;
    private long conversationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        initToolbar();
        initControls();

        //load decisions
        conversationId = getConversationIdFromIntent();

        loadMessages();
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
            callContact(address);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void callContact(String address) {
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
                    callContact(address);
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

        SmsDao dao = new SmsDao(this);
        //dao.showRows(Uris.PHONES);
        List<Message> messages = dao.getConversationMessages(conversationId);
        MessagesAdapter adapter = new MessagesAdapter(messages, null, null);
        mRecyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager ) mRecyclerView.getLayoutManager();
        linearLayoutManager.setReverseLayout(true);

        if (messages.size() > 0) {
            address = messages.get(0).getAddress();
            setTitle(messages.get(0).getDisplayName());
        }
    }

    private void initControls() {
        mRecyclerView = (RecyclerView) findViewById(R.id.messagesRecyclerView);
        mNewMessage = (EditText) findViewById(R.id.new_message);

        mNewMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    sendSMS(mNewMessage.getText().toString());
                }
                return false;
            }
        });

        sendBroadcastReceiver = new BaseBroadcastReceiver() {

            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        //save new message
                        SmsDao smsDao = new SmsDao(ConversationActivity.this);
                        smsDao.sendMessage(address, message);
                        loadMessages();
                        mNewMessage.setText(null);
                        Toast.makeText(getBaseContext(), "SMS Sent", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        deliveryBroadcastReceiver = new BaseBroadcastReceiver() {
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS Delivered", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        deliveryBroadcastReceiver.register(this, new IntentFilter(DELIVERED));
        sendBroadcastReceiver.register(this, new IntentFilter(SENT));
    }

    @Override
    protected void onStop() {
        sendBroadcastReceiver.unregister(this);
        deliveryBroadcastReceiver.unregister(this);
        super.onStop();
    }

    private long getConversationIdFromIntent() {
        Intent i = getIntent();
        return i.getExtras().getLong("conversationId");
    }

    //---sends an SMS message to another device---
    private void sendSMS(final String message) {
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(this, R.string.empty_message, Toast.LENGTH_SHORT).show();
            return;
        }
        this.message = message;
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(address, null, message, sentPI, deliveredPI);
    }
}
