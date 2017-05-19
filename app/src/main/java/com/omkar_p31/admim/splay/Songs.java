package com.omkar_p31.admim.splay;

/**
 * Created by Admin on 19-Mar-17.
 */

public class Songs {
//    Added variables for storing image id and song name

    private String mSongName = "";
    private int mAudioRes;

//    Default constructor
    public Songs(String name, int audioRes)
    {
        mAudioRes = audioRes;
        mSongName = name;
    }

    public String get_song_name()
    {
        return mSongName;
    }

    public int get_audio_res()
    {
        return mAudioRes;
    }



}
