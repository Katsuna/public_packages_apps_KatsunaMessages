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
package com.katsuna.messages;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.katsuna.messages.ui.activities.ConversationActivity;
import com.katsuna.messages.utils.ActivityVisibility;

public class MessagesApplication extends Application implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = "MessagesApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        // Register to be notified of activity state changes
        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        // no op
    }

    @Override
    public void onActivityStarted(Activity activity) {
        // no op
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (activity instanceof ConversationActivity) {
            ActivityVisibility.isConversationActivityVisible = true;
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (activity instanceof ConversationActivity) {
            ActivityVisibility.isConversationActivityVisible = false;
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        // no op
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        // no op
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        // no op
    }
}
