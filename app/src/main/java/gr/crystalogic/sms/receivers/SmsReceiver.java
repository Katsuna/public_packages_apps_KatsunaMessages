package gr.crystalogic.sms.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import gr.crystalogic.sms.domain.Message;
import gr.crystalogic.sms.providers.SmsProvider;
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

        //acquire wakelock
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        wakelock.acquire();

        Log.e(TAG, "onReceive: intent-ACTION=" + intent.getAction());
        Message message = getMessage(intent);

        if (message != null) {
            SmsProvider dao = new SmsProvider(context);
            dao.receiveMessage(message);
            long conversationId = dao.getConversationId(message);
            showConversation(context, conversationId);
        }

        //release wakelock
        wakelock.release();
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

                assert smsextras != null;
                for (Object smsextra : smsextras) {
                    SmsMessage smsMessage;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        smsMessage = SmsMessage.createFromPdu((byte[]) smsextra, format);
                    } else {
                        //noinspection deprecation
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
        playRingtone(context);

        Intent i = new Intent(context, ConversationActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("conversationId", conversationId);
        context.startActivity(i);
    }

    private void playRingtone(Context context) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context, notification);
            r.play();
        } catch (Exception ex) {
            Log.e(TAG, "Exception: " + ex);
        }
    }
}
