package gr.crystalogic.sms.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import gr.crystalogic.sms.R;
import gr.crystalogic.sms.domain.Conversation;
import gr.crystalogic.sms.ui.viewholders.ConversationViewHolder;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

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
        Conversation model = mModels.get(position);
        ((ConversationViewHolder) holder).bind(model);
    }

    @Override
    public int getItemCount() {
        return mModels.size();
    }

    public Conversation getItemAtPosition(int position) {
        return mModels.get(position);
    }
}
