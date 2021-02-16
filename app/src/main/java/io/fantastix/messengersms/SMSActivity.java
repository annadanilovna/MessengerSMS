package io.fantastix.messengersms;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.SearchManager;
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
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import io.fantastix.messengersms.adapter.SmsRecyclerViewAdapter;
import io.fantastix.messengersms.model.Contact;
import io.fantastix.messengersms.model.Sms;
import io.fantastix.messengersms.receivers.SmsReceiver;

import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;

public class SMSActivity extends AppCompatActivity implements /*AdapterView.OnItemClickListener, */ SmsRecyclerViewAdapter.SmsAdapterListener {
    private static final String TAG = "PTP_ChatFrag";
    public static final String INBOX = "content://sms/inbox";
    public static final String CONVERSATION = "content://sms/conversation";
    public static final String SENT = "content://sms/sent";
    public static final String DRAFT = "content://sms/draft";
    private static final int REQUEST_ID_MULTIPLE_PERMISSION = 1;
    private static final int PERMISSION_REQUEST_SMS = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int PERMISSION_REQUEST_CAMERA = 0;

    private List<Sms> smsList = new ArrayList<>();
    private SmsRecyclerViewAdapter smsRecyclerViewAdapter;
    private SmsReceiver smsReceiver;
    private SearchView searchView;
    private TelephonyManager telephonyManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        toolbar.setTitle("Chats");
//        toolbar.setNavigationIcon(R.drawable.haha);
//        toolbar.setSubtitle("Hello");
//        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chats");
//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        getSupportActionBar().setCustomView(R.layout.toolbar_center);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            checkAndRequestAllPermissions();
        }

        smsReceiver = new SmsReceiver();
        registerReceiver(smsReceiver, new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));
        smsReceiver.setListener(new SmsReceiver.Listener() {
            @Override
            public void onNewMessageReceived(Sms message) {
                Toast.makeText(SMSActivity.this, "Message received: " + message.getMsg(), Toast.LENGTH_SHORT).show();
                updateList(message, 1);
                smsRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onTextReceived(String text) {
                Toast.makeText(SMSActivity.this, "Message received: " + text, Toast.LENGTH_SHORT).show();
//                updateList(message, 1);
                smsRecyclerViewAdapter.notifyDataSetChanged();
            }
        });

        RecyclerView mRecyclerView = findViewById(R.id.sms_roll);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        smsRecyclerViewAdapter = new SmsRecyclerViewAdapter(this, smsList, this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(smsRecyclerViewAdapter));
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
//        mRecyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, mRecyclerView, this));
        mRecyclerView.setAdapter(smsRecyclerViewAdapter);

        telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, READ_SMS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{READ_SMS, READ_PHONE_NUMBERS, READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);
        } else {
            Toast.makeText(this, "#"
                            + telephonyManager.getNetworkCountryIso() + "\n"
//                            + telephonyManager.getSimSerialNumber() + "\n"
                            + telephonyManager.getNetworkOperatorName() + "\n"
                            + telephonyManager.getSimOperatorName() + "\n"
//                            telephonyManager.getSubscriberId() + "\n"
                    , Toast.LENGTH_LONG).show();
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            final String myPackageName = getPackageName();
//            if (Telephony.Sms.getDefaultSmsPackage(getApplicationContext()).equals(myPackageName)) {
//                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
//                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, myPackageName);
//                startActivityForResult(intent, 1);
//            }
//            else {
//                refreshSmsInbox();
//            }
//        }
//        else {
//            refreshSmsInbox();
//        }
        refreshSmsInbox();

        FloatingActionButton fab = findViewById(R.id.new_message);
        fab.setRippleColor(getResources().getColor(R.color.colorSecondary));
        fab.setOnClickListener(view -> startActivity(new Intent(this, ContactsActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshSmsInbox();
//        Toast.makeText(this, "resume", Toast.LENGTH_SHORT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(this, "pause", Toast.LENGTH_SHORT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    final String myPackageName = getPackageName();
                    if (Telephony.Sms.getDefaultSmsPackage(getApplicationContext()).equals(myPackageName)) {
                        refreshSmsInbox();
                    }
                }
            }
        }
    }

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Sms sms = smsList.get(position);
//        Intent i = new Intent(SMSActivity.this, SMSChatActivity.class);
//        i.putExtra("phoneNumber", sms.getContact().getPhoneNumber());
//        i.putExtra("name", sms.getContact().getName());
//        startActivity(i);
//    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sms, menu);

        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
        searchViewItem.setIcon(R.drawable.ic_search_black);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);
