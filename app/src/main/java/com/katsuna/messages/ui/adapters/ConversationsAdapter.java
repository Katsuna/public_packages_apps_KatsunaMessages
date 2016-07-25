package com.katsuna.messages.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;

import java.util.List;

import com.katsuna.commons.entities.Profile;
import com.katsuna.messages.R;
import com.katsuna.messages.domain.Contact;
import com.katsuna.messages.domain.Conversation;
import com.katsuna.messages.providers.SmsProvider;
import com.katsuna.messages.ui.viewholders.ConversationViewHolder;
import com.katsuna.messages.utils.ContactsCache;

public class ConversationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Conversation> mModels;

    private final OnClickListener mOnClickListener;
    private final OnLongClickListener mOnLongClickListener;
    private final Profile mProfile;

    public ConversationsAdapter(List<Conversation> models, OnClickListener onClickListener, OnLongClickListener onLongClickListener, Profile profile) {
        mModels = models;
        mOnClickListener = onClickListener;
        mOnLongClickListener = onLongClickListener;
        mProfile = profile;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation, parent, false);
        view.setOnClickListener(mOnClickListener);
        view.setOnLongClickListener(mOnLongClickListener);
        return new ConversationViewHolder(view, mProfile);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
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

        ((ConversationViewHolder) holder).bind(conversation);
    }

    @Override
    public int getItemCount() {
        return mModels.size();
    }

    public Conversation getItemAtPosition(int position) {
        return mModels.get(position);
    }
}
