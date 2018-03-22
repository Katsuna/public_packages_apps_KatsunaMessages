package com.katsuna.messages.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.katsuna.commons.ui.ContactsActivity;
import com.katsuna.commons.utils.KatsunaAlertBuilder;
import com.katsuna.messages.R;

import static com.katsuna.commons.entities.KatsunaConstants.EXTRA_NUMBER;

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
        mFab1 = findViewById(R.id.search_fab);
        mFab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFabToolbar(true);
            }
        });

        mFab2 = findViewById(R.id.dial_fab);
        mFab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                dialNumber();
            }
        });
    }

    @Override
    protected void setupPopupButtons() {
        mButtonsContainer1 = findViewById(com.katsuna.commons.R.id.search_buttons_container);
        mButtonsContainer2 = findViewById(com.katsuna.commons.R.id.dial_buttons_container);
        mButtonsContainer2.setVisibility(View.VISIBLE);

        mPopupButton1 = findViewById(R.id.search_button);
        mPopupButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFabToolbar(true);
            }
        });

        mPopupButton2 = findViewById(R.id.dial_button);
        mPopupButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialNumber();
            }
        });
    }

    private void dialNumber() {
        KatsunaAlertBuilder builder = new KatsunaAlertBuilder(this);
        builder.setTitle(getString(R.string.common_dial_instruction));
        builder.setView(R.layout.common_katsuna_alert);
        builder.setTextVisibility(View.VISIBLE);
        builder.setTextInputType(InputType.TYPE_CLASS_PHONE);
        builder.setUserProfile(mUserProfileContainer.getActiveUserProfile());
        builder.setOkListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        builder.setTextSelected(new KatsunaAlertBuilder.KatsunaAlertText() {
            @Override
            public void textSelected(String number) {
                if (!TextUtils.isEmpty(number)) {
                    startMessageActivity(number);
                }
            }
        });

        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void startMessageActivity(String number) {
        Intent i = new Intent(this, ConversationActivity.class);
        i.putExtra(EXTRA_NUMBER, number);
        startActivity(i);
        finish();
    }

}
