package com.katsuna.messages.ui.viewholders;

import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.katsuna.commons.entities.Profile;
import com.katsuna.commons.entities.ProfileType;
import com.katsuna.commons.utils.DateFormatter;
import com.katsuna.messages.R;
import com.katsuna.messages.domain.Message;
import com.katsuna.messages.providers.metadata.MessageType;
import com.squareup.picasso.Picasso;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    private final TextView mDateTime;
    private final TextView mBody;
    private final ImageView mPhoto;
    private final Profile mProfile;


    public MessageViewHolder(View itemView, Profile profile) {
        super(itemView);
        mDateTime = (TextView) itemView.findViewById(R.id.dateTime);
        mBody = (TextView) itemView.findViewById(R.id.body);
        mPhoto = (ImageView) itemView.findViewById(R.id.photo);
        mProfile = profile;
        adjustProfile();
    }

    private void adjustProfile() {
        if (mProfile != null) {
            int size = itemView.getResources().getDimensionPixelSize(R.dimen.contact_photo_size_intemediate);
            int fontSize = itemView.getResources().getDimensionPixelSize(R.dimen.font_size_intemediate);
            if (mProfile.getType() == ProfileType.ADVANCED.getNumVal()) {
                size = itemView.getResources().getDimensionPixelSize(R.dimen.contact_photo_size_advanced);
                fontSize = itemView.getResources().getDimensionPixelSize(R.dimen.font_size_advanced);
            } else if (mProfile.getType() == ProfileType.SIMPLE.getNumVal()) {
                size = itemView.getResources().getDimensionPixelSize(R.dimen.contact_photo_size_simple);
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
        } else {
            if (message.getContact() != null) {
                //load photo
                Picasso.with(itemView.getContext())
                        .load(message.getContact().getPhotoUri())
                        .fit()
                        .into(mPhoto);
            }
        }

        mDateTime.setText(DateFormatter.format(message.getDate()));
        mBody.setText(message.getBody());
    }
}
