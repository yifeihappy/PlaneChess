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
import android.widget.Toast;

import com.example.huangbin.network.BroacastLuncherThread;
import com.example.huangbin.network.BroacastReceiveLooperThread;
import com.example.huangbin.network.BroadCastBaseHelper;
import com.example.huangbin.network.BroascastGroupHelper;

import java.util.StringTokenizer;

public class SearchRoomActivity extends AppCompatActivity {

    public static final String ENTER_ROOM = "ENTERROOM";
    public static final String CREATE_ROOM = "CREATEROOM";
    public static final String WELCOME = "WELCOME";
    public static final String REFUSE = "REFUSE";
    public static final String BEGIN = "BEGIN";

    BroascastGroupHelper broascastGroupHelper = null;
    Handler handler = null;
    TextView txtRoomIP=null;
    SerliBroacastData serliBroacastData = null;
    Button btnEnter = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_room);
       // Log.e("doit", "begin onCreate");

       txtRoomIP = (TextView)findViewById(R.id.txtRoomIP);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                Bundle bundle = msg.getData();

                if(msg.what == 0x101) {
                    SerliBroacastData serliRoomData = new SerliBroacastData();
                    serliRoomData = (SerliBroacastData)bundle.getSerializable(CREATE_ROOM);
                    txtRoomIP.setText(serliRoomData.getRoomIP());
                }
                btnEnter.setEnabled(true);
                super.handleMessage(msg);
            }
        };

        broascastGroupHelper = new BroascastGroupHelper(30000);
        broascastGroupHelper.joinGroup();
        //boolean success=broascastGroupHelper.joinGroup();
       //if(success) Log.e("doit","succsss");
        broascastGroupHelper.setLoopback(true);
        broascastGroupHelper.setOnReceiveMsgListener(new BroascastGroupHelper.OnReceiveMsgListener() {

                                                         @Override
                                                         public void onReceive(BroadCastBaseHelper.BroadCastBaseMsg msg) {
                                                             //Log.e("doit","before handler"+msg.ip);

                                                             Deserializable deserializable = new Deserializable();
                                                             serliBroacastData = deserializable.deSerliBroacastData(msg.msg);
                                                             Message message = handler.obtainMessage();
                                                             Bundle bundle = new Bundle();
                                                             bundle.putSerializable(CREATE_ROOM,serliBroacastData);
                                                             message.what = 0x101;
                                                             message.setData(bundle);
                                                             handler.sendMessage(message);
                                                         }
                                                     }

        );


        final BroacastReceiveLooperThread receiveRoomIPLooperThread = new BroacastReceiveLooperThread(broascastGroupHelper);
        receiveRoomIPLooperThread.start();

        btnEnter = (Button)findViewById(R.id.btnEnter);

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //stop to receive room ip
                receiveRoomIPLooperThread.stopThread();

                Bundle bundle = new Bundle();
                bundle.putSerializable(CREATE_ROOM,serliBroacastData);
                Intent intent = new Intent(SearchRoomActivity.this, UserSettingActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

    }


}
