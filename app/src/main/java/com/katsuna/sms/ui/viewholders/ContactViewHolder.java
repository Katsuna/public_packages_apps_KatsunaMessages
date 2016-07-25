package com.katsuna.sms.ui.viewholders;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.katsuna.commons.entities.Profile;
import com.katsuna.commons.entities.ProfileType;
import com.katsuna.sms.R;
import com.katsuna.sms.domain.Contact;
import com.katsuna.sms.ui.adapters.models.ContactListItemModel;
import com.squareup.picasso.Picasso;

public class ContactViewHolder extends RecyclerView.ViewHolder {
    private final View mView;
    private final TextView mSeparatorView;
    private final TextView mDisplayName;
    private final ImageView mPhoto;
    private final ImageView mSeparatorImage;
    private final LinearLayout mSeparatorWrapper;
    private final Profile mProfile;

    public ContactViewHolder(View view, Profile profile) {
        super(view);
        mView = view;
        mSeparatorView = (TextView) view.findViewById(R.id.separator);
        mSeparatorImage = (ImageView) view.findViewById(R.id.separator_image);
        mDisplayName = (TextView) view.findViewById(R.id.contact_name);
        mSeparatorWrapper = (LinearLayout) itemView.findViewById(R.id.separator_wrapper);
        mPhoto = (ImageView) view.findViewById(R.id.photo);
        mProfile = profile;
        adjustProfile();
    }

    private void adjustProfile() {
        if (mProfile != null) {
            int size = itemView.getResources().getDimensionPixelSize(R.dimen.contact_photo_size_intemediate);
            int fontSize = itemView.getResources().getDimensionPixelSize(R.dimen.font_size_intemediate);
            if (mProfile.getType() == ProfileType.ADVANCED.getNumVal()) {
                size = itemView.getResources().getDimensionPixelSize(R.dimen.contact_photo_size_advanced);
                fontSize = itemView.getResources().getDimensionPixelSize(R.dimen.font_size_advanced);
            } else if (mProfile.getType() == ProfileType.SIMPLE.getNumVal()) {
                size = itemView.getResources().getDimensionPixelSize(R.dimen.contact_photo_size_simple);
                fontSize = itemView.getResources().getDimensionPixelSize(R.dimen.font_size_simple);
            }

            //adjust photo
            ViewGroup.LayoutParams layoutParams = mPhoto.getLayoutParams();
            layoutParams.height = size;
            layoutParams.width = size;

            //adjust displayName
            mDisplayName.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
        }
    }

    public void bind(final ContactListItemModel model) {
        initialize();

        final Contact contact = model.getContact();

        //load photo
        Picasso.with(mView.getContext())
                .load(contact.getPhotoUri())
                .fit()
                .into(mPhoto);

        mDisplayName.setText(contact.getDisplayName());

        switch (model.getSeparator()) {
            case FIRST_LETTER:
                mSeparatorView.setText(contact.getDisplayName().subSequence(0, 1).toString());
                mSeparatorView.setVisibility(View.VISIBLE);
                mSeparatorWrapper.setVisibility(View.VISIBLE);
                break;
            case STARRED:
                mSeparatorImage.setImageDrawable(ContextCompat.getDrawable(mView.getContext(), R.drawable.ic_star_grey800_24dp));
                mSeparatorImage.setVisibility(View.VISIBLE);
                mSeparatorWrapper.setVisibility(View.VISIBLE);
                break;
            case NONE:
                mSeparatorWrapper.setVisibility(View.GONE);
                break;
        }
    }

    private void initialize() {
        mSeparatorWrapper.setVisibility(View.GONE);
        mSeparatorView.setVisibility(View.GONE);
        mSeparatorImage.setVisibility(View.GONE);
    }

}