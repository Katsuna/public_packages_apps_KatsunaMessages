package com.katsuna.messages.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.katsuna.messages.R;
import com.katsuna.messages.ui.adapters.models.ContactListItemModel;
import com.katsuna.messages.ui.listeners.IContactInteractionListener;
import com.katsuna.messages.ui.viewholders.ContactViewHolder;
import com.katsuna.messages.utils.Separator;

import java.util.ArrayList;
import java.util.List;

public class ContactsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements Filterable {

    private final List<ContactListItemModel> mOriginalContacts;
    private final IContactInteractionListener mListener;
    private final ContactFilter mFilter = new ContactFilter();
    private List<ContactListItemModel> mFilteredContacts;

    public ContactsRecyclerViewAdapter(List<ContactListItemModel> models,
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

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ContactListItemModel model = mFilteredContacts.get(position);
        ((ContactViewHolder) holder).bind(model);
    }

    @Override
    public int getItemCount() {
        return mFilteredContacts.size();
    }

    public void resetFilter() {
        mFilteredContacts = mOriginalContacts;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class ContactFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ContactListItemModel> filteredContacts = filter(mOriginalContacts, constraint);
            FilterResults results = new FilterResults();
            results.values = filteredContacts;
            results.count = filteredContacts.size();
            return results;
        }

        private List<ContactListItemModel> filter(List<ContactListItemModel> models,
                                                  CharSequence query) {
            query = query.toString().toLowerCase();

            final List<ContactListItemModel> filteredModelList = new ArrayList<>();
            for (ContactListItemModel model : models) {
                final String text = model.getContact().getDisplayName().toLowerCase();
                if (text.contains(query)) {
                    //exclude premium contacts
                    if (!model.isPremium()) {
                        model.setSeparator(Separator.NONE);
                        filteredModelList.add(model);
                    }
                }
            }
            return filteredModelList;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mFilteredContacts = (ArrayList<ContactListItemModel>) results.values;
            notifyDataSetChanged();
        }
    }
}
