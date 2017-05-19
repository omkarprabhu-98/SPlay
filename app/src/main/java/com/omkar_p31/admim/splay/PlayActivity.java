package com.omkar_p31.admim.splay;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import static com.omkar_p31.admim.splay.R.id.currTime;

public class PlayActivity extends AppCompatActivity {

    private MediaPlayer mPlayer;
    private int check_p = 0;

    Handler handle = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            long curr_time = mPlayer.getCurrentPosition();
            TextView curr = (TextView) findViewById(currTime);
            curr.setText(milliSecondsToTimer(curr_time));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        // Intent bundle
        Bundle play = getIntent().getExtras();
        int to_play = play.getInt("id");
        mPlayer = MediaPlayer.create(PlayActivity.this, to_play);
        mPlayer.start();
        // Variables
        final ImageView playpause = (ImageView) findViewById(R.id.playpause);
        final ImageView album_art = (ImageView) findViewById(R.id.albumArt) ;
        final TextView album = (TextView) findViewById(R.id.songName);
        final TextView artist = (TextView) findViewById(R.id.artName);
        final TextView total = (TextView) findViewById(R.id.time);


        // time variables
        final long total_time = mPlayer.getDuration();


        // metaData Retrieval
        MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever();
        byte[] art;
        Uri mediaPath = Uri.parse("android.resource://" + getPackageName() + "/" + to_play);
        metaRetriver.setDataSource(this, mediaPath);
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


        // time
        total.setText(milliSecondsToTimer(total_time));


        // OnClickListeners




        playpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayer.isPlaying()) {
                    playpause.setImageResource(R.drawable.pla);
                    mPlayer.pause();
                    check_p = 0;
                }
                else {
                    playpause.setImageResource(R.drawable.pau);
                    mPlayer.start();
                    check_p = 1;
                }

            }
        });


        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                onBackPressed();
            }
        });



        Runnable currTime = new Runnable() {
            @Override
            public void run() {

                while(mPlayer.isPlaying())
                {
                    handle.sendEmptyMessage(0);
                }
            }
        };
        Thread cTime = new Thread(currTime);
        cTime.start();


    }



    @Override
    public void onBackPressed() {
        if (mPlayer.isPlaying()) {
            mPlayer.stop();
        }
        mPlayer.release();
        super.onBackPressed();
    }

    /*
        activity lifecycle callback
     */
    @Override
    protected void onStop() {
        if (mPlayer.isPlaying()) {
            mPlayer.stop();
        }
        mPlayer.release();
        super.onStop();

    }

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
