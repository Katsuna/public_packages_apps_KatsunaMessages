package com.katsuna.messages.ui.activities;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.katsuna.commons.domain.Contact;
import com.katsuna.commons.domain.Phone;
import com.katsuna.commons.entities.UserProfile;
import com.katsuna.commons.entities.UserProfileContainer;
import com.katsuna.commons.providers.ContactProvider;
import com.katsuna.commons.ui.SearchBarActivity;
import com.katsuna.commons.ui.adapters.models.ContactListItemModel;
import com.katsuna.commons.utils.ContactArranger;
import com.katsuna.commons.utils.KatsunaAlertBuilder;
import com.katsuna.messages.R;
import com.katsuna.messages.ui.adapters.ContactsAdapter;
import com.katsuna.messages.ui.listeners.IContactInteractionListener;
import com.katsuna.messages.utils.Constants;
import com.konifar.fab_transformation.FabTransformation;

import java.util.List;

import static com.katsuna.commons.utils.Constants.FAB_TRANSFORMATION_DURATION;

public class ContactsActivity extends SearchBarActivity implements IContactInteractionListener {

    private RecyclerView mRecyclerView;
    private SearchView mSearchView;
    private ContactsAdapter mAdapter;
    private TextView mNoResultsView;
    private View mPopupFrame;
    private boolean mSearchMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        initControls();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mUserProfileChanged) {
            loadContacts();
        }
    }

    @Override
    protected void deselectItem() {
        // do nothing.
    }

    @Override
    public void onBackPressed() {
        refreshLastTouchTimestamp();
        if (mFabToolbarOn) {
            showFabToolbar(false);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void showPopup(boolean show) {
        if (show) {
            //don't show popup if toolbar search is enabled or search with letters is shown.
            if (!mSearchMode && !mFabToolbarOn) {
                mPopupFrame.setVisibility(View.VISIBLE);
                mPopupButton1.setVisibility(View.VISIBLE);
                mPopupButton2.setVisibility(View.VISIBLE);
                mPopupVisible = true;
            }
        } else {
            mPopupFrame.setVisibility(View.GONE);
            mPopupButton1.setVisibility(View.GONE);
            mPopupButton2.setVisibility(View.GONE);
            mPopupVisible = false;
        }
    }

    private void initControls() {
        initToolbar();
        mLettersList = (RecyclerView) findViewById(R.id.letters_list);
        mRecyclerView = (RecyclerView) findViewById(R.id.contacts_list);
        mNoResultsView = (TextView) findViewById(R.id.no_results);

        setupFab();
    }

    private void setupFab() {
        mFabContainer = (LinearLayout) findViewById(R.id.fab_container);
        mFab1 = (FloatingActionButton) findViewById(R.id.dial_fab);
        mFab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                dialNumber();
            }
        });

        mFab2 = (FloatingActionButton) findViewById(R.id.search_fab);
        mFab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFabToolbar(true);
            }
        });

        mFabToolbarContainer = (FrameLayout) findViewById(R.id.fab_toolbar_container);
        mFabToolbar = findViewById(R.id.fab_toolbar);
        mNextButton = (ImageButton) findViewById(R.id.next_page_button);
        mNextButton.setOnTouchListener(new View.OnTouchListener() {
            private Handler mHandler;
            Runnable mAction = new Runnable() {
                @Override
                public void run() {
                    mLettersList.scrollBy(0, 30);
                    mHandler.postDelayed(this, 10);
                }
            };

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, 10);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return false;
            }
        });

        mPrevButton = (ImageButton) findViewById(R.id.prev_page_button);
        mPrevButton.setOnTouchListener(new View.OnTouchListener() {
            private Handler mHandler;
            Runnable mAction = new Runnable() {
                @Override
                public void run() {
                    mLettersList.scrollBy(0, -30);
                    mHandler.postDelayed(this, 10);
                }
            };

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, 10);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return false;
            }
        });

        mViewPagerContainer = findViewById(R.id.viewpager_container);
        mButtonsContainer2 = (LinearLayout) findViewById(R.id.search_buttons_container);
        mPopupButton2 = (Button) findViewById(R.id.search_button);
        mPopupButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFabToolbar(true);
            }
        });

        mButtonsContainer1 = (LinearLayout) findViewById(R.id.dial_buttons_container);
        mPopupButton1 = (Button) findViewById(R.id.dial_button);
        mPopupButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialNumber();
            }
        });

        mPopupFrame = findViewById(R.id.popup_frame);
        mPopupFrame.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showPopup(false);
                return true;
            }
        });

        mLastTouchTimestamp = System.currentTimeMillis();
        initPopupActionHandler();

    }

    private void dialNumber() {
        KatsunaAlertBuilder builder = new KatsunaAlertBuilder(this);
        builder.setTitle(R.string.common_dial_instruction);
        builder.setMessage(0);
        builder.setView(R.layout.common_katsuna_dialer);
        builder.setUserProfileContainer(mUserProfileContainer);
        builder.setOkListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        builder.setTextSelected(new KatsunaAlertBuilder.KatsunaAlertText() {
            @Override
            public void textSelected(String number) {
                if (!TextUtils.isEmpty(number)) {
                    startActivity(null, number);
                }
            }
        });

        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showFabToolbar(boolean show) {
        if (show) {
            FabTransformation.with(mFab2).duration(FAB_TRANSFORMATION_DURATION)
                    .transformTo(mFabToolbar);

            if (mPopupVisible) {
                showPopup(false);
            }
            mFab1.setVisibility(View.INVISIBLE);
        } else {
            FabTransformation.with(mFab2).duration(FAB_TRANSFORMATION_DURATION)
                    .transformFrom(mFabToolbar);
            mAdapter.unfocusFromSearch();
            mFab1.setVisibility(View.VISIBLE);
        }
        mFabToolbarOn = show;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return false;
            }
        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mSearchMode = false;
                mAdapter.resetFilter();
                return false;
            }
        });

        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchMode = true;
                showPopup(false);
                showFabToolbar(false);
            }
        });

        return true;
    }

    private void search(String query) {
        if (TextUtils.isEmpty(query)) {
            mAdapter.resetFilter();
        } else {
            mAdapter.getFilter().filter(query);
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

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            mSearchView.setQuery(query, false);
        }
    }

    private void loadContacts() {
        ContactProvider dao = new ContactProvider(this);
        List<Contact> contactList = dao.getContacts();
        List<ContactListItemModel> mModels = ContactArranger.getContactsProcessed(contactList);
        mAdapter = new ContactsAdapter(mModels, this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                showNoResultsView();
            }
        });

        initializeFabToolbar(mModels);

        showNoResultsView();
    }

    @Override
    public void selectContact(final Contact contact) {
        ContactProvider dao = new ContactProvider(this);
        List<Phone> phones = dao.getPhones(contact.getId());

        if (phones.size() == 1) {
            startActivity(contact.getDisplayName(), phones.get(0).getNumber());
        } else {
            final CharSequence phonesArray[] = new CharSequence[phones.size()];
            int i = 0;
            for (Phone phone : phones) {
                phonesArray[i++] = phone.getNumber();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.alert_title, null);
            builder.setCustomTitle(view);
            builder.setItems(phonesArray, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(contact.getDisplayName(), phonesArray[which].toString());
                }
            });
            builder.show();
        }
    }

    private void startActivity(String name, String number) {
        Intent i = new Intent(this, ConversationActivity.class);
        i.putExtra(Constants.EXTRA_DISPLAY_NAME, name);
        i.putExtra(Constants.EXTRA_NUMBER, number);
        startActivity(i);
        finish();
    }

    @Override
    public void selectItemByStartingLetter(String letter) {
        if (mAdapter != null) {
            int position = mAdapter.getPositionByStartingLetter(letter);
            scrollToPositionWithOffset(position, 0);
            mAdapter.focusFromSearch(position);
        }
    }

    private void scrollToPositionWithOffset(int position, int offset) {
        ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                .scrollToPositionWithOffset(position, offset);
    }

    @Override
    public UserProfileContainer getUserProfileContainer() {
        return mUserProfileContainer;
    }

    @Override
    public UserProfile getUserProfile() {
        return mUserProfileContainer.getActiveUserProfile();
    }
}
