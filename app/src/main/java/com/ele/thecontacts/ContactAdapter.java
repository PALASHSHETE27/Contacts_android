
package com.ele.thecontacts;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ele.thecontacts.databinding.ContactItemBinding;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private final List<Contact> contacts;
    private final Context context;

    public ContactAdapter(Context context, List<Contact> contacts) {
        this.context = context;
        this.contacts = contacts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ContactItemBinding binding = ContactItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.binding.contactName.setText(contact.getName());
        holder.binding.contactEmail.setText(contact.getEmail());
        holder.binding.contactPhone.setText(contact.getPhone());
        holder.binding.contactAddress.setText(contact.getAddress());

        if (contact.getImage() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(contact.getImage(), 0, contact.getImage().length);
            holder.binding.contactImage.setImageBitmap(bitmap);
        } else {
            holder.binding.contactImage.setImageResource(R.drawable.ic_user_placeholder); // Default image
        }

        holder.binding.deleteButton.setOnClickListener(v -> {
            ContactDatabaseHelper dbHelper = new ContactDatabaseHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            int rowsDeleted = db.delete(ContactDatabaseHelper.TABLE_CONTACTS, ContactDatabaseHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(contact.getId())});
            if (rowsDeleted > 0) {
                contacts.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, contacts.size());
                Toast.makeText(context, "Contact Deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Error Deleting Contact", Toast.LENGTH_SHORT).show();
            }
            db.close();
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddContact.class);
            intent.putExtra("contact_id", contact.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ContactItemBinding binding;

        ViewHolder(ContactItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
