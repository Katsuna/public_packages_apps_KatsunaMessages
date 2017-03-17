package com.katsuna.messages.providers;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.katsuna.commons.domain.Contact;
import com.katsuna.messages.domain.Conversation;
import com.katsuna.messages.domain.Message;
import com.katsuna.messages.providers.metadata.MessageColumns;
import com.katsuna.messages.providers.metadata.MessageType;
import com.katsuna.messages.providers.metadata.ThreadColumns;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class SmsProvider {

    private static final String TAG = "SmsProvider";

    private final ContentResolver cr;

    public SmsProvider(Context context) {
        cr = context.getContentResolver();
    }

    public List<Conversation> getConversations() {
        List<Conversation> conversations = new ArrayList<>();

        String[] projection = new String[]{ThreadColumns._ID, ThreadColumns.DATE,
                ThreadColumns.READ, ThreadColumns.RECIPIENT_IDS, ThreadColumns.SNIPPET};

        String where = ThreadColumns.MESSAGE_COUNT + " > 0 ";
        String orderBy = ThreadColumns.DATE + " DESC";

        Cursor cursor = cr.query(Uris.THREADS_URI_SIMPLE, projection, where, null, orderBy);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                //showCursor(cursor);

                Conversation conversation = new Conversation();
                conversation.setId(cursor.getLong(cursor.getColumnIndex(ThreadColumns._ID)));
                conversation.setDate(cursor.getLong(cursor.getColumnIndex(ThreadColumns.DATE)));
                conversation.setRead(cursor.getInt(cursor.getColumnIndex(ThreadColumns.READ)));
                conversation.setRecipientIds(cursor.getLong(cursor.getColumnIndex(ThreadColumns.RECIPIENT_IDS)));
                conversation.setSnippet(cursor.getString(cursor.getColumnIndex(ThreadColumns.SNIPPET)));

                int messageType = getLastMessageType(conversation.getId());
                if (messageType == MessageType.INCOMING) {
                    conversation.setUnanswered(true);
                }

                conversations.add(conversation);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return conversations;
    }

    public int deleteConversation(Conversation conversation) {
        String where = MessageColumns.THREAD_ID + " = ? ";
        String[] params = new String[]{String.valueOf(conversation.getId())};

        return cr.delete(Uris.THREADS_URI, where, params);
    }

    private int getLastMessageType(long threadId) {
        String[] projection = new String[]{ThreadColumns.TYPE, ThreadColumns.DATE};
        Uri uri = Uri.withAppendedPath(Uris.THREADS_URI, String.valueOf(threadId));
        String orderBy = ThreadColumns.DATE + " DESC ";

        int type = -1;
        Cursor cursor = cr.query(uri, projection, null, null, orderBy);
        if (cursor != null && cursor.moveToFirst()) {
            type = cursor.getInt(cursor.getColumnIndex(ThreadColumns.TYPE));
            cursor.close();
        }
        return type;
    }

    public List<Message> getConversationMessages(long threadId) {
        List<Message> messages = new ArrayList<>();

        Uri uri = Uri.withAppendedPath(Uris.THREADS_URI, String.valueOf(threadId));
        String[] projection = new String[]{MessageColumns._ID, MessageColumns.ADDRESS,
                MessageColumns.BODY, MessageColumns.DATE, MessageColumns.READ, MessageColumns.TYPE};
        String orderBy = ThreadColumns.DATE + " DESC ";

        LinkedHashMap<String, Contact> map = new LinkedHashMap<>();

        Cursor cursor = cr.query(uri, projection, null, null, orderBy);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                //showCursor(cursor);

                Message message = new Message();
                message.setId(cursor.getLong(cursor.getColumnIndex(MessageColumns._ID)));
                message.setAddress(cursor.getString(cursor.getColumnIndex(MessageColumns.ADDRESS)));
                message.setBody(cursor.getString(cursor.getColumnIndex(MessageColumns.BODY)));
                message.setDate(cursor.getLong(cursor.getColumnIndex(MessageColumns.DATE)));
                message.setType(cursor.getInt(cursor.getColumnIndex(MessageColumns.TYPE)));

                //find contact
                Contact contact = map.get(message.getAddress());
                if (contact == null) {
                    contact = getContactByAddress(message.getAddress());
                    map.put(message.getAddress(), contact);
                }
                message.setContact(contact);

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
        if (cursor != null && cursor.moveToFirst()) {
            address = cursor.getString(cursor.getColumnIndex("address"));
            cursor.close();
        }
        return address;
    }

    public long getConversationId(String address) {
        String[] projection = new String[]{MessageColumns.THREAD_ID};
        String filteredAddress = address.replaceAll("[-()/]", "");
        String selection = MessageColumns.ADDRESS + " like '%" + filteredAddress + "'";

        long conversationId = -1;
        Cursor cursor = cr.query(Uris.URI_SMS, projection, selection, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            conversationId = cursor.getLong(cursor.getColumnIndex(MessageColumns.THREAD_ID));
            cursor.close();
        }
        return conversationId;
    }

    public Contact getContactByAddress(String address) {
        Contact contact = new Contact();
        contact.setMessageAddress(address);

        String[] projection = {
                ContactsContract.PhoneLookup._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI
        };

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));

        Cursor cursor = cr.query(uri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            contact.setId(cursor.getLong(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID)));
            contact.setDisplayName(cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)));

            cursor.close();
        }
        return contact;
    }

    //returns the id of the created message
    public long sendMessage(String address, String message) {
        ContentValues values = new ContentValues();
        values.put(MessageColumns.TYPE, MessageType.OUTGOING);
        values.put(MessageColumns.BODY, message);
        values.put(MessageColumns.READ, 1);
        values.put(MessageColumns.ADDRESS, address);

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
        values.put(MessageColumns.BODY, message.getBody());
        values.put(MessageColumns.ADDRESS, message.getAddress());
        values.put(MessageColumns.DATE_SENT, message.getDate());

        try {
            cr.insert(Uris.URI_INBOX, values);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public long getConversationId(Message message) {
        long output = -1;
        String[] projection = {MessageColumns.THREAD_ID};
        String selection = MessageColumns.DATE_SENT + "= ? AND " + MessageColumns.BODY + "= ? ";
        String[] selectionArgs = new String[]{String.valueOf(message.getDate()), message.getBody()};

        Cursor cursor = cr.query(Uris.URI_SMS, projection, selection, selectionArgs, null);
        if (cursor != null && cursor.moveToFirst()) {
            output = cursor.getLong(cursor.getColumnIndex(MessageColumns.THREAD_ID));
            cursor.close();
        }
        return output;
    }

    public void markRead(long messageId) {
        ContentValues values = new ContentValues();
        values.put(MessageColumns.READ, true);

        String selection = MessageColumns._ID + "=? AND " + MessageColumns.READ + "=?";
        String[] selectionArgs = new String[]{String.valueOf(messageId), "0"};

        try {
            cr.update(Uris.URI_INBOX, values, selection, selectionArgs);
        } catch (Exception ex) {
            Log.e(TAG, "Error in Read: " + ex.toString());
            throw new RuntimeException(ex);
        }
    }

/*  use this for debug purposes
    private void showCursor(Cursor cursor) {
        int l = cursor.getColumnCount();
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < l; i++) {
            buf.append(i).append(":");
            buf.append(cursor.getColumnName(i));
            buf.append("=");
            buf.append(cursor.getString(i));
            buf.append(" type=");
            buf.append(cursor.getType(i));
            buf.append(" | ");
        }
        Log.e(TAG, buf.toString());
    } */
}
