package com.omkar_p31.admim.splay;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class SonglistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songlist);

        final ArrayList<Songs> song = new ArrayList<>();
        song.add(new Songs("Cornfield Chase", R.raw.cornfield_chase));



        SongAdapter sadap = new SongAdapter(SonglistActivity.this,song);
        ListView s = (ListView) findViewById(R.id.activity_songlist);
        s.setAdapter(sadap);

        s.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent play = new Intent(SonglistActivity.this, PlayActivity.class);
                Songs so = song.get(position);
                int m_id = so.get_audio_res();
                play.putExtra("id",m_id);
                startActivity(play);
            }
        });



    }
}
