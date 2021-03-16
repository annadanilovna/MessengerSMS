package io.fantastix.messengersms.receivers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import io.fantastix.messengersms.R;
import io.fantastix.messengersms.SMSActivity;
import io.fantastix.messengersms.model.Contact;
import io.fantastix.messengersms.model.Message;

import static android.content.Context.NOTIFICATION_SERVICE;
import static io.fantastix.messengersms.SMSActivity.NOTIFICATION_CHANNEL_ID;

/**
 * A broadcast receiver who listens for incoming SMS
 */
public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsBroadcastReceiver";
    public static final String SMS_BUNDLE = "pdus";
    private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
    public static String NOTIFICATION_ID = "notification-id" ;
    public static String NOTIFICATION = "notification" ;

    private String serviceProviderNumber;
    private String serviceProviderSmsCondition;

    private SmsListener listener;

    public SmsReceiver() {}

    public SmsReceiver(String serviceProviderNumber, String serviceProviderSmsCondition) {
        this.serviceProviderNumber = serviceProviderNumber;
        this.serviceProviderSmsCondition = serviceProviderSmsCondition;
    }

//    public boolean isSmsPermissionGranted() {
//        return ContextCompat.checkSelfPermission(ContextCompat, Manifest.permission.READ_SMS);
//    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
//        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
        if (intent.getAction().equals(ACTION)) {
            Bundle bundle = intent.getExtras();

            Message message = null;
            SmsMessage[] messages;
            String smsSender = "";
            String smsBody = "";
            String format = bundle.getString("format");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                    smsSender = smsMessage.getDisplayOriginatingAddress();
                    smsBody += smsMessage.getMessageBody();
                }

                Notification notification = new Notification.Builder(context)
                        .setContentText(smsBody)
                        .setContentTitle("New Message")
                        .setSmallIcon(R.drawable.ic_alert)
                        .setStyle(new Notification.BigTextStyle().bigText(smsBody))
                        .build();
                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                notificationManagerCompat.notify(1, notification);

            } else {
                Bundle smsBundle = intent.getExtras();
                if (smsBundle != null) {
                    // Make sure the data is there!
                    Object[] smsextras = (Object[]) bundle.get(SMS_BUNDLE);
                    messages = new SmsMessage[smsextras.length];
//                    messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);

                    if (smsextras != null) {
                        // Check the Android version.
                        boolean isVersionM = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
                        for (int i = 0; i < smsextras.length; i++) {
                            if (isVersionM) {
                                // if Android M or newer
                                messages[i] = SmsMessage.createFromPdu((byte[]) smsextras[i], format);
                            } else {
                                // if Android L or older
                                messages[i] = SmsMessage.createFromPdu((byte[]) smsextras[i]);
                            }

                        }

//                    SmsMessage smsMessage = msgs[0];

                        String smsMessageStr = "";
                        for (SmsMessage smsMessage : messages) {
                            message = new Message();

                            smsSender = smsMessage.getOriginatingAddress();
                            smsBody = smsMessage.getMessageBody();

                            smsMessageStr += "SMS From: " + smsSender + "\n";
                            smsMessageStr += ": ";
                            smsMessageStr += smsBody;
                            smsMessageStr += "\n";

                            //TODO Add to database!
                            message.setAddress(smsSender);
                            message.setMessage(smsMessageStr);
                            message.setTime(smsMessage.getTimestampMillis());

//                            if (contactNumber.substring(0, 1).equals("+"))
//                                // Remove country number prefix. Need to add 00-prefix too.
//                                contactNumber = contactNumber.substring(3, contactNumber.length());

                        }

                        // Log and display the SMS message.
                        Log.d(TAG, "onReceive: " + smsMessageStr);
                        Toast.makeText(context, smsMessageStr, Toast.LENGTH_LONG).show();
                    } else {
                        // Display some error to the user
                        Log.e(TAG, "SmsBundle had no pdus key");
                        return;
                    }
                }
            }


//                if (smsSender.equals(SmsHelper.SERVICE_PROVIDER) && smsBody.startsWith(SmsHelper.SMS_CONDITION)) {
//            if (smsSender.equals(serviceProviderNumber) && smsBody.startsWith(serviceProviderSmsCondition)) {
            if (listener != null) {
                listener.onMessageReceived(message);
            }
//            }
//            }

//            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context. NOTIFICATION_SERVICE ) ;
//            Notification notification = intent.getParcelableExtra( NOTIFICATION ) ;
//            if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
//                int importance = NotificationManager. IMPORTANCE_HIGH ;
//                NotificationChannel notificationChannel = new NotificationChannel( NOTIFICATION_CHANNEL_ID , "NOTIFICATION_CHANNEL_NAME" , importance) ;
//                assert notificationManager != null;
//                notificationManager.createNotificationChannel(notificationChannel) ;
//            }
//            int id = intent.getIntExtra( NOTIFICATION_ID , 0 ) ;
//            assert notificationManager != null;
//            notificationManager.notify(id , notification) ;

            Log.d(TAG, "creating notification");
            Notification builder = new Notification.Builder(context)
                    // Set Icon
                    .setSmallIcon(R.drawable.ic_message_icon)
                    // Set Ticker Message
                    .setTicker("message")
                    // Set Title
                    .setContentTitle("New Message")
                    // Set Text
                    .setContentText(smsBody)
                    // Add an Action Button below Notification
