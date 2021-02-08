package io.fantastix.messengersms.contacts;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;

import io.fantastix.messengersms.R;
import io.fantastix.messengersms.chats.SmsChatRoomActivity;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView lv_contacts;
    List<Contact> contacts;
    ArrayAdapter<Contact> adapter;
    Cursor c;
    private final static int READ_CONTACTS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lv_contacts = (ListView) findViewById(R.id.listview);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            readContacts();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.READ_CONTACTS }, READ_CONTACTS);
        }

        adapter = new ArrayAdapter<Contact>(this, android.R.layout.simple_list_item_1, contacts);
        lv_contacts.setAdapter(adapter);
        lv_contacts.setOnItemClickListener(this::onItemClick);
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

    private void readContacts() {
        contacts = new ArrayList<>();

        c = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                new String[] {ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts._ID, ContactsContract.Contacts.LOOKUP_KEY},
                null,
                null,
                null,
                null //ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ACS"
        );

//        startManagingCursor(c);

        if (c == null) {
            Toast.makeText(ContactsActivity.this, "No contacts", Toast.LENGTH_SHORT).show();
        }

        try {
            while (c.moveToNext()) {
                Contact contact = new Contact();
                contact.setName(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                contact.setPhoneNumber(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                contacts.add(contact);
            }
        } finally {
            c.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_contacts, menu);
        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                if (contacts.contains(query)) {
                    adapter.getFilter().filter(query);
                } else {
                    Toast.makeText(ContactsActivity.this, "No match found", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_controller_view_tag:
                startActivity(new Intent(getApplicationContext(), ContactsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            Contact contact = contacts.get(position);
            String name = contact.getName();
            String phone = contact.getPhoneNumber();
//            Toast.makeText(ContactsActivity.this, contact.toString(), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(ContactsActivity.this, SmsChatRoomActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("address", phone);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}