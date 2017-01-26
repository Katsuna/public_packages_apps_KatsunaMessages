package com.katsuna.messages.ui.viewholders;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.katsuna.commons.entities.ColorProfile;
import com.katsuna.commons.entities.ColorProfileKey;
import com.katsuna.commons.entities.ProfileType;
import com.katsuna.commons.utils.ColorCalc;
import com.katsuna.commons.utils.Shape;
import com.katsuna.messages.R;
import com.katsuna.messages.domain.Conversation;
import com.katsuna.messages.ui.listeners.IConversationInteractionListener;

public class ConversationSelectedViewHolder extends ConversationViewHolder {

    private final Button mCallButton;
    private final Button mMessageButton;
    private final ImageButton mDeleteConversationButton;

    public ConversationSelectedViewHolder(View itemView, IConversationInteractionListener listener) {
        super(itemView, listener);
        mCallButton = (Button) itemView.findViewById(R.id.call_button);
        mMessageButton = (Button) itemView.findViewById(R.id.message_button);
        mDeleteConversationButton = (ImageButton) itemView.findViewById(R.id.delete_conversation_button);
        adjustProfile();
    }

    private void adjustProfile() {

        ProfileType opticalSizeProfile = mUserProfileContainer.getOpticalSizeProfile();

        if (opticalSizeProfile != null) {
            int photoSize = itemView.getResources()
                    .getDimensionPixelSize(R.dimen.common_contact_photo_size_intemediate);
            int actionButtonHeight = itemView.getResources()
                    .getDimensionPixelSize(R.dimen.common_action_button_height_intemediate);

            if (opticalSizeProfile == ProfileType.ADVANCED) {
                photoSize = itemView.getResources()
                        .getDimensionPixelSize(R.dimen.common_contact_photo_size_advanced);
                actionButtonHeight = itemView.getResources()
                        .getDimensionPixelSize(R.dimen.common_action_button_height_advanced);
            } else if (opticalSizeProfile == ProfileType.SIMPLE) {
                photoSize = itemView.getResources()
                        .getDimensionPixelSize(R.dimen.common_contact_photo_size_simple);
                actionButtonHeight = itemView.getResources()
                        .getDimensionPixelSize(R.dimen.common_action_button_height_simple);
            }
            ViewGroup.LayoutParams layoutParams = mPhoto.getLayoutParams();
            layoutParams.height = photoSize;
            layoutParams.width = photoSize;
            mPhoto.setLayoutParams(layoutParams);

            ViewGroup.LayoutParams callButtonParams = mCallButton.getLayoutParams();
            callButtonParams.height = actionButtonHeight;

            ViewGroup.LayoutParams messageButtonParams = mMessageButton.getLayoutParams();
            messageButtonParams.height = actionButtonHeight;

            mCallButton.setLayoutParams(callButtonParams);
            mMessageButton.setLayoutParams(messageButtonParams);
        }

        adjustColorProfile();
    }

    private void adjustColorProfile() {
        ColorProfile colorProfile = mUserProfileContainer.getColorProfile();
        // set action buttons background color
        int color1 = ColorCalc.getColor(itemView.getContext(),
                ColorProfileKey.ACCENT1_COLOR, colorProfile);
        Shape.setRoundedBackground(mMessageButton, color1);

        int color2 = ColorCalc.getColor(itemView.getContext(), ColorProfileKey.ACCENT2_COLOR,
                colorProfile);
        Shape.setRoundedBackground(mCallButton, color2);
    }

    public void bind(final Conversation conversation, final int position) {
        super.bind(conversation, position);

        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.callContact(conversation);
            }
        });
        mMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.sendSMS(conversation);
            }
        });
        mDeleteConversationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.deleteConversation(conversation);
            }
        });
    }
}
