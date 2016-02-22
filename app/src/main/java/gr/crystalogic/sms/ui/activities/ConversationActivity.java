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
import gr.crystalogic.sms.dao.Uris;
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
        dao.showRows(Uris.CONVERSATIONS_SIMPLE);

        List<Conversation> conversations = dao.getConversations();

        if (conversations.size() == 0) {
            Log.e("ee", "zero");
        }

        for(Conversation conversation: conversations) {
            Log.e("3ee", conversation.toString());
            Log.e("3ee", "Show messages for threadId: " + conversation.getThreadId());
            dao.getConversationMessages(conversation.getThreadId());
        }

    }

}
