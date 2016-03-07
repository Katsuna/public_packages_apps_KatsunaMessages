package gr.crystalogic.sms.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import gr.crystalogic.sms.utils.Device;

public class MmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Device.isDefaultApp(context)) {

        }
    }
}
