package gr.crystalogic.sms.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public abstract class BaseBroadcastReceiver extends BroadcastReceiver {
    private boolean isRegistered;

    public Intent register(Context context, IntentFilter filter) {
        isRegistered = true;
        return context.registerReceiver(this, filter);
    }

    public boolean unregister(Context context) {
        if (isRegistered) {
            context.unregisterReceiver(this);
            isRegistered = false;
            return true;
        }
        return false;
    }
}