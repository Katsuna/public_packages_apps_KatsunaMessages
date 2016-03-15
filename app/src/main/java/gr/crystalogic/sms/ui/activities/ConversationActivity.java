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
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

import java.util.ArrayList;
import java.util.List;

import gr.crystalogic.sms.R;
import gr.crystalogic.sms.dao.SmsDao;
import gr.crystalogic.sms.domain.Message;
import gr.crystalogic.sms.receivers.BaseBroadcastReceiver;
import gr.crystalogic.sms.ui.adapters.MessagesAdapter;
import gr.crystalogic.sms.utils.Constants;

public class ConversationActivity extends BaseActivity implements EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {

    private static final String TAG = "ConversationActivity";
    private final String SENT = "SMS_SENT";
    private final String DELIVERED = "SMS_DELIVERED";
    private RecyclerView mRecyclerView;
    private EmojiconEditText mNewMessage;
    private BaseBroadcastReceiver sendBroadcastReceiver;
    private BaseBroadcastReceiver deliveryBroadcastReceiver;
    private String message;
    private long conversationId;
    private String conversationNumber;
    private FrameLayout mEmojiContainer;
    private long savedMessageId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        initToolbar();
        initControls();

        //load decisions
        conversationId = getConversationIdFromIntent();

        if (conversationId != -1) {
            loadMessages();
        } else {
            setTitle(conversationNumber);
        }

        //this is needed to open will screen is locked
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setEmojiconFragment(false);
    }

    private void setEmojiconFragment(boolean useSystemDefault) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojicons_container, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
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

        SmsDao dao = new SmsDao(this);
        //dao.showRows(Uris.PHONES);
        if (conversationId == -1) {
            conversationId = dao.getConversationId(conversationNumber);
        }
        List<Message> messages = dao.getConversationMessages(conversationId);
        MessagesAdapter adapter = new MessagesAdapter(messages, null, null);
        mRecyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        linearLayoutManager.setReverseLayout(true);

        if (messages.size() > 0) {
            conversationNumber = messages.get(0).getAddress();
            setTitle(messages.get(0).getDisplayName());
        }
    }

    private void initControls() {
        mRecyclerView = (RecyclerView) findViewById(R.id.messagesRecyclerView);
        mNewMessage = (EmojiconEditText) findViewById(R.id.new_message);

        mNewMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    sendSMS(mNewMessage.getText().toString());
                }
                return false;
            }
        });

        mEmojiContainer = (FrameLayout) findViewById(R.id.emojicons_container);

        Button mEmojiButton = (Button) findViewById(R.id.showEmojis);
        mEmojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmojiContainer.getVisibility() == View.VISIBLE) {
                    mEmojiContainer.setVisibility(View.GONE);
                } else {
                    mEmojiContainer.setVisibility(View.VISIBLE);
                }
            }
        });

        sendBroadcastReceiver = new BaseBroadcastReceiver() {

            public void onReceive(Context arg0, Intent arg1) {
                Log.e(TAG, "SENT RESULT: " + getResultCode());

                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        //save new message
                        SmsDao smsDao = new SmsDao(ConversationActivity.this);
                        if (savedMessageId == -1) {
                            savedMessageId = smsDao.sendMessage(conversationNumber, message);
                        }

                        loadMessages();
                        mNewMessage.setText(null);
                        mEmojiContainer.setVisibility(View.GONE);
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
        conversationNumber = i.getExtras().getString("conversationNumber");
        if (conversationNumber == null) {
            return i.getExtras().getLong("conversationId");
        } else {
            SmsDao dao = new SmsDao(this);
            return dao.getConversationId(conversationNumber);
        }
    }

    //---sends an SMS message to another device---
    private void sendSMS(final String message) {
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(this, R.string.empty_message, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(mNewMessage, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(mNewMessage);
    }
}
