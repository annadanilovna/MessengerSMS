package io.fantastix.messengersms;

import java.lang.reflect.Method;

import android.content.Context;
import android.telephony.TelephonyManager;

//public final class TelephonyInfo {
//
//    private static TelephonyInfo telephonyInfo;
//    private String imeiSIM1;
//    private String imeiSIM2;
//    private boolean isSIM1Ready;
//    private boolean isSIM2Ready;
//
//    public String getImsiSIM1() {
//        return imeiSIM1;
//    }
//
//    /*public static void setImsiSIM1(String imeiSIM1) {
//        TelephonyInfo.imeiSIM1 = imeiSIM1;
//    }*/
//
//    public String getImsiSIM2() {
//        return imeiSIM2;
//    }
//
//    /*public static void setImsiSIM2(String imeiSIM2) {
//        TelephonyInfo.imeiSIM2 = imeiSIM2;
//    }*/
//
//    public boolean isSIM1Ready() {
//        return isSIM1Ready;
//    }
//
//    /*public static void setSIM1Ready(boolean isSIM1Ready) {
//        TelephonyInfo.isSIM1Ready = isSIM1Ready;
//    }*/
//
//    public boolean isSIM2Ready() {
//        return isSIM2Ready;
//    }
//
//    /*public static void setSIM2Ready(boolean isSIM2Ready) {
//        TelephonyInfo.isSIM2Ready = isSIM2Ready;
//    }*/
//
//    public boolean isDualSIM() {
//        return imeiSIM2 != null;
//    }
//
//    private TelephonyInfo() {
//    }
//
//    public static TelephonyInfo getInstance(Context context){
//
//        if(telephonyInfo == null) {
//
//            telephonyInfo = new TelephonyInfo();
//
//            TelephonyManager telephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
//
//            telephonyInfo.imeiSIM1 = telephonyManager.getDeviceId();;
//            telephonyInfo.imeiSIM2 = null;
//
//            try {
//                telephonyInfo.imeiSIM1 = getDeviceIdBySlot(context, "getDeviceIdGemini", 0);
//                telephonyInfo.imeiSIM2 = getDeviceIdBySlot(context, "getDeviceIdGemini", 1);
//            } catch (GeminiMethodNotFoundException e) {
//                e.printStackTrace();
//
//                try {
//                    telephonyInfo.imeiSIM1 = getDeviceIdBySlot(context, "getDeviceId", 0);
//                    telephonyInfo.imeiSIM2 = getDeviceIdBySlot(context, "getDeviceId", 1);
//                } catch (GeminiMethodNotFoundException e1) {
//                    //Call here for next manufacturer's predicted method name if you wish
//                    e1.printStackTrace();
//                }
//            }
//
//            telephonyInfo.isSIM1Ready = telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY;
//            telephonyInfo.isSIM2Ready = false;
//
//            try {
//                telephonyInfo.isSIM1Ready = getSIMStateBySlot(context, "getSimStateGemini", 0);
//                telephonyInfo.isSIM2Ready = getSIMStateBySlot(context, "getSimStateGemini", 1);
//            } catch (GeminiMethodNotFoundException e) {
//
//                e.printStackTrace();
//
//                try {
//                    telephonyInfo.isSIM1Ready = getSIMStateBySlot(context, "getSimState", 0);
//                    telephonyInfo.isSIM2Ready = getSIMStateBySlot(context, "getSimState", 1);
//                } catch (GeminiMethodNotFoundException e1) {
//                    //Call here for next manufacturer's predicted method name if you wish
//                    e1.printStackTrace();
//                }
//            }
//        }
//
//        return telephonyInfo;
//    }
//
//    private static String getDeviceIdBySlot(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {
//
//        String imei = null;
//
//        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//
//        try{
//
//            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());
//
//            Class<?>[] parameter = new Class[1];
//            parameter[0] = int.class;
//            Method getSimID = telephonyClass.getMethod(predictedMethodName, parameter);
//
//            Object[] obParameter = new Object[1];
//            obParameter[0] = slotID;
//            Object ob_phone = getSimID.invoke(telephony, obParameter);
//
//            if(ob_phone != null){
//                imei = ob_phone.toString();
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new GeminiMethodNotFoundException(predictedMethodName);
//        }
//
//        return imei;
//    }
//
//    private static  boolean getSIMStateBySlot(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {
//
//        boolean isReady = false;
//
//        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//
//        try{
//
//            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());
//
//            Class<?>[] parameter = new Class[1];
//            parameter[0] = int.class;
//            Method getSimStateGemini = telephonyClass.getMethod(predictedMethodName, parameter);
//
//            Object[] obParameter = new Object[1];
//            obParameter[0] = slotID;
//            Object ob_phone = getSimStateGemini.invoke(telephony, obParameter);
//
//            if(ob_phone != null){
//                int simState = Integer.parseInt(ob_phone.toString());
//                if(simState == TelephonyManager.SIM_STATE_READY){
//                    isReady = true;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new GeminiMethodNotFoundException(predictedMethodName);
//        }
//
//        return isReady;
//    }
//
//
//    private static class GeminiMethodNotFoundException extends Exception {
//
//        private static final long serialVersionUID = -996812356902545308L;
//
//        public GeminiMethodNotFoundException(String info) {
//            super(info);
//        }
//    }
//}


