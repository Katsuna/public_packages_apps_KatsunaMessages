package com.katsuna.messages.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import com.katsuna.commons.entities.Profile;
import com.katsuna.commons.entities.ProfileType;
import com.katsuna.commons.utils.ProfileReader;
import com.katsuna.messages.R;
import com.katsuna.messages.providers.SmsProvider;
import com.katsuna.messages.domain.Conversation;
import com.katsuna.messages.ui.adapters.ConversationsAdapter;
import com.katsuna.messages.utils.Constants;
import com.katsuna.messages.utils.Device;
import com.katsuna.messages.utils.Settings;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String[] permissions = new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS};
    private RecyclerView mRecyclerView;
    private ConversationsAdapter mAdapter;
    private TextView mNoResultsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ContactsActivity.class);
                startActivity(i);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initControls();

        checkIsDefaultSmsHandler();
    }

    private void checkIsDefaultSmsHandler() {
        String smsDefaultSetting = Settings.readSetting(this, Constants.DEFAULT_SMS_KEY, Constants.DEFAULT_SMS_NOT_SET);
        //check for for the first time only
        if (smsDefaultSetting.equals(Constants.DEFAULT_SMS_NOT_SET)) {
            if (!Device.isDefaultApp(this)) {
                Device.makeDefaultApp(this, Constants.DEF_SMS_REQ_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.DEF_SMS_REQ_CODE:
                if (Device.isDefaultApp(this)) {
                    Settings.setSetting(this, Constants.DEFAULT_SMS_KEY, Constants.DEFAULT_SMS_ON);
                } else {
                    Settings.setSetting(this, Constants.DEFAULT_SMS_KEY, Constants.DEFAULT_SMS_OFF);
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupProfile();
        loadConversations();
    }

    private void setupProfile() {
        Profile freshProfileFromContentProvider = ProfileReader.getProfile(this);
        Profile profileFromPreferences = getProfileFromPreferences();
        if (freshProfileFromContentProvider == null) {
            setSelectedProfile(profileFromPreferences);
        } else {
            if (profileFromPreferences.getType() == ProfileType.AUTO.getNumVal()) {
                setSelectedProfile(freshProfileFromContentProvider);
            } else {
                setSelectedProfile(profileFromPreferences);
            }
        }
    }

    private void initControls() {
        mRecyclerView = (RecyclerView) findViewById(R.id.conversations_list);
        mNoResultsView = (TextView) findViewById(R.id.no_results);
    }

    private void showNoResultsView() {
        if (mAdapter.getItemCount() > 0) {
            mNoResultsView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mNoResultsView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }

    private void loadConversations() {

        if (!Device.hasAllPermissions(this, permissions)) {
            Device.requestPermissions(this, permissions, Constants.REQUEST_CODE_READ_SMS_AND_CONTACTS);
            return;
        }

        SmsProvider dao = new SmsProvider(this);
        List<Conversation> conversations = dao.getConversations();
        mAdapter = new ConversationsAdapter(conversations,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = mRecyclerView.getChildAdapterPosition(v);
                        Conversation conversation = mAdapter.getItemAtPosition(position);
                        showConversation(conversation.getId());
                    }
                },
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return false;
                    }
                }, mProfile);
        mRecyclerView.setAdapter(mAdapter);

        showNoResultsView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_CODE_READ_SMS_AND_CONTACTS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    loadConversations();
                } else {
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showConversation(long conversationId) {
        Intent intent = new Intent(getApplicationContext(), ConversationActivity.class);
        intent.putExtra("conversationId", conversationId);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.drawer_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        }
/*
        else if (id == R.id.drawer_help) {

        } else if (id == R.id.drawer_info) {

        }
*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
