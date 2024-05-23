package com.example.sqlite;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity {
    EditText jetI, jetN;
    Button jbnA, jbnL, jbnD, jbnU, jbnP;
    ImageView jivP;
    SQLiteDatabase sqld;
    Bitmap selectedImage;
    private static final int REQUEST_IMAGE_SELECT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        jetI = findViewById(R.id.xetI);
        jetN = findViewById(R.id.xetN);
        jbnA = findViewById(R.id.xbnA);
        jbnL = findViewById(R.id.xbnL);
        jbnD = findViewById(R.id.xbnD);
        jbnU = findViewById(R.id.xbnU);
        jbnP = findViewById(R.id.xbnP);
        jivP = findViewById(R.id.xivP);

        SQLiteHelper dsqlh = new SQLiteHelper(this);
        sqld = dsqlh.getWritableDatabase();

        jbnP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IMAGE_SELECT);
            }
        });

        jbnA.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String nombre = jetN.getText().toString();
                Cursor cursor = sqld.rawQuery("SELECT MAX(id) FROM Contactos", null);
                int newId = 1;
                if (cursor.moveToFirst()) {
                    newId = cursor.getInt(0) + 1;
                }
                ContentValues cv = new ContentValues();
                cv.put("id", newId);
                cv.put("nombre", nombre);
                if (selectedImage != null) {
                    String imagePath = saveImageToInternalStorage(selectedImage);
                    cv.put("image_path", imagePath);
                } else {
                    Bitmap defaultImage = BitmapFactory.decodeResource(getResources(), R.drawable.account);
                    String imagePath = saveImageToInternalStorage(defaultImage);
                    cv.put("image_path", imagePath);
                }
                sqld.insert("Contactos", null, cv);
                jetI.setText("");
                jetN.setText("");
                jivP.setImageResource(R.drawable.ic_launcher_background);
                selectedImage = null;
            }
        });

        jbnL.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                startActivity(intent);
            }
        });

        jbnD.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String id = jetI.getText().toString();
                sqld.delete("Contactos", "id=?", new String[]{id});
                jetI.setText("");
                jetN.setText("");
                jivP.setImageResource(R.drawable.ic_launcher_background);
                selectedImage = null;
            }
        });

        jbnU.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String id = jetI.getText().toString();
                String nombre = jetN.getText().toString();
                ContentValues cv = new ContentValues();
                cv.put("nombre", nombre);
                if (selectedImage != null) {
                    String imagePath = saveImageToInternalStorage(selectedImage);
                    cv.put("image_path", imagePath);
                }
                sqld.update("Contactos", cv, "id=?", new String[]{id});
                jetI.setText("");
                jetN.setText("");
                jivP.setImageResource(R.drawable.ic_launcher_background);
                selectedImage = null;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_SELECT && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                selectedImage = getResizedBitmap(originalBitmap, 100, 100); // Resize to 100x100 or any preferred size
                jivP.setImageBitmap(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String saveImageToInternalStorage(Bitmap bitmap) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File mypath = new File(directory, "profile_" + System.currentTimeMillis() + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mypath.getAbsolutePath();
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

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
    }
}
