package com.example.celineyee.forks;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PlayerListAdapter extends ArrayAdapter<Player> {
    private Context mContext;
    int mResource;

    public PlayerListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Player> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        String name = getItem(position).getName();
        Player player = new Player(name);
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource,parent,false);

        TextView player_name = (TextView) convertView.findViewById(R.id.text_player_name);
        player_name.setText(name);
        return convertView;
    }
}