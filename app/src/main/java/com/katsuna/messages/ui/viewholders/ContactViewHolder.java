package com.katsuna.messages.ui.viewholders;

import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.katsuna.commons.domain.Contact;
import com.katsuna.commons.entities.ColorProfile;
import com.katsuna.commons.entities.ColorProfileKey;
import com.katsuna.commons.entities.OpticalParams;
import com.katsuna.commons.entities.SizeProfile;
import com.katsuna.commons.entities.SizeProfileKey;
import com.katsuna.commons.entities.UserProfileContainer;
import com.katsuna.commons.ui.adapters.models.ContactListItemModel;
import com.katsuna.commons.utils.ColorCalc;
import com.katsuna.commons.utils.SizeAdjuster;
import com.katsuna.commons.utils.SizeCalc;
import com.katsuna.messages.R;
import com.katsuna.messages.ui.listeners.IContactInteractionListener;
import com.squareup.picasso.Picasso;

public class ContactViewHolder extends RecyclerView.ViewHolder {
    private final UserProfileContainer mUserProfileContainer;
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
        mUserProfileContainer = listener.getUserProfileContainer();
        adjustProfile();
    }

    private void adjustProfile() {
        SizeProfile opticalSizeProfile = mUserProfileContainer.getOpticalSizeProfile();

        if (opticalSizeProfile != null) {
            int size = itemView.getResources()
                    .getDimensionPixelSize(R.dimen.common_contact_photo_size_intemediate);
            if (opticalSizeProfile == SizeProfile.ADVANCED) {
                size = itemView.getResources()
                        .getDimensionPixelSize(R.dimen.common_contact_photo_size_advanced);
            } else if (opticalSizeProfile == SizeProfile.SIMPLE) {
                size = itemView.getResources()
                        .getDimensionPixelSize(R.dimen.common_contact_photo_size_simple);
            }

            ViewGroup.LayoutParams lp = mPhoto.getLayoutParams();
            lp.width = size;
            lp.height = size;


            // display name
            OpticalParams nameOpticalParams = SizeCalc.getOpticalParams(SizeProfileKey.TITLE,
                    opticalSizeProfile);
            SizeAdjuster.adjustText(itemView.getContext(), mDisplayName, nameOpticalParams);
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
                mSeparatorView.setText(contact.getFirstLetterNormalized());
                mSeparatorView.setVisibility(View.VISIBLE);
                mSeparatorWrapper.setVisibility(View.VISIBLE);
                break;
            case STARRED:
                mSeparatorImage.setImageDrawable(ContextCompat.getDrawable(mView.getContext(), R.drawable.ic_star_grey800_24dp));
                mSeparatorImage.setVisibility(View.VISIBLE);
                mSeparatorWrapper.setVisibility(View.VISIBLE);
                break;
            case NONE:
                mSeparatorView.setVisibility(View.GONE);
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
        mSeparatorWrapper.setVisibility(View.INVISIBLE);
        mSeparatorView.setVisibility(View.GONE);
        mSeparatorImage.setVisibility(View.GONE);
    }

    public void searchFocus(boolean flag) {
        if (mSeparatorView != null) {
            if (flag) {
                ColorProfile colorProfile = mUserProfileContainer.getColorProfile();
                // set action buttons background color
                int color1 = ColorCalc.getColor(itemView.getContext(),
                        ColorProfileKey.ACCENT1_COLOR, colorProfile);

                GradientDrawable circle = (GradientDrawable) ContextCompat.getDrawable(
                        itemView.getContext(), R.drawable.circle_black_36dp);
                circle.setColor(color1);

                mSeparatorView.setBackground(circle);
            } else {
                mSeparatorView.setBackground(null);
            }
        }
    }

}