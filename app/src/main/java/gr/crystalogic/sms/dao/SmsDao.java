package gr.crystalogic.sms.dao;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import gr.crystalogic.sms.dao.metadata.ConversationColumns;
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

        final String[] projection = new String[]{ConversationColumns.ID, ConversationColumns.CT_T,
                ConversationColumns.THREAD_ID};

        Cursor cursor = cr.query(Uris.CONVERSATIONS, projection, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex(ConversationColumns.ID));
                String ct_t = cursor.getString(cursor.getColumnIndex(ConversationColumns.CT_T));
                String threadId = cursor.getString(cursor.getColumnIndex(ConversationColumns.THREAD_ID));

                if ("application/vnd.wap.multipart.related".equals(ct_t)) {
                    // it's MMS
                } else {
                    // it's SMS
                }

                Conversation conversation = new Conversation();
                conversation.setId(id);
                conversation.setCt_t(ct_t);
                conversation.setThreadId(threadId);
                conversations.add(conversation);

            } while (cursor.moveToNext());
        }

        return conversations;
    }

    public List<Message> getConversationMessages(String threadId) {
        List<Message> messages = new ArrayList<>();

        Uri uri = Uri.withAppendedPath(Uris.CONVERSATIONS, threadId);

        String selection = null; /* = ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + "=" + contactId; */
        String orderBy = null; /* = ContactsContract.CommonDataKinds.StructuredPostal.IS_PRIMARY + " DESC"; */

        Cursor cursor = cr.query(uri, ConversationColumns.PROJECTION_FULL, selection, null, orderBy);
        if (cursor.moveToFirst()) {
            do {
                Message address = new Message();

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

    public void showRows(Uri u) {
        Log.e(TAG, "-----GET HEADERS-----");
        Log.e(TAG, "-- " + u.toString() + " --");
        Cursor c = cr.query(u, null, null, null, null);
        if (c != null) {
            int l = c.getColumnCount();
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < l; i++) {
                buf.append(i).append(":");
                buf.append(c.getColumnName(i));
                buf.append(" | ");
            }
            Log.e(TAG, buf.toString());
        }
    }
}
