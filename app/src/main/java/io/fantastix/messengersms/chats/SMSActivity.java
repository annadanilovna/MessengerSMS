package io.fantastix.messengersms.chats;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.fantastix.messengersms.BuildConfig;
import io.fantastix.messengersms.R;
import io.fantastix.messengersms.contacts.ContactsActivity;

public class SMSActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    public static final String INBOX = "content://sms/inbox";
    public static final String SENT = "content://sms/sent";
    public static final String DRAFT = "content://sms/draft";
    private static final int REQUEST_ID_MULTIPLE_PERMISSION = 1;
    private static final int SMS_PERMISSION_CODE = 1;

    private List<Sms> smsMessagesList = new ArrayList<Sms>();
    private SmsThreadAdapter threadAdapter;
    private SmsBroadcastReceiver smsBroadcastReceiver;

    private static SMSActivity mActivity;

    public static SMSActivity instance() {
        return mActivity;
    }

    @Override
    public void onStart() {
        super.onStart();
        mActivity = this;
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        //        Toolbar toolbar = findViewById(R.id.toolbar);
//        toolbar.setTitle("Chats");
//        toolbar.setNavigationIcon(R.drawable.haha);
//        toolbar.setSubtitle("Hello");
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(SMSActivity.this, "Back Arrow Toolbar Image Ic on Clicked", Toast.LENGTH_LONG).show();
//            }
//        });
//        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chats");
//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        getSupportActionBar().setCustomView(R.layout.toolbar_center);

//        smsListView = (ListView) findViewById(R.id.sms_roll);
//        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, smsMessagesList);
//        smsListView.setAdapter(arrayAdapter);
//        smsListView.setOnItemClickListener(this);

        //        Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.roboto_black);
//        Typeface typeface = getResources().getFont(R.font.roboto_black);
//        textView.setTypeFace(typeface);

        if (checkAndRequestPermissions()) {
            smsBroadcastReceiver = new SmsBroadcastReceiver();
            registerReceiver(smsBroadcastReceiver, new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));
            smsBroadcastReceiver.setListener(new SmsBroadcastReceiver.Listener() {
                @Override
                public void onTextReceived(String text) {
                    Toast.makeText(SMSActivity.this, "Message received: " + text, Toast.LENGTH_SHORT).show();
                }
            });

            RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.sms_roll);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            final String myPackageName = getPackageName();
//            if (Telephony.Sms.getDefaultSmsPackage(mActivity).equals(myPackageName)) {
//                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
//                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, myPackageName);
//                startActivityForResult(intent, 1);
//            }
//            else {
////                        List<Sms> lst = getAllSms();
//            }
//        }
//        else {
////                        List<Sms> lst = getAllSms();
//        }
            refreshSmsInbox();
//        getAllSms();

            threadAdapter = new SmsThreadAdapter(smsMessagesList);
            mRecyclerView.setAdapter(threadAdapter);
            threadAdapter.setOnItemClickListener(new SmsThreadAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View itemView, int position) {
                    Sms sms = smsMessagesList.get(position);
                    Intent i = new Intent(SMSActivity.this, SmsChatRoomActivity.class);
                    i.putExtra("address", sms.getAddress());
                    startActivity(i);
                }
            });

        }

        FloatingActionButton fab = findViewById(R.id.new_message);
        fab.setRippleColor(getResources().getColor(R.color.colorAccent));
//        ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
//        drawable.getPaint().setColor(getResources().getColor(R.color.white));
//        fab.setImageDrawable(drawable);
        fab.setOnClickListener(view -> {
            startActivity(new Intent(SMSActivity.this, ContactsActivity.class));
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    final String myPackageName = getPackageName();
                    if (Telephony.Sms.getDefaultSmsPackage(mActivity).equals(myPackageName)) {
//                        List<Sms> lst = getAllSms();
                    }
                }
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sms, menu);
        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
        searchViewItem.setIcon(R.drawable.ic_search_black);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                threadAdapter
                return false;
            }
        });
        return true;
    }

    @Override
    // Dynamically change menu depending on network status
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_sms, menu);
//        MenuItem item = menu.findItem(R.id.action_change_network_status);
//        if(isOnline){
//            // If connected to the network, show `go offline' action
//            item.setTitle(R.string.action_go_offline);
//        } else {
//            // If disconnected from the network, show `go online' action
//            item.setTitle(R.string.action_go_online);
//        }
        return super.onPrepareOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_bar_about:
                AlertDialogCreate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(smsBroadcastReceiver);
        super.onDestroy();
    }

    /**
     * Check if SMS permission is granted
     */
//    public boolean isSmsPermissionGranted() {
//        return ContextCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS);
//        return true;
//    }
    public int isSmsPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
    }

    /**
     *  Request runtime SMS permission
     */
    private void requestReadAndSendSmsPermission(Activity activity) {
//        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_SMS)) {
//        }
        ActivityCompat.requestPermissions(this, new String[] {
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.SEND_SMS
        }, SMS_PERMISSION_CODE);
    }

    public boolean checkAndRequestPermissions() {
        int sms = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        int contacts = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);

        if (sms != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_SMS,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, REQUEST_ID_MULTIPLE_PERMISSION);
        }
        else if (contacts != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_CONTACTS
            }, REQUEST_ID_MULTIPLE_PERMISSION);
        }
        return true;
    }

    public void showRequestPermissionInfoAlertDialog(final boolean makeSystenRequest) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SMSActivity.this)
                .setIcon(R.mipmap.ic_launcher)
