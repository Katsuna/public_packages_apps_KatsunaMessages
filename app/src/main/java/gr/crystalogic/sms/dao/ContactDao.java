package gr.crystalogic.sms.dao;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

import gr.crystalogic.sms.domain.Contact;
import gr.crystalogic.sms.domain.Phone;

public class ContactDao {
    private static final String TAG = "ContactDao";

    private final ContentResolver cr;
    private final Context mContext;

    public ContactDao(Context context) {
        cr = context.getContentResolver();
        mContext = context;
    }

    public List<Contact> getContacts() {
        List<Contact> contacts = new ArrayList<>();

        Uri baseUri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.LOOKUP_KEY,
                ContactsContract.Contacts.DISPLAY_NAME_ALTERNATIVE,
                ContactsContract.Contacts.TIMES_CONTACTED,
                ContactsContract.Contacts.LAST_TIME_CONTACTED,
                ContactsContract.Contacts.STARRED,
                ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
        };
        String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1";
        String orderBy = ContactsContract.Contacts.DISPLAY_NAME + " ASC";

        Cursor cursor = cr.query(baseUri, projection, selection, null, orderBy);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            int idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            int displayNameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_ALTERNATIVE);
            int timesContactedIndex = cursor.getColumnIndex(ContactsContract.Contacts.TIMES_CONTACTED);
            int lastTimeContactedIndex = cursor.getColumnIndex(ContactsContract.Contacts.LAST_TIME_CONTACTED);
            int starredIndex = cursor.getColumnIndex(ContactsContract.Contacts.STARRED);
            int photoThumbnailUriIndex = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI);

            do {
                Contact contact = new Contact();

                contact.setId(cursor.getLong(idIndex));
                contact.setDisplayName(cursor.getString(displayNameIndex));
                contact.setTimesContacted(cursor.getInt(timesContactedIndex));
                contact.setLastTimeContacted(cursor.getLong(lastTimeContactedIndex));
                int starred = cursor.getInt(starredIndex);
                contact.setStarred(starred == 1);
                contact.setPhotoUri(cursor.getString(photoThumbnailUriIndex));
                contacts.add(contact);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return contacts;
    }

    public List<Phone> getPhones(Long contactId) {
        List<Phone> phones = new ArrayList<>();

        Uri baseUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE
        };
        String selection = ContactsContract.Data.CONTACT_ID + "=" + contactId;
        String orderBy = ContactsContract.CommonDataKinds.Phone.IS_PRIMARY + " DESC";

        Cursor cursor = cr.query(baseUri, projection, selection, null, orderBy);
        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID);
            int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int typeIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
            do {
                Phone phone = new Phone();
                phone.setId(cursor.getString(idIndex));
                phone.setNumber(cursor.getString(numberIndex));
                phone.setType(cursor.getString(typeIndex));
                phones.add(phone);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return phones;
    }
}
