package com.katsuna.sms.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import com.katsuna.commons.entities.Profile;
import com.katsuna.sms.R;
import com.katsuna.sms.providers.metadata.MessageType;
import com.katsuna.sms.domain.Message;
import com.katsuna.sms.ui.viewholders.MessageViewHolder;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Message> mModels;
    private static final int INCOMING_MESSAGE = 1;
    private static final int OUTGOING_MESSAGE = 2;

    private final View.OnClickListener mOnClickListener;
    private final View.OnLongClickListener mOnLongClickListener;
    private final Profile mProfile;

    public MessagesAdapter(List<Message> models, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener, Profile profile) {
        mModels = models;
        mOnClickListener = onClickListener;
        mOnLongClickListener = onLongClickListener;
        mProfile = profile;
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = INCOMING_MESSAGE;
        Message message = mModels.get(position);
        if (message.getType() == MessageType.OUTGOING) {
            viewType = OUTGOING_MESSAGE;
        }
        return viewType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case INCOMING_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_incoming, parent, false);
                break;
            case OUTGOING_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_outgoing, parent, false);
                break;
            default:
                throw new RuntimeException("Unrecognized message type");
        }
        view.setOnClickListener(mOnClickListener);
        view.setOnLongClickListener(mOnLongClickListener);

        return new MessageViewHolder(view, mProfile);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message model = mModels.get(position);
        ((MessageViewHolder) holder).bind(model);
    }

    @Override
    public int getItemCount() {
        return mModels.size();
    }
}
