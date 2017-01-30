package com.katsuna.messages.domain;

import com.katsuna.commons.domain.Contact;

public class Conversation {

    private long id;
    private long date;
    //TODO check if multiple Ids (comma separated) arrive here....
    private long recipientIds;
    private String snippet;
    private boolean unanswered;
    private int read;

    private Contact contact;

    @Override
    public String toString() {
        return "Conversation: id=" + id + " date=" + date + " recipientIds=" + recipientIds + " snippet=" + snippet;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRecipientIds() {
        return recipientIds;
    }

    public void setRecipientIds(long recipientIds) {
        this.recipientIds = recipientIds;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public boolean isUnanswered() {
        return unanswered;
    }

    public void setUnanswered(boolean unanswered) {
        this.unanswered = unanswered;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }
}
