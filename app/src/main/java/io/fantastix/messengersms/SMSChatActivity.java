package io.fantastix.messengersms;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Person;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.telephony.SmsMessage;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
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
import io.fantastix.messengersms.model.Contact;
import io.fantastix.messengersms.model.Message;
import io.fantastix.messengersms.receivers.DeliveredReceiver;
import io.fantastix.messengersms.receivers.SentReceiver;
import io.fantastix.messengersms.receivers.SmsReceiver;

public class SMSChatActivity extends AppCompatActivity implements SmsReceiver.SmsListener {
    private static final int PERMISSION_REQUEST_SEND_SMS = 0;
    private static final int PERMISSION_REQUEST_PHONE_STATE = 999;
    private static final String TAG = "PTP_ChatFrag";
    private final int MAX_SMS_MESSAGE_LENGTH = 160;
    private TextInputEditText smsTextField;
    //    private EditText phoneNumber;
    private ImageButton sendBtn;
    private ImageButton micBtn;
    private ImageButton simBtn;
    private String phoneNo;
    private long threadId;
    private String message;
    private String scAddress;
    private List<Message> messageList = null;
    private SmsChatRecyclerViewAdapter mAdapter = null;
    private RecyclerView conversationView = null;
    private SmsReceiver smsReceiver;
    private DeliveredReceiver smsDeliveredReceiver;
    private SentReceiver smsSentReceiver;
    private TelephonyManager tManager;
    //    private tManagerPlus tManagerPlus;
    private SmsManager smsManager;
    private SubscriptionManager localSubscriptionManager;
    private List<SubscriptionInfo> subscriptionInfoList;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_chat);

        tManager = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
//        tManagerPlus = TelephonyManagerPlus.getInstance(this);
//        String imei1 = tManagerPlus.getImei1();
//        String imei2 = tManagerPlus.getImei2();
//        String simOperatorCode1 = tManagerPlus.getSimOperatorCode1();
//        String simOperatorCode2 = tManagerPlus.getSimOperatorCode2();
//        String simOperatorName1 = tManagerPlus.getSimOperatorName1();
//        String simOperatorName2 = tManagerPlus.getSimOperatorName2();
//        String simSerialNumber1 = tManagerPlus.getSimSerialNumber1();
//        String simSerialNumber2 = tManagerPlus.getSimSerialNumber2();
//        String subscriberId1 = tManagerPlus.getSubscriberId1();
//        String subscriberId2 = tManagerPlus.getSubscriberId2();
//        int mcc1 = tManagerPlus.getMcc1();
//        int mcc2 = tManagerPlus.getMcc2();
//        int mnc1 = tManagerPlus.getMnc1();
//        int mnc2 = tManagerPlus.getMnc2();
//        int cid1 = tManagerPlus.getCid1();
//        int cid2 = tManagerPlus.getCid2();
//        int lac1 = tManagerPlus.getLac1();
//        int lac2 = tManagerPlus.getLac2();
        localSubscriptionManager = SubscriptionManager.from(this);
        getSubscriptionList();
        subscriptionSelector();

        Intent intent = getIntent();
        String contactName = intent.getStringExtra("name");
        phoneNo = intent.getExtras().getString("phoneNumber");
        threadId = intent.getExtras().getLong("threadId");
        scAddress = intent.getExtras().getString("scAddress");

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        TextView mSubtitle = toolbar.findViewById(R.id.toolbar_subtitle);
        if (contactName != null) {
            mTitle.setText(contactName.split(" ")[0]);
        } else {
            mTitle.setText(phoneNo);
        }
        mSubtitle.setText(phoneNo);
//        toolbar.setTitle(contactName.isEmpty() ? phoneNo : contactName.split(" ")[0]);
//        toolbar.setTitle("Chats");
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
        conversationView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setItemPrefetchEnabled(true);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        conversationView.setLayoutManager(mLayoutManager);
        conversationView.setItemAnimator(new DefaultItemAnimator());
        // Make listView to scroll down automatically
