package com.omkar_p31.admim.splay;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class SonglistActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    private static final String mediaPath = "/sdcard/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_songlist);

        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkPermission()) {
                askPermission();
            }
            else{
                loadList();
            }
        }
        else{
            loadList();
        }









    }
    private void askPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(SonglistActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(SonglistActivity.this, "Write External Storage permission allows us to read songs. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(SonglistActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(SonglistActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadList();

                } else {
                    Toast.makeText(this, "Permsission not granted shutting down app", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    private void loadList()
    {
        // array list for songs object
        final ArrayList<Songs> song = new ArrayList<>();
        // append to list



        ContentResolver resolver = getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String sortOrder = MediaStore.Audio.AudioColumns.TITLE + " COLLATE LOCALIZED ASC";
        Cursor cursor = resolver.query(uri, null, null, null, sortOrder);
        if(cursor != null && cursor.moveToFirst()) {
            do {
                song.add(new Songs( cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE)),  cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))));

            } while (cursor.moveToNext());
            cursor.close();
        }



        SongAdapter sadap = new SongAdapter(SonglistActivity.this, song);
        // attach list view
        ListView s = (ListView) findViewById(R.id.activity_songlist);
        s.setAdapter(sadap);

//         onItem click for each list view item
        s.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent play = new Intent(SonglistActivity.this, PlayActivity.class);
                Songs so = song.get(position);
                String m_id = so.get_audio_index();

                play.putExtra("path", m_id);
                startActivity(play);
            }
        });



    }

}
