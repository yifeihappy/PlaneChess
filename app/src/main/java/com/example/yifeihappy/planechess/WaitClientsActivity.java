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

import com.example.PPP.GameMainActivity;
import com.example.huangbin.network.BroacastLuncherThread;
import com.example.huangbin.network.BroacastReceiveLooperThread;
import com.example.huangbin.network.BroadCastBaseHelper;
import com.example.huangbin.network.BroascastGroupHelper;
import com.example.huangbin.network.IPAdressHelper;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;


public class WaitClientsActivity extends AppCompatActivity {

    public static final String ENTER_ROOM = "EN";//ENTERROOM
    public static final String CREATE_ROOM = "CR";//CREATEROOM
    public static final String WELCOME = "WE";//WELCOME
    public static final String REFUSE = "RE";//REFUSE
    public static final String BEGIN = "BE";//BEGIN

    BroascastGroupHelper broascastGroupHelper=null;
    BroacastLuncherThread broacastRooMIPThread = null;
    BroacastReceiveLooperThread broacastReceiveLooperThread = null;
   // Button btnBegin = null;//click to start the game if all players hava entered.
    String roomIP = null;
    SerliBroacastData broacastData = null;

    Map playersWait = new TreeMap();//color and ip which players have selected and entered.
    Map waitsName = new TreeMap();//key = playerIp,value = planeColor