//        conversationView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        messageList = new ArrayList<>();

        mAdapter = new SmsChatRecyclerViewAdapter(this, messageList);
        conversationView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        conversationView.scrollToPosition(mAdapter.getItemCount() - 1);

        messageList.clear();
        getSMSMessages();

        int SCROLL_DIRECTION_UP = -1;
        if (conversationView.canScrollVertically(SCROLL_DIRECTION_UP)) {
            toolbar.setElevation(0f);
        } else {
            toolbar.setElevation(50f);
        }

//        smsTextField.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s != null && s.length() > 0) {
//                    sendBtn.setVisibility(View.VISIBLE);
//                    micBtn.setVisibility(View.GONE);
//                }
//                else {
//                    sendBtn.setVisibility(View.GONE);
//                    micBtn.setVisibility(View.VISIBLE);
//                }
//            }
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });

        // When entering a message, clear the field and broadcast the message
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = smsTextField.getText().toString();
                if (message.startsWith("*") && message.endsWith("#")) {
                    // remove # at the end of the message
                    message = message.substring(0, message.length() - 1);
                    String temp = phoneNo;
                    phoneNo = "*127*" + temp + message + Uri.encode("#");
                    makeAudioCall();
                    phoneNo = temp;
                } else {
                    sendSMSMessage(message, phoneNo);
                    Message msg = new Message();
                    msg.setAddress(phoneNo);
                    msg.setType(Telephony.Sms.Sent.MESSAGE_TYPE_SENT);
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
                }
            }
        });

        Toast.makeText(this, "phone number: " + tManager.getLine1Number(), Toast.LENGTH_SHORT).show();

        if (isDualSim() && localSubscriptionManager.getActiveSubscriptionInfoCount() > 1) {
            // if there are two sims in dual sim mobile
            List localList = localSubscriptionManager.getActiveSubscriptionInfoList();
            int slot = 0;
            SubscriptionInfo simInfo = (SubscriptionInfo) localList.get(slot);
            simBtn.setVisibility(View.VISIBLE);

//            smsManager = SmsManager.getSmsManagerForSubscriptionId(localSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slot).getSubscriptionId());
//            SubscriptionInfo simInfo = localSubscriptionManager.getActiveSubscriptionInfo(simInfo1.getSubscriptionId());
//            int slot = simInfo.getSimSlotIndex();
            int[] sims = { R.drawable.ic_sim_1, R.drawable.ic_sim_2 };
            simBtn.setImageResource(sims[slot]);
//            simBtn.setImageBitmap(simInfo.createIconBitmap(this));
            // toggle default sim
            simBtn.setOnClickListener(v -> {
//                localSubscriptionManager.g
//                List<SubscriptionInfo> subscription = SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoList();
//                for (int i = 0; i < subscription.size(); i++) {
//                    SubscriptionInfo info = subscription.get(i);
//                    Log.d(TAG, "number " + info.getNumber());
//                    Log.d(TAG, "network name : " + info.getCarrierName());
//                    Log.d(TAG, "country iso " + info.getCountryIso());
//                    Log.d(TAG, "country iso " + info.getDisplayName());
//                    Log.d(TAG, "country iso " + info.getSubscriptionId());
//                    Log.d(TAG, "country iso " + info.getSimSlotIndex());
//                }
                if (slot == 1) {
                    int sub_id = localSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(simInfo.getSimSlotIndex()).getSubscriptionId();
                    smsManager = SmsManager.getSmsManagerForSubscriptionId(sub_id);
                    simBtn.setImageBitmap(simInfo.createIconBitmap(this));
                }
                else {
//                    int sub_id = localSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(SubscriptionManager.getSlotIndex(simInfo.getSubscriptionId()).getSubscriptionId());
                    int sub_id = localSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(simInfo.getSimSlotIndex()).getSubscriptionId();
                    smsManager = SmsManager.getSmsManagerForSubscriptionId(sub_id);
                    simBtn.setImageBitmap(simInfo.createIconBitmap(this));
                }
            });
        }
        else {
            smsManager = SmsManager.getDefault();
        }

