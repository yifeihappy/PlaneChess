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
    Button btnBegin = null;//click to start the game if all players hava entered.
    //int numplayer = 1;//count the number of players who have entered the room ,init with 1 because owner has entered.
    String roomIP = null;
    SerliBroacastData broacastData = null;

    Map playersWait = new TreeMap();//color and ip which players have selected and entered.

    int playerNum = 4;//the num of player

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0x200)
                btnBegin.setEnabled(true);
            Bundle bundletm = msg.getData();
            Toast.makeText(WaitClientsActivity.this,"Click to begin...",Toast.LENGTH_LONG).show();
            super.handleMessage(msg);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait_clients);

        final Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        playerNum = Integer.parseInt(bundle.getString("playerNum"));

//        int color = Color.RED;
//        if(bundle.getString("planeColor").equals("RED")) color= Color.RED;
//        else if(bundle.getString("planeColor").equals("YELLO")) color = Color.YELLOW;
//            else if(bundle.getString("planeColor").equals("BLUE")) color = Color.BLUE;
//                else if(bundle.getString("planeColor").equals("WHITE")) color = Color.WHITE;

//        owner = new Player(bundle.getString("playerName"),color);
//        playerList.add(owner);

        roomIP = IPAdressHelper.getIPByWifi(this);
        playersWait.put(roomIP,bundle.getCharSequence("planeColor"));//add room creater plane color to colorList.key = playerIP,value = color.

        btnBegin = (Button)findViewById(R.id.btnBegin);
        TextView txtRoomPlayerNum = (TextView)findViewById(R.id.txtRoomPlayerNum);

        txtRoomPlayerNum.setText("飞机起飞点数：" + bundle.getString("startNums") + "\n" + "人数：" +
                bundle.getString("playerNum") + "\n" + "Host Name:" + bundle.getString("playerName") + "\n"
                + "Host Color:" + bundle.getString("planeColor") + "\n" + "ip" + roomIP);

        broascastGroupHelper= new BroascastGroupHelper(30000);
        broascastGroupHelper.joinGroup();
        broascastGroupHelper.setLoopback(true);

        //broadcast the ip of the creater of room
        broacastData = new SerliBroacastData(CREATE_ROOM,roomIP,roomIP,bundle.getString("planeColor"),bundle.getString("playerName"));
        broacastRooMIPThread = new BroacastLuncherThread(broascastGroupHelper,broacastData.toString());
        broacastRooMIPThread.start();


        broascastGroupHelper.setOnReceiveMsgListener(new BroascastGroupHelper.OnReceiveMsgListener() {
                                                         @Override
                                                         public void onReceive(BroadCastBaseHelper.BroadCastBaseMsg msg) {

                                                             Deserializable deserializable = new Deserializable();
                                                             SerliBroacastData receiveBroacastData = deserializable.deSerliBroacastData(msg.msg);

                                                             if (receiveBroacastData.getRoomIP().equals(roomIP)) {
                                                                 //msg belongs to this Room
                                                                 if (receiveBroacastData.getTag().equals(ENTER_ROOM)) {
                                                                     SerliBroacastData sendBroacastData = null;
                                                                     //if the player has entered,disgard the msg
                                                                     Log.e("doit", ENTER_ROOM + " " + receiveBroacastData.getPlaneColor() + " " + receiveBroacastData.getPlayerIP());
                                                                     if (!playersWait.containsKey(receiveBroacastData.getPlayerIP())) {
                                                                         //if the color which has not be selected,welcome to meet the player.
                                                                         if (!playersWait.containsValue(receiveBroacastData.getPlaneColor())) {
                                                                             sendBroacastData = new SerliBroacastData(WELCOME, roomIP, receiveBroacastData.getPlayerIP(),
                                                                                     receiveBroacastData.getPlaneColor(), receiveBroacastData.getPlayerName(), "TRUE");
                                                                             //put the player to the playsWait who have come.
                                                                             Log.e("doit", WELCOME + " " + receiveBroacastData.getPlayerIP() + " comes");
                                                                             playersWait.put(receiveBroacastData.getPlayerIP(), receiveBroacastData.getPlaneColor());

                                                                         } else {//the color has been selected,refuse the player.
                                                                             sendBroacastData = new SerliBroacastData(REFUSE, roomIP, receiveBroacastData.getPlayerIP(),
                                                                                     receiveBroacastData.getPlaneColor(), receiveBroacastData.getPlayerName(), "FALSE");

                                                                             Log.e("doit", REFUSE + " " + receiveBroacastData.getPlaneColor() + " " + receiveBroacastData.getPlayerIP());
                                                                         }

                                                                         broascastGroupHelper.sendMsg(sendBroacastData.toString());

                                                                         //if(playersWait.size() ==2) Log.e("doit","2");
                                                                         //All players hava come,it is able to begin.

                                                                     }
                                                                 }
                                                                 if (playersWait.size() == playerNum) {
                                                                     Message message = handler.obtainMessage();
                                                                     message.what = 0x200;
                                                                     // Bundle bundleCorlorsPlane = new Bundle();
                                                                     // bundleCorlorsPlane.putString("planeColor", receiveBroacastData.getPlaneColor());
                                                                     //  message.setData(bundleCorlorsPlane);
                                                                     handler.sendMessage(message);

                                                                     broacastRooMIPThread.stopThread();//stop the broacast of ip

                                                                     SerliBroacastData beginData = new SerliBroacastData(BEGIN, roomIP, roomIP,
                                                                             bundle.getString("planeColor"),bundle.getString("playerName"), "TRUE");
                                                                     //Tell all players who are waiting to begin
                                                                     broascastGroupHelper.sendMsg(beginData.toString());
                                                                 }
                                                             }

                                                         }
                                                     }

        );

        broacastReceiveLooperThread = new BroacastReceiveLooperThread(broascastGroupHelper);
        broacastReceiveLooperThread.start();

        btnBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentBegin = new Intent(WaitClientsActivity.this,GameMainActivity.class);
                startActivity(intentBegin);

            }
        });


        }



}
