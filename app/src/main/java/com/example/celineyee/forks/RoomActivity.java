package com.example.celineyee.forks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.celineyee.forks.Player;
import com.example.celineyee.forks.PlayerListAdapter;
import com.example.celineyee.forks.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class RoomActivity extends AppCompatActivity implements AccelerometerListener{
    private String player_name;
    private Button button_instructions;

    ArrayList<Player> playerList;
    NotificationPop pop;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        button_instructions = findViewById(R.id.button_instructions);
        button_instructions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toInstructions = new Intent(RoomActivity.this,InstructionsActivity.class);
                startActivity(toInstructions);
            }
        });

        Bundle bundle = getIntent().getExtras();
        player_name = bundle.getString("name_key");

        playerList = new ArrayList<>();
        playerList.add(new Player(player_name));
        playerList.add(new Player("PLAYER 1"));
        playerList.add(new Player("PLAYER 2"));


        ListView listView = (ListView) findViewById(R.id.listview);
        PlayerListAdapter playersAdapter = new PlayerListAdapter(RoomActivity.this,R.layout.cardview_player,playerList);
        listView.setAdapter(playersAdapter);

        pop = new NotificationPop(this);
        new Scheduler(getApplicationContext(), pop);
    }

    @Override
    public void onAccelerationChanged(float x, float y, float z){
    }

    @Override
    public void onShake(float force){
        Toast.makeText(this, "Shake detected", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume(){
        super.onResume();
        Toast.makeText(this, "onResume Acc started", Toast.LENGTH_SHORT).show();

        if(AccelerometerManager.isSupported(this)) {
            AccelerometerManager.startListening(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(AccelerometerManager.isListening()){
            AccelerometerManager.stopListening();
            Toast.makeText(this, "onStop", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(AccelerometerManager.isListening()){
            AccelerometerManager.stopListening();
            Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
        }
    }
}
