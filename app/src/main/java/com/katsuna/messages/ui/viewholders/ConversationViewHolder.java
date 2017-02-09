package com.katsuna.messages.ui.viewholders;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.katsuna.commons.entities.ColorProfileKey;
import com.katsuna.commons.entities.ProfileType;
import com.katsuna.commons.entities.UserProfileContainer;
import com.katsuna.commons.utils.ColorCalc;
import com.katsuna.commons.utils.DateFormatter;
import com.katsuna.messages.R;
import com.katsuna.messages.domain.Conversation;
import com.katsuna.messages.ui.listeners.IConversationInteractionListener;
import com.squareup.picasso.Picasso;

public class ConversationViewHolder extends RecyclerView.ViewHolder {

    final View mConversationContainer;
    final ImageView mPhoto;
    final UserProfileContainer mUserProfileContainer;
    final IConversationInteractionListener mListener;
    private final TextView mDisplayName;
    private final TextView mDateTime;
    private final TextView mSnippet;
    private final View mOpacityLayer;

    public ConversationViewHolder(View itemView, IConversationInteractionListener listener) {
        super(itemView);
        mConversationContainer = itemView.findViewById(R.id.conversation_container);
        mDisplayName = (TextView) itemView.findViewById(R.id.displayName);
        mDateTime = (TextView) itemView.findViewById(R.id.dateTime);
        mSnippet = (TextView) itemView.findViewById(R.id.body);
        mPhoto = (ImageView) itemView.findViewById(R.id.photo);
        mOpacityLayer = itemView.findViewById(R.id.opacity_layer);
        mListener = listener;
        mUserProfileContainer = listener.getUserProfileContainer();
        adjustProfile();
    }

    private void adjustProfile() {
        ProfileType opticalSizeProfile = mUserProfileContainer.getOpticalSizeProfile();
        if (opticalSizeProfile != null) {
            int size = itemView.getResources()
                    .getDimensionPixelSize(R.dimen.common_contact_photo_size_intemediate);
            if (opticalSizeProfile == ProfileType.ADVANCED) {
                size = itemView.getResources()
                        .getDimensionPixelSize(R.dimen.common_contact_photo_size_advanced);
            } else if (opticalSizeProfile == ProfileType.SIMPLE) {
                size = itemView.getResources()
                        .getDimensionPixelSize(R.dimen.common_contact_photo_size_simple);
            }

            //adjust photo
            ViewGroup.LayoutParams layoutParams = mPhoto.getLayoutParams();
            layoutParams.height = size;
            layoutParams.width = size;
        }
    }

    public void bindGreyed(Conversation conversation, final int position) {
        bind(conversation, position);
        if (mOpacityLayer != null) {
            mOpacityLayer.setVisibility(View.VISIBLE);
        }
    }

    public void bind(Conversation conversation, final int position) {
        String name;
        if (conversation.getContact().getId() > 0) {
            name = conversation.getContact().getDisplayName();

            //load photo
            Picasso.with(itemView.getContext())
                    .load(conversation.getContact().getPhotoUri())
                    .fit()
                    .into(mPhoto);

        } else {
            mPhoto.setImageBitmap(null);
            name = conversation.getContact().getMessageAddress();
        }
        mDisplayName.setText(name);

        mDateTime.setText(DateFormatter.format(conversation.getDate()));
        mSnippet.setText(conversation.getSnippet());

        if (conversation.getRead() == 0) {
            mDisplayName.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.common_grey600));
        } else if (conversation.isUnanswered()) {
            int color = ColorCalc.getColor(itemView.getContext(), ColorProfileKey.ACCENT1_COLOR,
                    mUserProfileContainer.getColorProfile());
            mDisplayName.setTextColor(color);
        } else {
            mDisplayName.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.common_black87));
        }

        if (mConversationContainer != null) {
            mConversationContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.selectConversation(position);
                }
            });
        }

        // direct focus on non selected contact if photo or name is clicked
        View.OnClickListener focusListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.focusConversation(position);
            }
        };
        mPhoto.setOnClickListener(focusListener);
        mDisplayName.setOnClickListener(focusListener);

        if (mOpacityLayer != null) {
            mOpacityLayer.setVisibility(View.INVISIBLE);
        }
    }
}
