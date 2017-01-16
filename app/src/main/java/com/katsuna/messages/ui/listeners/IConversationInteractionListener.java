package com.katsuna.messages.ui.listeners;

import com.katsuna.commons.entities.UserProfileContainer;
import com.katsuna.messages.domain.Conversation;

public interface IConversationInteractionListener {

    void selectConversation(int position);

    void callContact(Conversation conversation);

    void sendSMS(Conversation conversation);

    void deleteConversation(Conversation conversation);

    UserProfileContainer getUserProfileContainer();

}
