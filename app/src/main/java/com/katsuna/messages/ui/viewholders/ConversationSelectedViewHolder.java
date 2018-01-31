package com.katsuna.messages.ui.viewholders;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.katsuna.commons.entities.ColorProfile;
import com.katsuna.commons.entities.OpticalParams;
import com.katsuna.commons.entities.SizeProfile;
import com.katsuna.commons.entities.SizeProfileKeyV2;
import com.katsuna.commons.utils.Shape;
import com.katsuna.commons.utils.SizeAdjuster;
import com.katsuna.commons.utils.SizeCalcV2;
import com.katsuna.messages.R;
import com.katsuna.messages.domain.Conversation;
import com.katsuna.messages.ui.listeners.IConversationInteractionListener;

public class ConversationSelectedViewHolder extends ConversationViewHolder {

    private final Button mCallButton;
    private final Button mMessageButton;
    private final View mActionButtonsContainer;
    private final TextView mMoreText;
//    private final ImageView mDeleteConversationButton;

    public ConversationSelectedViewHolder(View itemView, IConversationInteractionListener listener) {
        super(itemView, listener);
        mMoreText = itemView.findViewById(R.id.txt_more);
        mCallButton = itemView.findViewById(R.id.button_call);
        mMessageButton = itemView.findViewById(R.id.button_message);
        mActionButtonsContainer = itemView.findViewById(R.id.action_buttons_container);

        //mDeleteConversationButton = itemView.findViewById(R.id.delete_conversation_button);
    }

    private void adjustProfile() {
        SizeProfile sizeProfile = mUserProfileContainer.getOpticalSizeProfile();

        if (sizeProfile != null) {
            // adjust buttons
            OpticalParams opticalParams = SizeCalcV2.getOpticalParams(SizeProfileKeyV2.BUTTON,
                    sizeProfile);
            SizeAdjuster.adjustText(itemView.getContext(), mCallButton, opticalParams);
            SizeAdjuster.adjustText(itemView.getContext(), mMessageButton, opticalParams);

            // more text
            opticalParams = SizeCalcV2.getOpticalParams(SizeProfileKeyV2.BUTTON,
                    sizeProfile);
            SizeAdjuster.adjustText(itemView.getContext(), mMoreText, opticalParams);
        }

        adjustColorProfile();
    }

    private void adjustColorProfile() {
        ColorProfile colorProfile = mUserProfileContainer.getColorProfile();

        // set action buttons background color
        adjustPrimaryButton(itemView.getContext(), mMessageButton);

        adjustSecondaryButton(itemView.getContext(), mCallButton);
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

        mActionButtonsContainer.setVisibility(View.VISIBLE);

/*      mDeleteConversationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.deleteConversation(conversation);
            }
        });*/

        adjustProfile();
    }

    private void adjustPrimaryButton(Context context, Button button) {
        int color1 = ContextCompat.getColor(context, R.color.buttons_color);
        Shape.setRoundedBackground(button, color1);
    }

    private void adjustSecondaryButton(Context context, Button button) {
        int color1 = ContextCompat.getColor(context, R.color.buttons_color);
        int white = ContextCompat.getColor(context, R.color.common_white);
        Shape.setRoundedBorder(button, color1, white);
    }
}
