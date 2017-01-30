package com.katsuna.messages.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.katsuna.commons.ui.adapters.ContactsAdapterBase;
import com.katsuna.commons.ui.adapters.models.ContactListItemModel;
import com.katsuna.messages.R;
import com.katsuna.messages.ui.listeners.IContactInteractionListener;
import com.katsuna.messages.ui.viewholders.ContactViewHolder;

import java.util.List;

public class ContactsAdapter extends ContactsAdapterBase {

    private final IContactInteractionListener mListener;

    public ContactsAdapter(List<ContactListItemModel> models,
                           IContactInteractionListener listener) {
        mOriginalContacts = models;
        mFilteredContacts = models;
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact, parent, false);
        return new ContactViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ContactListItemModel model = mFilteredContacts.get(position);
        ((ContactViewHolder) holder).bind(model);
    }
}