import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;

public class TelephonyInfo {
    private static TelephonyInfo telephonyInfo;
    private String imeiSIM1;
    private String imeiSIM2;
    private boolean isSIM1Ready;
    private boolean isSIM2Ready;
    private String isSIM1Number;
    private String isSIM2Number;


    public String isSIM1Number() {
        return isSIM1Number;
    }

    public String isSIM2Number() {
        return isSIM2Number;
    }

    public String getImsiSIM1() {
        return imeiSIM1;
    }

    /*public static void setImsiSIM1(String imeiSIM1) {
        TelephonyInfo.imeiSIM1 = imeiSIM1;
    }*/

    public String getImsiSIM2() {
        return imeiSIM2;
    }

    /*public static void setImsiSIM2(String imeiSIM2) {
        TelephonyInfo.imeiSIM2 = imeiSIM2;
    }*/

    public boolean isSIM1Ready() {
        return isSIM1Ready;
    }

    /*public static void setSIM1Ready(boolean isSIM1Ready) {
        TelephonyInfo.isSIM1Ready = isSIM1Ready;
    }*/

    public boolean isSIM2Ready() {
        return isSIM2Ready;
    }

    /*public static void setSIM2Ready(boolean isSIM2Ready) {
        TelephonyInfo.isSIM2Ready = isSIM2Ready;
    }*/

    public boolean isDualSIM() {
        return imeiSIM2 != null;
    }

    private TelephonyInfo() {
    }

    @SuppressLint("MissingPermission")
    public static TelephonyInfo getInstance(Context context) {

        if (telephonyInfo == null) {

            telephonyInfo = new TelephonyInfo();

            TelephonyManager telephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));

            telephonyInfo.imeiSIM1 = telephonyManager.getSimSerialNumber();
            telephonyInfo.imeiSIM2 = null;

            try {
                telephonyInfo.imeiSIM1 = getDeviceIdBySlot(context, "getSimSerialNumberGemini", 1);
                telephonyInfo.imeiSIM2 = getDeviceIdBySlot(context, "getSimSerialNumberGemini", 2);
            } catch (GeminiMethodNotFoundException e) {
                e.printStackTrace();
                Log.d("TAGGS", "ERROR1");
                try {
                    telephonyInfo.imeiSIM1 = getDeviceIdBySlot(context, "getSimSerialNumber", 1);
                    telephonyInfo.imeiSIM2 = getDeviceIdBySlot(context, "getSimSerialNumber", 2);

                } catch (GeminiMethodNotFoundException e1) {
                    //Call here for next manufacturer's predicted method name if you wish
                    e1.printStackTrace();
                    Log.d("TAGGS", "ERROR");
                }
//                try {
//                    telephonyInfo.imeiSIM1 = getDeviceIdBySlot(context, "getDeviceId", 0);
//                    telephonyInfo.imeiSIM2 = getDeviceIdBySlot(context, "getDeviceId", 1);
//                } catch (GeminiMethodNotFoundException e1) {
//                    //Call here for next manufacturer's predicted method name if you wish
//                    e1.printStackTrace();
//                }
//                try {
//                    telephonyInfo.imeiSIM1 = getDeviceIdBySlot(context, "getSimSerialNumberGemini", 0);
//                    telephonyInfo.imeiSIM2 = getDeviceIdBySlot(context, "getSimSerialNumberGemini", 1);
//                } catch (GeminiMethodNotFoundException e1) {
//                    //Call here for next manufacturer's predicted method name if you wish
//                    e1.printStackTrace();
//                    Log.d("TAGGS", "ERROR");
//                }

            }

