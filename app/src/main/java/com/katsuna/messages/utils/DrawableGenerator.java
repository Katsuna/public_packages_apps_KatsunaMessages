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

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;

import com.katsuna.commons.entities.ColorProfile;
import com.katsuna.commons.utils.DrawUtils;
import com.katsuna.messages.R;

public class DrawableGenerator {

    public static Drawable getItemTypeDrawable(Context ctx, int conv_status, int color,
                                               ColorProfile colorProfile) {

        int iconColorId = R.color.common_black87;
        // calc color and icon
        int iconId = R.drawable.ic_message_read;

        if (conv_status == ConversationStatus.SENT) {
            iconId = R.drawable.ic_message_sent;
            iconColorId = R.color.common_white;
        } else if (conv_status == ConversationStatus.READ) {
            iconId = R.drawable.ic_message_read;
        } else if (conv_status == ConversationStatus.UNREAD) {
            if (colorProfile == ColorProfile.CONTRAST) {
                iconColorId = R.color.common_white;
            }
            iconId = R.drawable.ic_message_unread;
        }

        // adjust circle
        GradientDrawable circleDrawable =
                (GradientDrawable) ctx.getDrawable(R.drawable.common_circle_black);
        if (circleDrawable != null) {
            circleDrawable.setColor(color);
        }

        // adjust icon
        Drawable icon = ctx.getDrawable(iconId);
        int iconColor = ContextCompat.getColor(ctx, iconColorId);
        DrawUtils.setColor(icon, iconColor);

        // compose layers
        Drawable[] layers = {circleDrawable, icon};
        LayerDrawable layerDrawable = new LayerDrawable(layers);

        int diff = ctx.getResources().getDimensionPixelSize(R.dimen.conv_type_icon_size_diff);

        layerDrawable.setLayerInset(1, diff, diff, diff, diff);

        return layerDrawable;
    }

}

