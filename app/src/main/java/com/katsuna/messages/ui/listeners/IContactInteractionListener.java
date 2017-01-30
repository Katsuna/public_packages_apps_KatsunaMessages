package com.katsuna.messages.ui.listeners;

import com.katsuna.commons.domain.Contact;
import com.katsuna.commons.entities.UserProfileContainer;

public interface IContactInteractionListener {

    void selectContact(Contact contact);

    UserProfileContainer getUserProfileContainer();

}
