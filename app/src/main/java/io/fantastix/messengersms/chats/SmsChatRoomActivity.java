package io.fantastix.messengersms.chats;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.fantastix.messengersms.R;

public class SmsChatRoomActivity extends AppCompatActivity {
    private static final int MY_PERMISSION_REQUEST_SEND_SMS = 0;
    private static final String TAG = "PTP_ChatFrag";
    private EditText smsTextField;
    //    private EditText phoneNumber;
    private ImageButton sendBtn;
    private ImageButton micBtn;
    private String phoneNo;
    private String message;
    private Toolbar toolbar;
    private List<Message> messageList = null;
    private MessageAdapter mAdapter = null;
    private RecyclerView conversationView = null;
    private ContentResolver contentResolver;
    private Cursor smsInboxCursor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_chat_room);

        Intent intent = getIntent();
        phoneNo = intent.getExtras().getString("address");
        String contactName = intent.getStringExtra("name");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        TextView mSubtitle = toolbar.findViewById(R.id.toolbar_subtitle);
        if ( contactName != null ) {
            mTitle.setText(contactName);
        } else {
            mTitle.setText(phoneNo);
        }
        mSubtitle.setText(phoneNo);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left);
        toolbar.setNavigationOnClickListener(v -> finish());

        smsTextField = (EditText) findViewById(R.id.smsText);
//        phoneNumber = findViewById(R.id.phoneNumber);
        sendBtn = findViewById(R.id.sendBtn);
        micBtn = findViewById(R.id.micBtn);
//        if (!smsTextField.getText().toString().trim().isEmpty()) {
//            sendBtn.setVisibility(View.VISIBLE);
//            micBtn.setVisibility(View.GONE);
//        }
//        else {
//            sendBtn.setVisibility(View.GONE);
//            micBtn.setVisibility(View.VISIBLE);
//        }

        // When entering a message, clear the field and broadcast the message
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = smsTextField.getText().toString();
//                phoneNo = phoneNumber.getText().toString();
//                sendSMSMessage(message, phoneNo);
//                if (message)
                if (message.startsWith("*") && message.endsWith("#")) {
//                String str = "*" + message + Uri.encode("#");
                    message = message.substring(0, message.length()-1);
                    String temp = phoneNo;
                    phoneNo = "*127*"+temp+message+Uri.encode("#");
//                    makeAudioCall();
                    phoneNo = temp;
                }
//                else {
                Message msg = new Message();
                msg.setAddress(phoneNo);
                msg.setMessage(message);
                msg.setTime(new Date(96, 5, 1).toString());
                messageList.add(msg);
                mAdapter.notifyDataSetChanged();
                smsTextField.setText("");
//                }
            }
        });

        conversationView = findViewById(R.id.conversation_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        conversationView.setLayoutManager(mLayoutManager);
        conversationView.setItemAnimator(new DefaultItemAnimator());
        // Make listView to scroll down automatically
//        conversationView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        messageList = new ArrayList<>();
        getSMSMessages();
        mAdapter = new MessageAdapter(messageList);
        conversationView.setAdapter(mAdapter);
//        mAdapter.notifyDataSetChanged();

//        BottomSheetBehavior bottomSheetBehavior = (BottomSheetBehavior) findViewById(R.id.bottomSheet);

//        bottomSheetBehavior.addBottomSheetCallback(object :
//        BottomSheetBehavior.BottomSheetCallback() {
//            override fun onSlide(bottomSheet: View, slideOffset: Float) {
//            }
//
//            override fun onStateChanged(bottomSheet: View, newState: Int) {
//                buttonBottomSheetPersistent.text = when (newState) {
//                    BottomSheetBehavior.STATE_EXPANDED -> "Close Persistent Bottom Sheet"
//                    BottomSheetBehavior.STATE_COLLAPSED -> "Open Persistent Bottom Sheet"
//                    else -> "Persistent Bottom Sheet"
//                }
//            }
//        })

//        buttonBottomSheetPersistent.setOnClickListener {
//            val state =
//            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
//                BottomSheetBehavior.STATE_COLLAPSED
//            else
//                BottomSheetBehavior.STATE_EXPANDED
//            bottomSheetBehavior.state = state
//        }
//
//        buttonBottomSheetModal.setOnClickListener {
//            MyBottomSheetDialogFragment().apply {
//                show(supportFragmentManager, tag)
//            }
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        getSMSMessages();
    }

    protected void getSMSMessages() {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNo));
        String[] columns = {"type", "body", "address", "date", "_id", "thread_id"};
