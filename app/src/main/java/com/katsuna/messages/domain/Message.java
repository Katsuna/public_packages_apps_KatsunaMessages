package com.katsuna.messages.domain;

import com.katsuna.commons.domain.Contact;

public class Message {

    private long id;
    private long date;
    private String address;
    private String body;
    private int type;
    private Contact contact;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDisplayName() {
        String displayName;
        if (contact != null && contact.getId() > 0) {
            displayName = contact.getDisplayName();
        } else {
            displayName = address;
        }
        return displayName;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

}
