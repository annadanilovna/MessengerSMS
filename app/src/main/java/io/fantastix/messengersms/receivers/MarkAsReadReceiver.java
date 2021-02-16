package io.fantastix.messengersms.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static android.provider.Telephony.TextBasedSmsColumns.THREAD_ID;

public class MarkAsReadReceiver extends BroadcastReceiver {
    private final String MARK_AS_READ = "mark_as_read";

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case MARK_AS_READ:
                int thread_id = intent.getIntExtra(THREAD_ID, 0);
        }
    }
}
