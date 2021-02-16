package io.fantastix.messengersms.receivers;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

import io.fantastix.messengersms.R;
import io.fantastix.messengersms.model.Sms;

/**
 * A broadcast receiver who listens for incoming SMS
 */
public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsBroadcastReceiver";
    public static final String SMS_BUNDLE = "pdus";

    private String serviceProviderNumber;
    private String serviceProviderSmsCondition;

    private Listener listener;

    public SmsReceiver() {}

    public SmsReceiver(String serviceProviderNumber, String serviceProviderSmsCondition) {
        this.serviceProviderNumber = serviceProviderNumber;
        this.serviceProviderSmsCondition = serviceProviderSmsCondition;
    }

//    public boolean isSmsPermissionGranted() {
//        return ContextCompat.checkSelfPermission(ContextCompat, Manifest.permission.READ_SMS);
//    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        SmsMessage[] msgs = null;
        String strMessage = "";
        String format = bundle.getString("format");

        // Make sure the data is there!
        if ( bundle != null ) {
            Object[] smsextras = (Object[]) bundle.get( "pdus" );

            if (smsextras != null) {
                // Check the Android version.
                boolean isVersionM = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
                msgs = new SmsMessage[smsextras.length];

                for (int i = 0; i < msgs.length; i++) {
                    if (isVersionM) {
                        // if Android M or newer
                        msgs[i] = SmsMessage.createFromPdu((byte[]) smsextras[i], format);
                    }
                    else {
                        // if Android L or older
                        msgs[i] = SmsMessage.createFromPdu((byte[]) smsextras[i]);
                    }

                    strMessage += "SMS from " + msgs[i].getOriginatingAddress();
                    strMessage += " :";
                    strMessage += msgs[i].getMessageBody();
                    strMessage += "\n";

                    // Log and display the SMS message.
                    Log.d(TAG, "onReceive: " + strMessage);
                    Toast.makeText(context, strMessage, Toast.LENGTH_LONG).show();
                }

                Notification notification = new Notification.Builder(context)
                        .setContentText(strMessage)
                        .setContentTitle("New Message")
                        .setSmallIcon(R.drawable.icon_144)
                        .setStyle(new Notification.BigTextStyle().bigText(strMessage))
                        .build();
                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                notificationManagerCompat.notify(1, notification);

//                for (Object smsextra : smsextras) {
//                    // TODO impliment the newest createFromPdu Format for better support!
//                    SmsMessage smsmsg = SmsMessage.createFromPdu((byte[]) smsextra);
//
//                    // Get the message body from the server.
//                    String strMsgBody = smsmsg.getMessageBody();
//
//                    //Get the address from where it came from.
//                    String strMsgSrc = smsmsg.getOriginatingAddress();
//
//                    strMessage += "SMS from " + strMsgSrc + " : " + strMsgBody;
//
//                    // Log and display the SMS message.
//                    Log.d(TAG, "onReceive: " + strMessage);
//                    Toast.makeText(context, strMessage, Toast.LENGTH_LONG).show();
//
//                    //TODO Add to database!
//                }
            } else {
                // TODO handle error, should not be reachable but never know anymore...
            }

            Toast.makeText(context, strMessage, Toast.LENGTH_SHORT).show();

//        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
//            Sms message = new Sms();
//            String smsSender = "";
//            String smsBody = "";
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
//                    smsSender = smsMessage.getDisplayOriginatingAddress();
//                    smsBody += smsMessage.getMessageBody();
//                }
//            } else {
//                Bundle smsBundle = intent.getExtras();
//                if (smsBundle != null) {
//                    Object[] sms = (Object[]) smsBundle.get(SMS_BUNDLE);
//                    if (sms == null) {
//                        // Display some error to the user
//                        Log.e(TAG, "SmsBundle had no pdus key");
//                        return;
//                    }
//                    String smsMessageStr = "";
//                    for (int i = 0; i < sms.length; ++i) {
//                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);
//
//                        smsSender = smsMessage.getOriginatingAddress();
//                        smsBody = smsMessage.getMessageBody().toString();
//
//                        smsMessageStr += "SMS From: " + smsSender + "\n";
//                        smsMessageStr += smsBody + "\n";
//                    }
//                    Toast.makeText(context, smsMessageStr, Toast.LENGTH_SHORT).show();
//
//                    // this will update the UI with message
//                    SMSActivity inst = SMSActivity.instance();
//                    message.setMsg(smsMessageStr);
//                    inst.updateList(message);
//                }
//            }

//            if (smsSender.equals(serviceProviderNumber) && smsBody.startsWith(serviceProviderSmsCondition)) {
//                if (listener != null) {
//                    listener.onTextReceived(smsBody);
//                }
//            }

//            if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
//                String smsSender = "";
//                String smsBody = "";
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                    smsSender = smsMessage.getDisplayOriginatingAddress();
//                    for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
//                        smsBody += smsMessage.getMessageBody();
//                    }
//                } else {
//                    Bundle smsBundle = intent.getExtras();
//                    if (smsBundle != null) {
//                        Object[] pdus = (Object[]) smsBundle.get("pdus");
//                        if (pdus == null) {
//                            // Display some error to the user
//                            Log.e(TAG, "SmsBundle had no pdus key");
//                            return;
//                        }
//                        SmsMessage[] messages = new SmsMessage[pdus.length];
//                        for (int i = 0; i < messages.length; i++) {
//                            messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
//                            smsBody += messages[i].getMessageBody();
//                        }
//                        smsSender = messages[0].getOriginatingAddress();
//                    }
//                }
//
//                if (smsSender.equals(SmsHelper.SERVICE_PROVIDER) && smsBody.startsWith(SmsHelper.SMS_CONDITION)) {
//                    if (listener != null) {
//                        listener.onTextReceived(smsBody);
//                    }
//                }
//            }
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onNewMessageReceived(Sms message);
        void onTextReceived(String text);
    }
}