            telephonyInfo.isSIM1Ready = telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY;
            telephonyInfo.isSIM2Ready = false;

            try {
                telephonyInfo.isSIM1Ready = getSIMStateBySlot(context, "getSimStateGemini", 0);
                telephonyInfo.isSIM2Ready = getSIMStateBySlot(context, "getSimStateGemini", 1);
            } catch (GeminiMethodNotFoundException e) {

                e.printStackTrace();

                try {
                    telephonyInfo.isSIM1Ready = getSIMStateBySlot(context, "getSimState", 0);
                    telephonyInfo.isSIM2Ready = getSIMStateBySlot(context, "getSimState", 1);
                } catch (GeminiMethodNotFoundException e1) {
                    //Call here for next manufacturer's predicted method name if you wish
                    e1.printStackTrace();
                }
            }
        }
        try {
            Log.d("IMEIBOX", getDeviceIdBySlot(context, "getSimSerialNumber", 1));
        } catch (GeminiMethodNotFoundException e) {
            e.printStackTrace();
        }
        try {

            Log.d("IMEIBOX1", getDeviceIdBySlot(context, "getSimSerialNumber", 2));
        } catch (GeminiMethodNotFoundException e) {
            e.printStackTrace();
        }
        try {
            Log.d("IMEIBOX", getDeviceIdBySlot(context, "getSimSerialNumberGemini", 1));
        } catch (GeminiMethodNotFoundException e) {
            e.printStackTrace();
        }
        try {

            Log.d("IMEIBOX1", getDeviceIdBySlot(context, "getSimSerialNumberGemini", 2));
        } catch (GeminiMethodNotFoundException e) {
            e.printStackTrace();
        }
        //------------------------------------------------------------
        try {
            Log.d("IMDIBOX", getDeviceIdBySlot(context, "getNetworkOperator", 1));
            telephonyInfo.isSIM1Number = getDeviceIdBySlot(context, "getNetworkOperator", 1);
        } catch (GeminiMethodNotFoundException e) {
            e.printStackTrace();
        }
        try {

            Log.d("IMDIBOX1", getDeviceIdBySlot(context, "getNetworkOperator", 2));
            telephonyInfo.isSIM2Number = getDeviceIdBySlot(context, "getNetworkOperator", 2);
        } catch (GeminiMethodNotFoundException e) {
            e.printStackTrace();
        }
        try {
            Log.d("IMDIBOX", getDeviceIdBySlot(context, "getNetworkOperatorGemini", 1));
            telephonyInfo.isSIM1Number = getDeviceIdBySlot(context, "getNetworkOperatorGemini", 1);
        } catch (GeminiMethodNotFoundException e) {
            e.printStackTrace();
        }
        try {

            Log.d("IMDIBOX1", getDeviceIdBySlot(context, "getNetworkOperatorGemini", 2));
            telephonyInfo.isSIM2Number = getDeviceIdBySlot(context, "getNetworkOperatorGemini", 2);
        } catch (GeminiMethodNotFoundException e) {
            e.printStackTrace();
        }

        return telephonyInfo;
    }

    @SuppressLint("MissingPermission")
    private static String getDeviceIdBySlot(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {

        String imei = null;

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        try {

            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimID = telephonyClass.getMethod(predictedMethodName, parameter);
            Log.d("IMEIBOX_N", getSimID.getName());

            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimID.invoke(telephony, obParameter);
            Log.d("IMEIBOX_N", getSimID.toString());
            if (ob_phone != null) {
                imei = ob_phone.toString();

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeminiMethodNotFoundException(predictedMethodName);
        }

        return imei;
    }

    private static boolean getSIMStateBySlot(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {

        boolean isReady = false;

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        try {

            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimStateGemini = telephonyClass.getMethod(predictedMethodName, parameter);

            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimStateGemini.invoke(telephony, obParameter);

            if (ob_phone != null) {
                int simState = Integer.parseInt(ob_phone.toString());
                if (simState == TelephonyManager.SIM_STATE_READY) {
                    isReady = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeminiMethodNotFoundException(predictedMethodName);
        }

        return isReady;
    }


    private static class GeminiMethodNotFoundException extends Exception {

        private static final long serialVersionUID = -996812356902545308L;

        public GeminiMethodNotFoundException(String info) {
            super(info);
        }
    }
}