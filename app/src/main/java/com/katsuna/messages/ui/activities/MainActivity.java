package com.katsuna.messages.ui.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.katsuna.commons.entities.UserProfile;
import com.katsuna.commons.entities.UserProfileContainer;
import com.katsuna.commons.ui.SearchBarActivity;
import com.katsuna.commons.utils.KatsunaAlertBuilder;
import com.katsuna.commons.utils.KatsunaUtils;
import com.katsuna.messages.R;
import com.katsuna.messages.domain.Conversation;
import com.katsuna.messages.providers.SmsProvider;
import com.katsuna.messages.ui.adapters.ConversationsAdapter;
import com.katsuna.messages.ui.listeners.IConversationInteractionListener;
import com.katsuna.messages.utils.Constants;
import com.katsuna.messages.utils.Device;
import com.katsuna.messages.utils.Settings;

import java.util.List;

import static com.katsuna.commons.utils.Constants.KATSUNA_PRIVACY_URL;

public class MainActivity extends SearchBarActivity
        implements IConversationInteractionListener {

    private final String[] permissions = new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS};
    private RecyclerView mRecyclerView;
    private ConversationsAdapter mAdapter;
    private TextView mNoResultsView;
    private View mPopupFrame;
    private DrawerLayout mDrawer;
    private int mSelectedConversationPosition = ConversationsAdapter.NO_CONVERSATION_POSITION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initControls();

        mFab1 = findViewById(R.id.fab);
        mFab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectContact();
            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                mDrawer.closeDrawers();

                switch (item.getItemId()) {
                    case R.id.drawer_settings:
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        break;
                    case R.id.drawer_info:
                        startActivity(new Intent(MainActivity.this, InfoActivity.class));
                        break;
                    case R.id.drawer_privacy:
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(KATSUNA_PRIVACY_URL));
                        startActivity(browserIntent);
                        break;
                }

                return true;
            }
        });

        checkIsDefaultSmsHandler();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                showPopup(false);
                return true;
        }

        return super.onOptionsItemSelected(item);
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

    private void selectContact() {
        PackageManager manager = getPackageManager();
        Intent i = manager.getLaunchIntentForPackage(KatsunaUtils.KATSUNA_CONTACTS_PACKAGE);
        if (i == null) {
            showContactsAppInstallationDialog();
        } else {
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            startActivity(i);
        }
    }

    private void showContactsAppInstallationDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        String contactsAppName = getString(R.string.common_katsuna_contacts_app);
        String title = getString(R.string.common_missing_app, contactsAppName);
        alert.setTitle(title);
        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                KatsunaUtils.goToGooglePlay(MainActivity.this,
                        KatsunaUtils.KATSUNA_CONTACTS_PACKAGE);
            }
        });
        alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Put actions for CANCEL button here, or leave in blank
            }
        });
        alert.show();
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
        loadConversations();

        scrollToPosition(mSelectedConversationPosition);

        if (mItemSelected) {
            deselectItem();
        }
    }

    private void scrollToPosition(int position) {
        if (position != ConversationsAdapter.NO_CONVERSATION_POSITION) {
            ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .scrollToPositionWithOffset(position, (mRecyclerView.getHeight() / 2) - 170);
        }
    }

    @Override
    protected void showPopup(boolean show) {
        if (show) {
            //don't show popup if menu drawer is open or conversation is selected.
            if (!mDrawer.isDrawerOpen(GravityCompat.START) && !mItemSelected) {
                mPopupFrame.setVisibility(View.VISIBLE);
                mPopupButton1.setVisibility(View.VISIBLE);
                mPopupVisible = true;
            }
        } else {
            mPopupFrame.setVisibility(View.GONE);
            mPopupButton1.setVisibility(View.GONE);
            mPopupVisible = false;
        }
    }

    private void initControls() {
        initToolbar(R.drawable.common_ic_menu_black_24dp);
        mRecyclerView = findViewById(R.id.conversations_list);
        mRecyclerView.setItemAnimator(null);
        mNoResultsView = findViewById(R.id.no_results);

        mDrawer = findViewById(R.id.drawer_layout);

        mFabContainer = findViewById(R.id.fab_container);
        mButtonsContainer1 = findViewById(R.id.message_buttons_container);
        mPopupButton1 = findViewById(R.id.message_button);
        mPopupButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectContact();
            }
        });

        mPopupFrame = findViewById(R.id.popup_frame);
        mPopupFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(false);
            }
        });

        mLastTouchTimestamp = System.currentTimeMillis();
        initPopupActionHandler();
        initDeselectionActionHandler();
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
        mAdapter = new ConversationsAdapter(conversations, this);
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
            case Constants.REQUEST_CODE_ASK_CALL_PERMISSION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showConversation(long conversationId) {
        Intent intent = new Intent(getApplicationContext(), ConversationActivity.class);
        intent.putExtra(Constants.EXTRA_CONVERASTION_ID, conversationId);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void callContact(Conversation conversation) {
        callNumber(conversation.getContact().getMessageAddress());
    }

    private void callNumber(String number) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},
                    Constants.REQUEST_CODE_ASK_CALL_PERMISSION);
            return;
        }

        Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
        startActivity(i);
    }

    @Override
    public void sendSMS(Conversation conversation) {
        showConversation(conversation.getId());
    }

    @Override
    public void deleteConversation(final Conversation conversation) {
        KatsunaAlertBuilder builder = new KatsunaAlertBuilder(this);
        builder.setTitle(R.string.confirmation);
        builder.setMessage(R.string.confirmation_delete_conversation);
        builder.setView(R.layout.common_katsuna_alert);
        builder.setUserProfileContainer(mUserProfileContainer);
        builder.setOkListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmsProvider dao = new SmsProvider(MainActivity.this);
                int rowsDeleted = dao.deleteConversation(conversation);
                if (rowsDeleted > 0) {
                    loadConversations();
                    Toast.makeText(MainActivity.this, R.string.conversation_deleted,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.create().show();
    }

    @Override
    public void selectItemByStartingLetter(String s) {
        // No search bar here so nothing to be done.
    }

    @Override
    public UserProfileContainer getUserProfileContainer() {
        return mUserProfileContainer;
    }

    @Override
    public void selectConversation(int position) {
        if (mItemSelected) {
            deselectItem();
        } else {
            focusConversation(position);
        }
    }

    @Override
    protected void deselectItem() {
        mDrawer.setBackgroundColor(ContextCompat.getColor(this, R.color.common_grey50));
        mSelectedConversationPosition = ConversationsAdapter.NO_CONVERSATION_POSITION;
        mItemSelected = false;
        mAdapter.deselectConversation();
        tintFabs(false);
        adjustFabPosition(true);
        refreshLastTouchTimestamp();
    }

    @Override
    public void focusConversation(int position) {
        mDrawer.setBackgroundColor(ContextCompat.getColor(this, R.color.common_black07));
        mSelectedConversationPosition = position;

        mAdapter.setSelectedConversationAtPosition(position);
        scrollToPosition(position);

        tintFabs(true);

        adjustFabPosition(false);
        mItemSelected = true;
        refreshLastSelectionTimestamp();
    }

    @Override
    public UserProfile getUserProfile() {
        return mUserProfileContainer.getActiveUserProfile();
    }

}
