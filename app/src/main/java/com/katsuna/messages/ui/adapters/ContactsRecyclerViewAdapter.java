package com.katsuna.messages.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.katsuna.commons.entities.UserProfileContainer;
import com.katsuna.messages.R;
import com.katsuna.messages.domain.Contact;
import com.katsuna.messages.domain.Phone;
import com.katsuna.messages.providers.ContactProvider;
import com.katsuna.messages.ui.activities.ConversationActivity;
import com.katsuna.messages.ui.adapters.models.ContactListItemModel;
import com.katsuna.messages.ui.viewholders.ContactViewHolder;

import java.util.List;

public class ContactsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ContactListItemModel> mModels;
    private final UserProfileContainer mUserProfileContainer;

    public ContactsRecyclerViewAdapter(List<ContactListItemModel> models,
                                       UserProfileContainer userProfileContainer) {
        mModels = models;
        mUserProfileContainer = userProfileContainer;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact, parent, false);
        return new ContactViewHolder(view, mUserProfileContainer);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ContactViewHolder) holder).bind(mModels.get(position));

        holder.itemView.setOnClickListener(new PopupPhone(position));
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

    private class PopupPhone implements View.OnClickListener {

        private final int mPosition;

        public PopupPhone(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(final View v) {

            Contact contact = mModels.get(mPosition).getContact();

            ContactProvider dao = new ContactProvider(v.getContext());
            List<Phone> phones = dao.getPhones(contact.getId());

            if (phones.size() == 1) {
                startActivity(v.getContext(), phones.get(0).getNumber());
            } else {
                final CharSequence phonesArray[] = new CharSequence[phones.size()];
                int i = 0;
                for (Phone phone : phones) {
                    phonesArray[i++] = phone.getNumber();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                LayoutInflater inflater = (LayoutInflater) v.getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.alert_title, null);
                builder.setCustomTitle(view);
                builder.setItems(phonesArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(v.getContext(), phonesArray[which].toString());
                    }
                });
                builder.show();
            }
        }

        private void startActivity(Context context, String number) {
            Intent i = new Intent(context, ConversationActivity.class);
            i.putExtra("conversationNumber", number);
            context.startActivity(i);
        }
    }


}
