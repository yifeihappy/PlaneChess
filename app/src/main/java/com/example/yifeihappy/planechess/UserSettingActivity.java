package com.example.yifeihappy.planechess;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.PPP.GameMainActivity;
import com.example.huangbin.network.BroacastLuncherThread;
import com.example.huangbin.network.BroacastReceiveLooperThread;
import com.example.huangbin.network.BroadCastBaseHelper;
import com.example.huangbin.network.BroascastGroupHelper;
import com.example.huangbin.network.IPAdressHelper;

public class UserSettingActivity extends AppCompatActivity {

    BroascastGroupHelper broascastGroupHelper = null;
    String roomIP = null;
    String playersNum = null;
    String mPlayerIP = null;
    BroacastReceiveLooperThread broacastReceiveLooperThread = null;
    BroacastLuncherThread enterRoomThread = null;
    Button btnEnter = null;

    public static final String ENTER_ROOM = "EN";//ENTERROOM
    public static final String CREATE_ROOM = "CR";//CREATEROOM
    public static final String WELCOME = "WE";//WELCOME
    public static final String REFUSE = "RE";//REFUSE
    public static final String BEGIN = "BE";//BEGIN


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle changeRadioBundle = msg.getData();
            String planeColor = changeRadioBundle.getString("planeColor");
            RadioGroup radioGroupColor = (RadioGroup)findViewById(R.id.radiogroupColor);
            btnEnter = (Button)findViewById(R.id.btnEnter);

            //the msg is to answer other players  or the msg is refuse to meet this player
            if(msg.what==0x201||msg.what==0x401) {
                RadioButton radioButton = (RadioButton)radioGroupColor.getChildAt(Integer.parseInt(planeColor));
                radioButton.setEnabled(false);
                radioGroupColor.clearCheck();
                for(int j = 0;;j=(++j)%4) {
                    RadioButton radioButtonCheck = (RadioButton)radioGroupColor.getChildAt(j);
                    if(j != Integer.parseInt(planeColor)) {
                        radioButtonCheck.setChecked(true);
                        break;
                    }
                }
            }


            //the msg is welcome to meet this player, just wait to begin and has no right to select planecolor.
            if(msg.what == 0x200) {
                Toast.makeText(UserSettingActivity.this,"Waiting to begin.",Toast.LENGTH_LONG).show();
                //radioGroupColor.setEnabled(false);
                EditText edtName = (EditText)findViewById(R.id.edtName);
                edtName.setEnabled(false);
                for(int i = 0;i<radioGroupColor.getChildCount();i++) {
                    RadioButton radioButton = (RadioButton)radioGroupColor.getChildAt(i);
                    radioButton.setEnabled(false);
                }
                //radioGroupColor.setEnabled(false);
                //btnEnter.setEnabled(false);
            }
            //you need to select again.
            if(msg.what == 0x401) {
                btnEnter.setEnabled(true);
            }

