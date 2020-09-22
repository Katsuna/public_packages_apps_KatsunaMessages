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
package com.katsuna.messages.domain;

import com.katsuna.commons.domain.Contact;
import com.katsuna.messages.utils.ConversationStatus;

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

    public int getStatus() {
        int output;
        if (unanswered) {
            if (getRead() == 0) {
                // unread conversation
                output = ConversationStatus.UNREAD;
            } else {
                // read conversation
                output = ConversationStatus.READ;
            }
        } else {
            output = ConversationStatus.SENT;
        }

        return output;
    }

}
