package com.katsuna.messages.receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.SmsMessage;
import android.util.Log;

import com.katsuna.messages.R;
import com.katsuna.messages.domain.Message;
import com.katsuna.messages.providers.SmsProvider;
import com.katsuna.messages.ui.activities.ConversationActivity;
import com.katsuna.messages.utils.ActivityVisibility;
import com.katsuna.messages.utils.Constants;
import com.katsuna.messages.utils.Device;

import java.util.ArrayList;
import java.util.List;

public class SmsReceiver extends BroadcastReceiver {

    private static final String ACTION = "android.provider.Telephony.SMS_DELIVER";
    private final String TAG = this.getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.getAction().equals(ACTION) || !Device.isDefaultApp(context)) {
            return;
        }

        Log.e(TAG, "onReceive: intent-ACTION=" + intent.getAction());
        Message message = getMessage(intent);

        if (message != null) {
            SmsProvider dao = new SmsProvider(context);
            dao.receiveMessage(message);
            long conversationId = dao.getConversationId(message);
            if (ActivityVisibility.isConversationActivityVisible &&
                    message.getAddress().equals(ConversationActivity.conversationNumberActive)) {
                // refresh conversation activity
                Intent i = new Intent(context, ConversationActivity.class);
                i.putExtra("reload", true);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(i);

                playRingtone(context);
            } else {
                sendNotification(context, conversationId, message);
            }
            wakeUp(context);
        }
    }

    private void wakeUp(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        long timeout = 30000;
        wl.acquire(timeout);
    }

    private Message getMessage(Intent intent) {
        Message message = new Message();

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

        return message;
    }

    private void sendNotification(Context context, long conversationId, Message message) {
        playRingtone(context);

        //create a pending intent with stack
        Intent resultIntent = new Intent(context, ConversationActivity.class);
        resultIntent.putExtra(Constants.EXTRA_CONVERASTION_ID, conversationId);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(ConversationActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        // Gets a PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        //build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_sms_white_18dp)
                .setContentTitle(context.getResources().getString(R.string.new_message))
                .setContentText(message.getDisplayName() + ": " + message.getBody())
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);

        //send the notification
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String convIdStr = String.valueOf(conversationId);
        mNotificationManager.notify(Integer.parseInt(convIdStr), builder.build());
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
