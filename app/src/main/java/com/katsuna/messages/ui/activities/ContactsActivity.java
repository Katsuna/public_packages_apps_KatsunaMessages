package com.katsuna.messages.ui.activities;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.katsuna.commons.entities.UserProfileContainer;
import com.katsuna.commons.ui.KatsunaActivity;
import com.katsuna.messages.R;
import com.katsuna.messages.domain.Contact;
import com.katsuna.messages.domain.Phone;
import com.katsuna.messages.providers.ContactProvider;
import com.katsuna.messages.ui.adapters.ContactsRecyclerViewAdapter;
import com.katsuna.messages.ui.adapters.models.ContactListItemModel;
import com.katsuna.messages.ui.listeners.IContactInteractionListener;
import com.katsuna.messages.utils.Constants;
import com.katsuna.messages.utils.ContactArranger;
import com.katsuna.messages.utils.Separator;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends KatsunaActivity implements IContactInteractionListener {

    private RecyclerView mRecyclerView;
    private SearchView mSearchView;
    private ContactsRecyclerViewAdapter mAdapter;
    private List<ContactListItemModel> mModels;
    private TextView mNoResultsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        initToolbar();
        initControls();
    }

    private void initToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mUserProfileChanged) {
            loadContacts();
        }
    }

    private void initControls() {
        mRecyclerView = (RecyclerView) findViewById(R.id.contacts_list);
        mNoResultsView = (TextView) findViewById(R.id.no_results);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ContactsActivity.this);
                LayoutInflater inflater = (LayoutInflater) view.getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.alert_title, null);
                alert.setCustomTitle(v);
                final EditText input = new EditText(ContactsActivity.this);
                input.setInputType(InputType.TYPE_CLASS_PHONE);
                alert.setView(input);
                alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        startActivity(null, input.getText().toString());
                    }
                });
                alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
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
                            if (dialog.getWindow() != null) {
                                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                            }
                        }
                    }
                });
            }
        });

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
                mAdapter.animateTo(getDeepCopy(mModels));
                showNoResultsView();
                return false;
            }
        });

        return true;
    }

    private void search(String query) {
        Log.e("TAG", "searching for: " + query);
        if (TextUtils.isEmpty(query)) {
            mAdapter.animateTo(getDeepCopy(mModels));
        } else {
            final List<ContactListItemModel> filteredModelList = filter(getDeepCopy(mModels), query);
            mAdapter.animateTo(filteredModelList);
            mRecyclerView.scrollToPosition(0);
        }
        showNoResultsView();
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
        mModels = ContactArranger.getContactsProcessed(contactList);
        mAdapter = new ContactsRecyclerViewAdapter(getDeepCopy(mModels), this);
        mRecyclerView.setAdapter(mAdapter);
        showNoResultsView();
    }

    private List<ContactListItemModel> getDeepCopy(List<ContactListItemModel> contactListItemModels) {
        List<ContactListItemModel> output = new ArrayList<>();
        for (ContactListItemModel model : contactListItemModels) {
            output.add(new ContactListItemModel(model));
        }
        return output;
    }

    private List<ContactListItemModel> filter(List<ContactListItemModel> models, String query) {
        query = query.toLowerCase();

        final List<ContactListItemModel> filteredModelList = new ArrayList<>();
        for (ContactListItemModel model : models) {
            final String text = model.getContact().getDisplayName().toLowerCase();
            if (text.contains(query)) {
                //exclude premium contacts
                if (!model.isPremium()) {
                    model.setSeparator(Separator.NONE);
                    filteredModelList.add(model);
                }
            }
        }
        return filteredModelList;
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
    public UserProfileContainer getUserProfileContainer() {
        return mUserProfileContainer;
    }
}