    int playersNum ;//the num of player

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //if(msg.what == 0x200)
                //btnBegin.setEnabled(true);
            Bundle bundleBegin = msg.getData();
            //Toast.makeText(WaitClientsActivity.this,"Click to begin...",Toast.LENGTH_LONG).show();
            Intent intentBegin = new Intent(WaitClientsActivity.this,GameMainActivity.class);
            intentBegin.putExtras(bundleBegin);
            startActivity(intentBegin);
           finish();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait_clients);

        final Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        playersNum = Integer.parseInt(bundle.getString("playersNum"));
        roomIP = IPAdressHelper.getIPByWifi(this);

        playersWait.put(roomIP,bundle.getCharSequence("planeColor"));//add room creater plane color to colorList.key = playerIP,value = color.
        waitsName.put(roomIP, bundle.getString("playerName"));

    //    btnBegin = (Button)findViewById(R.id.btnBegin);
        TextView txtRoomPlayerNum = (TextView)findViewById(R.id.txtRoomPlayerNum);

        //txtRoomPlayerNum.setText("IP"+roomIP);
        txtRoomPlayerNum.setText("飞机起飞点数：" + bundle.getString("startNums") + "\n" + "人数：" +
                bundle.getString("playersNum") + "\n" + "Host Name:" + bundle.getString("playerName") + "\n"
                + "Host Color:" + bundle.getString("planeColor") + "\n" + "ip" + roomIP);

        broascastGroupHelper= new BroascastGroupHelper(30000);
        broascastGroupHelper.joinGroup();
        broascastGroupHelper.setLoopback(true);

        //broadcast the ip of the creater of room
        broacastData = new SerliBroacastData(CREATE_ROOM,roomIP,bundle.getString("playersNum"),roomIP,bundle.getString("planeColor"),bundle.getString("playerName"));
        broacastRooMIPThread = new BroacastLuncherThread(broascastGroupHelper,broacastData.toString());
        broacastRooMIPThread.start();


        broascastGroupHelper.setOnReceiveMsgListener(
                new BroascastGroupHelper.OnReceiveMsgListener() {
                    @Override
                    public void onReceive(BroadCastBaseHelper.BroadCastBaseMsg msg) {

                        Deserializable deserializable = new Deserializable();
                        SerliBroacastData receiveBroacastData = deserializable.deSerliBroacastData(msg.msg);

                        if (receiveBroacastData.getRoomIP().startsWith(roomIP)) {//msg belongs to this Room
                            if (receiveBroacastData.getTag().startsWith(ENTER_ROOM)) {
                                SerliBroacastData sendBroacastData = null;
                                //if the player has entered,disgard the msg

                                Log.e("doit", "WaitClients if "+ENTER_ROOM);

                                if (!playersWait.containsKey(receiveBroacastData.getPlayerIP())) {
                                    //if the color which has not be selected,welcome to meet the player.
                                    if (!playersWait.containsValue(receiveBroacastData.getPlaneColor())) {
                                        sendBroacastData = new SerliBroacastData(WELCOME,roomIP, receiveBroacastData.getPlayersNum(),receiveBroacastData.getPlayerIP(),
                                                receiveBroacastData.getPlaneColor(), receiveBroacastData.getPlayerName());
                                        //put the player to the playsWait who have come.

                                        Log.e("doit","WaitClients if "+ WELCOME + " IP:" + receiveBroacastData.getPlayerIP());


                                        playersWait.put(receiveBroacastData.getPlayerIP(), receiveBroacastData.getPlaneColor());
                                        waitsName.put(receiveBroacastData.getPlayerIP(), receiveBroacastData.getPlayerName());

                                    } else {//the color has been selected,refuse the player.
                                        sendBroacastData = new SerliBroacastData(REFUSE, roomIP,receiveBroacastData.getPlayersNum(), receiveBroacastData.getPlayerIP(),
                                                receiveBroacastData.getPlaneColor(), receiveBroacastData.getPlayerName());
                                    }
                                    broascastGroupHelper.sendMsg(sendBroacastData.toString());
                                }
                            }
                            if (playersWait.size() == playersNum) {

                                broacastReceiveLooperThread.stopThread();//begin to start a new gameMainActivity

                                broacastRooMIPThread.stopThread();//stop the broacast of ip
                                Message message = handler.obtainMessage();
                                message.what = 0x200;

                                String planesColor = new String();
                                String playersIP = new String();
                                String playersName = new String();

                                Iterator itr = playersWait.entrySet().iterator();
                                Map.Entry entry;
                                Log.e("doit","waitersname :"+String.valueOf(waitsName.size())+"playersWait :"+playersWait.size());
                                while(itr.hasNext()) {
                                    entry = (Map.Entry) itr.next();

                                    planesColor += entry.getValue()+"#";
                                    playersIP += entry.getKey()+"#";
                                    playersName += waitsName.get(entry.getKey())+"#";
                                }

                                SerliBroacastData beginData = new SerliBroacastData(
                                        BEGIN+bundle.getString("startNums"), roomIP,bundle.getString("playersNum"), playersIP,planesColor,playersName);
                                Bundle bundleBegin = new Bundle();
                                bundleBegin.putSerializable("begin",beginData);
                                message.setData(bundleBegin);
                                SerliBroacastData a =(SerliBroacastData) bundleBegin.getSerializable("begin");

                                broascastGroupHelper.sendMsg(beginData.toString());

                                broascastGroupHelper.sendMsg(beginData.toString());
                                broascastGroupHelper.sendMsg(beginData.toString());
                                broascastGroupHelper.sendMsg(beginData.toString());

                                handler.sendMessage(message);

                                                                 }
                                                             }

                                                         }
                                                     }

        );

        broacastReceiveLooperThread = new BroacastReceiveLooperThread(broascastGroupHelper);
        broacastReceiveLooperThread.start();
//
//        btnBegin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
////                SerliBroacastData beginData = new SerliBroacastData(BEGIN, roomIP, roomIP,
////                                                                             bundle.getString("planeColor"),bundle.getString("playerName"), "TRUE");
////                                                                     //Tell all players who are waiting to begin
////                broascastGroupHelper.sendMsg(beginData.toString());
////
////                Intent intentBegin = new Intent(WaitClientsActivity.this,GameMainActivity.class);
////                startActivity(intentBegin);
//
//            }
//        });


        }



}
//        int color = Color.RED;
//        if(bundle.getString("planeColor").equals("RED")) color= Color.RED;
//        else if(bundle.getString("planeColor").equals("YELLO")) color = Color.YELLOW;
//            else if(bundle.getString("planeColor").equals("BLUE")) color = Color.BLUE;
//                else if(bundle.getString("planeColor").equals("WHITE")) color = Color.WHITE;