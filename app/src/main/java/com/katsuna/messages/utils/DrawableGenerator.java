package com.katsuna.messages.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;

import com.katsuna.commons.utils.DrawUtils;
import com.katsuna.messages.R;

public class DrawableGenerator {

    public static Drawable getItemTypeDrawable(Context ctx, int conv_status) {

        // calc color and icon
        int circleColorId = R.color.priority_one;
        int iconId = R.drawable.ic_message_read;

        if (conv_status == ConversationStatus.SENT) {
            circleColorId = R.color.priority_two;
            iconId = R.drawable.ic_message_sent;
        } else if (conv_status == ConversationStatus.READ) {
            circleColorId = R.color.priority_one;
            iconId = R.drawable.ic_message_read;
        } else if (conv_status == ConversationStatus.UNREAD) {
            circleColorId = R.color.priority_three;
            iconId = R.drawable.ic_message_unread;
        }

        // adjust circle
        GradientDrawable circleDrawable =
                (GradientDrawable) ctx.getDrawable(R.drawable.common_circle_black);
        if (circleDrawable != null) {
            circleDrawable.setColor(ContextCompat.getColor(ctx, circleColorId));
        }

        // adjust icon
        Drawable icon = ctx.getDrawable(iconId);
        int black87 = ContextCompat.getColor(ctx, com.katsuna.commons.R.color.common_black87);
        DrawUtils.setColor(icon, black87);

        // compose layers
        Drawable[] layers = {circleDrawable, icon};
        LayerDrawable layerDrawable = new LayerDrawable(layers);

        int diff = ctx.getResources().getDimensionPixelSize(R.dimen.conv_type_icon_size_diff);

        layerDrawable.setLayerInset(1, diff, diff, diff, diff);

        return layerDrawable;
    }

}

