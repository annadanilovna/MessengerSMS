package io.fantastix.messengersms;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.provider.Telephony;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.txusballesteros.bubbles.BubbleLayout;
import com.txusballesteros.bubbles.BubblesManager;
import com.txusballesteros.bubbles.OnInitializedCallback;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import co.dift.ui.SwipeToAction;
import io.fantastix.messengersms.adapter.SmsRecyclerViewAdapter;
import io.fantastix.messengersms.model.Contact;
import io.fantastix.messengersms.model.Message;
import io.fantastix.messengersms.receivers.SmsReceiver;
import io.fantastix.messengersms.viewmodel.SmsViewModel;
import kotlin.ExtensionFunctionType;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;
import static io.fantastix.messengersms.receivers.SmsReceiver.NOTIFICATION;
import static io.fantastix.messengersms.receivers.SmsReceiver.NOTIFICATION_ID;

public class SMSActivity extends AppCompatActivity implements /*AdapterView.OnItemClickListener, */ SmsRecyclerViewAdapter.SmsAdapterListener {
    private static final String TAG = "PTP_ChatFrag";
    private static final int REQUEST_ID_MULTIPLE_PERMISSION = 1;
    private static final int PERMISSION_REQUEST_SMS = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int PERMISSION_REQUEST_CAMERA = 0;
    private static final int PERMISSIONS_REQUEST_CALL_PHONE = 2;
    // Key for the string that's delivered in the action's intent.
    private static final String KEY_TEXT_REPLY = "key_text_reply";
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;
    private static final String PREF_USER_MOBILE_PHONE = "pref_user_mobile_phone";

    private final List<Message> smsList = new ArrayList<>();
    private RecyclerView smsRecyclerView;
    private SmsRecyclerViewAdapter smsRecyclerViewAdapter;
    private SmsReceiver smsReceiver;
    private SearchView searchView;
    private TelephonyManager telephonyManager;
    private BubblesManager bubblesManager;
    private CoordinatorLayout coordinatorLayout;
    private SwipeToAction swipeToAction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        coordinatorLayout = findViewById(R.id.coordinator);
//        initializeBubblesManager();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Chats");
        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("Chats");
//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        getSupportActionBar().setCustomView(R.layout.toolbar_center);

//        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        String mUserMobilePhone = mSharedPreferences.getString(PREF_USER_MOBILE_PHONE, "");
//        if (!TextUtils.isEmpty(mUserMobilePhone)) {
//            mNumberEditText.setText(mUserMobilePhone);
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            checkAndRequestAllPermissions();
            if (!hasReadSmsPermission()) {
                showRequestPermissionsInfoAlertDialog();
            }
        }

        // Permission to allow the bubble to overlay the window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(SMSActivity.this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1234);
            }
        }
        else {
            Intent intent = new Intent(SMSActivity.this, Service.class);
            startService(intent);
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ActivityCompat.checkSelfPermission(this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//            List<SubscriptionInfo> subscription = SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoList();
//            for (int i = 0; i < subscription.size(); i++) {
//                SubscriptionInfo info = subscription.get(i);
//                Log.d(TAG, "number " + info.getNumber());
//                Log.d(TAG, "network name : " + info.getCarrierName());
//                Log.d(TAG, "country iso " + info.getCountryIso());
//                Log.d(TAG, "country iso " + info.getDisplayName());
//                Log.d(TAG, "country iso " + info.getSubscriptionId());
//                Log.d(TAG, "country iso " + info.getSimSlotIndex());
//            }
//        }

//        if (!isDefaultSmsApp()) {
//            Toast.makeText(SMSActivity.this, "Make app default", Toast.LENGTH_SHORT).show();
//            setAppAsDefault();
//        }
//        else {
//            Toast.makeText(SMSActivity.this, "app is default", Toast.LENGTH_SHORT).show();
//        }

//        Intent service = new Intent(this, SmsService.class);
//        startService(service);
//        stopService(service);

        smsReceiver = new SmsReceiver();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(smsReceiver, new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));
        }
        smsReceiver.setListener(new SmsReceiver.SmsListener() {
            @Override
            public void onMessageReceived(Message message) {
                Toast.makeText(SMSActivity.this, "Message received: " + message.getMessage(), Toast.LENGTH_SHORT).show();
                // this will update the UI with message
                updateList(message, 1);
                smsRecyclerViewAdapter.notifyDataSetChanged();
                scheduleNotification(getNotification(message.getMessage()), 2);
            }
        });

        smsRecyclerView = findViewById(R.id.sms_roll);

        refreshSmsInbox();

