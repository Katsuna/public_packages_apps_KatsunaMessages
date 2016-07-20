package com.katsuna.sms.ui.adapters.models;

import com.katsuna.sms.domain.Contact;
import com.katsuna.sms.utils.Separator;

public class ContactListItemModel {

    private Contact contact;
    private Separator separator = Separator.NONE;
    private boolean premium;
    private boolean selected;

    public ContactListItemModel() {
    }

    public ContactListItemModel(ContactListItemModel model) {
        setSeparator(model.getSeparator());
        setPremium(model.isPremium());
        setSelected(model.isSelected());
        if (model.getContact() != null) {
            contact = new Contact(model.getContact());
        }
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public Separator getSeparator() {
        return separator;
    }

    public void setSeparator(Separator separator) {
        this.separator = separator;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    private boolean isSelected() {
        return selected;
    }

    private void setSelected(boolean selected) {
        this.selected = selected;
    }
}