package com.katsuna.messages.utils;

import android.util.LongSparseArray;

import com.katsuna.commons.domain.Contact;

public class ContactsCache {

    private static final ContactsCache instance = new ContactsCache();
    private final LongSparseArray<Contact> contactsMap = new LongSparseArray<>();

    private ContactsCache() {
    }

    public static ContactsCache getInstance() {
        return instance;
    }

    public void putContact(long recipientId, Contact contact) {
        contactsMap.put(recipientId, contact);
    }

    public Contact getContact(long recipientId) {
        return contactsMap.get(recipientId);
    }
}
