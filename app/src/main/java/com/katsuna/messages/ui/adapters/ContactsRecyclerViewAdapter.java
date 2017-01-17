package com.katsuna.messages.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.katsuna.messages.R;
import com.katsuna.messages.ui.adapters.models.ContactListItemModel;
import com.katsuna.messages.ui.listeners.IContactInteractionListener;
import com.katsuna.messages.ui.viewholders.ContactViewHolder;

import java.util.List;

public class ContactsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ContactListItemModel> mModels;
    private final IContactInteractionListener mListener;

    public ContactsRecyclerViewAdapter(List<ContactListItemModel> models,
                                       IContactInteractionListener listener) {
        mModels = models;
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact, parent, false);
        return new ContactViewHolder(view, mListener);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ContactViewHolder) holder).bind(mModels.get(position));
    }

    @Override
    public int getItemCount() {
        return mModels.size();
    }

    public void animateTo(List<ContactListItemModel> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<ContactListItemModel> newModels) {
        for (int i = mModels.size() - 1; i >= 0; i--) {
            final ContactListItemModel model = mModels.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<ContactListItemModel> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final ContactListItemModel model = newModels.get(i);
            if (!mModels.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<ContactListItemModel> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final ContactListItemModel model = newModels.get(toPosition);
            final int fromPosition = mModels.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    private void removeItem(int position) {
        mModels.remove(position);
        notifyItemRemoved(position);
    }

    private void addItem(int position, ContactListItemModel model) {
        mModels.add(position, model);
        notifyItemInserted(position);
    }

    private void moveItem(int fromPosition, int toPosition) {
        final ContactListItemModel model = mModels.remove(fromPosition);
        mModels.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

}
