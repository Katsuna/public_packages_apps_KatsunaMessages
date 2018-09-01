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
