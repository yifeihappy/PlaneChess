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
import com.example.huangbin.network.BroadCastBaseHelper;
import com.example.huangbin.network.BroascastGroupHelper;

public class SearchRoomActivity extends AppCompatActivity {

    BroascastGroupHelper broascastGroupHelper = null;
    String ip = null;
    Handler handler = null;
    TextView txtRoomIP=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_room);
        Log.e("doit", "begin onCreate");
       txtRoomIP = (TextView)findViewById(R.id.txtRoomIP);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                if(msg.what == 0x101) {
                    txtRoomIP.setText(msg.getData().getString("ip"));
                }
                super.handleMessage(msg);
            }
        };

        broascastGroupHelper = new BroascastGroupHelper(30000);
        boolean success=broascastGroupHelper.joinGroup();
       if(success) Log.e("doit","succsss");
        broascastGroupHelper.setOnReceiveMsgListener(new BroascastGroupHelper.OnReceiveMsgListener() {

                                                         @Override
                                                         public void onReceive(BroadCastBaseHelper.BroadCastBaseMsg msg) {

                                                             Log.e("doit","before handler"+msg.ip);
                                                             Message message = handler.obtainMessage();
                                                             Bundle bundle = new Bundle();
                                                             bundle.putString("ip",msg.ip);
                                                             message.what = 0x101;
                                                             message.setData(bundle);
                                                             handler.sendMessage(message);

                                                         }
                                                     }

        );

        SearchRoomListernThread searchRoomListernThread = new SearchRoomListernThread();
        searchRoomListernThread.start();//search room IP

        Button btnEnter = (Button)findViewById(R.id.btnEnter);

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchRoomActivity.this, UserSettingActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    class SearchRoomListernThread extends Thread {

        @Override
        public void run() {
//            Toast.makeText(SearchRoomActivity.this,"begin",Toast.LENGTH_LONG).show();

            broascastGroupHelper.receiveMsg();

        }
    }
}
