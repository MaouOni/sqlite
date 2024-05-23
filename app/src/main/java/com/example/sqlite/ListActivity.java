package com.example.sqlite;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ListActivity extends Activity {
    ListView listView;
    CustomAdapter adapter;
    List<Contact> listItems = new ArrayList<>();
    SQLiteDatabase sqld;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listView = findViewById(R.id.listView);
        Button backButton = findViewById(R.id.backButton);
        SQLiteHelper dsqlh = new SQLiteHelper(this);
        sqld = dsqlh.getWritableDatabase();

        adapter = new CustomAdapter(this, listItems);
        listView.setAdapter(adapter);

        updateListView();

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ListActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void updateListView() {
        listItems.clear();
        Cursor c = sqld.rawQuery("SELECT id, nombre, image_path FROM Contactos", null);
        if (c.moveToFirst()) {
            do {
                int id = c.getInt(0);
                String nombre = c.getString(1);
                String imagePath = c.getString(2);
                Bitmap image = loadImageFromStorage(imagePath);
                listItems.add(new Contact(id, nombre, image));
            } while (c.moveToNext());
        }
        adapter.notifyDataSetChanged();
    }

    private Bitmap loadImageFromStorage(String path) {
        try {
            File f = new File(path);
            return BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
