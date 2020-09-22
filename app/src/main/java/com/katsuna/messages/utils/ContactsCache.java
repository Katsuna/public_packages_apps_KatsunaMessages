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

    public void removeContact(long recipientId) {
        contactsMap.remove(recipientId);
    }

    public Contact getContact(long recipientId) {
        return contactsMap.get(recipientId);
    }
}