//        searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                smsRecyclerViewAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                smsRecyclerViewAdapter.getFilter().filter(query);
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
            case R.id.app_bar_search:
                return true;
            case R.id.app_bar_about:
                AlertDialogCreate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(smsReceiver);
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) !=
                                PackageManager.PERMISSION_GRANTED) {
                    return;
                } else {
                    Toast.makeText(SMSActivity.this, telephonyManager.getSimSerialNumber(), Toast.LENGTH_SHORT).show();
                }
                break;
            case PERMISSION_REQUEST_CAMERA:
                // Request for camera permission.
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission has been granted. Start camera preview Activity.
                    Snackbar.make(getCurrentFocus(), "Camera_Permission_Granted",
                            Snackbar.LENGTH_SHORT)
                            .show();
//                    startCamera();
                } else {
                    // Permission request was denied.
                    Snackbar.make(getCurrentFocus(), "Camera_permission_denied",
                            Snackbar.LENGTH_SHORT)
                            .show();
                }
        }
    }

//    @Override
//    public void onPermissionsDenied(int requestCode, List<String> perms) {
//        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());
//        if (EasyPermissions.somePermissionPermanentlyDenied(activity, perms)) {
//            new AppSettingsDialog.Builder(activity)
//                    .setTitle("Permissions Required")
//                    .setPositiveButton("Settings")
//                    .setNegativeButton("Cancel")
//                    .setRequestCode(RC_SETTINGS)
//                    .build()
//                    .show();
//        }
//    }

    /**
     * Check if SMS permission is granted
     */
    public boolean isSmsPermissionGranted() {
        return ActivityCompat.shouldShowRequestPermissionRationale(this, READ_SMS);
//    }
//    public int isSmsPermissionGranted() {
//        return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
    }

    /**
     *  Request runtime SMS permission
     */
    private void requestReadAndSendSmsPermission() {
        if (isSmsPermissionGranted()) {
            Log.d(TAG, "shouldShowRequestPermissionRationale(), no permission requested");
            return;
        }
        ActivityCompat.requestPermissions(this, new String[] {
                READ_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.SEND_SMS
        }, PERMISSION_REQUEST_SMS);
    }

    public void checkAndRequestAllPermissions() {
        if (ContextCompat.checkSelfPermission(this, READ_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_MMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_WAP_PUSH) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[] {
                            READ_SMS,
                            Manifest.permission.RECEIVE_SMS,
                            Manifest.permission.SEND_SMS,
                            Manifest.permission.CALL_PHONE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.VIBRATE,
                            Manifest.permission.INTERNET
                    }, REQUEST_ID_MULTIPLE_PERMISSION);
        }

    }

    private boolean hasReadSmsPermission() {
        return ContextCompat.checkSelfPermission(SMSActivity.this,
                READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(SMSActivity.this,
                        Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasValidPreConditions() {
        if (!hasReadSmsPermission()) {
            requestReadAndSendSmsPermission();
            return false;
        }

//        if (!SmsHelper.isValidPhoneNumber(mNumberEditText.getText().toString())) {
//            Toast.makeText(getApplicationContext(), R.string.error_invalid_phone_number, Toast.LENGTH_SHORT).show();
//            return false;
//        }
        return true;
    }

    private void showRequestPermissionsInfoAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("About")
                .setMessage("Made with ❤ by Christian Augustyn")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        // Display system runtime request
//                        if (makeSystenRequest) {
//                            requestReadAndSendSmsPermission(SMSActivity.this);
//                        }
                        requestReadAndSendSmsPermission();
                    }
                });
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
        String[] projection = {"DISTINCT THREAD_ID", "_ID", "ADDRESS", "BODY", "DATE",
                "TYPE",
                "READ",
//                "MESSAGE_COUNT",
//                "RECIPIENT_IDS",
//                "SNIPPET"
        };
        String selection = "THREAD_ID IS NOT NULL) GROUP BY (THREAD_ID";

        Cursor c = cr.query(
                Uri.parse(INBOX),
//                Uri.parse("content://sms"),
                projection,
                selection,
                null,
                "DATE DESC");

