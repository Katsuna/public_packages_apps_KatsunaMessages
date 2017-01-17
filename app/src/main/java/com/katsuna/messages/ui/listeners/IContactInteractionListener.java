package com.katsuna.messages.ui.listeners;

import com.katsuna.commons.entities.UserProfileContainer;
import com.katsuna.messages.domain.Contact;

public interface IContactInteractionListener {

    void selectContact(Contact contact);

    UserProfileContainer getUserProfileContainer();

}
