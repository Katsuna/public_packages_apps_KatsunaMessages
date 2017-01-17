package com.katsuna.messages.ui.viewholders;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.katsuna.commons.entities.ProfileType;
import com.katsuna.messages.R;
import com.katsuna.messages.domain.Contact;
import com.katsuna.messages.ui.adapters.models.ContactListItemModel;
import com.katsuna.messages.ui.listeners.IContactInteractionListener;
import com.squareup.picasso.Picasso;

public class ContactViewHolder extends RecyclerView.ViewHolder {
    private final View mView;
    private final TextView mSeparatorView;
    private final TextView mDisplayName;
    private final ImageView mPhoto;
    private final ImageView mSeparatorImage;
    private final LinearLayout mSeparatorWrapper;
    private final IContactInteractionListener mListener;
    private final View mContactContainer;

    public ContactViewHolder(View view, IContactInteractionListener listener) {
        super(view);
        mView = view;
        mSeparatorView = (TextView) view.findViewById(R.id.separator);
        mSeparatorImage = (ImageView) view.findViewById(R.id.separator_image);
        mDisplayName = (TextView) view.findViewById(R.id.contact_name);
        mSeparatorWrapper = (LinearLayout) itemView.findViewById(R.id.separator_wrapper);
        mPhoto = (ImageView) view.findViewById(R.id.photo);
        mContactContainer = view.findViewById(R.id.contact_container);
        mListener = listener;
        adjustProfile();
    }

    private void adjustProfile() {
        ProfileType opticalSizeProfile = mListener.getUserProfileContainer().getOpticalSizeProfile();

        if (opticalSizeProfile != null) {
            int size = itemView.getResources()
                    .getDimensionPixelSize(R.dimen.common_contact_photo_size_intemediate);
            int fontSize = itemView.getResources().getDimensionPixelSize(R.dimen.font_size_intemediate);
            if (opticalSizeProfile == ProfileType.ADVANCED) {
                size = itemView.getResources()
                        .getDimensionPixelSize(R.dimen.common_contact_photo_size_advanced);
                fontSize = itemView.getResources().getDimensionPixelSize(R.dimen.font_size_advanced);
            } else if (opticalSizeProfile == ProfileType.SIMPLE) {
                size = itemView.getResources()
                        .getDimensionPixelSize(R.dimen.common_contact_photo_size_simple);
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

        mContactContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.selectContact(contact);
            }
        });
    }

    private void initialize() {
        mSeparatorWrapper.setVisibility(View.GONE);
        mSeparatorView.setVisibility(View.GONE);
        mSeparatorImage.setVisibility(View.GONE);
    }

}