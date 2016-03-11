package gr.crystalogic.sms.ui.viewholders;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import gr.crystalogic.sms.R;
import gr.crystalogic.sms.domain.Contact;
import gr.crystalogic.sms.ui.adapters.models.ContactListItemModel;

public class ContactViewHolder extends RecyclerView.ViewHolder {
    private final View mView;
    private final TextView mSeparatorView;
    private final TextView mContentView;
    private final ImageView mPhoto;
    private final ImageView mSeparatorImage;

    public ContactViewHolder(View view) {
        super(view);
        mView = view;
        mSeparatorView = (TextView) view.findViewById(R.id.separator);
        mSeparatorImage = (ImageView) view.findViewById(R.id.separator_image);
        mContentView = (TextView) view.findViewById(R.id.contact_name);
        mPhoto = (ImageView) view.findViewById(R.id.photo);
    }

    public void bind(final ContactListItemModel model) {
        initialize();

        final Contact contact = model.getContact();

        //load photo
        Picasso.with(mView.getContext())
                .load(contact.getPhotoUri())
                .fit()
                .into(mPhoto);

        mContentView.setText(contact.getDisplayName());

        switch (model.getSeparator()) {
            case FIRST_LETTER:
                mSeparatorView.setText(contact.getDisplayName().subSequence(0, 1).toString());
                mSeparatorView.setVisibility(View.VISIBLE);
                break;
            case STARRED:
                mSeparatorImage.setImageDrawable(ContextCompat.getDrawable(mView.getContext(), R.drawable.ic_star_pink_500_24dp));
                mSeparatorImage.setVisibility(View.VISIBLE);
                break;
            case NONE:
                mSeparatorView.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private void initialize() {
        mSeparatorView.setVisibility(View.GONE);
        mSeparatorImage.setVisibility(View.GONE);
    }

}