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
package com.katsuna.messages.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.katsuna.commons.entities.ColorProfile;
import com.katsuna.commons.entities.ColorProfileKeyV2;
import com.katsuna.commons.entities.SizeProfile;
import com.katsuna.commons.ui.SettingsActivityBase;
import com.katsuna.commons.utils.ColorAdjusterV2;
import com.katsuna.commons.utils.ColorCalcV2;
import com.katsuna.commons.utils.ProfileReader;
import com.katsuna.commons.utils.SizeAdjuster;
import com.katsuna.messages.R;
import com.katsuna.messages.utils.Constants;
import com.katsuna.messages.utils.Device;

public class SettingsActivity extends SettingsActivityBase {

    private TextView defaultSmsTextView;
    private CardView mDefaultAppCard;
    private View mDefaultAppCardInner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initControls();
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyProfiles();
        activateControls();
    }

    @Override
    protected void applyColorProfile(ColorProfile colorProfile) {
        ColorProfile profile = colorProfile;
        if (colorProfile == ColorProfile.AUTO) {
            if (mUserProfileContainer.hasKatsunaServices()) {
                profile = ProfileReader.getUserProfileFromKatsunaServices(this).colorProfile;
            } else {
                profile = ColorProfile.COLOR_IMPAIREMENT;
            }
        }

        // color profiles
        int primary2 = ColorCalcV2.getColor(this, ColorProfileKeyV2.PRIMARY_COLOR_2, profile);
        ColorAdjusterV2.setTextViewDrawableColor(defaultSmsTextView, primary2);

        int primaryGrey1 = ColorCalcV2.getColor(this, ColorProfileKeyV2.PRIMARY_GREY_1, profile);

        int secondaryGrey2 = ColorCalcV2.getColor(this, ColorProfileKeyV2.SECONDARY_GREY_2, profile);

        mDefaultAppCard.setCardBackgroundColor(primaryGrey1);
        mDefaultAppCardInner.setBackgroundColor(secondaryGrey2);
    }

    @Override
    protected void applySizeProfile(SizeProfile sizeProfile) {
        ViewGroup topViewGroup = findViewById(android.R.id.content);
        SizeAdjuster.applySizeProfile(this, topViewGroup, sizeProfile);
    }

    protected void initControls() {
        super.initControls();
        initToolbar();

        mScrollViewContainer = findViewById(R.id.scroll_view_container);

        defaultSmsTextView = findViewById(R.id.default_sms_status);
        mDefaultAppCard = findViewById(R.id.default_app_card);
        mDefaultAppCardInner = findViewById(R.id.default_app_card_inner);
    }

    private void activateControls() {
        if (Device.isDefaultApp(this)) {
            defaultSmsTextView.setText(R.string.app_is_default_sms_handler);
            defaultSmsTextView.setOnClickListener(null);
        } else {
            defaultSmsTextView.setText(R.string.set_as_default);
            defaultSmsTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Device.makeDefaultApp(SettingsActivity.this, 0);
                }
            });
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
