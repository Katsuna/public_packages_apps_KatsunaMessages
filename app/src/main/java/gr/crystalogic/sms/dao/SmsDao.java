package gr.crystalogic.sms.dao;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import gr.crystalogic.sms.dao.metadata.ConversationColumns;
import gr.crystalogic.sms.dao.metadata.MessageType;
import gr.crystalogic.sms.domain.Contact;
import gr.crystalogic.sms.domain.Conversation;
import gr.crystalogic.sms.domain.Message;

public class SmsDao {

    private static final String TAG = "ContactDao";

    private final ContentResolver cr;

    public SmsDao(Context context) {
        cr = context.getContentResolver();
    }

    public List<Conversation> getConversations() {
        List<Conversation> conversations = new ArrayList<>();

        String orderBy = ConversationColumns.DATE + " DESC";

        Cursor cursor = cr.query(Uris.CONVERSATIONS_SIMPLE, null, null, null, orderBy);
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

                int messageType = getLastMessageType(conversation.getId());
                Log.e(TAG, "messageType: " + messageType);
                if (messageType == MessageType.INCOMING) {
                    conversation.setUnanswered(true);
                }

                //find address
                String address = getAddress(conversation.getRecipientIds());
                conversation.setAddress(address);

                //find contact
                Contact contact = getContactByAddress(address);
                conversation.setContact(contact);

                conversations.add(conversation);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return conversations;
    }

    private int getLastMessageType(long conversationId) {
        String[] projection = new String[]{ConversationColumns.TYPE, ConversationColumns.DATE};
        Uri uri = Uri.withAppendedPath(Uris.CONVERSATIONS, String.valueOf(conversationId));
        String orderBy = ConversationColumns.DATE + " DESC ";

        int type = -1;
        Cursor cursor = cr.query(uri, projection, null, null, orderBy);
        if (cursor.moveToFirst()) {
            type = cursor.getInt(cursor.getColumnIndex(ConversationColumns.TYPE));
            cursor.close();
        }
        return type;
    }

    public List<Message> getConversationMessages(long threadId) {
        List<Message> messages = new ArrayList<>();

        Uri uri = Uri.withAppendedPath(Uris.CONVERSATIONS, String.valueOf(threadId));
        String orderBy = ConversationColumns.DATE + " DESC ";

        Cursor cursor = cr.query(uri, ConversationColumns.PROJECTION_ULTRA, null, null, orderBy);
        if (cursor.moveToFirst()) {
            do {
                Message message = new Message();
                message.setId(cursor.getLong(cursor.getColumnIndex(ConversationColumns.ID)));
                message.setAddress(cursor.getString(cursor.getColumnIndex(ConversationColumns.ADDRESS)));
                message.setBody(cursor.getString(cursor.getColumnIndex(ConversationColumns.BODY)));
                message.setDate(cursor.getLong(cursor.getColumnIndex(ConversationColumns.DATE)));
                message.setRead(cursor.getInt(cursor.getColumnIndex(ConversationColumns.READ)));
                message.setType(cursor.getInt(cursor.getColumnIndex(ConversationColumns.TYPE)));

                //find contact
                Contact contact = getContactByAddress(message.getAddress());
                message.setContact(contact);

/*                 if ("application/vnd.wap.multipart.related".equals(ct_t)) {
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

                messages.add(message);
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
            cursor.close();
        }
        return address;
    }

    public long getConversationId(String address) {
        String[] projection = new String[]{ConversationColumns.THREAD_ID};
        String filteredAddress = address.replaceAll("[-()/ ]", "");
        String selection = ConversationColumns.ADDRESS + " like '%" + filteredAddress + "'";

        long conversationId = -1;
        Cursor cursor = cr.query(Uris.URI_SMS, projection, selection, null, null);
        if (cursor.moveToFirst()) {
            conversationId = cursor.getLong(cursor.getColumnIndex(ConversationColumns.THREAD_ID));
            cursor.close();
        }
        return conversationId;
    }

    public Contact getContactByAddress(String address) {
        Contact contact = null;

        String[] projection = {
                ContactsContract.PhoneLookup._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup.NUMBER,
                ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI
        };

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));

        Cursor cursor = cr.query(uri, projection, null, null, null);
        if (cursor.moveToFirst()) {
            contact = new Contact();
            contact.setId(cursor.getLong(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID)));
            contact.setName(cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)));
            contact.setNumber(cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.NUMBER)));
            contact.setPhotoUri(cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI)));

            cursor.close();
        }
        return contact;
    }

    //returns the id of the created message
    public long sendMessage(String address, String message) {
        ContentValues values = new ContentValues();
        values.put(ConversationColumns.TYPE, MessageType.OUTGOING);
        values.put(ConversationColumns.BODY, message);
        values.put(ConversationColumns.READ, 1);
        values.put(ConversationColumns.ADDRESS, address);

        long output = -1;
        try {
            Uri result = cr.insert(Uris.URI_SENT, values);
            output = ContentUris.parseId(result);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return output;
    }

    public void receiveMessage(Message message) {
        ContentValues values = new ContentValues();
        values.put(ConversationColumns.BODY, message.getBody());
        values.put(ConversationColumns.ADDRESS, message.getAddress());
        values.put(ConversationColumns.DATE_SENT, message.getDate());

        try {
            cr.insert(Uris.URI_INBOX, values);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public long getConversationId(Message message) {
        long output = -1;
        String[] projection = {ConversationColumns.THREAD_ID};
        String selection = ConversationColumns.DATE_SENT + "= ? AND " + ConversationColumns.BODY + "= ? ";
        String[] selectionArgs = new String[]{String.valueOf(message.getDate()), message.getBody()};

        Cursor cursor = cr.query(Uris.URI_SMS, projection, selection, selectionArgs, null);
        if (cursor.moveToFirst()) {
            output = cursor.getLong(cursor.getColumnIndex(ConversationColumns.THREAD_ID));
            cursor.close();
        }

        Log.e(TAG, "conversatioId: " + output);
        return output;
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
            c.close();
            Log.e(TAG, buf.toString());
        }
    }
}
