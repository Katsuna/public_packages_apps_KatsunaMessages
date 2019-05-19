package com.katsuna.messages.ui.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.katsuna.commons.controls.KatsunaNavigationView;
import com.katsuna.commons.entities.UserProfile;
import com.katsuna.commons.entities.UserProfileContainer;
import com.katsuna.commons.ui.SearchBarActivity;
import com.katsuna.commons.utils.BrowserUtils;
import com.katsuna.commons.utils.KatsunaAlertBuilder;
import com.katsuna.commons.utils.KatsunaSnackbarBuilder;
import com.katsuna.commons.utils.KatsunaUtils;
import com.katsuna.commons.utils.KeyboardUtils;
import com.katsuna.messages.R;
import com.katsuna.messages.domain.Conversation;
import com.katsuna.messages.providers.SmsProvider;
import com.katsuna.messages.ui.adapters.ConversationsAdapter;
import com.katsuna.messages.ui.listeners.IConversationInteractionListener;
import com.katsuna.messages.utils.Constants;
import com.katsuna.messages.utils.ContactsCache;
import com.katsuna.messages.utils.Device;
import com.katsuna.messages.utils.Settings;

import java.util.List;

import static com.katsuna.commons.utils.Constants.ADD_TO_CONTACT_ACTION;
import static com.katsuna.commons.utils.Constants.ADD_TO_CONTACT_ACTION_NUMBER;
import static com.katsuna.commons.utils.Constants.CREATE_CONTACT_ACTION;
import static com.katsuna.commons.utils.Constants.KATSUNA_PRIVACY_URL;
import static com.katsuna.commons.utils.Constants.KATSUNA_TERMS_OF_USE;
import static com.katsuna.commons.utils.Constants.SELECT_CONTACT_NUMBER_ACTION;