//                    .addAction(R.drawable.ic_menu_circle, "Action Button", pIntent)
                    // Set PendingIntent into Notification
//                    .setContentIntent(pIntent)
                    // Dismiss Notification
//                    .setAutoCancel(true);
                    .setDefaults(Notification.DEFAULT_ALL)
                    //.setContent(notificationView)
                    .setPriority(Notification.PRIORITY_MAX).build();
//                    .setContentIntent(pIntent);

            // Create Notification Manager
            NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // Build Notification with Notification Manager
            notificationmanager.notify(0, builder);
        }
    }

    public void setListener(SmsListener listener) {
        this.listener = listener;
    }

    public interface SmsListener {
        void onMessageReceived(Message message);
    }

    public void createNotification(Context context, String contactNumber, String from, String message, String average) {
        // Prepare intent which is triggered if the
        // notification is selected

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", contactNumber, null));
        intent.setPackage(Telephony.Sms.getDefaultSmsPackage(context));
        //Intent intent = new Intent(this, NotificationReceiverActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);

        //RemoteViews notificationView = new RemoteViews(this.getPackageName(), R.layout.custom_notification);

        //notificationView.setImageViewResource(R.id.notiIcon, R.drawable.ic_stat_name);
        //notificationView.setTextViewText(R.id.notiTitle, "Textra");
        //notificationView.setTextViewText(R.id.notiName, from);
        //notificationView.setTextViewText(R.id.notiAvg, "Average response time: " + average);

        // Build notification
        // Actions are just fake
        Notification noti = new Notification.Builder(context)
                .setContentTitle(from)
                .setSubText("Response Time: " + average)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_message_icon)
                .setDefaults(Notification.DEFAULT_ALL)
                //.setContent(notificationView)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(pIntent).build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags = Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, noti);

        //
//                Context mContext =  getApplicationContext();
//                Dialog dialog = new Dialog(mContext);
//
//                dialog.setContentView(R.layout.custom_dialog); dialog.setTitle("Custom Dialog");
//
//                TextView text = dialog.findViewById(R.id.text); text.setText("Hello, this is a custom dialog!");
//                ImageView image = dialog.findViewById(R.id.image);
//                image.setImageResource(R.drawable.ic_default_profile_pic);
//
//
//                String replyLabel = getResources().getString(R.string.reply_label);
//                RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
//                        .setLabel(replyLabel)
//                        .build();
//                // Build a PendingIntent for the reply action to trigger.
//                PendingIntent replyPendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
//                        conversation.getConversationId(),
//                        getMessageReplyIntent(conversation.getConversationId()),
//                        PendingIntent.FLAG_UPDATE_CURRENT);
//                // Create the reply action and add the remote input.
//                NotificationCompat.Action action =
//                        new NotificationCompat.Action.Builder(R.drawable.ic_reply_icon,
//                                getString(R.string.label), replyPendingIntent)
//                                .addRemoteInput(remoteInput)
//                                .build();
//
//                // Build the notification and add the action.
//                // Notification newMessageNotification = new Notification.Builder(context, CHANNEL_ID)
//                Notification newMessageNotification = new Notification.Builder(getApplicationContext())
//                        .setContentText(message.getMessage())
//                        .setContentTitle("New Message")
//                        .setSmallIcon(R.drawable.icon_144)
//                        .setSmallIcon(R.drawable.ic_message)
//                        .setStyle(new Notification.BigTextStyle().bigText("strMessage"))
//                        .addAction(action)
//                        .build();
//                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
//                notificationManagerCompat.notify(1, notification);
//
//                // Issue the notification.
//                // NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//                // notificationManager.notify(1, newMessageNotification);

    }

//    public Contact getContact(String contactNumber) {
//        if(!contacts.isEmpty()) {
//            for(Contact contact : contacts) {
//                if(contact.getPhoneNumber().equals(contactNumber)) {
//                    return contact;
//                }
//            }
//        } else {
//            Log.i("MainActivity", "No such contact.");
//        }
//
//        return null;
//    }

    public String getContactName(Context context, String number) {
        // Convert phone number to contact name from phone book

        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
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

        return contactName;
    }

    public String convertToTime(int averageInSeconds) {
        int hours = averageInSeconds / 3600;
        int minutes = (averageInSeconds % 3600) / 60;
        int seconds = averageInSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}