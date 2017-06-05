package com.omkar_p31.admim.splay;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Admin on 19-Mar-17.
 */

public class SongAdapter extends ArrayAdapter<Songs> {

    public SongAdapter(Activity context, ArrayList<Songs> song)
    {
        super(context,0,song);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View sons_list = convertView;
        if(sons_list==null)
        {
            sons_list= LayoutInflater.from(getContext()).inflate(R.layout.song_list,parent,false);
        }

        // getting item
        Songs s = getItem(position);
        // displayig item
        TextView sName = (TextView) sons_list.findViewById(R.id.s_name);

        sName.setText(s.get_song_name());
        return sons_list;

    }
}