//        if (!Utils.isValidPhoneNumber(phoneNo)) {
//            findViewById(R.id.invalidSender).setVisibility(View.VISIBLE);
//            findViewById(R.id.validSender).setVisibility(View.GONE);
//        }
//        else {
//            findViewById(R.id.invalidSender).setVisibility(View.GONE);
//            findViewById(R.id.validSender).setVisibility(View.VISIBLE);
//        }

        ImageButton bottomMenu = findViewById(R.id.bottom_menu);
//        bottomMenu.setEnabled(SmsHelper.isValidPhoneNumber(phoneNo));
        bottomMenu.setOnClickListener(v -> {
//                if (!Utils.isValidPhoneNumber(phoneNo)) {
//                    Toast.makeText(getApplicationContext(), "Invalid phone number", Toast.LENGTH_SHORT).show();
//                } else {
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
//                }
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

        Notification.Builder builder = new Notification.Builder(this)
//                        .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.icon_144)
                .setBubbleMetadata(bubbleData)
                .addPerson(chatPartner);
        builder.build();

//        TedImagePicker.with(this)
//        .start(new OnSelectedListener() {
//            @Override
//            public void onSelected(@NotNull Uri uri) {
//                showSingleImage(uri);
//            }
//        });
//
//        TedImagePicker.with(this)
//        .start(uri -> {
//            showSingleImage(uri);
//        });
//        TedImagePicker.with(this)
//        .startMultiImage(new OnMultiSelectedListener() {
//            @Override
//            public void onSelected(@NotNull List<? extends Uri> uriList) {
//                showMultiImage(uriList);
//            }
//        });
//        TedImagePicker.with(this)
//        .startMultiImage(uriList -> {
//            showMultiImage(uriList);
//        });
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        unregisterReceiver(smsSentReceiver);
//        unregisterReceiver(smsDeliveredReceiver);
//        unregisterReceiver(smsReceiver);
//    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        registerReceiver(smsReceiver, new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));
//        registerReceiver(smsDeliveredReceiver, new IntentFilter("SMS_DELIVERED"));
//        registerReceiver(smsSentReceiver, new IntentFilter("SMS_SENT"));
////        messageList.clear();
////        getSMSMessages();
//        mAdapter.notifyDataSetChanged();
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsSentReceiver);
        unregisterReceiver(smsDeliveredReceiver);
        unregisterReceiver(smsReceiver);
        messageList.clear();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /*
    // group messages
    2020
        Tue, 13 Mar 2020
    2021
        Mon, 16 Jun 2021
            Mon 16
                8:30 am
                    now
     */

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

    private void getSubscriptionList() {
        SubscriptionManager subscriptionManager = SubscriptionManager.from(getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSION_REQUEST_PHONE_STATE);
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
    }

    private void subscriptionSelector() {
        if (subscriptionInfoList != null && subscriptionInfoList.size() > 0) {
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(SMSChatActivity.this);
            builderSingle.setTitle("Select One");

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SMSChatActivity.this,
                    android.R.layout.select_dialog_item);

            for (SubscriptionInfo subscriptionInfo : subscriptionInfoList) {
                arrayAdapter.add(subscriptionInfo.getCarrierName().toString());
            }
            builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    String strName = arrayAdapter.getItem(which);
//                    Log.d("=====", "onClick: " + strName);
                    int subscriptionId = subscriptionInfoList.get(which).getSubscriptionId();
                    sendSMS(subscriptionId);
                }
            });
            builderSingle.show();
        }
    }

    private void sendSMS(int subscriptionId) {
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED"), 0);
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0);

//        SmsManager smsManager = SmsManager.getDefault();
        SmsManager smsManager = SmsManager.getSmsManagerForSubscriptionId(subscriptionId);
