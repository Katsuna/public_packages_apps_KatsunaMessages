/**
* Copyright (C) 2020 Manos Saratsis
*
* This file is part of Katsuna.
*
* Katsuna is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Katsuna is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Katsuna.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.katsuna.messages.ui.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import com.katsuna.commons.entities.UserProfileContainer;
import com.katsuna.messages.R;
import com.katsuna.messages.providers.metadata.MessageType;
import com.katsuna.messages.domain.Message;
import com.katsuna.messages.ui.viewholders.MessageViewHolder;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Message> mModels;
    private static final int INCOMING_MESSAGE = 1;
    private static final int OUTGOING_MESSAGE = 2;

    private final View.OnClickListener mOnClickListener;
    private final View.OnLongClickListener mOnLongClickListener;
    private final UserProfileContainer mUserProfileContainer;

    public MessagesAdapter(List<Message> models, View.OnClickListener onClickListener,
                           View.OnLongClickListener onLongClickListener,
                           UserProfileContainer userProfileContainer) {
        mModels = models;
        mOnClickListener = onClickListener;
        mOnLongClickListener = onLongClickListener;
        mUserProfileContainer = userProfileContainer;
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = INCOMING_MESSAGE;
        Message message = mModels.get(position);
        if (message.getType() == MessageType.OUTGOING) {
            viewType = OUTGOING_MESSAGE;
        }
        return viewType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        boolean isRightHanded = mUserProfileContainer.isRightHanded();

        switch (viewType) {
            case INCOMING_MESSAGE:
                if (isRightHanded) {
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_incoming, parent, false);
                } else {
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_outgoing, parent, false);
                }
                break;
            case OUTGOING_MESSAGE:
                if (isRightHanded) {
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_outgoing, parent, false);
                } else {
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_incoming, parent, false);
                }
                break;
            default:
                throw new RuntimeException("Unrecognized message type");
        }
        view.setOnClickListener(mOnClickListener);
        view.setOnLongClickListener(mOnLongClickListener);

        return new MessageViewHolder(view, mUserProfileContainer);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message model = mModels.get(position);
        ((MessageViewHolder) holder).bind(model);
    }

    @Override
    public int getItemCount() {
        return mModels.size();
    }
}
