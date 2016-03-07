package gr.crystalogic.sms.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import gr.crystalogic.sms.dao.SmsDao;
import gr.crystalogic.sms.domain.Message;
import gr.crystalogic.sms.ui.activities.MainActivity;
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
            SmsDao dao = new SmsDao(context);
            dao.receiveMessage(message);
            showConversation(context, message);
        }
    }

    private Message getMessage(Intent intent) {
        Message message = null;

        if (intent.getAction().equals(ACTION)) {

            message = new Message();

           /* The SMS-Messages are 'hiding' within the extras of the Intent. */
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

    private void showConversation(Context context, Message message) {
        Intent i = new Intent(context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //i.setLaunchFlags(Intent.NEW_TASK_LAUNCH);
        //i.putExtra("conversationId", conversationId);
        context.startActivity(i);
    }
}
