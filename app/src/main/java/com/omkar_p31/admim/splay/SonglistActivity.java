package com.omkar_p31.admim.splay;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;


public class SonglistActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songlist);

        //Ask permission specifically if sdk version >= 23
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


    /**
     * Ask Permission
     */
    private void askPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(SonglistActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(SonglistActivity.this, "Write External Storage permission allows us to read songs. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(SonglistActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    /**
    * Check whether permission is already asked
    */
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


    /**
     * load contents from sdcard
      */

    private void loadList() {

        // array list for Songs object
        final ArrayList<Songs> song = new ArrayList<>();

        //path for getting songs
        String path = "/sdcard/Music/";

        try {

            //file for path to music
            File home = new File(path);

            //filter files with mp3 extension
            class FileExtensionFilter implements FilenameFilter {
                public boolean accept(File dir, String name) {
                    return (name.endsWith(".mp3") || name.endsWith(".MP3"));
                }
            }


            if (home.listFiles(new FileExtensionFilter()).length > 0) {
                for (File file : home.listFiles(new FileExtensionFilter())) {

                    //add to song to song Array List
                    song.add(new Songs(file.getName()));


                }
            }

            //attach Adapter to song Array List
            SongAdapter sadap = new SongAdapter(SonglistActivity.this, song);
            // attach list view
            ListView s = (ListView) findViewById(R.id.activity_songlist);
            s.setAdapter(sadap);

            //onItem click for each list view item
            s.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent play = new Intent(SonglistActivity.this, PlayActivity.class);
                    Songs so = song.get(position);
                    String m_id = so.get_song_name();
                    //send song name with Intent
                    play.putExtra("path", m_id);
                    startActivity(play);
                }
            });


        }catch (Exception e){

        }

    }
}