            //start a new activity
            if(msg.what == 0x100) {
                Bundle bundleBegin = msg.getData();
                Intent intentBegin = new Intent(UserSettingActivity.this,GameMainActivity.class);
                intentBegin.putExtras(bundleBegin);
                startActivity(intentBegin);
            }

            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_setting);

        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        SerliBroacastData createRoomData = (SerliBroacastData)bundle.getSerializable(CREATE_ROOM);
        String planeColor = createRoomData.getPlaneColor();//The color the creater of room has selected.
        roomIP = createRoomData.getRoomIP();//　This room IP.
        playersNum = createRoomData.getPlayersNum();//players num
        mPlayerIP = IPAdressHelper.getIPByWifi(this);//my IP

        //set the planeColor which the creater of the room has selected enable = false
        RadioGroup radioGroupColor = (RadioGroup)findViewById(R.id.radiogroupColor);
        RadioButton radioButton = (RadioButton)radioGroupColor.getChildAt(Integer.parseInt(planeColor));
        radioButton.setEnabled(false);
        for(int j = 0;;j=(++j)%4) {
            RadioButton radioButtonCheck = (RadioButton)radioGroupColor.getChildAt(j);
            if(j != Integer.parseInt(planeColor)) {
                radioButtonCheck.setChecked(true);
                break;
            }

        }



        //wait to begin and receive message.
        broascastGroupHelper = new BroascastGroupHelper(30000);
        broascastGroupHelper.joinGroup();
        broascastGroupHelper.setLoopback(false);
        broascastGroupHelper.setOnReceiveMsgListener(new BroascastGroupHelper.OnReceiveMsgListener() {
            @Override
            public void onReceive(BroadCastBaseHelper.BroadCastBaseMsg msg) {
                Deserializable deserializable = new Deserializable();

                Bundle changeRadioBundle = new Bundle();
                SerliBroacastData serliBroacastData = deserializable.deSerliBroacastData(msg.msg);
                //msg belongs to this room

                Log.e("doit", "Receive form room " + serliBroacastData.getTag() + " " + serliBroacastData.getPlaneColor());

                if (serliBroacastData.getRoomIP().equals(roomIP)) {
                    Message message = handler.obtainMessage();


                    if (serliBroacastData.getTag().startsWith(WELCOME) || serliBroacastData.getTag().startsWith(REFUSE)) {
                        changeRadioBundle.putString("planeColor", serliBroacastData.getPlaneColor());
                        message.setData(changeRadioBundle);
                        message.what = 0x201;//the msg is to answer other players

                        //stop the enterRoomThread sending ENTERROOM message.
                        if (serliBroacastData.getPlayerIP().equals(mPlayerIP)) {
                            enterRoomThread.stopThread();

                            if (serliBroacastData.getTag().startsWith(WELCOME)) {
                                message.what = 0x200;//the msg is welcome to meet this player
                                //Toast.makeText(UserSettingActivity.this,"Waiting",Toast.LENGTH_LONG).show();
                                Log.e("doit", "receive " + WELCOME);
                            }
                            if (serliBroacastData.getTag().startsWith(REFUSE)) {
                                message.what = 0x401;//the msg is refuse to meet this player
                            }
                        }

                        handler.sendMessage(message);
                    }

                    if (serliBroacastData.getTag().startsWith(BEGIN)) {

                        broacastReceiveLooperThread.stopThread();//begin to start a new gameMainActivity

                        message.what = 0x100;
                        Bundle bundleBegin = new Bundle();
                        bundleBegin.putSerializable("begin",serliBroacastData);
                        message.setData(bundleBegin);
                        handler.sendMessage(message);
                        Log.e("doit", "Receive BEGIN from room");


                    }

                }

            }
        });

        broacastReceiveLooperThread = new BroacastReceiveLooperThread(broascastGroupHelper);
        broacastReceiveLooperThread.start();

        btnEnter = (Button)findViewById(R.id.btnEnter);
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String playerName = null;
                String planeColor = null;
                RadioGroup radioGroupColor = (RadioGroup) findViewById(R.id.radiogroupColor);
                EditText edtName = (EditText) findViewById(R.id.edtName);

                playerName = edtName.getText().toString();
                if (playerName.equals("")) {
                    Toast.makeText(UserSettingActivity.this, "请输入玩家姓名", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (int i = 0; i < radioGroupColor.getChildCount(); i++) {
                    RadioButton r = (RadioButton) radioGroupColor.getChildAt(i);
                    if (r.isChecked()) {
                        planeColor = String.valueOf(i);
                        break;
                    }
                }


                SerliBroacastData enterRoomData = new SerliBroacastData(ENTER_ROOM, roomIP, playersNum,mPlayerIP, planeColor, playerName);
                enterRoomThread = new BroacastLuncherThread(broascastGroupHelper, enterRoomData.toString());
                enterRoomThread.start();
                btnEnter.setEnabled(false);//wait for check


            }
        });

    }
}
