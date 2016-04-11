package com.example.yifeihappy.planechess;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class WaitClientsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait_clients);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        TextView txtRoomPlayerNum = (TextView)findViewById(R.id.txtRoomPlayerNum);
        txtRoomPlayerNum.setText("飞机起飞点数："+bundle.getString("startNums")+"\n"+"人数："+
                bundle.getString("playerNum")+"\n"+"Host Name:"+bundle.getString("playerName")+"\n"+"Host Color:"+bundle.getString("planeColor"));


    }
}