//        SmsViewModel viewModel = new ViewModelProvider(this).get(SmsViewModel.class);
//        viewModel.loadUsers();
//        viewModel.getMessages().observe(this, messages -> {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        smsRecyclerView.setLayoutManager(mLinearLayoutManager);

//            smsRecyclerViewAdapter = new SmsRecyclerViewAdapter(this, messages, this);
        smsRecyclerViewAdapter = new SmsRecyclerViewAdapter(this, smsList, this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(this.SwipeToDeleteCallback());
        itemTouchHelper.attachToRecyclerView(smsRecyclerView);
        smsRecyclerView.setHasFixedSize(true);
        /*swipeToAction = new SwipeToAction(smsRecyclerView, new SwipeToAction.SwipeListener<Message>() {
            @Override
            public boolean swipeLeft(final Message itemData) {
//                final int pos = removeBook(itemData);
                displaySnackbar(itemData.getMessage() + " removed", "Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        addBook(pos, itemData);
                    }
                });
                return true;
            }

            @Override
            public boolean swipeRight(Message itemData) {
                displaySnackbar(itemData.getMessage() + " loved", null, null);
                return true;
            }

            @Override
            public void onClick(Message itemData) {
                displaySnackbar(itemData.getMessage() + " clicked", null, null);
            }

            @Override
            public void onLongClick(Message itemData) {
                displaySnackbar(itemData.getMessage() + " long clicked", null, null);
            }
        });*/
        smsRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, smsRecyclerView, this));
        smsRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        smsRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
//        smsRecyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));
//        smsRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
//            @Override
//            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
//                new SwipeToPerformActionCallback(getApplicationContext()).onDraw(c);
//            }
//        });
        smsRecyclerView.setAdapter(smsRecyclerViewAdapter);
//            progressBar.setVisibility(View.GONE);
//        });

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

        FloatingActionButton fab = findViewById(R.id.new_message);
//        fab.setRippleColor(getResources().getColor(R.color.secondaryColor));
        fab.setOnClickListener(view -> startActivity(new Intent(this, ContactsActivity.class)));
    }

    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(KEY_TEXT_REPLY);
        }
        return null;
    }

    public void simNumberGenerate() {
        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(this);

        String imeiSIM1 = telephonyInfo.getImsiSIM1();
        String imeiSIM2 = telephonyInfo.getImsiSIM2();
        String imeiSIM1NUM = telephonyInfo.isSIM1Number();
        String imeiSIM2NUM = telephonyInfo.isSIM2Number();

        boolean isSIM1Ready = telephonyInfo.isSIM1Ready();
        boolean isSIM2Ready = telephonyInfo.isSIM2Ready();

        boolean isDualSIM = telephonyInfo.isDualSIM();

//        TextView tv = (TextView) findViewById(R.id.txtSecureNumber);
//        tv.setText(" SIM NO:1 : " + imeiSIM1 + "\n" +
//                " SIM NO:2 : " + imeiSIM2 + "\n" +
//                " IS DUAL SIM : " + isDualSIM + "\n" +
//                " IS SIM1 READY : " + isSIM1Ready + "\n" +
//                " IS SIM2 READY : " + isSIM2Ready + "\n" +
//                " Network 1 Code: " + imeiSIM1NUM + "\n" +
//                " Network 2 Code: : " + imeiSIM2NUM + "\n"
//                );
    }

    private void displaySnackbar(String text, String actionName, View.OnClickListener action) {
        Snackbar snack = Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG)
                .setAction(actionName, action);

        View v = snack.getView();
        v.setBackgroundColor(getResources().getColor(R.color.secondaryColor));
        ((TextView) v.findViewById(R.id.snackbar_text)).setTextColor(Color.WHITE);
        ((TextView) v.findViewById(R.id.snackbar_action)).setTextColor(Color.BLACK);

        snack.show();
    }

    private void initDialog(){
        View view = getLayoutInflater().inflate(R.layout.dialog_layout,null);
        EditText et_country = view.findViewById(R.id.et_country);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setView(view);
        alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                if (add) {
//                    add = false;
//                    adapter.addItem(et_country.getText().toString());
//                    dialog.dismiss();
//                } else {
//                    mess.set(edit_position, et_country.getText().toString());
//                    smsRecyclerViewAdapter.notifyDataSetChanged();
                dialog.dismiss();
//                }

            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        refreshSmsInbox();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsReceiver);
//        bubblesManager.recycle();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1) {
//            if (resultCode == RESULT_OK) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                    final String myPackageName = getPackageName();
//                    if (Telephony.Sms.getDefaultSmsPackage(getApplicationContext()).equals(myPackageName)) {
//                        refreshSmsInbox();
//                    }
//                }
//            }
//        }
//    }

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Sms sms = smsList.get(position);
//        Intent i = new Intent(SMSActivity.this, SMSChatActivity.class);
//        i.putExtra("phoneNumber", sms.getContact().getPhoneNumber());
//        i.putExtra("name", sms.getContact().getName());
//        startActivity(i);
//    }

    public String convertToTime(int averageInSeconds) {
        int hours = averageInSeconds / 3600;
        int minutes = (averageInSeconds % 3600) / 60;
        int seconds = averageInSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sms, menu);

        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
//        searchViewItem.setIcon(R.drawable.ic_search_black);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);
//        searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("Search");

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                smsRecyclerViewAdapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                smsRecyclerViewAdapter.getFilter().filter(query);
                return true;
            }
        });
        // return true;
        return super.onCreateOptionsMenu(menu);
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
//                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.app_bar_sms_dump:
                startActivity(new Intent(this, SmsDumpActivity.class));
                return true;
            case R.id.app_bar_bubble:
                addNewBubble();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    @Override
