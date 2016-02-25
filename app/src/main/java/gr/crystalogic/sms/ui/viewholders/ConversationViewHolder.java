package gr.crystalogic.sms.ui.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import gr.crystalogic.sms.R;
import gr.crystalogic.sms.domain.Conversation;

public class ConversationViewHolder extends RecyclerView.ViewHolder {

    private final TextView mDisplayName;
    private final TextView mDateTime;
    private final TextView mMessageCount;
    private final TextView mSnippet;

    public ConversationViewHolder(View itemView) {
        super(itemView);
        mDisplayName = (TextView) itemView.findViewById(R.id.displayName);
        mDateTime = (TextView) itemView.findViewById(R.id.dateTime);
        mMessageCount = (TextView) itemView.findViewById(R.id.messageCount);
        mSnippet = (TextView) itemView.findViewById(R.id.snippet);
    }

    public void bind(Conversation model) {
        String name = "";
        if (model.getContact() != null) {
            name = model.getContact().getName();
        } else
        {

        }
        mDisplayName.setText(name);

        mDateTime.setText(model.getDateFormatted("HH:mm dd/MM/yyyy"));
        mMessageCount.setText(" (" + model.getMessageCount() + ") ");
        mSnippet.setText(model.getSnippet());
    }
}
