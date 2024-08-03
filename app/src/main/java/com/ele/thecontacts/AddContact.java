
package com.ele.thecontacts;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.ele.thecontacts.databinding.ActivityAddContactBinding;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Objects;


public class AddContact extends AppCompatActivity {

    private ActivityAddContactBinding binding;
    private long contactId = -1;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    try {
                        assert imageUri != null;
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        binding.userImage.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddContactBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("                  Add Contact");
        }

        binding.userImage.setOnClickListener(v -> openImageChooser());
        binding.saveButton.setOnClickListener(v -> saveContact());

        contactId = getIntent().getLongExtra("contact_id", -1);
        if (contactId != -1) {
            loadContact(contactId);
        }
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void loadContact(long contactId) {
        ContactDatabaseHelper dbHelper = new ContactDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                ContactDatabaseHelper.COLUMN_NAME,
                ContactDatabaseHelper.COLUMN_EMAIL,
                ContactDatabaseHelper.COLUMN_PHONE,
                ContactDatabaseHelper.COLUMN_ADDRESS,
                ContactDatabaseHelper.COLUMN_IMAGE
        };

        String selection = ContactDatabaseHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(contactId) };

        Cursor cursor = db.query(
                ContactDatabaseHelper.TABLE_CONTACTS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            binding.nameInput.setText(cursor.getString(cursor.getColumnIndexOrThrow(ContactDatabaseHelper.COLUMN_NAME)));
            binding.emailInput.setText(cursor.getString(cursor.getColumnIndexOrThrow(ContactDatabaseHelper.COLUMN_EMAIL)));
            binding.phoneInput.setText(cursor.getString(cursor.getColumnIndexOrThrow(ContactDatabaseHelper.COLUMN_PHONE)));
            binding.addressInput.setText(cursor.getString(cursor.getColumnIndexOrThrow(ContactDatabaseHelper.COLUMN_ADDRESS)));

            byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow(ContactDatabaseHelper.COLUMN_IMAGE));
            if (image != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                binding.userImage.setImageBitmap(bitmap);
            }
            cursor.close();
        }
        db.close();
    }

    private void saveContact() {
        String name = Objects.requireNonNull(binding.nameInput.getText()).toString();
        String email = Objects.requireNonNull(binding.emailInput.getText()).toString();
        String phone = Objects.requireNonNull(binding.phoneInput.getText()).toString();
        String address = Objects.requireNonNull(binding.addressInput.getText()).toString();

        Drawable drawable = binding.userImage.getDrawable();
        Bitmap bitmap = (drawable instanceof BitmapDrawable) ? ((BitmapDrawable) drawable).getBitmap() : null;
        byte[] image = bitmap != null ? bitmapToByteArray(bitmap) : null;

        ContactDatabaseHelper dbHelper = new ContactDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContactDatabaseHelper.COLUMN_NAME, name);
        values.put(ContactDatabaseHelper.COLUMN_EMAIL, email);
        values.put(ContactDatabaseHelper.COLUMN_PHONE, phone);
        values.put(ContactDatabaseHelper.COLUMN_ADDRESS, address);
        values.put(ContactDatabaseHelper.COLUMN_IMAGE, image);

        if (contactId == -1) {
            long newRowId = db.insert(ContactDatabaseHelper.TABLE_CONTACTS, null, values);
            if (newRowId != -1) {
                Toast.makeText(this, "Contact Saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error Saving Contact", Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsUpdated = db.update(ContactDatabaseHelper.TABLE_CONTACTS, values, ContactDatabaseHelper.COLUMN_ID + " = ?", new String[] { String.valueOf(contactId) });
            if (rowsUpdated > 0) {
                Toast.makeText(this, "Contact Updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error Updating Contact", Toast.LENGTH_SHORT).show();
            }
        }

        db.close();
        finish();
    }

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }
}