//    public void onBackPressed() {
//        // close search view on back button pressed
//        if (!searchView.isIconified()) {
//            searchView.setIconified(true);
//            return;
//        }
//        super.onBackPressed();
//    }

    private void initializeBubblesManager() {
        bubblesManager = new BubblesManager.Builder(this)
                .setTrashLayout(R.layout.bubble_trash_layout)
                .setInitializationCallback(new OnInitializedCallback() {
                    @Override
                    public void onInitialized() {
                        addNewBubble();
                    }
                })
                .build();
        bubblesManager.initialize();
    }


    //This method is executed to add a new bubble.
    private void addNewBubble() {
        BubbleLayout bubbleView = (BubbleLayout) LayoutInflater.from(SMSActivity.this).inflate(R.layout.bubble_layout, null);
//        bubbleView.setOnBubbleRemoveListener(new BubbleLayout.OnBubbleRemoveListener() {
//            @Override
//            public void onBubbleRemoved(BubbleLayout bubble) { }
//        });
//
//        //The Onclick Listener for the bubble has been set below.
//        bubbleView.setOnBubbleClickListener(new BubbleLayout.OnBubbleClickListener() {
//
//            @Override
//            public void onBubbleClick(BubbleLayout bubble) {
//
//                //Do what you want onClick of bubble.
//                Toast.makeText(getApplicationContext(), "Clicked !",
//                        Toast.LENGTH_SHORT).show();
//            }
//        });
//        bubbleView.setShouldStickToWall(true);
        bubblesManager.addBubble(bubbleView, 60, 20);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(this, READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                ) {
                    return;
                } else {
                    Toast.makeText(SMSActivity.this, telephonyManager.getSimSerialNumber(), Toast.LENGTH_SHORT).show();
                }
                break;
            case PERMISSION_REQUEST_CAMERA:
                // Request for camera permission.
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission has been granted. Start camera preview Activity.
                    Snackbar.make(coordinatorLayout, "Camera_Permission_Granted",
                            Snackbar.LENGTH_SHORT)
                            .show();
//                    startCamera();
                } else {
                    // Permission request was denied.
                    Snackbar.make(coordinatorLayout, "Camera_permission_denied",
                            Snackbar.LENGTH_SHORT)
                            .show();
                }
        }

        // Make sure it's our original READ_CONTACTS request
