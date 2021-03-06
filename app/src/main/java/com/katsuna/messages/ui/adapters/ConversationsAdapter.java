/**
* Copyright (C) 2020 Manos Saratsis
*
* This file is part of Katsuna.
*
* Katsuna is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Katsuna is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Katsuna.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.katsuna.messages.ui.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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

import java.util.ArrayList;
import java.util.List;

public class ConversationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int NO_CONVERSATION_POSITION = -1;
    private static final int CONVERSATION_NOT_SELECTED = 1;
    private static final int CONVERSATION_SELECTED = 2;
    private static final int CONVERSATION_GREYED = 3;
    private List<Conversation> mFilteredConversations;
    private final List<Conversation> mOriginalConversations;
    private final IConversationInteractionListener mListener;
    private int mSelectedConversationPosition = NO_CONVERSATION_POSITION;

    public ConversationsAdapter(List<Conversation> models,
                                IConversationInteractionListener listener) {
        mFilteredConversations = models;
        mOriginalConversations = models;
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
        Conversation conversation = mFilteredConversations.get(position);

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
        return mFilteredConversations.size();
    }

    public void setSelectedConversationAtPosition(int position) {
        mSelectedConversationPosition = position;
        notifyItemChanged(position);
    }

    public void deselectConversation() {
        setSelectedConversationAtPosition(NO_CONVERSATION_POSITION);
    }

    public void resetFilter() {
        mFilteredConversations = mOriginalConversations;
        notifyDataSetChanged();
    }

    public void filter(String query) {
        query = query.toLowerCase();

        List<Conversation> filteredConversations = new ArrayList<>();
        for (Conversation conversation : mOriginalConversations) {
            boolean found = false;

            // check contact name
            Contact contact = conversation.getContact();
            if (contact != null) {
                String name = contact.getDisplayName().toLowerCase();
                if (name.contains(query)) {
                    found = true;
                }
            }

            // check text
            if (!found) {
                String text = conversation.getSnippet();
                if (!TextUtils.isEmpty(text)) {
                    if (text.toLowerCase().contains(query)) {
                        found = true;
                    }
                }
            }

            // check number
            if (!found) {
                if (contact != null) {
                    String number = contact.getMessageAddress().toLowerCase();
                    if (number.contains(query)) {
                        found = true;
                    }
                }
            }

            if (found) {
                filteredConversations.add(conversation);
            }
        }

        mFilteredConversations = filteredConversations;
        notifyDataSetChanged();
    }

    public void clearData() {
        // clear the data
        mOriginalConversations.clear();
        mFilteredConversations.clear();
        notifyDataSetChanged();
    }
}
