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
package com.katsuna.messages.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Telephony;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class Device {

    private static final String TAG = "DEVICE";

    public static boolean isDefaultApp(final Context context) {
        // there is no default messages app before android 4.4

        try {
            // check if this is the default messages app.
            // If the device doesn't support Telephony.Sms (i.e. tablet) getDefaultSmsPackage() will
            // be null.
            final String smsPackage = Telephony.Sms.getDefaultSmsPackage(context);
            return smsPackage == null || smsPackage.equals(context.getPackageName());
        } catch (SecurityException e) {
            // some samsung devices/tablets want permission GET_TASKS o.O
            Log.e(TAG, "failed to query default SMS app", e);
            return true;
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void makeDefaultApp(final Activity activity, int requestCode) {
        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, activity.getPackageName());
        activity.startActivityForResult(intent, requestCode);
    }

    public static boolean hasAllPermissions(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (permissionMissing(context, permission)) {
                return false;
            }
        }
        return true;
    }

    private static boolean permissionMissing(final Context context, final String permission) {
        return !(ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED);
    }

}
