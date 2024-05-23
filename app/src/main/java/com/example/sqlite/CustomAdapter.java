package com.example.sqlite;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<Contact> {
    private LayoutInflater inflater;

    public CustomAdapter(Context context, List<Contact> contacts) {
        super(context, 0, contacts);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item, parent, false);
        }

        Contact contact = getItem(position);

        TextView idView = convertView.findViewById(R.id.item_id);
        TextView nameView = convertView.findViewById(R.id.item_name);
        ImageView imageView = convertView.findViewById(R.id.item_image);

        idView.setText(String.valueOf(contact.getId()));
        nameView.setText(contact.getName());
        Bitmap image = contact.getImage();
        if (image != null) {
            imageView.setImageBitmap(image);
        } else {
            imageView.setImageResource(R.drawable.ic_launcher_background); // Default image
        }

        return convertView;
    }
}
