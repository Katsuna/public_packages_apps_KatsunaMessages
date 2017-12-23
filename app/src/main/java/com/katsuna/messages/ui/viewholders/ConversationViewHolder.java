package com.katsuna.messages.ui.viewholders;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.katsuna.commons.entities.UserProfileContainer;
import com.katsuna.commons.utils.DateFormatter;
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

    void adjustProfile() {

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
        mDateTime.setText(convStatus + ", " + DateFormatter.format(conversation.getDate()));

        adjustColorBasedOnCallType(conversation.getStatus());
    }

    private void adjustColorBasedOnCallType(int convStatus) {
        // calc colors
        int cardColor = 0;
        int cardColorAlpha = 0;

        if (convStatus == ConversationStatus.SENT) {
            cardColor = R.color.priority_two;
            cardColorAlpha = R.color.priority_two_tone_one;
        } else if (convStatus == ConversationStatus.READ) {
            cardColor = R.color.priority_one;
            cardColorAlpha = R.color.priority_one_tone_one;
        } else if (convStatus == ConversationStatus.UNREAD) {
            cardColor = R.color.priority_three;
            cardColorAlpha = R.color.priority_three_tone_one;
        }

        // set colors
        if (cardColor != 0) {
            mConversationContainer.setCardBackgroundColor(ColorStateList.valueOf(
                    ContextCompat.getColor(itemView.getContext(), cardColor)));
            mConversationContainerInner.setBackgroundColor(
                    ContextCompat.getColor(itemView.getContext(), cardColorAlpha));
        }

        // style callTypeDrawable based on call type
        Drawable itemTypeDrawable = DrawableGenerator.getItemTypeDrawable(itemView.getContext(),
                convStatus);
        mItemTypeImage.setImageDrawable(itemTypeDrawable);
    }
}
