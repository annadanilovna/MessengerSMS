package io.fantastix.messengersms;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Person;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SubscriptionManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.fantastix.messengersms.adapter.SmsChatRecyclerViewAdapter;
import io.fantastix.messengersms.model.Message;
import io.fantastix.messengersms.model.Sms;
import io.fantastix.messengersms.receivers.DeliveredReceiver;
import io.fantastix.messengersms.receivers.SentReceiver;
import io.fantastix.messengersms.receivers.SmsReceiver;

public class SMSChatActivity extends AppCompatActivity implements SmsReceiver.Listener {
    private static final int MY_PERMISSION_REQUEST_SEND_SMS = 0;
    private static final String TAG = "PTP_ChatFrag";
    private TextInputEditText smsTextField;
    //    private EditText phoneNumber;
    private ImageButton sendBtn;
    private ImageButton micBtn;
    private ImageButton simBtn;
    private String phoneNo;
    private String message;
    private List<Message> messageList = null;
    private SmsChatRecyclerViewAdapter mAdapter = null;
    private RecyclerView conversationView = null;
    private SmsReceiver smsReceiver;
    private DeliveredReceiver smsDeliveredReceiver;
    private SentReceiver smsSentReceiver;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_chat);

        Intent intent = getIntent();
        phoneNo = intent.getExtras().getString("phoneNumber");
        String contactName = intent.getStringExtra("name");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            requestReadAndSendSmsPermission();
        }

        smsDeliveredReceiver = new DeliveredReceiver();
        smsSentReceiver = new SentReceiver();
        smsReceiver = new SmsReceiver();
        registerReceiver(smsReceiver, new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));
        registerReceiver(smsDeliveredReceiver, new IntentFilter("SMS_DELIVERED"));
        registerReceiver(smsSentReceiver, new IntentFilter("SMS_SENT"));
        smsReceiver.setListener(this);

        smsTextField = findViewById(R.id.smsText);
//        phoneNumber = findViewById(R.id.phoneNumber);
        sendBtn = findViewById(R.id.sendBtn);
        micBtn = findViewById(R.id.micBtn);
        simBtn = findViewById(R.id.simBtn);

        conversationView = findViewById(R.id.conversation_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setItemPrefetchEnabled(true);//.setReverseLayout(true);
        conversationView.setLayoutManager(mLayoutManager);
        conversationView.setItemAnimator(new DefaultItemAnimator());
        // Make listView to scroll down automatically
//        conversationView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        messageList = new ArrayList<>();

        getSMSMessages();

        mAdapter = new SmsChatRecyclerViewAdapter(this, messageList);
        conversationView.setAdapter(mAdapter);
//        mAdapter.notifyDataSetChanged();
        conversationView.scrollToPosition(mAdapter.getItemCount()-1);

        int SCROLL_DIRECTION_UP = -1;
        if (conversationView.canScrollVertically(SCROLL_DIRECTION_UP)) {
            toolbar.setElevation(0f);
        } else {
            toolbar.setElevation(50f);
        }

        smsTextField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() > 0) {
                    sendBtn.setVisibility(View.VISIBLE);
                    micBtn.setVisibility(View.GONE);
                }
                else {
                    sendBtn.setVisibility(View.GONE);
                    micBtn.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // When entering a message, clear the field and broadcast the message
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = smsTextField.getText().toString();
//                sendSMSMessage(message, phoneNo);
                if (message.startsWith("*") && message.endsWith("#")) {
                    // remove # at the end of the message
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
                msg.setTime(new Date().getTime());
                messageList.add(msg);
                mAdapter.notifyDataSetChanged();
                final int itemCount = mAdapter.getItemCount() - 1;
                // Notify recycler view insert one new data.
                mAdapter.notifyItemInserted(itemCount);
                // Scroll RecyclerView to the last message.
                conversationView.scrollToPosition(itemCount);
                smsTextField.getText().clear();
//                }
            }
        });

        simBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simBtn.setImageResource(R.drawable.ic_sim_1);
            }
        });

        ImageButton bottomMenu = findViewById(R.id.bottom_menu);
