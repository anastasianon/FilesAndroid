package com.example.filesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_READ_STORAGE = 1;
    private static boolean READ_STORAGE_GRANTED = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button updateBtn = findViewById(R.id.updateBtn);
        updateBtn.setOnClickListener(view -> {
            getFiles();
        });

        int hasReadStoragePermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
        );

        if (hasReadStoragePermission == PackageManager.PERMISSION_GRANTED) {
            READ_STORAGE_GRANTED = true;
        }
        else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                    REQUEST_CODE_READ_STORAGE
            );
        }

        if (READ_STORAGE_GRANTED) {
            getFiles();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_READ_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                READ_STORAGE_GRANTED = true;
            }
        }

        if (READ_STORAGE_GRANTED) {
            getFiles();
        }
        else {
            Toast.makeText(
                    this,
                    "Требуется доступ к хранилищу",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void getFiles() {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, null, null, null
        );

        List<String> images = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range")
                String file = cursor.getString(
                        cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME)
                );

                images.add(file);
            }

            cursor.close();
        }

        ArrayAdapter<String> imagesAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, images
        );

        ListView imagesListView = findViewById(R.id.imagesListView);
        imagesListView.setAdapter(imagesAdapter);

        cursor = contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, null, null, null
        );

        List<String> videos = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range")
                String file = cursor.getString(
                        cursor.getColumnIndex(MediaStore.Video.VideoColumns.DISPLAY_NAME)
                );

                videos.add(file);
            }

            cursor.close();
        }

        ArrayAdapter<String> videosAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, videos
        );

        ListView videosListView = findViewById(R.id.videosListView);
        videosListView.setAdapter(videosAdapter);
    }
}