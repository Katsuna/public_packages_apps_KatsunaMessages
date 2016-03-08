package gr.crystalogic.sms.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import gr.crystalogic.sms.dao.SmsDao;
import gr.crystalogic.sms.domain.Message;
import gr.crystalogic.sms.ui.activities.ConversationActivity;
import gr.crystalogic.sms.utils.Device;

public class SmsReceiver extends BroadcastReceiver {

    private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
    private final String TAG = this.getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Device.isDefaultApp(context)) {
            return;
        }

        Log.e(TAG, "onReceive: intent-ACTION=" + intent.getAction());
        Message message = getMessage(intent);

        if (message != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                new SaveMessageTask(context).execute(message);
            } else {
                SmsDao dao = new SmsDao(context);
                dao.receiveMessage(message);
                long conversationId = dao.getConversationId(message);
                showConversation(context, conversationId);
            }
        }
    }

    private Message getMessage(Intent intent) {
        Message message = null;

        if (intent.getAction().equals(ACTION)) {

            message = new Message();

            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                List<SmsMessage> smsMessages = new ArrayList<>();

                Object[] smsextras = (Object[]) bundle.get("pdus");
                String format = intent.getStringExtra("format");

                for (Object smsextra : smsextras) {
                    SmsMessage smsMessage;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        smsMessage = SmsMessage.createFromPdu((byte[]) smsextra, format);
                    } else {
                        smsMessage = SmsMessage.createFromPdu((byte[]) smsextra);
                    }
                    smsMessages.add(smsMessage);
                }

                message.setAddress(smsMessages.get(0).getDisplayOriginatingAddress());
                message.setDate(smsMessages.get(0).getTimestampMillis());

                StringBuilder sb = new StringBuilder();
                for (SmsMessage currentMessage : smsMessages) {
                    sb.append(currentMessage.getDisplayMessageBody());
                }
                message.setBody(sb.toString());
            }
        }
        return message;
    }

    private void showConversation(Context context, long conversationId) {
        Intent i = new Intent(context, ConversationActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("conversationId", conversationId);
        context.startActivity(i);
    }

    //use this for api < 19
    private class SaveMessageTask extends AsyncTask<Message, Void, Long> {

        private Context mContext;

        public SaveMessageTask(Context context) {
            mContext = context;
        }

        @Override
        protected Long doInBackground(Message... params) {
            Message message = params[0];

            long output;

            try {
                //if you have multiple receivers don't save again
                //check after 5 secs to allow other receivers to save first
                Thread.sleep(5000);

                SmsDao dao = new SmsDao(mContext);
                output = dao.getConversationId(message);
                if (output == -1) {
                    Log.e(TAG, "conversation not found");
                    dao.receiveMessage(message);
                    output = dao.getConversationId(message);
                    Log.e(TAG, "conversation found after insert: " + output);
                } else {
                    Log.e(TAG, "conversation found before insert: " + output);
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return output;
        }

        @Override
        protected void onPostExecute(Long conversationId) {
            showConversation(mContext, conversationId);
        }
    }
}