//        smsManager.getSmsManagerForSubscriptionId(subscriptionId).sendTextMessage(smsContents.getNumber(), null, smsContents.getText(), sentPI, deliverPI);
        smsManager.sendTextMessage("675", null, "s", sentPI, deliveredPI);
    }

    protected void getSMSMessages() {
        String[] columns = {"type", "body", "address", "date", "_id", "thread_id"};
        String where = "address=?";
        String[] whereArgs = new String[]{phoneNo};

        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(
                Uri.parse("content://sms/conversations/" + threadId),
                null, //columns,
                null, //where,
                null, //whereArgs,
                "date asc");

        try {
            startManagingCursor(smsInboxCursor);
            int indexBody = smsInboxCursor.getColumnIndex("body");
            int indexAddress = smsInboxCursor.getColumnIndex("address");
            int indexDate = smsInboxCursor.getColumnIndex("date");
            int indexType = smsInboxCursor.getColumnIndex("type");
            int count = smsInboxCursor.getCount();

            if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;

            if (smsInboxCursor.moveToFirst()) {
                do {
                    Message msgData = new Message();
                    msgData.setMessage(smsInboxCursor.getString(indexBody));
                    Contact c = new Contact();
                    c.setName("");
                    msgData.setAddress(smsInboxCursor.getString(indexAddress));
                    msgData.setTime(smsInboxCursor.getLong(indexDate));
                    msgData.setType(smsInboxCursor.getInt(indexType));

                    messageList.add(msgData);
//                String str = "";
//                str +=
//                    Toast.makeText(SmsChatRoomActivity.this, "From: " + msgData.toString(), Toast.LENGTH_SHORT).show();

                } while (smsInboxCursor.moveToNext());
            } else {
                Snackbar.make(getCurrentFocus(), "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
        catch (IllegalArgumentException e) {
            Log.e("SMSChatActivity", e.getMessage());
        } finally {
            try {
                if (!smsInboxCursor.isClosed()) {
                    smsInboxCursor.close();
                }
                smsInboxCursor = null;
            } catch (Exception e) {
                Log.e("While closing cursor", e.getMessage());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected void sendSMSMessage(String message, String phone) {
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED"), 0);
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0);

        // above Android API 22
        if (Build.VERSION.SDK_INT > 22) {
            // for dual sim mobile
            localSubscriptionManager = SubscriptionManager.from(this);

//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
//                    ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_PHONE_NUMBERS, Manifest.permission.READ_PHONE_STATE}, MY_PERMISSION_REQUEST_SEND_SMS);
////                getPermissionToReadSMS();
//
//                Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            else {
//                SmsManager smsManager = SmsManager.getDefault();
//                smsManager.sendTextMessage(phone, scAddress, message, null, null);
//                Toast.makeText(this, "Message sent!", Toast.LENGTH_SHORT).show();
//            }

            if (localSubscriptionManager.getActiveSubscriptionInfoCount() > 1) {
                //if there are two sims in dual sim mobile
                List localList = localSubscriptionManager.getActiveSubscriptionInfoList();
                SubscriptionInfo simInfo = (SubscriptionInfo) localList.get(0);
                SubscriptionInfo simInfo1 = (SubscriptionInfo) localList.get(1);

                final String sim1 = simInfo.getDisplayName().toString();
                final String sim2 = simInfo1.getDisplayName().toString();

                Toast.makeText(this, "sim1: " + sim1 + "\n sim2:" + sim2, Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "phone number" + simInfo.getNumber(), Toast.LENGTH_SHORT).show();

                // SendSMS From SIM one
//                SmsManager.getSmsManagerForSubscriptionId(simInfo1.getSubscriptionId()).sendTextMessage(customer.getMobile(), null, smsText, sentPI, deliveredPI);

                // SendSMS From SIM Two
//                SmsManager.getSmsManagerForSubscriptionId(simInfo2.getSubscriptionId()).sendTextMessage(customer.getMobile(), null, smsText, sentPI, deliveredPI);

            } else {
                // if there is 1 sim in dual sim mobile
                TelephonyManager tManager = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
                String sim1 = tManager.getNetworkOperatorName();
                Toast.makeText(this, "sim1: " + sim1, Toast.LENGTH_SHORT).show();
            }

        } else {
            // below android API 22
            tManager = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            String sim1 = tManager.getNetworkOperatorName();
            Toast.makeText(this, "sim1: " + sim1, Toast.LENGTH_SHORT).show();
        }

        if (tManager.getPhoneCount() == 2) {
            // Dual sim
        }

//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
//            } else {
//                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.SEND_SMS}, MY_PERMISSION_REQUEST_SEND_SMS);
//            }
//        }
//        else {
//              SubscriptionManager subscriptionManager = (SubscriptionManager) getApplicationContext().getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        SubscriptionManager subscriptionManager = SubscriptionManager.from(this);
//            List subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
        SubscriptionInfo info = subscriptionManager.getActiveSubscriptionInfo(SubscriptionManager.getDefaultSmsSubscriptionId());
//            new SubscriptionInfo().getSubscriptionId()
//            subscriptionManager.getSubscriptionIds(info.getSimSlotIndex())[0];
//           int sub_id = info.getSubscriptionId();
//           Toast.makeText(this, "#" + sub_id, Toast.LENGTH_SHORT).show();
//            int sub_id = subscriptionInfoList.get(0).subscriptionId

        SmsManager smsManager;
        int sub_id = SmsManager.getDefaultSmsSubscriptionId();
//            if (sub_id == -1) {
        smsManager = SmsManager.getDefault();
//            }
//            else {
        smsManager = SmsManager.getSmsManagerForSubscriptionId(sub_id);
//            }
//            String[] numbers={ "8527801400","8307489274"} //add more
//            for (int i = 0; i < numbers.length; i++) {
//                smsManager.sendMultipartTextMessage(numbers[i], null, parts, null, null);
//            }
//            JSONObject obj = new JSONObject();
//            obj.put("address", msgs[0].getOriginatingAddress());
//            obj.put("date", (new Date()).getTime());
//            obj.put("date_sent", msgs[0].getTimestampMillis());
//            obj.put("read", (msgs[0].getStatusOnIcc() == SmsManager.STATUS_ON_ICC_READ) ? 1 : 0);
//            obj.put("thread_id", TelephonyCompat.getOrCreateThreadId(context, msgs[0].getOriginatingAddress()));
//
//            if (message.length() > MAX_SMS_MESSAGE_LENGTH) {
//                ArrayList<String> parts = sms.divideMessage(message);
//                smsManager.sendMultipartTextMessage(phoneNo, null, parts, null, null);
//        List<String> ms = smsManager.divideMessage(message);

//           SubscriptionManager subscriptionManager = SubscriptionManager.from(getApplicationContext());
//           List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
//           for (SubscriptionInfo subscriptionInfo : subscriptionInfoList) {
//               int subscriptionId = subscriptionInfo.getSubscriptionId();
//               Log.d("apipas", "subscriptionId:" + subscriptionId);
//           }
//
        smsManager.sendTextMessage(phoneNo, null, message, sentPI, deliveredPI);

//        for (String str : ms) {
//            smsManager.sendTextMessage(phoneNo, null, str, sentIntent, null);
//        }

//            }
//            else {
//                sms.sendTextMessage(phoneNo, null, message, sentApi, deliveredApi);
//            }
//        }

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

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isDualSim() {
        return tManager.getPhoneCount() == 2;
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
            case PERMISSION_REQUEST_SEND_SMS: {
                //check grant result is greater than 0 and equal to PERMISSION_GRANTED
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Thanks for permitting ", Toast.LENGTH_SHORT).show();
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        sendBtn.setEnabled(true);
                    }
                    break;
                } else {
                    Toast.makeText(getApplicationContext(), "SMS failed, please try again", Toast.LENGTH_LONG).show();
                    Toast.makeText(this, "Why you denied! LOL", Toast.LENGTH_SHORT).show();
                    sendBtn.setEnabled(false);
                }
            }
            case PERMISSION_REQUEST_PHONE_STATE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getSubscriptionList();
                    Toast.makeText(this, "Thanks for permitting ", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Why you denied! LOL", Toast.LENGTH_SHORT).show();
                }
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
    public void onMessageReceived(Message sms) {
        Message message = new Message();
        message.setMessage(sms.getMessage());
        message.setTime(sms.getTime());
        message.setContact(sms.getContact());
        message.setReadState(sms.getReadState());
        messageList.add(message);

        Toast.makeText(SMSChatActivity.this, "Message received: " + message.getMessage(), Toast.LENGTH_SHORT).show();
//        updateList(message, 1);
        mAdapter.notifyDataSetChanged();
    }
}