//        try {
        startManagingCursor(c);
        int indexBody = c.getColumnIndex("body");
        int indexAddress = c.getColumnIndex("address");
        int indexDate = c.getColumnIndex("date");
        int indexRead = c.getColumnIndex("read");
        int indexId = c.getColumnIndexOrThrow("_id");
        int count = c.getCount();

        if (indexBody < 0 || !c.moveToFirst()) return;

        if (c.moveToFirst()) { // must check the result to prevent exception
//                String msgData = "";
//                for (int idx = 0; idx < c.getColumnCount(); idx++) {
//                    msgData += " " + c.getColumnName(idx) + ":" + c.getString(idx) + "\n";
//                }
//                for (int i = 0; i < count; i++) {
//
//                    objSms = new Sms();
//                    objSms.setId(c.getString(c.getColumnIndexOrThrow("_id")));
//                    objSms.setAddress(c.getString(c.getColumnIndexOrThrow("address")));
//                    objSms.setMsg(c.getString(c.getColumnIndexOrThrow("body")));
//                    objSms.setReadState(c.getString(c.getColumnIndex("read")));
//                    objSms.setTime(c.getString(c.getColumnIndexOrThrow("date")));
//                    if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
//                        objSms.setFolderName("inbox");
//                    } else {
//                        objSms.setFolderName("sent");
//                    }
//
//                    smsMessagesList.add(objSms);
//                    c.moveToNext();
//                }

            do {
                Sms msgData = new Sms();
//                msgData.setId(c.getString(indexId));
                msgData.setMsg(c.getString(indexBody));
                Contact contact = new Contact();
                contact.setPhoneNumber(c.getString(indexAddress));
                contact.setName(Utils.getContactName(getApplicationContext(), c.getString(indexAddress)));
                msgData.setContact(contact);
                msgData.setTime(c.getLong(indexDate));
                msgData.setReadState(c.getInt(indexRead));
//                if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
//                    msgData.setFolderName("inbox");
//                } else {
//                    msgData.setFolderName("sent");
//                }

                smsList.add(msgData);

            } while (c.moveToNext());
        } else {
//            throw new RuntimeException("You have no SMS");
//            textView.setVisibility(View.INVISIBLE);
//            textView.setText("No SMS to display");
            Snackbar.make(getCurrentFocus(), "No SMS to display", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
//            // empty box, no SMS
        }
//    } finally {
//        c.close();
//    }

//        c.close();
    }

    public void updateList(final Sms smsMessage, int direction) {
        smsList.add(smsMessage);
        smsRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(View itemView, int position) {
        // Values are passing to activity & to fragment as well
        Sms sms = smsList.get(position);
        ImageView picture = (ImageView) itemView.findViewById(R.id.contact_image);
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SMSActivity.this, "Single Click on Image :"+position,
                        Toast.LENGTH_SHORT).show();
            }
        });
        Intent i = new Intent(SMSActivity.this, SMSChatActivity.class);
        i.putExtra("phoneNumber", sms.getContact().getPhoneNumber());
        i.putExtra("name", sms.getContact().getName());
        startActivity(i);
    }

    @Override
    public void onSelectedSms(Sms sms) {
        Toast.makeText(getApplicationContext(), "Selected: " + sms.getContact().getPhoneNumber() + ", " + sms.getMsg(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLongClick(View view, int position) {
        Sms sms = smsList.get(position);
        Toast.makeText(SMSActivity.this, "Long press on position :"+sms.getMsg(),
                Toast.LENGTH_LONG).show();
    }

    public boolean isSimExists() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        int SIM_STATE = telephonyManager.getSimState();

        if (SIM_STATE == TelephonyManager.SIM_STATE_READY)
            return true;
        else {
            // we can inform user about sim state
            switch (SIM_STATE) {
                case TelephonyManager.SIM_STATE_ABSENT:
                case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                case TelephonyManager.SIM_STATE_UNKNOWN:
                    break;
            }
            return false;
        }
    }

}