//        bottomMenu.setEnabled(SmsHelper.isValidPhoneNumber(phoneNo));
        bottomMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//        if (Utils.isValidPhoneNumber("+67570523228")) {
//            Toast.makeText(this, "MATCH", Toast.LENGTH_LONG).show();
//        } else {
//            Toast.makeText(this, "NO MATCH", Toast.LENGTH_LONG).show();
//        }
                if (!Utils.isValidPhoneNumber(phoneNo)) {
                    Toast.makeText(getApplicationContext(), "Invalid phone number", Toast.LENGTH_SHORT).show();
                } else {
//                ShortCodesListDialogFragment.newInstance().show(getSupportFragmentManager(), "BottomSheet");
                    ShortCodesListDialogFragment shortCodes = new ShortCodesListDialogFragment();
                    shortCodes.setOnItemClickListener(new ShortCodesListDialogFragment.OnItemClickListener() {
                        @Override
                        public void onItemClick(View itemView, int position) {
                            String tempNo = phoneNo.replace("+675", "");
                            String code = shortCodes.digicelShortCodes[position];
                            // remove # at the end of the code
                            code = code.substring(0, code.length() - 1);
                            phoneNo = "*127*" + tempNo + code + Uri.encode("#");
                            makeAudioCall();
                            // *127*70523228*99#
//                        Toast.makeText(getApplicationContext(), phoneNo, Toast.LENGTH_SHORT).show();
                            phoneNo = tempNo;
                        }
                    });
                    shortCodes.show(getSupportFragmentManager(), "BottomSheet");
                }
            }
        });

        // Create bubble intent
        Intent target = new Intent(this, SMSChatActivity.class);
        PendingIntent bubbleIntent = PendingIntent.getActivity(this, 0, target, 0 /* flags */);

        // Create bubble metadata
        Notification.BubbleMetadata bubbleData =
                new Notification.BubbleMetadata.Builder()
                        .setIntent(bubbleIntent)
                        .setIcon(Icon.createWithResource(this, R.drawable.ic_message_icon))
                        .setDesiredHeight(600)
                        .build();

        // Create notification
        Person chatPartner = new Person.Builder()
                .setName("Chat partner")
                .setImportant(true)
                .build();

        Notification.Builder builder =
                new Notification.Builder(this)
//                        .setContentIntent(contentIntent)
                        .setSmallIcon(R.drawable.icon_144)
                        .setBubbleMetadata(bubbleData)
                        .addPerson(chatPartner);
        builder.build();

    }

    @Override
    protected void onResume() {
        super.onResume();
//        getSMSMessages();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsSentReceiver);
        unregisterReceiver(smsDeliveredReceiver);
        unregisterReceiver(smsReceiver);
    }

    //    private HashMap<String, List<Message>> groupDataIntoHashMap(List<Message> messageList) {
