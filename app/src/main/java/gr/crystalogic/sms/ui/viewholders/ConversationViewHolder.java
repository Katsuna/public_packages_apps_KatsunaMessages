package gr.crystalogic.sms.ui.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import gr.crystalogic.sms.R;
import gr.crystalogic.sms.domain.Conversation;

public class ConversationViewHolder extends RecyclerView.ViewHolder {

    private final TextView mDisplayName;
    private final TextView mDateTime;
    private final TextView mMessageCount;
    private final TextView mSnippet;
    private final ImageView mPhoto;

    public ConversationViewHolder(View itemView) {
        super(itemView);
        mDisplayName = (TextView) itemView.findViewById(R.id.displayName);
        mDateTime = (TextView) itemView.findViewById(R.id.dateTime);
        mMessageCount = (TextView) itemView.findViewById(R.id.messageCount);
        mSnippet = (TextView) itemView.findViewById(R.id.body);
        mPhoto = (ImageView) itemView.findViewById(R.id.photo);
    }

    public void bind(Conversation conversation) {
        String name;
        if (conversation.getContact() != null) {
            name = conversation.getContact().getName();

            //load photo
            Picasso.with(itemView.getContext())
                    .load(conversation.getContact().getPhotoUri())
                    .fit()
                    .into(mPhoto);

        } else {
            name = conversation.getAddress();
        }
        mDisplayName.setText(name);

        mDateTime.setText(conversation.getDateFormatted());
        mMessageCount.setText(" (" + conversation.getMessageCount() + ") ");
        mSnippet.setText(conversation.getSnippet());
    }
}
