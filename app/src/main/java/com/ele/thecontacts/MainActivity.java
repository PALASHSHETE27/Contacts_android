
package com.ele.thecontacts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ContactAdapter adapter;
    private List<Contact> contactList;
    private List<Contact> filteredContactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Contacts");
        }

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        contactList = new ArrayList<>();
        filteredContactList = new ArrayList<>();
        adapter = new ContactAdapter(this, filteredContactList);  // Use the filtered list
        recyclerView.setAdapter(adapter);

        loadContacts();

        FloatingActionButton fabAddContact = findViewById(R.id.floatingActionButton);
        fabAddContact.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddContact.class);
            startActivity(intent);
        });

        // Set up SearchView
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterContacts(newText);
                return true;
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadContacts() {
        ContactDatabaseHelper dbHelper = new ContactDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                ContactDatabaseHelper.COLUMN_ID,
                ContactDatabaseHelper.COLUMN_NAME,
                ContactDatabaseHelper.COLUMN_EMAIL,
                ContactDatabaseHelper.COLUMN_PHONE,
                ContactDatabaseHelper.COLUMN_ADDRESS,
                ContactDatabaseHelper.COLUMN_IMAGE
        };

        Cursor cursor = null;
        try {
            cursor = db.query(
                    ContactDatabaseHelper.TABLE_CONTACTS,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            contactList.clear();
            filteredContactList.clear();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int idIndex = cursor.getColumnIndex(ContactDatabaseHelper.COLUMN_ID);
                    int nameIndex = cursor.getColumnIndex(ContactDatabaseHelper.COLUMN_NAME);
                    int emailIndex = cursor.getColumnIndex(ContactDatabaseHelper.COLUMN_EMAIL);
                    int phoneIndex = cursor.getColumnIndex(ContactDatabaseHelper.COLUMN_PHONE);
                    int addressIndex = cursor.getColumnIndex(ContactDatabaseHelper.COLUMN_ADDRESS);
                    int imageIndex = cursor.getColumnIndex(ContactDatabaseHelper.COLUMN_IMAGE);

                    long id = cursor.getLong(idIndex);
                    String name = cursor.getString(nameIndex);
                    String email = cursor.getString(emailIndex);
                    String phone = cursor.getString(phoneIndex);
                    String address = cursor.getString(addressIndex);
                    byte[] image = cursor.getBlob(imageIndex);

                    Contact contact = new Contact(id, name, email, phone, address, image);
                    contactList.add(contact);
                    filteredContactList.add(contact);  // Initially, filtered list shows all contacts
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
            adapter.notifyDataSetChanged();
        }
    }

    private void filterContacts(String text) {
        filteredContactList.clear();
        if (text.isEmpty()) {
            filteredContactList.addAll(contactList);
        } else {
            text = text.toLowerCase();
            for (Contact contact : contactList) {
                if (contact.getName().toLowerCase().contains(text) ||
                        contact.getEmail().toLowerCase().contains(text) ||
                        contact.getPhone().toLowerCase().contains(text) ||
                        contact.getAddress().toLowerCase().contains(text)) {
                    filteredContactList.add(contact);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
