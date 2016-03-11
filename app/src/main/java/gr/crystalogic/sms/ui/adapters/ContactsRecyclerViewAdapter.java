package gr.crystalogic.sms.ui.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import gr.crystalogic.sms.R;
import gr.crystalogic.sms.dao.ContactDao;
import gr.crystalogic.sms.domain.Contact;
import gr.crystalogic.sms.domain.Phone;
import gr.crystalogic.sms.ui.activities.ConversationActivity;
import gr.crystalogic.sms.ui.adapters.models.ContactListItemModel;
import gr.crystalogic.sms.ui.viewholders.ContactViewHolder;

public class ContactsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ContactListItemModel> mModels;

    public ContactsRecyclerViewAdapter(List<ContactListItemModel> models) {
        mModels = models;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact, parent, false);
        return new ContactViewHolder(view);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ContactViewHolder) holder).bind(mModels.get(position));

        holder.itemView.setOnClickListener(new PopupPhone(position));
    }

    private class PopupPhone implements View.OnClickListener {

        private int mPosition;

        public PopupPhone(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(final View v) {

            Contact contact = mModels.get(mPosition).getContact();

            ContactDao dao = new ContactDao(v.getContext());
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
                builder.setTitle(R.string.select_phone);
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

    @Override
    public int getItemCount() {
        return mModels.size();
    }
}