public class MainActivity extends SearchBarActivity
        implements IConversationInteractionListener {

    private static final int CREATE_CONTACT_REQUEST = 1;
    private static final int ADD_TO_CONTACT_REQUEST = 2;
    private final String[] permissions = new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS};
    private RecyclerView mRecyclerView;
    private ConversationsAdapter mAdapter;
    private TextView mNoResultsView;
    private View mPopupFrame;
    private DrawerLayout mDrawer;
    private int mSelectedConversationPosition = ConversationsAdapter.NO_CONVERSATION_POSITION;
    private SearchView mSearchView;
    private boolean mSearchMode;
    private Snackbar mSnackbar;
    private View mFabsTopContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initControls();

        mFabsTopContainer = findViewById(R.id.fabs_top_container);
        mFab1 = findViewById(R.id.fab);
        mFab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectContact();
            }
        });

        KatsunaNavigationView mKatsunaNavigationView = findViewById(R.id.katsuna_navigation_view);
        mKatsunaNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
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
                        BrowserUtils.openUrl(MainActivity.this, KATSUNA_PRIVACY_URL);
                        break;
                    case R.id.drawer_terms:
                        BrowserUtils.openUrl(MainActivity.this, KATSUNA_TERMS_OF_USE);
                        break;
                }

                return true;
            }
        });
        mKatsunaNavigationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.closeDrawers();
            }
        });

        checkIsDefaultSmsHandler();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        // Assumes current activity is the searchable activity
        if (searchManager != null) {
            mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
            mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (mItemSelected) {
                        deselectItem();
                    }
                    search(newText);
                    return false;
                }
            });
            mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    mSearchMode = false;
                    if (mAdapter != null) {
                        mAdapter.resetFilter();
                    }
                    return false;
                }
            });
            mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    mSearchMode = hasFocus;
                    if (hasFocus) {
                        mFabsTopContainer.setVisibility(View.GONE);
                        showPopup(false);
                    } else {
                        refreshLastTouchTimestamp();
                        mFabsTopContainer.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            mSearchView.setQuery(query, false);
        }
    }

    private void search(String query) {
        if (mAdapter == null) return;
        if (TextUtils.isEmpty(query)) {
            mAdapter.resetFilter();
        } else {
            mAdapter.filter(query);
        }
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
        if (!Device.isDefaultApp(this)) {
            return;
        }

        Intent i = new Intent(this, SelectContactActivity.class);
        i.setAction(SELECT_CONTACT_NUMBER_ACTION);
        startActivity(i);
    }

    private void showContactsAppInstallationDialog() {
        KatsunaAlertBuilder builder = new KatsunaAlertBuilder(this);
        String contactsAppName = getString(R.string.common_katsuna_contacts_app);
        String title = getString(R.string.common_missing_app, contactsAppName);
        builder.setTitle(title);
        builder.setView(R.layout.common_katsuna_alert);
        builder.setUserProfile(mUserProfileContainer.getActiveUserProfile());
        builder.setOkListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KatsunaUtils.goToGooglePlay(MainActivity.this,
                        KatsunaUtils.KATSUNA_CONTACTS_PACKAGE);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.DEF_SMS_REQ_CODE:
                if (Device.isDefaultApp(this)) {
                    Settings.setSetting(this, Constants.DEFAULT_SMS_KEY, Constants.DEFAULT_SMS_ON);
                    if (mSnackbar.isShown()) {
                        mSnackbar.dismiss();
                    }
                    initPopupActionHandler();
                    initDeselectionActionHandler();
                } else {
                    Settings.setSetting(this, Constants.DEFAULT_SMS_KEY, Constants.DEFAULT_SMS_OFF);
                }
                break;
            case CREATE_CONTACT_REQUEST:
                loadConversations();
                break;
            case ADD_TO_CONTACT_REQUEST:
                loadConversations();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!Device.isDefaultApp(this)) {
            // clear data if there are any
            if (mAdapter != null) {
                mAdapter.clearData();
            }

            // clear handlers
            if (mPopupActionHandler != null) {
                mPopupActionHandler.removeCallbacksAndMessages(null);
                mPopupActionHandler = null;
            }

            if (mDeselectionActionHandler != null) {
                mDeselectionActionHandler.removeCallbacksAndMessages(null);
                mDeselectionActionHandler = null;
            }

            showDefaultAppNotSetDialog();

            return;
        } else {
            if (mSnackbar != null) {
                mSnackbar.dismiss();
            }
        }

        loadConversations();

        scrollToPosition(mSelectedConversationPosition);

        if (mItemSelected) {
            deselectItem();
        }
    }

    private void showDefaultAppNotSetDialog() {
        View parentLayout = findViewById(android.R.id.content);
        int layoutId = R.layout.default_app_snackbar;
        int duration = Snackbar.LENGTH_INDEFINITE;
        View.OnClickListener button1Pressed = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Device.makeDefaultApp(MainActivity.this, Constants.DEF_SMS_REQ_CODE);
            }
        };

        KatsunaSnackbarBuilder builder = new KatsunaSnackbarBuilder(parentLayout, layoutId, duration);
        builder.setView1Id(R.id.snackbar_view_1);
        builder.setView1Pressed(button1Pressed);
        mSnackbar = builder.create();
        mSnackbar.show();
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
            if (!mDrawer.isDrawerOpen(GravityCompat.START)
                    && !mSearchMode
                    && !mItemSelected) {
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

        if (Device.isDefaultApp(this)) {
            initPopupActionHandler();
            initDeselectionActionHandler();
        }
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
            ActivityCompat.requestPermissions(this, permissions,
                    Constants.REQUEST_CODE_READ_SMS_AND_CONTACTS);
            return;
        }

        SmsProvider dao = new SmsProvider(this);
        List<Conversation> conversations = dao.getConversations();
        mAdapter = new ConversationsAdapter(conversations, this);
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                showNoResultsView();
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        showNoResultsView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_CODE_READ_SMS_AND_CONTACTS:
                boolean allGood = true;
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        allGood = false;
                        break;
                    }
                }
                if (allGood) {
                    // Permissions Granted
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
        } else if (mItemSelected) {
            deselectItem();
        } else if (mSearchMode) {
            mSearchView.onActionViewCollapsed();
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
    public void createContact(Conversation conversation) {
        Intent i = new Intent(CREATE_CONTACT_ACTION);
        i.putExtra("number", conversation.getContact().getMessageAddress());

        PackageManager packageManager = getPackageManager();
        List activities = packageManager.queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);
        boolean isIntentSafe = activities.size() > 0;

        if (isIntentSafe) {
            startActivityForResult(i, CREATE_CONTACT_REQUEST);
            //clear it from cache to enable fetching the fresh contact
            ContactsCache.getInstance().removeContact(conversation.getRecipientIds());
        } else {
            showContactsAppInstallationDialog();
        }
    }

    @Override
    public void addToContact(Conversation conversation) {
        Intent i = new Intent(ADD_TO_CONTACT_ACTION);
        i.putExtra(ADD_TO_CONTACT_ACTION_NUMBER, conversation.getContact().getMessageAddress());

        PackageManager packageManager = getPackageManager();
        List activities = packageManager.queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);
        boolean isIntentSafe = activities.size() > 0;

        if (isIntentSafe) {
            startActivityForResult(i, ADD_TO_CONTACT_REQUEST);
            //clear it from cache to enable fetching the fresh contact
            ContactsCache.getInstance().removeContact(conversation.getRecipientIds());
        } else {
            showContactsAppInstallationDialog();
        }
    }

    @Override
    public void deleteConversation(final Conversation conversation) {
        KatsunaAlertBuilder builder = new KatsunaAlertBuilder(this);
        builder.setTitle(getString(R.string.confirmation));
        builder.setMessage(getString(R.string.confirmation_delete_conversation));
        builder.setView(R.layout.common_katsuna_alert);
        builder.setUserProfile(mUserProfileContainer.getActiveUserProfile());
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
        KeyboardUtils.hideKeyboard(this);

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
