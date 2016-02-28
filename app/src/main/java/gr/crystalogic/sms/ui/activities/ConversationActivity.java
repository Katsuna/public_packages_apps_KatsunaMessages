package gr.crystalogic.sms.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import gr.crystalogic.sms.R;
import gr.crystalogic.sms.dao.SmsDao;
import gr.crystalogic.sms.domain.Message;
import gr.crystalogic.sms.ui.adapters.MessagesAdapter;

public class ConversationActivity extends BaseActivity {

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        initToolbar();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        initControls();


        //load decisions
        long conversationId = getConversationIdFromIntent();
        SmsDao dao = new SmsDao(this);
        //dao.showRows(Uris.PHONES);
        List<Message> messages = dao.getConversationMessages(conversationId);
        MessagesAdapter adapter = new MessagesAdapter(messages, null, null);
        mRecyclerView.setAdapter(adapter);
    }

    private void initControls() {
        mRecyclerView = (RecyclerView) findViewById(R.id.messagesRecyclerView);
    }

    private long getConversationIdFromIntent() {
        Intent i = getIntent();
        return i.getExtras().getLong("conversationId");
    }

}
