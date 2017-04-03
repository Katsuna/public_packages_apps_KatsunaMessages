package com.katsuna.messages.ui.viewholders;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.katsuna.commons.entities.ColorProfileKey;
import com.katsuna.commons.entities.SizeProfile;
import com.katsuna.commons.entities.UserProfileContainer;
import com.katsuna.commons.utils.ColorCalc;
import com.katsuna.commons.utils.DateFormatter;
import com.katsuna.messages.R;
import com.katsuna.messages.domain.Message;
import com.katsuna.messages.providers.metadata.MessageType;
import com.squareup.picasso.Picasso;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    private final TextView mDateTime;
    private final TextView mBody;
    private final ImageView mPhoto;
    private final UserProfileContainer mUserProfileContainer;
    private final View mMessageContainer;

    public MessageViewHolder(View itemView, UserProfileContainer userProfileContainer) {
        super(itemView);
        mDateTime = (TextView) itemView.findViewById(R.id.dateTime);
        mBody = (TextView) itemView.findViewById(R.id.body);
        mPhoto = (ImageView) itemView.findViewById(R.id.photo);
        mMessageContainer = itemView.findViewById(R.id.message_container);
        mUserProfileContainer = userProfileContainer;
        adjustProfile();
    }

    private void adjustProfile() {
        SizeProfile opticalSizeProfile = mUserProfileContainer.getOpticalSizeProfile();

        if (opticalSizeProfile != null) {
            int size = itemView.getResources()
                    .getDimensionPixelSize(R.dimen.common_contact_photo_size_intemediate);
            int fontSize = itemView.getResources().getDimensionPixelSize(R.dimen.font_size_intemediate);
            if (opticalSizeProfile == SizeProfile.ADVANCED) {
                size = itemView.getResources()
                        .getDimensionPixelSize(R.dimen.common_contact_photo_size_advanced);
                fontSize = itemView.getResources().getDimensionPixelSize(R.dimen.font_size_advanced);
            } else if (opticalSizeProfile == SizeProfile.SIMPLE) {
                size = itemView.getResources()
                        .getDimensionPixelSize(R.dimen.common_contact_photo_size_simple);
                fontSize = itemView.getResources().getDimensionPixelSize(R.dimen.font_size_simple);
            }

            //adjust photo
            ViewGroup.LayoutParams layoutParams = mPhoto.getLayoutParams();
            layoutParams.height = size;
            layoutParams.width = size;

            //adjust text body
            mBody.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
        }
    }

    public void bind(Message message) {

        if (message.getType() == MessageType.OUTGOING) {
            mPhoto.setImageBitmap(null);

            // adjust dialog background
            int bgColor = ColorCalc.getColor(itemView.getContext(),
                    ColorProfileKey.MAIN_COLOR_MEDIUM, mUserProfileContainer.getColorProfile());

            Drawable drawable = ContextCompat.getDrawable(itemView.getContext(),
                    R.drawable.dialog_right);
            drawable.setColorFilter(new PorterDuffColorFilter(bgColor, PorterDuff.Mode.SRC_ATOP));

            mMessageContainer.setBackground(drawable);
        } else {
            if (message.getContact() != null) {
                //load photo
                Picasso.with(itemView.getContext())
                        .load(message.getContact().getPhotoUri())
                        .fit()
                        .into(mPhoto);
            }

            // adjust dialog background
            int bgColor = ColorCalc.getColor(itemView.getContext(),
                    ColorProfileKey.ACCENT1_COLOR, mUserProfileContainer.getColorProfile());

            Drawable drawable = ContextCompat.getDrawable(itemView.getContext(),
                    R.drawable.dialog_left);
            drawable.setColorFilter(new PorterDuffColorFilter(bgColor, PorterDuff.Mode.SRC_ATOP));

            mMessageContainer.setBackground(drawable);
        }

        mDateTime.setText(DateFormatter.format(message.getDate()));
        mBody.setText(message.getBody());
    }
}
