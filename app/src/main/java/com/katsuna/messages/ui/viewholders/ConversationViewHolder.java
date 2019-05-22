package com.katsuna.messages.ui.viewholders;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.katsuna.commons.entities.ColorProfile;
import com.katsuna.commons.entities.ColorProfileKeyV2;
import com.katsuna.commons.entities.OpticalParams;
import com.katsuna.commons.entities.SizeProfile;
import com.katsuna.commons.entities.SizeProfileKeyV2;
import com.katsuna.commons.entities.UserProfileContainer;
import com.katsuna.commons.utils.ColorCalcV2;
import com.katsuna.commons.utils.DateFormatter;
import com.katsuna.commons.utils.SizeAdjuster;
import com.katsuna.commons.utils.SizeCalcV2;
import com.katsuna.messages.R;
import com.katsuna.messages.domain.Conversation;
import com.katsuna.messages.ui.listeners.IConversationInteractionListener;
import com.katsuna.messages.utils.ConversationStatus;
import com.katsuna.messages.utils.DrawableGenerator;

public class ConversationViewHolder extends RecyclerView.ViewHolder {

    private final CardView mConversationContainer;
    private final RelativeLayout mConversationContainerInner;
    final UserProfileContainer mUserProfileContainer;
    final IConversationInteractionListener mListener;
    private final TextView mDisplayName;
    private final TextView mDateTime;
    private final TextView mSnippet;
    private final View mOpacityLayer;
    private final ImageView mItemTypeImage;

    public ConversationViewHolder(View itemView, IConversationInteractionListener listener) {
        super(itemView);
        mConversationContainer = itemView.findViewById(R.id.conversation_container);
        mConversationContainerInner = itemView.findViewById(R.id.conversation_container_inner);
        mDisplayName = itemView.findViewById(R.id.displayName);
        mDateTime = itemView.findViewById(R.id.dateTime);
        mSnippet = itemView.findViewById(R.id.body);
        mOpacityLayer = itemView.findViewById(R.id.opacity_layer);
        mListener = listener;
        mUserProfileContainer = listener.getUserProfileContainer();
        mItemTypeImage = itemView.findViewById(R.id.item_type_image);
    }

    private void adjustProfile() {

        SizeProfile sizeProfile = mUserProfileContainer.getOpticalSizeProfile();

        // item type icon
        OpticalParams opticalParams = SizeCalcV2.getOpticalParams(SizeProfileKeyV2.ICON_1,
                sizeProfile);
        SizeAdjuster.adjustIcon(itemView.getContext(), mItemTypeImage, opticalParams);

        // date
        opticalParams = SizeCalcV2.getOpticalParams(SizeProfileKeyV2.BODY_1, sizeProfile);
        SizeAdjuster.adjustText(itemView.getContext(), mDateTime, opticalParams);

        // display name
        opticalParams = SizeCalcV2.getOpticalParams(SizeProfileKeyV2.TITLE, sizeProfile);
        SizeAdjuster.adjustText(itemView.getContext(), mDisplayName, opticalParams);

        // contact description
        opticalParams = SizeCalcV2.getOpticalParams(SizeProfileKeyV2.SUBHEADING_1, sizeProfile);
        SizeAdjuster.adjustText(itemView.getContext(), mSnippet, opticalParams);
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
        } else {
            name = conversation.getContact().getMessageAddress();
        }
        mDisplayName.setText(name);

        mSnippet.setText(conversation.getSnippet());

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
        mDisplayName.setOnClickListener(focusListener);

        if (mOpacityLayer != null) {
            mOpacityLayer.setVisibility(View.INVISIBLE);
        }

        //adjustProfile();
        adjustmentsByConversation(conversation);

        adjustProfile();
    }

    private void adjustmentsByConversation(Conversation conversation) {

        String convStatus = "";
        switch (conversation.getStatus()) {
            case ConversationStatus.READ:
                convStatus = itemView.getResources().getString(R.string.conversation_read);
                break;
            case ConversationStatus.UNREAD:
                convStatus = itemView.getResources().getString(R.string.conversation_unread);
                break;
            case ConversationStatus.SENT:
                convStatus = itemView.getResources().getString(R.string.conversation_sent);
                break;
        }
        String date = DateFormatter.format(conversation.getDate());
        String status = String.format("%s, %s", convStatus, date);
        mDateTime.setText(status);

        adjustColorBasedOnCallType(conversation.getStatus());
    }

    private void adjustColorBasedOnCallType(int convStatus) {
        // calc colors
        int cardColor = 0;
        int cardColorAlpha = 0;

        ColorProfile colorProfile = mUserProfileContainer.getColorProfile();
        if (convStatus == ConversationStatus.SENT) {
            cardColor = ColorCalcV2.getColor(itemView.getContext(),
                    ColorProfileKeyV2.PRIMARY_COLOR_2, colorProfile);
            cardColorAlpha = ColorCalcV2.getColor(itemView.getContext(),
                    ColorProfileKeyV2.SECONDARY_COLOR_2, colorProfile);
        } else if (convStatus == ConversationStatus.READ) {
            cardColor = ColorCalcV2.getColor(itemView.getContext(),
                    ColorProfileKeyV2.PRIMARY_GREY_1, colorProfile);
            cardColorAlpha = ColorCalcV2.getColor(itemView.getContext(),
                    ColorProfileKeyV2.SECONDARY_GREY_2, colorProfile);
        } else if (convStatus == ConversationStatus.UNREAD) {
            cardColor = ColorCalcV2.getColor(itemView.getContext(),
                    ColorProfileKeyV2.PRIMARY_COLOR_1, colorProfile);
            cardColorAlpha = ColorCalcV2.getColor(itemView.getContext(),
                    ColorProfileKeyV2.SECONDARY_COLOR_1, colorProfile);
        }

        // set colors
        if (cardColor != 0) {
            mConversationContainer.setCardBackgroundColor(ColorStateList.valueOf(cardColor));
            mConversationContainerInner.setBackgroundColor(cardColorAlpha);
        }

        // style callTypeDrawable based on call type
        Drawable itemTypeDrawable = DrawableGenerator.getItemTypeDrawable(itemView.getContext(),
                convStatus, cardColor, colorProfile);
        mItemTypeImage.setImageDrawable(itemTypeDrawable);
    }
}
