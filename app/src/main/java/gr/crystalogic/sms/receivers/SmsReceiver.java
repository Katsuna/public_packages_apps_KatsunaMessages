 package gr.crystalogic.sms.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import gr.crystalogic.sms.ui.activities.MainActivity;

public class SmsReceiver extends BroadcastReceiver {

    private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
    private final String TAG = this.getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {

           /* The SMS-Messages are 'hiding' within the extras of the Intent. */
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                List<SmsMessage> messages = new ArrayList<>();

                Object[] smsextras = (Object[]) bundle.get("pdus");
                String format = intent.getStringExtra("format");

                for (Object smsextra : smsextras) {
                    SmsMessage message;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        message = SmsMessage.createFromPdu((byte[]) smsextra, format);
                    } else {
                        message = SmsMessage.createFromPdu((byte[]) smsextra);
                    }
                    messages.add(message);
                }
                StringBuilder sb = new StringBuilder();
                for (SmsMessage currentMessage : messages) {
                    sb.append("Received compressed SMSnFrom: ");
                                        /* Sender-Number */
                    sb.append(currentMessage.getDisplayOriginatingAddress());
                    sb.append("n----Message----n");
                                        /* Actual Message-Content */
                    sb.append(currentMessage.getDisplayMessageBody());
                }
                Log.i(TAG, "[SMSApp] onReceiveIntent: " + sb);

                /* Show the Notification containing the Message. */
                Toast.makeText(context, sb.toString(), Toast.LENGTH_LONG).show();

                /* Start the Main-Activity */
                Intent i = new Intent(context, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //i.setLaunchFlags(Intent.NEW_TASK_LAUNCH);
                context.startActivity(i);
            }
        }
    }
}
