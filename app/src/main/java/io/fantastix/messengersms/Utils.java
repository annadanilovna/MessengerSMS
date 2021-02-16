package io.fantastix.messengersms;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static final String SMS_CONDITION = "Some condition";

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
//        String pattern = "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$";
//        Pattern r = Pattern.compile(pattern);
//        Matcher m = r.matcher(phoneNumber.trim());
//        return m.find();
        return Patterns.PHONE.matcher(phoneNumber).matches();
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
}
