package io.fantastix.messengersms;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.fantastix.messengersms.model.Contact;
import io.fantastix.messengersms.model.Message;
import io.fantastix.messengersms.model.MessageDetail;


public class Utils {
    private final String TAG = "Utils";
    public static final String SMS_CONDITION = "Some condition";
    public static final Uri SMS_INBOX_CONTENT_URI = Telephony.Sms.CONTENT_URI;
    public static final String ALL = "content://mms-sms/conversations";
    public static final String SMS = "content://sms/";
    public static final String INBOX = "content://sms/inbox";
    public static final String CONVERSATION = "content://sms/conversations";
    public static final String SENT = "content://sms/sent";
    public static final String DRAFT = "content://sms/draft";

    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        String[] projection = {BaseColumns._ID, ContactsContract.PhoneLookup.DISPLAY_NAME};
        Cursor cursor = contentResolver.query(
                uri,
                projection,
                null,
                null,
                ContactsContract.PhoneLookup.DISPLAY_NAME + " DESC");
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        Log.d("TAG_ANDROID_CONTACTS", "raw contact id : " + contactName);

        return contactName;
    }

    public static String getDate(String date) {
        return new SimpleDateFormat("hh:mm", Locale.ENGLISH).format(new Date(Long.parseLong(date)));
        //        String receiveDayTime = Functions.dateFromMilisec(Long.valueOf(c.getColumnIndexOrThrow("date")), "hh:mm a MMM dd, yyyy");
    }

    public static String getDate(long time) {
        return new SimpleDateFormat("hh:mm", Locale.ENGLISH).format(new Date(time));
    }

    public static String pretifier(long time) {
//        MM/dd/yyyy
        return new SimpleDateFormat("E, dd MMM YYY", Locale.ENGLISH).format(new Date(time));
    }

    public static String serializePhoneNumber(String phoneNo) {
        if (phoneNo.startsWith("+")) {
            phoneNo = phoneNo.substring(0, phoneNo.length()-1);
        }

        // if phoneNo is greater than 8 than it is assumed that the phoneNo contains + and country code in the beginning of it
        if (phoneNo.length() > 8) {
            phoneNo = phoneNo.substring(0, phoneNo.length()-1);
        }

        return phoneNo;
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        String pattern = "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(phoneNumber.trim());
        return m.find();
//        return Patterns.PHONE.matcher(phoneNumber).matches();
    }

    public static boolean isDigicelPhoneNumber(String number) {
        String pattern = "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(number.trim());
        return m.find();
    }

    public static boolean isBMobilePhoneNumber(String number) {
        String pattern = "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(number.trim());
        return m.find();
    }

    public static boolean isTelikomPhoneNumber(String number) {
        String pattern = "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(number.trim());
        return m.find();
    }

    public static void sendDebugSms(String number, String smsBody) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(number, null, smsBody, null, null);
    }

    public static Uri ussdToCallableUri(String ussd) {
        String uriString = "";

        if (!ussd.startsWith("tel:")) {
            uriString += "tel:";
        }

        for (char c : ussd.toCharArray()) {
            if (c == '#') {
                uriString += Uri.encode("#");
            } else {
                uriString += c;
            }
        }
        return Uri.parse(uriString);
    }

    private Message getSmsDetails(Context context, long ignoreThreadId, boolean unreadOnly) {
        String SMS_READ_COLUMN = "read";
        String WHERE_CONDITION = unreadOnly ? SMS_READ_COLUMN + " = 0" : null;
        String SORT_ORDER = "date DESC";
        int count = 0;
        // Log.v(WHERE_CONDITION);
        if (ignoreThreadId > 0) {
            // Log.v("Ignoring sms threadId = " + ignoreThreadId);
            WHERE_CONDITION += " AND thread_id != " + ignoreThreadId;
        }
        Cursor cursor = context.getContentResolver().query(
                SMS_INBOX_CONTENT_URI,
                new String[] { "_id", "thread_id", "address", "person", "date", "body" },
                WHERE_CONDITION,
                null,
                SORT_ORDER);
        if (cursor != null) {
            try {
                count = cursor.getCount();
                if (count > 0) {
                    cursor.moveToFirst();
                    // String[] columns = cursor.getColumnNames();
                    // for (int i=0; i<columns.length; i++) {
                    // Log.v("columns " + i + ": " + columns[i] + ": " + cursor.getString(i));
                    // }
                    long messageId = cursor.getLong(0);
                    long threadId = cursor.getLong(1);
                    String address = cursor.getString(2);
                    long contactId = cursor.getLong(3);
                    String contactId_string = String.valueOf(contactId);
                    long timestamp = cursor.getLong(4);

                    String body = cursor.getString(5);
                    if (!unreadOnly) {
                        count = 0;
                    }

                    Message smsMessage = new Message(context, address,
                            contactId_string, body, timestamp,
                            threadId, count, messageId, Message.MESSAGE_TYPE_SMS);
                    return smsMessage;
                }
            } finally {
                cursor.close();
            }
        }
        return null;
    }

    private static long findMessageId(Context context, long threadId, long _timestamp, int messageType) {
        return 1L;
    }

    public void markSmsAsRead(Context context, final String from, final String body) {

        Uri uri = Uri.parse("content://sms/inbox");
        String selection = "read = ?";
        String[] selectionArgs = {"0"};

        ContentValues values = new ContentValues();
        values.put("read", true);
        context.getContentResolver().update(uri, values, selection, selectionArgs);

        Thread waiter = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Log.w(TAG, "Exception while sleeping markSmsAsReadThread: " + e.getMessage());
                }

                Uri uri = Uri.parse("content://sms/inbox");
                String selection = "address = ? AND body = ? AND read = ?";
                String[] selectionArgs = {from, body, "0"};

                ContentValues values = new ContentValues();
                values.put("read", true);

                int rowsUpdated = context.getContentResolver().update(uri, values, selection, selectionArgs);
                Log.i("TAGGG", "rows updated: " + rowsUpdated);
            }
        });
        waiter.start();
    }

    public void setSMSRead(Context context, long threadId) {
        ContentValues values = new ContentValues();
        values.put("read", true);
        // String where = "read = 0";
        // String where = "_id < 100000";
        context.getContentResolver().update(Uri.parse("content://sms/inbox"), values, "_id<" + "100000", null);

    }

    private static void deleteMessage(Context context, long messageId, long threadId, int messageType) {

    }

    public static String convertToTime(long averageInSeconds) {
        long hours = averageInSeconds / 3600;
        long minutes = (averageInSeconds % 3600) / 60;
        long seconds = averageInSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static String convertToTime(int averageInSeconds) {
        int hours = averageInSeconds / 3600;
        int minutes = (averageInSeconds % 3600) / 60;
        int seconds = averageInSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static Contact getContactByRecipientId(Context context, long recipientId) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor addrCursor = contentResolver.query(Uri.parse("content://mms-sms/canonical-address/" + recipientId), null, null, null, null);
        addrCursor.moveToFirst();
        String number = addrCursor.getString(0); // we got number here
        number = number.replace(" ", "");
        number = number.replace("-", "");
        //Log.d("GET_NUMBER", number);
        Contact c = findContactByNumber(context, number);
        return c;
    }

    public static Contact findContactByNumber(Context context, String phoneNumber) {

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        String[] projection = {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup.NORMALIZED_NUMBER, ContactsContract.PhoneLookup._ID};

        String name = null;
        String nPhoneNumber = phoneNumber;
        long id = 0;

        try (Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null)) {

            if (cursor.moveToFirst()) {
                nPhoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.NORMALIZED_NUMBER));
                name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                id = cursor.getLong(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
            }
        }

        Contact contact = new Contact();
        contact.setId(id);
        contact.setName(name);
        contact.setPhoneNumber(nPhoneNumber);

        return contact;
    }

    public static Contact getThreadIdByAddress(Context context, String phoneNumber) {

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        String[] projection = {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup.NORMALIZED_NUMBER, ContactsContract.PhoneLookup._ID};

        String name = null;
        String nPhoneNumber = phoneNumber;
        long id = 0;

        try (Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null)) {

            if (cursor.moveToFirst()) {
                nPhoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.NORMALIZED_NUMBER));
                name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                id = cursor.getLong(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
            }
        }

        Contact contact = new Contact();
        contact.setId(id);
        contact.setName(name);
        contact.setPhoneNumber(nPhoneNumber);

        return contact;
    }

    public static List<Message> getMessages(Context context) {
        List<Message> messages = new ArrayList<Message>();
        final String[] PROJECTION = new String[] {
                "_id",
                "date",
                "message_count",
                "recipient_ids",
                "snippet", // last message
                "read",
                "type"
        };
        Uri URI = Uri.parse("content://mms-sms/conversations?simple=true");
        ContentResolver mResolver = context.getContentResolver();

        Cursor cursor = mResolver.query(URI, PROJECTION, null, null, "date DESC");
        //context.startManagingCursor(cursor);

        cursor.moveToFirst();
        do {

            Message message = new Message();
            message.setThreadId(cursor.getLong(0));
            message.setTime(cursor.getLong(1));
            message.setMsgCount(cursor.getInt(2));
            message.setRecipient(cursor.getString(3));
            message.setSnippet(cursor.getString(4));
            message.setRead(cursor.getInt(5) == 1);

            int recipient_id = cursor.getInt(3);
            Contact contact = getContactByRecipientId(context, recipient_id);
            message.setContact(contact);
            messages.add(message);

            //Log.d("GET_NUMBER", String.valueOf(recipient_id));
            //Toast.makeText(context, String.valueOf(recipient_id), Toast.LENGTH_SHORT).show();

        } while (cursor.moveToNext());

        return messages;
    }

    public static List<MessageDetail> getMessagesByThreadId(Context context, long id) {
        List<MessageDetail> messageDetailsList = new ArrayList<MessageDetail>();
        final Uri URI = Uri.parse("content://sms/");
        ContentResolver mResolver = context.getContentResolver();
        String[] PROJECTION = new String[]{
                "_id",
                "thread_id",
                "address", // phone number
                "body",
                "date",
                "type",
                "read"
        };
        Cursor cursor = mResolver.query(URI, PROJECTION, "thread_id=" + id, null, "date DESC");

        cursor.moveToFirst();
        do {

            MessageDetail message = new MessageDetail();
            message.setId(cursor.getLong(0));
            message.setThreadId(cursor.getLong(1));
            message.setAddress(cursor.getString(2));
            message.setBody(cursor.getString(3));
            message.setDate(cursor.getLong(4));
            messageDetailsList.add(message);

        } while (cursor.moveToNext());

        return messageDetailsList;
    }

    public static List retrieveSms(Context context) {
        List<Message> messageList = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
//        final String[] projection = new String[]{
//                "_id",
//                "date",
//                "message_count",
//                "recipient_ids",
//                "snippet", // last message
//                "read",
//                "type"
//        };
        String[] projection = {"DISTINCT THREAD_ID", "_ID", "ADDRESS", "BODY", "DATE",
                "TYPE",
                "READ",
                "SEEN",
//                "MESSAGE_COUNT",
//                "RECIPIENT_IDS",
//                "SNIPPET"
        };
        String selection = "THREAD_ID IS NOT NULL) GROUP BY (THREAD_ID";

        Cursor c = cr.query(
                Uri.parse(Utils.SMS),
                null,//projection,
                selection,
                null,
                "DATE DESC");

        int indexId = c.getColumnIndexOrThrow("_id");
        int indexBody = c.getColumnIndexOrThrow("body");
        int indexAddress = c.getColumnIndexOrThrow("address");
        int indexDate = c.getColumnIndexOrThrow("date");
        int indexSnippet = c.getColumnIndex("snippet");
        int indexThreadId = c.getColumnIndex("thread_id");
        int indexMsgCount = c.getColumnIndex("msg_count");
        int indexSeen = c.getColumnIndex("seen");
        int indexRead = c.getColumnIndexOrThrow("read");
        int count = c.getCount();

//        if (indexBody < 0 || !c.moveToFirst()) return;

        if (c.moveToFirst()) { // must check the result to prevent exception
            do {
                Message msgData = new Message();
                msgData.setId(c.getLong(indexId));
                msgData.setThreadId(c.getLong(indexThreadId));
                msgData.setMessage(c.getString(indexBody));

                Contact contact = new Contact();
                String contactNumber = c.getString(indexAddress);
//                if(contactNumber.startsWith("+")) {
//                    // Remove country number prefix. Need to add 00-prefix too.
//                    contactNumber = contactNumber.substring(4);
//                }
                contact.setPhoneNumber(contactNumber);
                String s = Utils.getContactName(context, c.getString(indexAddress));
                if (s != null) {
                    contact.setName(Utils.getContactName(context, c.getString(indexAddress)));
                }
                else {
                    contact.setName(contactNumber);
                }
                msgData.setContact(contact);
                msgData.setTime(c.getLong(indexDate));
                msgData.setReadState(c.getInt(indexRead));
                msgData.setSeenState(c.getInt(indexSeen) == 1);
//                if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
//                    msgData.setFolderName("inbox");
//                } else {
//                    msgData.setFolderName("sent");
//                }
                switch (Integer.parseInt(c.getString(c.getColumnIndexOrThrow(Telephony.Sms.TYPE)))) {
                    case Telephony.Sms.MESSAGE_TYPE_INBOX: // coming in from left
                        msgData.setFolderName("inbox");
                        break;
                    case Telephony.Sms.MESSAGE_TYPE_SENT:
                        msgData.setFolderName("sent");
                        break;
                    case Telephony.Sms.MESSAGE_TYPE_OUTBOX:
                        msgData.setFolderName("outbox");
                        break;
                    case Telephony.Sms.MESSAGE_TYPE_DRAFT:
                        msgData.setFolderName("draft");
                        break;
                    case Telephony.Sms.MESSAGE_TYPE_FAILED:
                        msgData.setFolderName("failed");
                        break;
                    case Telephony.Sms.MESSAGE_TYPE_QUEUED:
                        msgData.setFolderName("queued");
                        break;
                    default:
                        break;
                }

                messageList.add(msgData);

            } while (c.moveToNext());
        }
        return messageList;
    }

    private void markMessageRead(Context context, String number, String body) {

        Uri uri = Uri.parse("content://sms/inbox");
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        try{

            while (cursor.moveToNext()) {
                if ((cursor.getString(cursor.getColumnIndex("address")).equals(number)) &&
                        (cursor.getInt(cursor.getColumnIndex("read")) == 0)) {
                    if (cursor.getString(cursor.getColumnIndex("body")).startsWith(body)) {
                        String SmsMessageId = cursor.getString(cursor.getColumnIndex("_id"));
                        ContentValues values = new ContentValues();
                        values.put("read", true);
                        context.getContentResolver().update(Uri.parse("content://sms/inbox"), values, "_id=" + SmsMessageId, null);
                        return;
                    }
                }
            }
        }catch(Exception e)
        {
            Log.e("Mark Read", "Error in Read: "+e.toString());
        }
    }

}