//
//        HashMap<String, List<Message>> groupedHashMap = new HashMap<>();
//
//        for(Message msg : messageList) {
//
//            String hashMapKey = messageList.getKey4Value();
//
//            if(groupedHashMap.containsKey(hashMapKey)) {
//                // The key is already in the HashMap; add the pojo object
//                // against the existing key.
//                groupedHashMap.get(hashMapKey).add(msg);
//            } else {
//                // The key is not there in the HashMap; create a new key-value pair
//                List<Message> list = new ArrayList<>();
//                list.add(msg);
//                groupedHashMap.put(hashMapKey, list);
//            }
//        }
//
//        return groupedHashMap;
//    }

    protected void getSMSMessages() {
        String[] columns = {"type", "body", "address", "date", "_id", "thread_id"};
        String where = "address=?";
        String[] whereArgs = new String[] {phoneNo};

        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(
                Uri.parse("content://sms"),
                columns,
                where,
                whereArgs,
                "date asc");

        try {
            startManagingCursor(smsInboxCursor);
            int indexBody = smsInboxCursor.getColumnIndex("body");
            int indexAddress = smsInboxCursor.getColumnIndex("address");
            int indexDate = smsInboxCursor.getColumnIndex("date");
            int count = smsInboxCursor.getCount();

            if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;

            if (smsInboxCursor.moveToFirst()) {
                do {
                    Message msgData = new Message();
                    msgData.setMessage(smsInboxCursor.getString(indexBody));
                    msgData.setAddress(smsInboxCursor.getString(indexAddress));
                    msgData.setTime(smsInboxCursor.getLong(indexDate));

                    messageList.add(msgData);
//                String str = "";
//                str +=
//                    Toast.makeText(SmsChatRoomActivity.this, "From: " + msgData.toString(), Toast.LENGTH_SHORT).show();

                } while (smsInboxCursor.moveToNext());
            } else {
                Snackbar.make(getCurrentFocus(), "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        } finally {
            smsInboxCursor.close();
        }
    }

    protected void sendSMSMessage(String message, String phone) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.SEND_SMS}, MY_PERMISSION_REQUEST_SEND_SMS);
            }
        }
        else {
            PendingIntent deliveredApi = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED"), 0);
            PendingIntent sentApi = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0);
            SubscriptionManager subscriptionManager = (SubscriptionManager) getApplicationContext().getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            List subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
            //https://stackoverflow.com/questions/27351936/how-to-send-a-sms-using-smsmanager-in-dual-sim-mobile
            SmsManager sms = SmsManager.getDefault();
//            SmsManager sms = SmsManager.getSmsManagerForSubscriptionId(subscriptionInfoList.get(0).subscriptionId)
            sms.sendTextMessage(phoneNo, null, message, sentApi, deliveredApi);
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

        MenuItem callItem = menu.findItem(R.id.app_bar_call);
        callItem.setEnabled(Utils.isValidPhoneNumber(phoneNo));
        MenuItem videoCallItem = menu.findItem(R.id.app_bar_video_call);
        videoCallItem.setEnabled(Utils.isValidPhoneNumber(phoneNo));

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

    private void makeAudioCall() {
        Intent intentCall = new Intent(Intent.ACTION_CALL);
        Uri uri = Uri.parse("tel:"+phoneNo);
        intentCall.setData(uri);
        Toast.makeText(getApplicationContext(), "made request", Toast.LENGTH_LONG).show();
        if (isCallPermissionGranted()) {
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
        if (isCallPermissionGranted()) {
            Toast.makeText(getApplicationContext(), "Please grant permission", Toast.LENGTH_LONG).show();
            requestPermission();
        }
        else {
            startActivity(intentCall);
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

    public boolean isCallPermissionGranted() {
//        return ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED;
        return ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE);
    }

    /**
     * Check if SMS permission is granted
     */
    public boolean isSmsPermissionGranted() {
        return ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS);
    }

    /**
     *  Request runtime SMS permission
     */
    private void requestReadAndSendSmsPermission() {
        int SMS_PERMISSION_CODE = 1;
        if (isSmsPermissionGranted()) {
            Log.d(TAG, "shouldShowRequestPermissionRationale(), no permission requested");
            return;
        }
        ActivityCompat.requestPermissions(this, new String[] {
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.SEND_SMS
        }, SMS_PERMISSION_CODE);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(SMSChatActivity.this, new String[] {Manifest.permission.CALL_PHONE}, 1);
    }

    @Override
    public void onNewMessageReceived(Sms sms) {
        Message message = new Message();
        message.setMessage(sms.getMsg());
        message.setTime(sms.getTime());
        message.setContact(sms.getContact());
        message.setReadState(String.valueOf(sms.getReadState()));
        messageList.add(message);

        Toast.makeText(SMSChatActivity.this, "Message received: " + message.getMessage(), Toast.LENGTH_SHORT).show();
//        updateList(message, 1);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTextReceived(String text) {
        Toast.makeText(SMSChatActivity.this, "Message received: " + text, Toast.LENGTH_SHORT).show();
//                updateList(message, 1);
        mAdapter.notifyDataSetChanged();
    }
}