package gr.crystalogic.sms.ui.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.List;

import gr.crystalogic.sms.R;
import gr.crystalogic.sms.dao.SmsDao;
import gr.crystalogic.sms.domain.Contact;
import gr.crystalogic.sms.domain.Conversation;

public class ConversationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        SmsDao dao = new SmsDao(this);
        //dao.showRows(Uris.PHONES);

        List<Conversation> conversations = dao.getConversations();

        for(Conversation conversation: conversations) {
            Log.e("3ee", conversation.toString());

            String address = dao.getAddress(conversation.getRecipientIds());
            Log.e("xxx", " address for rid: " + conversation.getRecipientIds() + " = "  + address);
            Contact contact = dao.getContactByAddress(address);
            if (contact == null) {
                Log.e("xxx", " contact not found for address " + address);
            } else {
                Log.e("xxx", " contact found: " + contact.toString());
            }

            //dao.getConversationMessages(conversation.getId());
        }

    }

}
