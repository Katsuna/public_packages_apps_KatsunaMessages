package com.katsuna.messages.ui.activities;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.katsuna.commons.ui.ContactsActivity;
import com.katsuna.messages.R;

public class SelectContactActivity extends ContactsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAnalytics.getInstance(this);
    }

    @Override
    protected void setupDrawerLayout() {
        // no op here
    }

    @Override
    protected void setupToolbar() {
        initToolbar(R.drawable.common_ic_close_black54_24dp);
    }

    @Override
    protected void createContact() {
        // no op here
    }

    @Override
    protected void addNumberToExistingContact(long contactId, String addNumberToExistingContact) {
        // no op here
    }

    @Override
    public void editContact(long contactId) {
        // no op here
    }

    @Override
    protected void setupFab() {
        mButtonsContainer1 = findViewById(com.katsuna.commons.R.id.search_buttons_container);

        mFab1 = findViewById(R.id.search_fab);
        mFab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFabToolbar(true);
            }
        });
    }

}
