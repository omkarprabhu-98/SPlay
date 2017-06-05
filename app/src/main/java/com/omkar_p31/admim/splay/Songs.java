package com.omkar_p31.admim.splay;

/**
 * Created by Admin on 19-Mar-17.
 */

public class Songs {
//    Added variables for storing image id and song name

    private String mSongName = "Nthing";
    private String mIndex;

//    Default constructor
    public Songs(String name,String index)
    {
        mIndex = index;
        mSongName = name;
    }

    public String get_song_name()
    {
        return mSongName;
    }

    public String get_audio_index()
    {
        return mIndex;
    }



}
