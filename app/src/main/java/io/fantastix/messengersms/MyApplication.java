package io.fantastix.messengersms;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.provider.FontRequest;
import android.provider.Telephony;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;
//import androidx.emoji.text.FontRequestEmojiCompatConfig;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MyApplication extends Application {

    public static final String TAG = MyApplication.class.getSimpleName();

    private RequestQueue mRequestQueue;

    private static MyApplication mInstance;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

//        FontRequest fontRequest = new FontRequest(
//                "com.example.fontprovider",
//                "com.example",
//                "emoji compat Font Query",
//                CERTIFICATES);
//        EmojiCompat.Config config = new FontRequestEmojiCompatConfig(this, fontRequest);
//        EmojiCompat.init(config);
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }


    static boolean isDefaultSmsApp(final Context context) {
//        return Telephony.Sms.getDefaultSmsPackage(context).equals(context.getPackageName());
        // there is no default sms app before android 4.4
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return true;
        }

        try {
            // check if this is the default sms app.
            // If the device doesn't support Telephony.Sms (i.e. tablet) getDefaultSmsPackage() will
            // be null.
            final String smsPackage = Telephony.Sms.getDefaultSmsPackage(context);
            return smsPackage == null || smsPackage.equals(BuildConfig.APPLICATION_ID);
        } catch (SecurityException e) {
            // some samsung devices/tablets want permission GET_TASKS o.O
            Log.e(TAG, "failed to query default SMS app", e);
            return true;
        }
    }

}