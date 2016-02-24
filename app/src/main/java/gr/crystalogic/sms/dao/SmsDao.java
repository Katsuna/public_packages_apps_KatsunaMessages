package gr.crystalogic.sms.dao;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import gr.crystalogic.sms.dao.metadata.ConversationColumns;
import gr.crystalogic.sms.domain.Contact;
import gr.crystalogic.sms.domain.Conversation;
import gr.crystalogic.sms.domain.Message;

public class SmsDao {

    private static final String TAG = "ContactDao";

    private final ContentResolver cr;
    private final Context mContext;

    public SmsDao(Context context) {
        cr = context.getContentResolver();
        mContext = context;
    }

    public List<Conversation> getConversations() {
        List<Conversation> conversations = new ArrayList<>();

        Cursor cursor = cr.query(Uris.CONVERSATIONS_SIMPLE, ConversationColumns.PROJECTION_SIMPLE, null, null, null);
        if (cursor.moveToFirst()) {
            do {
/*                0:_id type:1 | 1:date type:1 | 2:message_count type:1 | 3:recipient_ids type:3 | 4:snippet type:3 | 5:snippet_cs type:1 | 6:read type:
                1 | 7:type type:1 | 8:error type:1 | 9:has_attachment type:1 |*/
                Conversation conversation = new Conversation();
                conversation.setId(cursor.getLong(cursor.getColumnIndex(ConversationColumns.ID)));
                conversation.setDate(cursor.getLong(cursor.getColumnIndex(ConversationColumns.DATE)));
                conversation.setMessageCount(cursor.getLong(cursor.getColumnIndex(ConversationColumns.MESSAGE_COUNT)));
                conversation.setRecipientIds(cursor.getLong(cursor.getColumnIndex(ConversationColumns.RECIPIENT_IDS)));
                conversation.setSnippet(cursor.getString(cursor.getColumnIndex(ConversationColumns.SNIPPET)));
                conversation.setSnippetCs(cursor.getLong(cursor.getColumnIndex(ConversationColumns.SNIPPET_CS)));

                //find address
                String address = getAddress(conversation.getRecipientIds());
                //find contact
                Contact contact = getContactByAddress(address);
                conversation.setContact(contact);

                conversations.add(conversation);
            } while (cursor.moveToNext());
        }

        return conversations;
    }

    public List<Message> getConversationMessages(long threadId) {
        List<Message> messages = new ArrayList<>();

        Uri uri = Uri.withAppendedPath(Uris.CONVERSATIONS, String.valueOf(threadId));

        String selection = null; /* = ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + "=" + contactId; */
        String orderBy = null; /* = ContactsContract.CommonDataKinds.StructuredPostal.IS_PRIMARY + " DESC"; */

        Cursor cursor = cr.query(uri, ConversationColumns.PROJECTION_FULL, selection, null, orderBy);
        if (cursor.moveToFirst()) {
            do {
                Message address = new Message();

                /* if ("application/vnd.wap.multipart.related".equals(ct_t)) {
                    // it's MMS
                } else {
                    // it's SMS
                }*/


                int l = cursor.getColumnCount();
                StringBuilder buf = new StringBuilder();
                for (int i = 0; i < l; i++) {
                    buf.append(i).append(":");
                    buf.append(cursor.getColumnName(i));
                    buf.append("=");
                    buf.append(cursor.getString(i));
                    buf.append(" | ");
                }
                Log.e(TAG, buf.toString());

                messages.add(address);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return messages;
    }

    public String getAddress(long recipientId) {
        String address = null;

        Uri uri = Uri.withAppendedPath(Uris.CANONICAL_ADDRESS, String.valueOf(recipientId));

        String[] projection = new String[]{"address"};

        Cursor cursor = cr.query(uri, projection, null, null, null);
        if (cursor.moveToFirst()) {
            address = cursor.getString(cursor.getColumnIndex("address"));
        }
        return address;
    }

    public Contact getContactByAddress(String address) {
        Contact contact = null;

        String[] projection = {
                ContactsContract.PhoneLookup._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup.NUMBER
        };

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));

        Cursor cursor = cr.query(uri, projection, null, null, null);
        if (cursor.moveToFirst()) {
            contact = new Contact();
            contact.setId(cursor.getLong(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID)));
            contact.setName(cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)));
            contact.setNumber(cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.NUMBER)));
        }
        return contact;
    }

    public void showRows(Uri u) {
        Log.e(TAG, "-----GET HEADERS-----");
        Log.e(TAG, "-- " + u.toString() + " --");
        Cursor c = cr.query(u, null, null, null, null);
        if (c != null) {
            int l = c.getColumnCount();
            StringBuilder buf = new StringBuilder();
            c.moveToFirst();
            for (int i = 0; i < l; i++) {
                buf.append(i).append(":");
                buf.append(c.getColumnName(i));
                buf.append(" type:");
                buf.append(c.getType(i));
                buf.append(" | ");
            }
            Log.e(TAG, buf.toString());
        }
    }
}
