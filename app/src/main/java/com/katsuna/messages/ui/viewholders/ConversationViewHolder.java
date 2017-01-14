package com.katsuna.messages.ui.viewholders;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.katsuna.commons.entities.ProfileType;
import com.katsuna.commons.entities.UserProfileContainer;
import com.katsuna.commons.utils.DateFormatter;
import com.katsuna.messages.R;
import com.katsuna.messages.domain.Conversation;
import com.squareup.picasso.Picasso;

public class ConversationViewHolder extends RecyclerView.ViewHolder {

    private final TextView mDisplayName;
    private final TextView mDateTime;
    private final TextView mSnippet;
    private final ImageView mPhoto;
    private final UserProfileContainer mUserProfileContainer;

    public ConversationViewHolder(View itemView, UserProfileContainer userProfileContainer) {
        super(itemView);
        mDisplayName = (TextView) itemView.findViewById(R.id.displayName);
        mDateTime = (TextView) itemView.findViewById(R.id.dateTime);
        mSnippet = (TextView) itemView.findViewById(R.id.body);
        mPhoto = (ImageView) itemView.findViewById(R.id.photo);
        mUserProfileContainer = userProfileContainer;
        adjustProfile();
    }

    private void adjustProfile() {
        ProfileType opticalSizeProfile = mUserProfileContainer.getOpticalSizeProfile();
        if (opticalSizeProfile != null) {
            int size = itemView.getResources().getDimensionPixelSize(R.dimen.contact_photo_size_intemediate);
            int fontSize = itemView.getResources().getDimensionPixelSize(R.dimen.font_size_intemediate);
            if (opticalSizeProfile == ProfileType.ADVANCED) {
                size = itemView.getResources().getDimensionPixelSize(R.dimen.contact_photo_size_advanced);
                fontSize = itemView.getResources().getDimensionPixelSize(R.dimen.font_size_advanced);
            } else if (opticalSizeProfile == ProfileType.SIMPLE) {
                size = itemView.getResources().getDimensionPixelSize(R.dimen.contact_photo_size_simple);
                fontSize = itemView.getResources().getDimensionPixelSize(R.dimen.font_size_simple);
            }

            //adjust photo
            ViewGroup.LayoutParams layoutParams = mPhoto.getLayoutParams();
            layoutParams.height = size;
            layoutParams.width = size;

            //adjust displayName
            mDisplayName.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
        }
    }

    public void bind(Conversation conversation) {
        String name;
        if (conversation.getContact().getId() > 0) {
            name = conversation.getContact().getName();

            //load photo
            Picasso.with(itemView.getContext())
                    .load(conversation.getContact().getPhotoUri())
                    .fit()
                    .into(mPhoto);

        } else {
            mPhoto.setImageBitmap(null);
            name = conversation.getContact().getAddress();
        }
        mDisplayName.setText(name);

        mDateTime.setText(DateFormatter.format(conversation.getDate()));
        mSnippet.setText(conversation.getSnippet());

        if (conversation.getRead() == 0) {
            mDisplayName.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.dark_grey));
        } else if (conversation.isUnanswered()) {
            mDisplayName.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.pink));
        } else {
            mDisplayName.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.black87));
        }
    }
}