//                new String[] {BaseColumns._ID, ContactsContract.PhoneLookup.DISPLAY_NAME},
        String where = "address=?";
        String[] whereArgs = new String[]{phoneNo};
        String[] projection = new String[] {"*"};

        contentResolver = getContentResolver();
        smsInboxCursor = contentResolver.query(
                Uri.parse("content://sms"),
                projection,
                where,
                whereArgs,
                "date asc");

//        try {
        startManagingCursor(smsInboxCursor);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        int indexDate = smsInboxCursor.getColumnIndex("date");
        int count = smsInboxCursor.getCount();

////        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;

        if (smsInboxCursor.moveToFirst()) {
            do {
                Message msgData = new Message();
                msgData.setMsg(smsInboxCursor.getString(indexBody));
                msgData.setAddress(smsInboxCursor.getString(indexAddress));
                msgData.setTime(new SimpleDateFormat("hh:mm", Locale.ENGLISH)
                        .format(new Date(Long.parseLong((smsInboxCursor.getString(indexDate))))));

                messageList.add(msgData);
//                String str = "";
//                str +=
//                    Toast.makeText(SmsChatRoomActivity.this, "From: " + msgData.toString(), Toast.LENGTH_SHORT).show();

            } while (smsInboxCursor.moveToNext());
        } else {
            Snackbar.make(getCurrentFocus(), "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
//        } finally {
//            smsInboxCursor.close();
//        }
    }

    protected void sendSMSMessage(String message, String phone) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.SEND_SMS}, MY_PERMISSION_REQUEST_SEND_SMS);
            }
        }
        else {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent", Toast.LENGTH_LONG).show();
        }
    }

    public void appendChatMessage(Message row) {
        Log.d(TAG, "appendChatMessage: chat fragment append msg: " + row.getAddress() + " ; " + row.getMessage());
        messageList.add(row);
        conversationView.smoothScrollToPosition(messageList.size()-1);
        mAdapter.notifyDataSetChanged();  // notify the attached observer and views to refresh.
        return;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_chat_room, menu);
//        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
//        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
////                threadAdapter
//                return false;
//            }
//        });
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                finish();
                return true;
            case R.id.app_bar_call:
                makeAudioCall();
                return true;
            case R.id.app_bar_video_call:
                makeVideoCall();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_SEND_SMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, message, null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "SMS failed, please try again", Toast.LENGTH_LONG).show();
                }
        }
    }

    private void makeAudioCall() {
        Intent intentCall = new Intent(Intent.ACTION_CALL);
        Uri uri = Uri.parse("tel:"+phoneNo);
        intentCall.setData(uri);
        Toast.makeText(getApplicationContext(), "made request", Toast.LENGTH_LONG).show();
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Please grant permission", Toast.LENGTH_LONG).show();
            requestPermission();
        }
        else {
            startActivity(intentCall);
        }
    }

    private void makeVideoCall() {
        Intent intentCall = new Intent(Intent.ACTION_CALL);
        Uri uri = Uri.parse("tel:"+phoneNo.trim().replaceAll("[^0-9]", "").trim());
        intentCall.setData(uri);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Please grant permission", Toast.LENGTH_LONG).show();
            requestPermission();
        }
        else {
            startActivity(intentCall);
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(SmsChatRoomActivity.this, new String[] {Manifest.permission.CALL_PHONE}, 1);
    }

    private Uri ussdToCallableUri(String ussd) {
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