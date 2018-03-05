package com.katsuna.messages.ui.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.katsuna.commons.domain.Contact;
import com.katsuna.messages.R;
import com.katsuna.messages.domain.Conversation;
import com.katsuna.messages.providers.SmsProvider;
import com.katsuna.messages.ui.listeners.IConversationInteractionListener;
import com.katsuna.messages.ui.viewholders.ConversationSelectedViewHolder;
import com.katsuna.messages.ui.viewholders.ConversationViewHolder;
import com.katsuna.messages.utils.ContactsCache;

import java.util.List;

public class ConversationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int NO_CONVERSATION_POSITION = -1;
    private static final int CONVERSATION_NOT_SELECTED = 1;
    private static final int CONVERSATION_SELECTED = 2;
    private static final int CONVERSATION_GREYED = 3;
    private final List<Conversation> mModels;
    private final IConversationInteractionListener mListener;
    private int mSelectedConversationPosition = NO_CONVERSATION_POSITION;

    public ConversationsAdapter(List<Conversation> models,
                                IConversationInteractionListener listener) {
        mModels = models;
        mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = CONVERSATION_NOT_SELECTED;
        if (position == mSelectedConversationPosition) {
            viewType = CONVERSATION_SELECTED;
        } else if (mSelectedConversationPosition != NO_CONVERSATION_POSITION) {
            viewType = CONVERSATION_GREYED;
        }
        return viewType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;

        boolean isRightHanded = mListener.getUserProfileContainer().isRightHanded();
        View view;

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case CONVERSATION_NOT_SELECTED:
            case CONVERSATION_GREYED:
                view = inflater.inflate(R.layout.conversation, parent, false);
                viewHolder = new ConversationViewHolder(view, mListener);
                break;
            case CONVERSATION_SELECTED:
                view = inflater.inflate(R.layout.conversation, parent, false);
                ViewGroup buttonsWrapper = view.findViewById(R.id.action_buttons_wrapper);
                if (isRightHanded) {
                    View buttonsView = inflater.inflate(R.layout.action_buttons_rh, buttonsWrapper,
                            false);
                    buttonsWrapper.addView(buttonsView);
                } else {
                    View buttonsView = inflater.inflate(R.layout.action_buttons_lh, buttonsWrapper,
                            false);
                    buttonsWrapper.addView(buttonsView);
                }
                viewHolder = new ConversationSelectedViewHolder(view, mListener);
                break;
            default:
                throw new RuntimeException("viewType not supported: " + viewType);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Conversation conversation = mModels.get(position);

        ContactsCache contactsCache = ContactsCache.getInstance();
        Contact contact = contactsCache.getContact(conversation.getRecipientIds());
        if (contact == null) {
            SmsProvider smsProvider = new SmsProvider(holder.itemView.getContext());

            //find address by recipientIds
            String address = smsProvider.getAddress(conversation.getRecipientIds());
            //find contact
            contact = smsProvider.getContactByAddress(address);
            //put in cache
            contactsCache.putContact(conversation.getRecipientIds(), contact);
        }

        conversation.setContact(contact);

        switch (holder.getItemViewType()) {
            case CONVERSATION_NOT_SELECTED:
            case CONVERSATION_SELECTED:
                ((ConversationViewHolder) holder).bind(conversation, position);
                break;
            case CONVERSATION_GREYED:
                ((ConversationViewHolder) holder).bindGreyed(conversation, position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mModels.size();
    }

    public void setSelectedConversationAtPosition(int position) {
        mSelectedConversationPosition = position;
        notifyItemChanged(position);
    }

    public void deselectConversation() {
        setSelectedConversationAtPosition(NO_CONVERSATION_POSITION);
    }

}
