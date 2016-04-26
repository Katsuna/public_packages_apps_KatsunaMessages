package gr.crystalogic.sms.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;

import java.util.List;

import gr.crystalogic.sms.R;
import gr.crystalogic.sms.domain.Contact;
import gr.crystalogic.sms.domain.Conversation;
import gr.crystalogic.sms.providers.SmsProvider;
import gr.crystalogic.sms.ui.viewholders.ConversationViewHolder;
import gr.crystalogic.sms.utils.ContactsCache;

public class ConversationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Conversation> mModels;

    private final OnClickListener mOnClickListener;
    private final OnLongClickListener mOnLongClickListener;

    public ConversationsAdapter(List<Conversation> models, OnClickListener onClickListener, OnLongClickListener onLongClickListener) {
        mModels = models;
        mOnClickListener = onClickListener;
        mOnLongClickListener = onLongClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation, parent, false);
        view.setOnClickListener(mOnClickListener);
        view.setOnLongClickListener(mOnLongClickListener);
        return new ConversationViewHolder(view);
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
