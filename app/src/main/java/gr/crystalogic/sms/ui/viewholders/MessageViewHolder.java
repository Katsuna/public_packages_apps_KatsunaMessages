package gr.crystalogic.sms.ui.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import gr.crystalogic.sms.R;
import gr.crystalogic.sms.dao.metadata.MessageType;
import gr.crystalogic.sms.domain.Message;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    private final TextView mDisplayName;
    private final TextView mDateTime;
    private final TextView mBody;
    private final ImageView mPhoto;


    public MessageViewHolder(View itemView) {
        super(itemView);
        mDisplayName = (TextView) itemView.findViewById(R.id.displayName);
        mDateTime = (TextView) itemView.findViewById(R.id.dateTime);
        mBody = (TextView) itemView.findViewById(R.id.body);
        mPhoto = (ImageView) itemView.findViewById(R.id.photo);
    }

    public void bind(Message message) {
        mDisplayName.setText(message.getAddress());

        String name = "";
        if (message.getType() == MessageType.OUTGOING) {
            mPhoto.setImageDrawable(null);
        } else if (message.getContact() != null) {
            name = message.getContact().getName();

            //load photo
            Picasso.with(itemView.getContext())
                    .load(message.getContact().getPhotoUri())
                    .fit()
                    .into(mPhoto);

        } else {
            name = message.getAddress();
        }
        mDisplayName.setText(name);

        mDateTime.setText(message.getDateFormatted());
        mBody.setText(message.getBody());
    }
}
