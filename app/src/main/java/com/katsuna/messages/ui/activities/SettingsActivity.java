package com.katsuna.messages.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.katsuna.commons.entities.PreferenceKey;
import com.katsuna.commons.entities.ProfileType;
import com.katsuna.commons.utils.SettingsManager;
import com.katsuna.messages.R;
import com.katsuna.messages.utils.Constants;
import com.katsuna.messages.utils.Device;

public class SettingsActivity extends AppCompatActivity {

    private TextView defaultSmsTextView;
    private Button defaultSmsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initToolbar();
        initControls();
    }

    private void initToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        activateControls();
    }

    private void initControls() {
        defaultSmsTextView = (TextView) findViewById(R.id.default_sms_status);
        defaultSmsButton = (Button) findViewById(R.id.default_sms_button);
        defaultSmsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Device.makeDefaultApp(SettingsActivity.this, 0);
            }
        });

        Spinner mProfileTypes = (Spinner) findViewById(R.id.profiles);
        String profileSetting = SettingsManager.readSetting(SettingsActivity.this,
                PreferenceKey.OPTICAL_SIZE_PROFILE,
                String.valueOf(ProfileType.INTERMEDIATE.getNumVal()));
        mProfileTypes.setSelection(Integer.parseInt(profileSetting));
        mProfileTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SettingsManager.setSetting(SettingsActivity.this,
                        PreferenceKey.OPTICAL_SIZE_PROFILE, String.valueOf(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void activateControls() {
        if (Device.isDefaultApp(this)) {
            defaultSmsTextView.setText(R.string.app_is_default_sms_handler);
            defaultSmsButton.setVisibility(View.GONE);
        } else {
            defaultSmsTextView.setText(R.string.app_is_not_the_default_sms_handler);
            defaultSmsButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.DEF_SMS_REQ_CODE:
                activateControls();
                break;
        }
    }

}
