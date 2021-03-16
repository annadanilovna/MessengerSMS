package io.fantastix.messengersms;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import ir.siaray.telephonymanagerplus.TelephonyManagerPlus;

public class TelComManager {

    public TelComManager(Context context) {
        TelephonyManagerPlus t = TelephonyManagerPlus.getInstance(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
//            return TODO;
        }
        t.isDualSim();
    }

    public void makeCall() {

    }

    public void sendSMS() {

    }

    public void sendMMS() {

    }


//        telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
//        if (ActivityCompat.checkSelfPermission(this, READ_SMS) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this, READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{READ_SMS, READ_PHONE_NUMBERS, READ_PHONE_STATE, ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
//        } else {
//            Toast.makeText(this, "cell location: "+telephonyManager.getCellLocation(), Toast.LENGTH_SHORT).show();
//            Toast.makeText(this, "#"
//                            + telephonyManager.getNetworkCountryIso() + "\n"
////                            + telephonyManager.getSimSerialNumber() + "\n"
//                            + telephonyManager.getNetworkOperatorName() + "\n"
//                            + telephonyManager.getSimOperatorName() + "\n"
////                            telephonyManager.getSubscriberId() + "\n"
//                    , Toast.LENGTH_LONG).show();
//        }


//        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(this);
//
//        String imeiSIM1 = telephonyInfo.getImsiSIM1();
//        String imeiSIM2 = telephonyInfo.getImsiSIM2();
//
//        boolean isSIM1Ready = telephonyInfo.isSIM1Ready();
//        boolean isSIM2Ready = telephonyInfo.isSIM2Ready();
//
//        boolean isDualSIM = telephonyInfo.isDualSIM();
//
//        TextView tv = (TextView) findViewById(R.id.tv);
//        tv.setText(" IME1 : " + imeiSIM1 + "\n" +
//                " IME2 : " + imeiSIM2 + "\n" +
//                " IS DUAL SIM : " + isDualSIM + "\n" +
//                " IS SIM1 READY : " + isSIM1Ready + "\n" +
//                " IS SIM2 READY : " + isSIM2Ready + "\n");


}
