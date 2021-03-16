package io.fantastix.messengersms;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Needed to make default sms app for testing
 */
public class HeadlessSmsSendService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
