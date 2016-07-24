package com.katsuna.sms.utils;

import java.util.HashMap;

import com.katsuna.sms.domain.Contact;

public class ContactsCache {

    private static final ContactsCache instance = new ContactsCache();

    private ContactsCache() {
    }

    public static ContactsCache getInstance() {
        return instance;
    }

    private final HashMap<Long, Contact> contactsMap = new HashMap<>();

    public void putContact(long recipientId, Contact contact) {
        contactsMap.put(recipientId, contact);
    }

    public Contact getContact(long recipientId) {
        return contactsMap.get(recipientId);
    }
}