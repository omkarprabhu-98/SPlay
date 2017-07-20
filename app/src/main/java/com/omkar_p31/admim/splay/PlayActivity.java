package com.omkar_p31.admim.splay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK;

public class PlayActivity extends AppCompatActivity {


    // layout Variables
     ImageView playpause;
     ImageView album_art;
     TextView album  ;
     TextView artist;
     TextView total ;
     TextView curr ;
     SeekBar songSeek;


    MediaPlayer mPlayer = new MediaPlayer();
    Handler handleTimeUpdate ;
    Thread update;
    static boolean mediaOnHold = false;
    AudioManager am ;



    AudioManager.OnAudioFocusChangeListener afChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                public void onAudioFocusChange(int focusChange) {
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        // Permanent loss of audio focus
                        // Pause playback immediately
                        onBackPressed();

                    }
                    else if (focusChange == AUDIOFOCUS_LOSS_TRANSIENT) {
                        // Pause playback
                        mPlayer.pause();
                        changeButton();
                        mPlayer.seekTo(0);
                    } else if (focusChange == AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                        // Lower the volume, keep playing
                        mPlayer.pause();
                        changeButton();
                        mPlayer.seekTo(0);
                    } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                        // Your app has been granted audio focus again
                        // Raise volume to normal, restart playback if necessary
                        mPlayer.start();
                        changeButton();
                    }
                }
            };







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

            Log.d("Check","Inside On create");

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // audio manager for audio focus
        am  = (AudioManager) PlayActivity.this.getSystemService(Context.AUDIO_SERVICE);

        // handler for updating time
        handleTimeUpdate = new Handler(getApplicationContext().getMainLooper());

        // layout Variables
        playpause = (ImageView) findViewById(R.id.playpause);
         album_art = (ImageView) findViewById(R.id.albumArt) ;
        album = (TextView) findViewById(R.id.songName);
         artist = (TextView) findViewById(R.id.artName);
         total = (TextView) findViewById(R.id.time);
        curr = (TextView) findViewById(R.id.currTime);
         songSeek = (SeekBar) findViewById(R.id.songProgressBar);


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

            // request audio focus
            int result = am.requestAudioFocus(afChangeListener,
                    // Use the music stream.
                    AudioManager.STREAM_MUSIC,
                    // Request permanent focus.
                    AudioManager.AUDIOFOCUS_GAIN);

            // start media on successful audio focus
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {


                mPlayer.setDataSource(PlayActivity.this, Uri.parse(Environment.getExternalStorageDirectory().getPath() + "/Music/" + to_play));
                mPlayer.prepareAsync();

                mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {

                        // total time of song
                        total.setText(milliSecondsToTimer(mPlayer.getDuration()));

                        // seek bar max duration
                        songSeek.setMax(mPlayer.getDuration());


                        // thread for updating current song time
                        update = new Thread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            curr.setText(milliSecondsToTimer(mPlayer.getCurrentPosition()));
                                            songSeek.setProgress(mPlayer.getCurrentPosition());
                                            handleTimeUpdate.postDelayed(this, 1000);

                                        } catch (Exception e) {

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
            }
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


        // seek bar touch listener
        songSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mPlayer != null && fromUser){
                    mPlayer.seekTo(progress);
                }
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
            am.abandonAudioFocus(afChangeListener);
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
    protected void onDestroy() {

        super.onDestroy();

        try {
            if (mPlayer.isPlaying()) {

                mPlayer.stop();
            }
            mPlayer.release();
            am.abandonAudioFocus(afChangeListener);


        }
        catch (Exception e){

        }
    }

    @Override
    protected void onStop() {
        Log.d("Check", "onStop Called");
        super.onStop();

        try {
            if (mPlayer.isPlaying()) {
                mediaOnHold = true;
                changeButton();
                mPlayer.pause();
            }
        }
        catch (Exception e){

        }

    }

    @Override
    protected void onPause() {

        Log.d("Check", "onPause Called");

        super.onPause();
        try {
            if (mPlayer.isPlaying()) {
                mediaOnHold = true;
                changeButton();
                mPlayer.pause();
            }
        }
        catch (Exception e){

        }
    }

    @Override
    protected void onRestart() {
        Log.d("Check", "onRestart Called");
        super.onRestart();
        if(mediaOnHold){
            changeButton();
            mediaOnHold = false;
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


    public void changeButton(){
        if (mPlayer.isPlaying()) {
            playpause.setImageResource(R.drawable.pause);


        }
        else {
            playpause.setImageResource(R.drawable.play);

        }


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }





}
