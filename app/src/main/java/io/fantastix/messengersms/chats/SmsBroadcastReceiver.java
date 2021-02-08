package io.fantastix.messengersms.chats;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

/**
 * A broadcast receiver who listens for incoming SMS
 */
public class SmsBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsBroadcastReceiver";
    public static final String SMS_BUNDLE = "pdus";

//    private final String serviceProviderNumber;
//    private final String serviceProviderSmsCondition;

    private Listener listener;

    public SmsBroadcastReceiver() {}

//    public SmsBroadcastReceiver(String serviceProviderNumber, String serviceProviderSmsCondition) {
//        this.serviceProviderNumber = serviceProviderNumber;
//        this.serviceProviderSmsCondition = serviceProviderSmsCondition;
//    }

//    public boolean isSmsPermissionGranted() {
//        return ContextCompat.checkSelfPermission(ContextCompat, Manifest.permission.READ_SMS);
//    }

    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String str = "";
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i = 0; i< msgs.length; i++) {
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                str += "SMS from " + msgs[i].getOriginatingAddress();
                str += " :";
                str += msgs[i].getMessageBody().toString();
                str += "\n";
            }
            Toast.makeText(context, str, Toast.LENGTH_SHORT).show();

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
        }
    }

    void setListener(Listener listener) {
        this.listener = listener;
    }

    interface Listener {
        void onTextReceived(String text);
    }
}