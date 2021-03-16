package io.fantastix.messengersms;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.Telephony;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class SmsDumpActivity extends AppCompatActivity {
    ListView listView;
    private List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_dump);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ContentResolver cr = this.getContentResolver();
//        final String[] projection = new String[]{
//                "_id",
//                "date",
//                "message_count",
//                "recipient_ids",
//                "snippet", // last message
//                "read",
//                "type"
//        };
//        String[] projection = {"DISTINCT THREAD_ID", "_ID", "ADDRESS", "BODY", "DATE",
//                "TYPE",
//                "READ",
//                "SEEN",
//                "MESSAGE_COUNT",
//                "RECIPIENT_IDS",
//                "SNIPPET"
//        };
        String[] projection = {
                "DISTINCT THREAD_ID", "SNIPPET", "MSG_COUNT"
        };
        String selection = "THREAD_ID IS NOT NULL) GROUP BY (THREAD_ID";
        Cursor c = cr.query(
//                Uri.parse(Utils.SMS),
                Uri.parse(Utils.CONVERSATION),
                null, //projection,
                selection,
                null,
                "DATE DESC");

        int count = c.getCount();

        if (c.moveToFirst()) { // must check the result to prevent exception
            for (int row = 0; row < count; row++) {
                String msgData = "";
                for (int idx = 0; idx < c.getColumnCount(); idx++) {
                    msgData += " " + c.getColumnName(idx) + ":" + c.getString(idx) + "\n";
                }
                list.add(msgData);
                c.moveToNext();
            }

            Cursor smsThreads = cr.query(
                    Telephony.Sms.Conversations.CONTENT_URI,
                    null,
                    null,
                    null,
                    "DATE DESC");
            smsThreads.moveToNext();
            smsThreads.getString(1);

        } else {
//            throw new RuntimeException("You have no SMS");
//            textView.setVisibility(View.INVISIBLE);
//            textView.setText("No SMS to display");
            Snackbar.make(getCurrentFocus(), "No SMS to display", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
//            // empty box, no SMS
        }

        listView = findViewById(R.id.list);
        ArrayAdapter aAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(aAdapter);
    }

}