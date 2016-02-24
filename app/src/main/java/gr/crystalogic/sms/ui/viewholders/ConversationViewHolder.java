package gr.crystalogic.sms.ui.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import gr.crystalogic.sms.R;
import gr.crystalogic.sms.domain.Conversation;

public class ConversationViewHolder extends RecyclerView.ViewHolder {

    private final TextView mDescription;

    public ConversationViewHolder(View itemView) {
        super(itemView);
        mDescription = (TextView) itemView.findViewById(R.id.description);
    }

    public void bind(Conversation model) {
        mDescription.setText(model.toString());
    }
}
