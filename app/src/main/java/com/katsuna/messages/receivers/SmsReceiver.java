/**
* Copyright (C) 2020 Manos Saratsis
*
* This file is part of Katsuna.
*
* Katsuna is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Katsuna is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Katsuna.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.katsuna.messages.receivers;

import android.app.NotificationChannel;
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
import android.text.TextUtils;
import android.util.Log;

import com.katsuna.commons.domain.Contact;
import com.katsuna.commons.utils.KatsunaUtils;
import com.katsuna.messages.R;
import com.katsuna.messages.domain.Message;
import com.katsuna.messages.providers.SmsProvider;
import com.katsuna.messages.ui.activities.ConversationActivity;
import com.katsuna.messages.utils.ActivityVisibility;
import com.katsuna.messages.utils.Constants;
import com.katsuna.messages.utils.Device;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SmsReceiver extends BroadcastReceiver {

    private static final String ACTION = "android.provider.Telephony.SMS_DELIVER";
    private final String TAG = this.getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Objects.equals(intent.getAction(), ACTION) || !Device.isDefaultApp(context)) {
            return;
        }

        Log.d(TAG, "onReceive: intent-ACTION=" + intent.getAction());
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
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);

                playRingtone(context);
            } else {
                sendNotification(context, conversationId, message);
            }
            wakeUp(context);
        }
    }

    @SuppressWarnings("deprecation")
    private void wakeUp(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (pm != null) {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                    | PowerManager.ACQUIRE_CAUSES_WAKEUP, "KatsunaMessages:WakeUpTag");
            long timeout = 30000;
            wl.acquire(timeout);
        }
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
        SmsProvider provider = new SmsProvider(context);
        Contact contact = provider.getContactByAddress(message.getAddress());
        String contentText;
        if (contact != null && !TextUtils.isEmpty(contact.getDisplayName())) {
            contentText = contact.getDisplayName();
        } else {
            contentText = message.getDisplayName();
        }
        contentText += ": " + message.getBody();

        //send the notification
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            String channelId = "katsuna-messages-channel-id";
            String channelName = "katsuna-messages-channel";
            if (Build.VERSION.SDK_INT >=  Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId, channelName,
                        NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription("Channel description");
                notificationManager.createNotificationChannel(channel);
            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);

            builder.setSmallIcon(R.drawable.ic_sms_white_18dp)
                    .setContentTitle(context.getResources().getString(R.string.new_message))
                    .setContentText(contentText)
                    .setAutoCancel(true)
                    .setContentIntent(resultPendingIntent);

            String convIdStr = String.valueOf(conversationId);
            notificationManager.notify(Integer.parseInt(convIdStr), builder.build());
        }
    }

    private void playRingtone(Context context) {
        if (KatsunaUtils.katsunaOsDetected()) {
            // no need to play ringtone
            return;
        }

        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context, notification);
            r.play();
        } catch (Exception ex) {
            Log.e(TAG, "Exception: " + ex);
        }
    }
}
