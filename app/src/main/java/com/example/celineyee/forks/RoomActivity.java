package com.example.celineyee.forks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.celineyee.forks.Player;
import com.example.celineyee.forks.PlayerListAdapter;
import com.example.celineyee.forks.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class RoomActivity extends AppCompatActivity {
    private String player_name;

    ArrayList<Player> playerList;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        Bundle bundle = getIntent().getExtras();
        player_name = bundle.getString("name_key");

        playerList = new ArrayList<>();
        playerList.add(new Player(player_name));
        playerList.add(new Player("PLAYER 1"));
        playerList.add(new Player("PLAYER 2"));



        ListView listView = (ListView) findViewById(R.id.listview);
        PlayerListAdapter playersAdapter = new PlayerListAdapter(RoomActivity.this,R.layout.cardview_player,playerList);
        listView.setAdapter(playersAdapter);
    }
}
