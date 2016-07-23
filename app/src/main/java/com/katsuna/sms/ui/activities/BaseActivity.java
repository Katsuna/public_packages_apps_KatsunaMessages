package com.katsuna.sms.ui.activities;

import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.katsuna.commons.KatsunaConstants;
import com.katsuna.commons.entities.Profile;
import com.katsuna.commons.entities.ProfileType;
import com.katsuna.sms.R;

abstract public class BaseActivity extends AppCompatActivity {

    Toolbar mToolbar;

    void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    Profile getProfileFromPreferences() {
        Profile profile = new Profile();
        int profileType = PreferenceManager.getDefaultSharedPreferences(this)
                .getInt(KatsunaConstants.PROFILE_KEY, ProfileType.INTERMEDIATE.getNumVal());

        profile.setType(profileType);

        return profile;
    }

    Profile mProfile;

    boolean setSelectedProfile(Profile profile) {
        boolean profileChanged = false;
        if (mProfile == null) {
            mProfile = profile;
            profileChanged = true;
        } else {
            if (mProfile.getType() != profile.getType()) {
                profileChanged = true;
                mProfile.setType(profile.getType());
            }
        }
        return profileChanged;
    }
}
