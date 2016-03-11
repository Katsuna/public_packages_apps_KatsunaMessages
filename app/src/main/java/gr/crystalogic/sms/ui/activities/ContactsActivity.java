package gr.crystalogic.sms.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ContactsActivity.this);
                alert.setTitle(R.string.select_phone);
                final EditText input = new EditText(ContactsActivity.this);
                input.setInputType(InputType.TYPE_CLASS_PHONE);
                alert.setView(input);
                alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent i = new Intent(view.getContext(), ConversationActivity.class);
                        i.putExtra("conversationNumber", input.getText().toString());
                        startActivity(i);
                    }
                });
                alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //Put actions for CANCEL button here, or leave in blank
                    }
                });
                final AlertDialog dialog = alert.show();

                //focus on input
                input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        }
                    }
                });
            }
        });

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