//        if (requestCode == READ_SMS_PERMISSIONS_REQUEST) {
//            if (grantResults.length == 1 &&
//                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Read SMS permission granted", Toast.LENGTH_SHORT).show();
//                refreshSmsInbox();
//            } else {
//                Toast.makeText(this, "Read SMS permission denied", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
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
                .setTitle("SMS Permission")
                .setMessage("Messenger SMS will now request send/read SMS permission on your device.")
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

    public void retrieveSmsConversations() {
        ContentResolver cr = this.getContentResolver();
        String[] projection = {"DISTINCT THREAD_ID", "_ID", "ADDRESS", "BODY", "DATE",
                "TYPE",
                "READ",
                "SEEN",
        };
        String selection = "THREAD_ID IS NOT NULL) GROUP BY (THREAD_ID";
        Cursor c = cr.query(
                Uri.parse(Utils.CONVERSATION),
                null,
                null,
                null,
                "DATE DESC");

    }

    public void refreshSmsInbox() {
//        Cursor cursorID = getApplication().getContentResolver().query(
//                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                new String[]{ContactsContract.Contacts._ID},
//                null, null, null);
//
//        if (cursorID.moveToFirst()) {
//            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
//        }
//        cursorID.close();
//
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
                "SEEN",
//                "MESSAGE_COUNT",
//                "RECIPIENT_IDS",
//                "SNIPPET"
        };
        String selection = "THREAD_ID IS NOT NULL) GROUP BY (THREAD_ID";

        Cursor c = cr.query(
                Uri.parse(Utils.SMS),
                null,//projection,
                selection,
                null,
                "DATE DESC");

//        try {
        startManagingCursor(c);
        int indexId = c.getColumnIndexOrThrow("_id");
        int indexBody = c.getColumnIndexOrThrow("body");
        int indexAddress = c.getColumnIndexOrThrow("address");
        int indexDate = c.getColumnIndexOrThrow("date");
        int indexSnippet = c.getColumnIndex("snippet");
        int indexThreadId = c.getColumnIndex("thread_id");
        int indexMsgCount = c.getColumnIndex("msg_count");
        int indexSeen = c.getColumnIndex("seen");
        int indexRead = c.getColumnIndexOrThrow("read");
        int count = c.getCount();

