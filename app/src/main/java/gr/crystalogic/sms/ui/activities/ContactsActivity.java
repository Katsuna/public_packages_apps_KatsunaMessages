package gr.crystalogic.sms.ui.activities;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import gr.crystalogic.sms.R;
import gr.crystalogic.sms.dao.ContactDao;
import gr.crystalogic.sms.domain.Contact;
import gr.crystalogic.sms.ui.adapters.ContactsRecyclerViewAdapter;
import gr.crystalogic.sms.ui.adapters.models.ContactListItemModel;
import gr.crystalogic.sms.utils.ContactArranger;

public class ContactsActivity extends BaseActivity {

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        initToolbar();
        initControls();
        loadContacts();
    }

    private void initControls() {
        mRecyclerView = (RecyclerView) findViewById(R.id.contacts_list);
    }

    private void loadContacts() {
        ContactDao dao = new ContactDao(this);
        List<Contact> contactList = dao.getContacts();
        List<ContactListItemModel> models = ContactArranger.getContactsProcessed(contactList);
        ContactsRecyclerViewAdapter adapter = new ContactsRecyclerViewAdapter(getDeepCopy(models));
        mRecyclerView.setAdapter(adapter);
    }

    private List<ContactListItemModel> getDeepCopy(List<ContactListItemModel> contactListItemModels) {
        List<ContactListItemModel> output = new ArrayList<>();
        for (ContactListItemModel model : contactListItemModels) {
            output.add(new ContactListItemModel(model));
        }
        return output;
    }

}
