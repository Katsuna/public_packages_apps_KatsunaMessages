package com.katsuna.messages.ui.viewholders;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.katsuna.commons.entities.OpticalParams;
import com.katsuna.commons.entities.SizeProfile;
import com.katsuna.commons.entities.SizeProfileKeyV2;
import com.katsuna.commons.utils.ColorAdjusterV2;
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
    private final View mMoreActionsContainer;
    private final TextView mCreateContactText;
    private final TextView mAddToExistingText;
    private final TextView mDeleteConversationText;
    private final View mDeleteConversationDivider;

    private final TextView mConvNumber;

    public ConversationSelectedViewHolder(View itemView, IConversationInteractionListener listener) {
        super(itemView, listener);
        mMoreText = itemView.findViewById(R.id.txt_more);
        mCallButton = itemView.findViewById(R.id.button_call);
        mMessageButton = itemView.findViewById(R.id.button_message);
        mActionButtonsContainer = itemView.findViewById(R.id.action_buttons_container);
        mMoreActionsContainer = itemView.findViewById(R.id.more_actions_container);
        mCreateContactText = itemView.findViewById(R.id.create_contact_text);
        mAddToExistingText = itemView.findViewById(R.id.add_to_existing_contact_text);
        mDeleteConversationText = itemView.findViewById(R.id.delete_conversation_text);
        mDeleteConversationDivider = itemView.findViewById(R.id.delete_conversation_divider);
        mConvNumber = itemView.findViewById(R.id.conversation_number);
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
        ColorAdjusterV2.adjustButtons(itemView.getContext(),
                mUserProfileContainer.getActiveUserProfile(),
                mMessageButton, mCallButton, mMoreText);
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

        mMoreText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMoreActionsContainer.getVisibility() == View.VISIBLE) {
                    expandMoreActions(false);
                } else {
                    expandMoreActions(true);
                }
            }
        });

        // by default more actions are hidden
        expandMoreActions(false);

        mCreateContactText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.createContact(conversation);
            }
        });

        mAddToExistingText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.addToContact(conversation);
            }
        });

        mDeleteConversationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.deleteConversation(conversation);
            }
        });

        boolean contactFound = conversation.getContact().getId() > 0;
        mCreateContactText.setVisibility(contactFound ? View.GONE : View.VISIBLE);
        mAddToExistingText.setVisibility(contactFound ? View.GONE : View.VISIBLE);
        mDeleteConversationDivider.setVisibility(contactFound ? View.GONE : View.VISIBLE);
        mConvNumber.setVisibility(contactFound ? View.VISIBLE : View.GONE);
        mDeleteConversationText.setVisibility(View.VISIBLE);

        if (contactFound) {
            mConvNumber.setText(conversation.getContact().getMessageAddress());
        }

        adjustProfile();
    }

    private void expandMoreActions(boolean flag) {
        if (flag) {
            mMoreActionsContainer.setVisibility(View.VISIBLE);
            mMoreText.setText(R.string.common_less);
        } else {
            mMoreActionsContainer.setVisibility(View.GONE);
            mMoreText.setText(R.string.common_more);
        }
    }
}