//        if (indexBody < 0 || !c.moveToFirst()) return;

        if (c.moveToFirst()) { // must check the result to prevent exception

//                for (int i = 0; i < count; i++) {
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
                Message msgData = new Message();
                msgData.setId(c.getLong(indexId));
                msgData.setThreadId(c.getLong(indexThreadId));
                msgData.setMessage(c.getString(indexBody));

                Contact contact = new Contact();
                String contactNumber = c.getString(indexAddress);
//                if(contactNumber.startsWith("+")) {
//                    // Remove country number prefix. Need to add 00-prefix too.
//                    contactNumber = contactNumber.substring(4);
//                }
                contact.setPhoneNumber(contactNumber);
                String s = Utils.getContactName(getApplicationContext(), c.getString(indexAddress));
                if (s != null) {
                    contact.setName(Utils.getContactName(getApplicationContext(), c.getString(indexAddress)));
                }
                else {
                    contact.setName(contactNumber);
                }
                msgData.setContact(contact);
                msgData.setTime(c.getLong(indexDate));
                msgData.setReadState(c.getInt(indexRead));
                msgData.setSeenState(c.getInt(indexSeen) == 1);
//                if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
//                    msgData.setFolderName("inbox");
//                } else {
//                    msgData.setFolderName("sent");
//                }
                switch (Integer.parseInt(c.getString(c.getColumnIndexOrThrow(Telephony.Sms.TYPE)))) {
                    case Telephony.Sms.MESSAGE_TYPE_INBOX: // coming in from left
                        msgData.setFolderName("inbox");
                        break;
                    case Telephony.Sms.MESSAGE_TYPE_SENT:
                        msgData.setFolderName("sent");
                        break;
                    case Telephony.Sms.MESSAGE_TYPE_OUTBOX:
                        msgData.setFolderName("outbox");
                        break;
                    case Telephony.Sms.MESSAGE_TYPE_DRAFT:
                        msgData.setFolderName("draft");
                        break;
                    case Telephony.Sms.MESSAGE_TYPE_FAILED:
                        msgData.setFolderName("failed");
                        break;
                    case Telephony.Sms.MESSAGE_TYPE_QUEUED:
                        msgData.setFolderName("queued");
                        break;
                    default:
                        break;
                }

                smsList.add(msgData);

            } while (c.moveToNext());
        } else {
//            throw new RuntimeException("You have no SMS");
//            textView.setVisibility(View.INVISIBLE);
//            textView.setText("No SMS to display");
            Snackbar.make(coordinatorLayout, "No SMS to display", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
//            // empty box, no SMS
        }
//    } finally {
//        c.close();
//    }

//        c.close();
    }

    public void updateList(final Message smsMessage, int direction) {
        smsList.add(smsMessage);
        smsRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(View itemView, int position) {
        // Values are passing to activity & to fragment as well
        Message sms = smsList.get(position);
        ImageView picture = itemView.findViewById(R.id.contact_image);
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
        i.putExtra("threadId", sms.getThreadId());
        startActivity(i);
    }

    @Override
    public void onSelectedSms(Message sms) {
//        Toast.makeText(getApplicationContext(), "Selected: " + sms.getContact().getPhoneNumber() + ", " + sms.getMessage()(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLongClick(View view, int position) {
        Message sms = smsList.get(position);
        Toast.makeText(SMSActivity.this, "Long press on position :"+sms.getMessage(),
                Toast.LENGTH_LONG).show();

        Toast.makeText(getApplicationContext(), sms.getMessage(), Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(), sms.getContact().getPhoneNumber(), Toast.LENGTH_LONG).show();

//        deleteSms(sms.getId());

    }

    public boolean isDefaultSmsApp() {
        return Telephony.Sms.getDefaultSmsPackage(this).equals(this.getPackageName());
    }

    public void setAppAsDefault() {
        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName());
        startActivityForResult(intent, 1);
    }

    public boolean isSimExists() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        int SIM_STATE = telephonyManager.getSimState();

        if (SIM_STATE == TelephonyManager.SIM_STATE_READY) {
            return true;
        } else {
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

    private String getPhoneNumber() {
        TelephonyManager phoneMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        if (ActivityCompat.checkSelfPermission(this, "wantPermission") != PackageManager.PERMISSION_GRANTED) {
//            return "";
//        }
//        return phoneMgr.getLine1Number();
        return phoneMgr.getDeviceId();
    }

    private boolean deleteConversation(String thread_id) {
        String id = "";
        boolean isSmsDeleted = false;
        try {
            getContentResolver().delete(Uri.parse("content://sms/conversations/" + thread_id), "_id = ?", new String[]{id});
            isSmsDeleted = true;
        } catch (Exception e) {
            isSmsDeleted = false;
        }
        return isSmsDeleted;
    }

    /** Returns a list of contacts with whom the user has had a conversation. */
    public static ArrayList<String> getContacts(Context context) {
        ArrayList<String> lst = new ArrayList<String>();
        Cursor cursor = context.getContentResolver().query(
                Uri.parse("content://sms/inbox"),
                new String[] {"address"},
                null,
                null,
                "date DESC");
        cursor.moveToFirst();
        do {
            String address = cursor.getString(0);
            if (!lst.contains(address)) lst.add(address);
        } while (cursor.moveToNext());
        return lst;
    }

    private void scheduleNotification (Notification notification , int delay) {
        Intent notificationIntent = new Intent( this, SmsReceiver.class ) ;
        notificationIntent.putExtra(NOTIFICATION_ID , 1 ) ;
        notificationIntent.putExtra(NOTIFICATION , notification) ;
        PendingIntent pendingIntent = PendingIntent. getBroadcast ( this, 0 , notificationIntent , PendingIntent. FLAG_UPDATE_CURRENT ) ;
        long futureInMillis = SystemClock. elapsedRealtime () + delay ;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context. ALARM_SERVICE ) ;
        assert alarmManager != null;
        alarmManager.set(AlarmManager. ELAPSED_REALTIME_WAKEUP , futureInMillis , pendingIntent) ;
    }

    private Notification getNotification (String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder( this, default_notification_channel_id ) ;
        builder.setContentTitle( "Scheduled Notification" ) ;
        builder.setContentText(content) ;
        builder.setSmallIcon(R.drawable. ic_launcher_foreground ) ;
        builder.setAutoCancel( true ) ;
        builder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
        return builder.build() ;
    }

    private SwipeToPerformActionCallback SwipeToDeleteCallback() {
        return new SwipeToPerformActionCallback(getApplicationContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                switch (direction) {
                    case ItemTouchHelper.LEFT:
                        smsRecyclerViewAdapter.deleteItem(position);
//                        Toast.makeText(getApplicationContext(), "Swiping left is working", Toast.LENGTH_SHORT).show();
//                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
//                        snackbar.setAction("UNDO", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
////                                smsRecyclerViewAdapter.deleteItem(item, position);
//                                smsRecyclerViewAdapter.
//                                smsRecyclerView.scrollToPosition(position);
//                            }
//                        });
//                        snackbar.setActionTextColor(Color.YELLOW);
//                        snackbar.show();
                        break;
                    case ItemTouchHelper.RIGHT:
                        smsRecyclerViewAdapter.callContact(position);
                        Toast.makeText(getApplicationContext(), "Swiping right is working", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + direction);
                }

            }
        };
    }
}
