package com.omkar_p31.admim.splay;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

import static com.omkar_p31.admim.splay.R.drawable.play;

public class PlayActivity extends AppCompatActivity {

    private MediaPlayer mPlayer = new MediaPlayer();
    // Intent bundle for getting extra info

    String to_play;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Bundle play = getIntent().getExtras();
        to_play = play.getString("uri");


        if(to_play != null) {
            playSong();
        }










    }


    /**
     * back press event
     */

    @Override
    public void onBackPressed() {
        if (mPlayer.isPlaying()) {
            mPlayer.stop();
        }
        mPlayer.release();
        super.onBackPressed();
    }

    /**
     * activity lifecycle callback
     */
    @Override
    protected void onStop() {
        if (mPlayer.isPlaying()) {
            mPlayer.stop();
        }
        mPlayer.release();
        super.onStop();

    }

    /**
     *time converter
     */
    public String milliSecondsToTimer(long milliseconds){
        String finalTimerString = "";
        String secondsString;

        // Convert total duration into time
        int hours = (int)( milliseconds / (1000*60*60));
        int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
        int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
        // Add hours if there
        if(hours > 0){
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if(seconds < 10){
            secondsString = "0" + seconds;
        }else{
            secondsString = "" + seconds;}

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    private void playSong()
    {
        Uri toPlayUri = Uri.parse(to_play);
        try {
            mPlayer.setDataSource(PlayActivity.this,toPlayUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mPlayer.start();

        // Variables in play layout
        final ImageView playpause = (ImageView) findViewById(R.id.playpause);
        final ImageView album_art = (ImageView) findViewById(R.id.albumArt) ;
        final TextView album = (TextView) findViewById(R.id.songName);
        final TextView artist = (TextView) findViewById(R.id.artName);
        final TextView total = (TextView) findViewById(R.id.time);
        final TextView curr = (TextView) findViewById(R.id.currTime);


        // time variable
        final long total_time = mPlayer.getDuration();


        // metaData Retrieval
        MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever();
        byte[] art;

        metaRetriver.setDataSource(PlayActivity.this, toPlayUri);
        try {
            art = metaRetriver.getEmbeddedPicture();
            Bitmap songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
            album_art.setImageBitmap(songImage);
            album.setText(metaRetriver .extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
            artist.setText(metaRetriver .extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));

        }
        catch (Exception e)
        {
            album.setText("Unknown Album");
            artist.setText("Unknown Artist");
        }


        // Event Listeners

        // play/pause image onClick
        playpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayer.isPlaying()) {
                    playpause.setImageResource(play);
                    mPlayer.pause();

                }
                else {
                    playpause.setImageResource(R.drawable.pause);
                    mPlayer.start();
                }

            }
        });

        // on completion of mediaPlayer
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                onBackPressed();
            }
        });


    }


}
