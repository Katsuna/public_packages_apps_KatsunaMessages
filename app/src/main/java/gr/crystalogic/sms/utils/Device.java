package gr.crystalogic.sms.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import gr.crystalogic.sms.BuildConfig;

public class Device {

    private static final String TAG = "DEVICE";

    public static boolean isDefaultApp(final Context context) {
        // there is no default sms app before android 4.4
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return true;
        }

        try {
            // check if this is the default sms app.
            // If the device doesn't support Telephony.Sms (i.e. tablet) getDefaultSmsPackage() will
            // be null.
            final String smsPackage = Telephony.Sms.getDefaultSmsPackage(context);
            return smsPackage == null || smsPackage.equals(BuildConfig.APPLICATION_ID);
        } catch (SecurityException e) {
            // some samsung devices/tablets want permission GET_TASKS o.O
            Log.e(TAG, "failed to query default SMS app", e);
            return true;
        }
    }

    public static boolean hasAllPermissions(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (!hasPermission(context, permission)) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasPermission(final Context context, final String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermissions(final Activity activity, final String[] permissions,
                                          final int requestCode) {
        String[] notGrantedPermissions = getNotGrantedPermissions(activity, permissions);

        if (notGrantedPermissions.length > 0) {
            ActivityCompat.requestPermissions(activity, notGrantedPermissions, requestCode);
        }
    }

    private static String[] getNotGrantedPermissions(Context context, String[] permissions) {
        List<String> notGrantedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (!hasPermission(context, permission)) {
                notGrantedPermissions.add(permission);
            }
        }
        return notGrantedPermissions.toArray(new String[notGrantedPermissions.size()]);
    }

}
