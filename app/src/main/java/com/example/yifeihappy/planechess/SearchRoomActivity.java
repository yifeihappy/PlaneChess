package com.example.yifeihappy.planechess;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.huangbin.network.BroadCastBaseHelper;
import com.example.huangbin.network.BroascastGroupHelper;
import com.example.huangbin.network.BroastcastP2PHelper;
import com.example.huangbin.network.IPAdressHelper;

public class SearchRoomActivity extends AppCompatActivity {

    public static final String SEARCH_ROOM = "SE";//SEARCHROOM
    public static final String CREATE_ROOM = "CR";//CREATEROOM
    public static final String WELCOME = "WE";//WELCOME
    public static final String REFUSE = "RE";//REFUSE
    public static final String BEGIN = "BE";//BEGIN
    public static final int INPORT_MUL = 31111;
    public static final int GET_ROOM_IP = 0x200;
    public static final String ROOM_DATA = "roomData";


    BroascastGroupHelper broascastGroupHelper = null;
    TextView txtRoomIP=null;
    Button btnEnter = null;
    Handler handler = null;
    SerliBroacastData roomData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_room);

        txtRoomIP = (TextView)findViewById(R.id.txtRoomIP);
        btnEnter = (Button)findViewById(R.id.btnEnter);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == GET_ROOM_IP) {
                    Bundle bundleHandler = msg.getData();
                    SerliBroacastData s = (SerliBroacastData)bundleHandler.getSerializable(ROOM_DATA);
                    txtRoomIP.setText(s.getRoomIP());
                    btnEnter.setEnabled(true);
                }
            }
        };

        broascastGroupHelper = new BroascastGroupHelper(INPORT_MUL);
        broascastGroupHelper.joinGroup();
        broascastGroupHelper.setLoopback(true);
        broascastGroupHelper.setOnReceiveMsgListener(new BroadCastBaseHelper.OnReceiveMsgListener() {
            @Override
            public void onReceive(BroadCastBaseHelper.BroadCastBaseMsg msg) {
                Deserializable deserializable = new Deserializable();
                roomData = (SerliBroacastData) deserializable.deSerliBroacastData(msg.msg);
                //receive Room IP
                if (roomData.getTag().startsWith(CREATE_ROOM)) {
                    Message message = handler.obtainMessage();
                    message.what = GET_ROOM_IP;
                    Bundle bundleReceive = new Bundle();
                    bundleReceive.putSerializable(ROOM_DATA, roomData);
                    message.setData(bundleReceive);
                    handler.sendMessage(message);
                }
            }
        });


        new SearchRoomThread().start();

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundletoUserSetting = new Bundle();
                bundletoUserSetting.putSerializable(ROOM_DATA,roomData);
                Intent intentToUserSetting = new Intent(SearchRoomActivity.this, UserSettingActivity.class);
                intentToUserSetting.putExtras(bundletoUserSetting);
                startActivity(intentToUserSetting);
                broascastGroupHelper.destory();
                finish();
            }
        });
    }


    class SearchRoomThread extends Thread
    {
        @Override
        public void run() {
            super.run();
            broascastGroupHelper.receiveMsg();
        }
    }
}
