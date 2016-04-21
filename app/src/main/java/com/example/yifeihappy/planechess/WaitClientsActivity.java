package com.example.yifeihappy.planechess;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huangbin.network.BroacastLuncherThread;
import com.example.huangbin.network.BroadCastBaseHelper;
import com.example.huangbin.network.BroascastGroupHelper;
import com.example.huangbin.network.IPAdressHelper;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;

public class WaitClientsActivity extends AppCompatActivity {

    BroascastGroupHelper broascastGroupHelper=null;
    BroacastLuncherThread broacastLuncherThread = null;
    Thread  countClientListenThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait_clients);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        String ip = IPAdressHelper.getIPByWifi(this);


        TextView txtRoomPlayerNum = (TextView)findViewById(R.id.txtRoomPlayerNum);
        txtRoomPlayerNum.setText("飞机起飞点数："+bundle.getString("startNums")+"\n"+"人数："+
                bundle.getString("playerNum")+"\n"+"Host Name:"+bundle.getString("playerName")+"\n"
                +"Host Color:"+bundle.getString("planeColor")+"\n"+"ip"+ip);


        broascastGroupHelper= new BroascastGroupHelper(30000);
        broascastGroupHelper.joinGroup();

        broacastLuncherThread = new BroacastLuncherThread(broascastGroupHelper,ip);




        broascastGroupHelper.setOnReceiveMsgListener(new BroascastGroupHelper.OnReceiveMsgListener() {

                                                         @Override
                                                         public void onReceive(BroadCastBaseHelper.BroadCastBaseMsg msg) {
                                                              //  Toast.makeText(WaitClientsActivity.this, msg.ip, Toast.LENGTH_SHORT).show();

                                                         }
                                                     }

        );

        broacastLuncherThread.start();//broadcast the ip of the creater of room


        countClientListenThread = new CountClientListenThread();
        countClientListenThread.start();//count the number of client who enter the room


    }

    class CountClientListenThread extends Thread {
        private Object mLock = new Object();
        boolean stopListen = false;
        public void stopThread() {
            synchronized (mLock) {
                stopListen = true;
            }
        }
        @Override
        public void run() {

            synchronized (mLock)
            {

                   while(!stopListen) {
                        broascastGroupHelper.receiveMsg();
                        try {
                            mLock.wait(2000);
                        }catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
            }
        }

    }


}
