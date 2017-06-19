package com.omkar_p31.admim.splay;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

public class PlayActivity extends AppCompatActivity {


    MediaPlayer mPlayer = new MediaPlayer();
    Handler handleTimeUpdate ;
    Thread update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        // handler for updating time
        handleTimeUpdate = new Handler(getApplicationContext().getMainLooper());

        // layout Variables
        final ImageView playpause = (ImageView) findViewById(R.id.playpause);
        final ImageView album_art = (ImageView) findViewById(R.id.albumArt) ;
        final TextView album = (TextView) findViewById(R.id.songName);
        final TextView artist = (TextView) findViewById(R.id.artName);
        final TextView total = (TextView) findViewById(R.id.time);
        final TextView curr = (TextView) findViewById(R.id.currTime);


        // Intent bundle for getting extra info
        Bundle play = getIntent().getExtras();
        String to_play = play.getString("path");

        // metaData from song
        MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever();
        byte[] art;
        try {
            metaRetriver.setDataSource(PlayActivity.this, Uri.parse(Environment.getExternalStorageDirectory().getPath()+ "/Music/" +  to_play));

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


        // Play selected media
        try {
            mPlayer.setDataSource(PlayActivity.this,  Uri.parse(Environment.getExternalStorageDirectory().getPath()+ "/Music/" + to_play));
            mPlayer.prepareAsync();

            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                    // total time of song
                    total.setText(milliSecondsToTimer(mPlayer.getDuration()));


                    // thread for updating current song time
                    update = new Thread(
                            new Runnable() {
                                @Override
                                public void run(){
                                    try {
                                        curr.setText(milliSecondsToTimer(mPlayer.getCurrentPosition()));
                                        handleTimeUpdate.postDelayed(this, 100);
                                    }catch (Exception e){

                                    }
                                }
                            }
                    );
                    // start thread
                    update.start();

                    // start song
                    mp.start();


                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }



//      Event Listeners

        // play/pause image onClick
        playpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayer.isPlaying()) {
                    playpause.setImageResource(R.drawable.play);
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





    /**
     * back press event
     */

    @Override
    public void onBackPressed() {

        try {
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
            }
            mPlayer.release();
            super.onBackPressed();

        }catch(Exception e)
        {
            super.onBackPressed();
        }


    }

    /**
     * activity lifecycle callback
     */
    @Override
    protected void onStop() {

        try {
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
            }
            mPlayer.release();
            super.onStop();

        }
        catch (Exception e){
            super.onStop();
        }

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






}
