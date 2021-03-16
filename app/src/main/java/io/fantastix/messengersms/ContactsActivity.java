package io.fantastix.messengersms;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;

import io.fantastix.messengersms.adapter.ContactsListViewAdapter;
import io.fantastix.messengersms.model.Contact;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ContactsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = ContactsActivity.class.getSimpleName();
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private final static int READ_CONTACTS = 1;
    private Uri uriContact = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    private String contactID;     // contacts unique ID

    private EditText input_search;
    private ListView lv_contacts;
    private List<Contact> contacts;
    private Set<Contact> contactSet;
    private ContactsListViewAdapter adapter;
    private Cursor c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        Toolbar toolbar = findViewById(R.id.toolbar);
//        toolbar.setTitle("Chats");
        setSupportActionBar(toolbar);
//        toolbar.setNavigationIcon(R.drawable.ic_arrow_left);
//        toolbar.setNavigationOnClickListener(v -> finish());

//        input_search = (EditText) findViewById(R.id.inputSearch);
        lv_contacts = findViewById(R.id.listview);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS);
        }
        readContacts();
//        retrieveContactNumbers();

//        contactSet = new Contact();
        adapter = new ContactsListViewAdapter(this, contacts);
        lv_contacts.setAdapter(adapter);
        lv_contacts.setOnItemClickListener(this::onItemClick);

//        input_search.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                // When user changed the Text
////                ContactsActivity.this.adapter.getFilter().filter(s);
//                Toast.makeText(ContactsActivity.this, s, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK) {
//            Log.d(TAG, "Response: " + data.toString());
//            uriContact = data.getData();
//
//            retrieveContactName();
//            retrieveContactNumber();
//            retrieveContactPhoto();
//
//        }
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readContacts();
            } else {
                Toast.makeText(this, "Permission Required", Toast.LENGTH_SHORT).show();
            }
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(final Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_contacts, menu);
//        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
//        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                searchView.clearFocus();
//                if (contacts.contains(query)) {
//                    adapter.getFilter().filter(query);
//                } else {
//                    Toast.makeText(ContactsActivity.this, "No match found", Toast.LENGTH_SHORT).show();
//                }
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                adapter.getFilter().filter(newText);
//                return false;
//            }
//        });
//        return super.onCreateOptionsMenu(menu);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.nav_controller_view_tag:
//                startActivity(new Intent(getApplicationContext(), ContactsActivity.class));
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            Contact contact = contacts.get(position);
            String name = contact.getName();
            String phone = contact.getPhoneNumber();

            Intent intent = new Intent(ContactsActivity.this, SMSChatActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("phoneNumber", phone);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readContacts() {
        contacts = new ArrayList<>();
//        String DISPLAY_NAME = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
//                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY : ContactsContract.Contacts.DISPLAY_NAME;
//
//        String[] PROJECTTION = new String[]{
//                DISPLAY_NAME,
//                ContactsContract.Contacts._ID,
//                ContactsContract.Contacts.LOOKUP_KEY,
//                ContactsContract.Contacts.HAS_PHONE_NUMBER
//        };
//        String FILTER = DISPLAY_NAME + " NOT LIKE '%@%'";
//        String ORDER = String.format("%1$s COLLATE NOCASE", DISPLAY_NAME);

        c = getContentResolver().query(
                uriContact,
                null, // PROJECTTION,
                null, // FILTER,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        );

        startManagingCursor(c);

        if (c == null) {
            Toast.makeText(ContactsActivity.this, "No contacts", Toast.LENGTH_SHORT).show();
        }

        try {
            while (c.moveToNext()) {
                Contact contact = new Contact();
                contact.setName(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                contact.setPhoneNumber(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                contact.setId(Long.parseLong(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID))));
                contact.setPhotoUri(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)));
                contacts.add(contact);
            }
        } finally {
            c.close();
        }
    }

    private void retrieveContactPhoto() {
        Bitmap photo = null;

        try {
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contactID)));

            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);
                ImageView imageView = findViewById(R.id.contact_image);
                imageView.setImageBitmap(photo);
            }

            assert inputStream != null;
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void retrieveContactNumber() {
        String contactNumber = null;

        // getting contacts ID
        Cursor cursorID = getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        if (cursorID.moveToFirst()) {
            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }

        cursorID.close();

        Log.d(TAG, "Contact ID: " + contactID);

        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                new String[]{contactID},
                null);

        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }

        cursorPhone.close();

        Log.d(TAG, "Contact Phone Number: " + contactNumber);
    }

    private void retrieveContactNumbers() {
        contacts = new ArrayList<>();
        String contactNumber = null;
        // getting contacts ID
        Cursor cursorID = getApplication().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        if (cursorID.moveToFirst()) {
            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }
        cursorID.close();

        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getApplication().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_HOME +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE +
                        ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK +
                        ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME +
                        ContactsContract.CommonDataKinds.Phone.TYPE_PAGER +
                        ContactsContract.CommonDataKinds.Phone.TYPE_OTHER +
                        ContactsContract.CommonDataKinds.Phone.TYPE_CALLBACK +
                        ContactsContract.CommonDataKinds.Phone.TYPE_CAR +
                        ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN +
                        ContactsContract.CommonDataKinds.Phone.TYPE_OTHER_FAX +
                        ContactsContract.CommonDataKinds.Phone.TYPE_RADIO +
                        ContactsContract.CommonDataKinds.Phone.TYPE_TELEX +
                        ContactsContract.CommonDataKinds.Phone.TYPE_TTY_TDD +
                        ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE +
                        ContactsContract.CommonDataKinds.Phone.TYPE_WORK_PAGER +
                        ContactsContract.CommonDataKinds.Phone.TYPE_ASSISTANT +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MMS,

                new String[]{contactID},
                null);

        while (cursorPhone.moveToNext()){
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
            Log.d(TAG, "Contact Phone Number: " + contactNumber);

            Contact contact = new Contact();
            contact.setName(cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
            contact.setPhoneNumber(cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            contact.setId(Long.parseLong(cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID))));
//            contact.setPhotoUri(cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)));
            contacts.add(contact);
        }
        cursorPhone.close();
    }

    private void retrieveContactName() {
        String contactName = null;

        // querying contact data store
        Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);

        if (cursor.moveToFirst()) {

            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.

            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }

        cursor.close();

        Log.d(TAG, "Contact Name: " + contactName);

    }

}