//                .setTitle("About")
                .setMessage("Made with ❤ by Christian Augustyn")
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        // Display system runtime request
                        if (makeSystenRequest) {
                            requestReadAndSendSmsPermission(SMSActivity.this);
                        }
                    }
                });
//                .setNegativeButton("Cancel", (dialog, which) -> Toast.makeText(SMSActivity.this, "You Clicked on Cancel", Toast.LENGTH_SHORT).show());
        builder.setCancelable(false);
        builder.show();
    }

    public void AlertDialogCreate(){
        new AlertDialog.Builder(SMSActivity.this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("About")
                .setMessage("Made with ❤ by Christian Augustyn")
                .setPositiveButton("OK", null).show();
//                .setNegativeButton("Cancel", null)
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(SMSActivity.this, "You Clicked on OK", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .setNegativeButton("Cancel", (dialog, which) -> Toast.makeText(SMSActivity.this, "You Clicked on Cancel", Toast.LENGTH_SHORT).show()).show();
    }

    public void refreshSmsInbox() {
        ContentResolver contentResolver = getContentResolver();
        String[] projection = {"DISTINCT THREAD_ID", "_ID", "ADDRESS", "BODY", "DATE", "TYPE"};
        String selection = "THREAD_ID IS NOT NULL) GROUP BY (THREAD_ID";
//        Cursor smsInboxCursor = contentResolver.query(Uri.parse(INBOX),
        Cursor smsInboxCursor = contentResolver.query(
                Uri.parse("content://sms"),
                projection,
                selection,
                null,
                "date desc");

//        try {
        startManagingCursor(smsInboxCursor);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        int indexDate = smsInboxCursor.getColumnIndex("date");
        int count = smsInboxCursor.getCount();

//        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;
//        arrayAdapter.clear();

        if (smsInboxCursor.moveToFirst()) { // must check the result to prevent exception
            do {
//                String msgData = "";
//                for (int idx = 0; idx < smsInboxCursor.getColumnCount(); idx++) {
//                    msgData += " " + smsInboxCursor.getColumnName(idx) + ":" + smsInboxCursor.getString(idx) + "\n";
//                }
//                arrayAdapter.add(msgData);

                Sms msgData = new Sms();
                msgData.setMsg(smsInboxCursor.getString(indexBody));
//                msgData.setSender(getContactName(getApplicationContext(), smsInboxCursor.getString(indexAddress)));
                msgData.setSender(smsInboxCursor.getString(indexAddress));
                msgData.setAddress(smsInboxCursor.getString(indexAddress));
                msgData.setTime(new SimpleDateFormat("hh:mm", Locale.ENGLISH)
                        .format(new Date(Long.parseLong((smsInboxCursor.getString(indexDate))))));

                smsMessagesList.add(msgData);

            } while (smsInboxCursor.moveToNext());
        } else {
//            textView.setVisibility(View.INVISIBLE);
//            textView.setText("No SMS to display");
            Snackbar.make(getCurrentFocus(), "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
//            // empty box, no SMS
        }
//        }
//        finally {
//            smsInboxCursor.close();
//        }

//        String receiveDayTime = Functions.dateFromMilisec(Long.valueOf(c.getColumnIndexOrThrow("date")), "hh:mm a MMM dd, yyyy");

    }

    public /*List<Sms>*/ void getAllSms() {
//        List<Sms> lstSms = new ArrayList<Sms>();
        Sms objSms = new Sms();
        Uri message = Uri.parse("content://sms/");
        ContentResolver cr = mActivity.getContentResolver();

        Cursor c = cr.query(message, null, null, null, null);
        mActivity.startManagingCursor(c);
        int totalSMS = c.getCount();

        if (c.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {

                objSms = new Sms();
                objSms.setId(c.getString(c.getColumnIndexOrThrow("_id")));
                objSms.setAddress(c.getString(c.getColumnIndexOrThrow("address")));
                objSms.setMsg(c.getString(c.getColumnIndexOrThrow("body")));
                objSms.setReadState(c.getString(c.getColumnIndex("read")));
                objSms.setTime(c.getString(c.getColumnIndexOrThrow("date")));
                if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
                    objSms.setFolderName("inbox");
                } else {
                    objSms.setFolderName("sent");
                }

                smsMessagesList.add(objSms);
                c.moveToNext();
            }
        }
        else {
//            throw new RuntimeException("You have no SMS");
        }

        c.close();

//        return lstSms;
    }

    public String getContactName(Context context, String phoneNumber) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        String[] projection = {ContactsContract.PhoneLookup.DISPLAY_NAME};
        Cursor cursor = contentResolver.query(
                uri,
                projection,
                null,
                null,
                "date desc");
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

    public void updateList(final Sms smsMessage) {
        smsMessagesList.add(smsMessage);
        threadAdapter.notifyDataSetChanged();
    }
}