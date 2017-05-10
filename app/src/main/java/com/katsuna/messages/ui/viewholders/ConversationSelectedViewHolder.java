package com.katsuna.messages.ui.viewholders;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.katsuna.commons.entities.ColorProfile;
import com.katsuna.commons.entities.ColorProfileKey;
import com.katsuna.commons.entities.OpticalParams;
import com.katsuna.commons.entities.SizeProfile;
import com.katsuna.commons.entities.SizeProfileKey;
import com.katsuna.commons.utils.ColorCalc;
import com.katsuna.commons.utils.DrawUtils;
import com.katsuna.commons.utils.Shape;
import com.katsuna.commons.utils.SizeAdjuster;
import com.katsuna.commons.utils.SizeCalc;
import com.katsuna.messages.R;
import com.katsuna.messages.domain.Conversation;
import com.katsuna.messages.ui.listeners.IConversationInteractionListener;

public class ConversationSelectedViewHolder extends ConversationViewHolder {

    private final Button mCallButton;
    private final Button mMessageButton;
    private final View mMessageButtonContainer;
    private final View mCallButtonContainer;
    private final ImageView mDeleteConversationButton;

    public ConversationSelectedViewHolder(View itemView, IConversationInteractionListener listener) {
        super(itemView, listener);
        mCallButton = (Button) itemView.findViewById(R.id.call_button);
        mMessageButton = (Button) itemView.findViewById(R.id.message_button);
        mDeleteConversationButton = (ImageView) itemView.findViewById(R.id.delete_conversation_button);
        mMessageButtonContainer = itemView.findViewById(R.id.message_button_container);
        mCallButtonContainer = itemView.findViewById(R.id.call_button_container);
    }

    protected void adjustProfile() {
        super.adjustProfile();

        SizeProfile opticalSizeProfile = mUserProfileContainer.getOpticalSizeProfile();

        if (opticalSizeProfile != null) {
            int messageButtonDrawable = R.drawable.common_ic_message_black_24dp;
            int callButtonDrawable = R.drawable.common_ic_call_white_24dp;
            int deleteButtonDrawable = R.drawable.common_ic_delete_black54_24dp;

            if (opticalSizeProfile == SizeProfile.SIMPLE) {
                messageButtonDrawable = R.drawable.common_ic_message_black_28dp;
                callButtonDrawable = R.drawable.common_ic_call_white_28dp;
                deleteButtonDrawable = R.drawable.common_ic_delete_black54_28dp;
            }
            mMessageButton.setCompoundDrawablesWithIntrinsicBounds(messageButtonDrawable, 0, 0,0);
            mCallButton.setCompoundDrawablesWithIntrinsicBounds(callButtonDrawable, 0, 0,0);
            mDeleteConversationButton.setImageResource(deleteButtonDrawable);

            OpticalParams opticalParams = SizeCalc.getOpticalParams(SizeProfileKey.ACTION_BUTTON,
                    opticalSizeProfile);
            SizeAdjuster.adjustText(itemView.getContext(), mCallButton, opticalParams);
            SizeAdjuster.adjustText(itemView.getContext(), mMessageButton, opticalParams);

            SizeAdjuster.adjustButtonContainer(itemView.getContext(), mCallButtonContainer,
                    opticalParams);
            SizeAdjuster.adjustButtonContainer(itemView.getContext(), mMessageButtonContainer,
                    opticalParams);
        }

        adjustColorProfile();
    }

    private void adjustColorProfile() {
        ColorProfile colorProfile = mUserProfileContainer.getColorProfile();
        // set action buttons background color
        int color1 = ColorCalc.getColor(itemView.getContext(),
                ColorProfileKey.ACCENT1_COLOR, colorProfile);
        Shape.setRoundedBackground(mMessageButtonContainer, color1);

        int color2 = ColorCalc.getColor(itemView.getContext(), ColorProfileKey.ACCENT2_COLOR,
                colorProfile);
        Shape.setRoundedBorder(mCallButtonContainer, color2);
        Drawable[] drawables = mCallButton.getCompoundDrawables();
        DrawUtils.setColor(drawables[0], color2);
        mCallButton.setTextColor(color2);

        int bgColor = ColorCalc.getColor(itemView.getContext(), ColorProfileKey.POP_UP_COLOR,
                colorProfile);
        mConversationContainer.setBackgroundColor(bgColor);
    }

    public void bind(final Conversation conversation, final int position) {
        super.bind(conversation, position);

        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.callContact(conversation);
            }
        });
        mCallButtonContainer.setOnClickListener(new View.OnClickListener() {
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
        mMessageButtonContainer.setOnClickListener(new View.OnClickListener() {
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

        adjustProfile();
    }
}
