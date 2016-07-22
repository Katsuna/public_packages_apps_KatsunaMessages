package com.katsuna.sms.ui.viewholders;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.katsuna.sms.R;
import com.katsuna.sms.domain.Conversation;
import com.katsuna.sms.utils.DateFormatter;
import com.squareup.picasso.Picasso;

public class ConversationViewHolder extends RecyclerView.ViewHolder {

    private final TextView mDisplayName;
    private final TextView mDateTime;
    private final TextView mSnippet;
    private final ImageView mPhoto;

    public ConversationViewHolder(View itemView) {
        super(itemView);
        mDisplayName = (TextView) itemView.findViewById(R.id.displayName);
        mDateTime = (TextView) itemView.findViewById(R.id.dateTime);
        mSnippet = (TextView) itemView.findViewById(R.id.body);
        mPhoto = (ImageView) itemView.findViewById(R.id.photo);
    }

    public void bind(Conversation conversation) {
        String name;
        if (conversation.getContact().getId() > 0) {
            name = conversation.getContact().getName();

            //load photo
            Picasso.with(itemView.getContext())
                    .load(conversation.getContact().getPhotoUri())
                    .fit()
                    .into(mPhoto);

        } else {
            mPhoto.setImageBitmap(null);
            name = conversation.getContact().getAddress();
        }
        mDisplayName.setText(name);

        mDateTime.setText(DateFormatter.format(itemView.getContext(), conversation.getDate()));
        mSnippet.setText(conversation.getSnippet());

        if (conversation.getRead() == 0) {
            mDisplayName.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.dark_grey));
        } else if (conversation.isUnanswered()) {
            mDisplayName.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.pink));
        } else {
            mDisplayName.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.black87));
        }
    }
}
