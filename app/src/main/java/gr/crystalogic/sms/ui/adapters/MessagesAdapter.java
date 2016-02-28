package gr.crystalogic.sms.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import gr.crystalogic.sms.R;
import gr.crystalogic.sms.domain.Message;
import gr.crystalogic.sms.ui.viewholders.MessageViewHolder;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Message> mModels;

    private final View.OnClickListener mOnClickListener;
    private final View.OnLongClickListener mOnLongClickListener;

    public MessagesAdapter(List<Message> models, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
        mModels = models;
        mOnClickListener = onClickListener;
        mOnLongClickListener = onLongClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message, parent, false);
        view.setOnClickListener(mOnClickListener);
        view.setOnLongClickListener(mOnLongClickListener);
        return new MessageViewHolder(view);
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
