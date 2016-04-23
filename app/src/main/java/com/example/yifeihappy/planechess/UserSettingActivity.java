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

import com.example.huangbin.network.BroacastLuncherThread;
import com.example.huangbin.network.BroacastReceiveLooperThread;
import com.example.huangbin.network.BroadCastBaseHelper;
import com.example.huangbin.network.BroascastGroupHelper;
import com.example.huangbin.network.IPAdressHelper;

public class UserSettingActivity extends AppCompatActivity {

    BroascastGroupHelper broascastGroupHelper = null;
    String roomIP = null;
    String mPlayerIP = null;
    BroacastReceiveLooperThread broacastReceiveLooperThread = null;
    BroacastLuncherThread enterRoomThread = null;
    Button btnEnter = null;

    public static final String ENTER_ROOM = "ENTERROOM";
    public static final String CREATE_ROOM = "CREATEROOM";
    public static final String WELCOME = "WELCOME";
    public static final String REFUSE = "REFUSE";
    public static final String BEGIN = "BEGIN";

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle changeRadioBundle = msg.getData();
            String planeColor = changeRadioBundle.getString("planeColor");
            RadioGroup radioGroupColor = (RadioGroup)findViewById(R.id.radiogroupColor);
            btnEnter = (Button)findViewById(R.id.btnEnter);

            //the msg is to answer other players  or the msg is refuse to meet this player
            if(msg.what==0x201||msg.what==0x401) {
                for(int i = 0; i<radioGroupColor.getChildCount(); i++) {
                    RadioButton radioButton = (RadioButton)radioGroupColor.getChildAt(i);
                    //find the selected color ,if the button is the default checked button.
                    if(radioButton.getText().toString().equals(planeColor)&&radioButton.isChecked()) {

                        radioGroupColor.clearCheck();
                        //select a new default checked radiobutton
                        for(int j = (i+1)%4;;j=(++j)%4) {
                            RadioButton radioButtonCheck = (RadioButton)radioGroupColor.getChildAt(j);
                            if(!radioButtonCheck.isChecked()) {
                                radioButtonCheck.setChecked(true);
                                break;
                            }
                        }
                        radioButton.setEnabled(false);

                        break;
                    }

                }
            }


            //the msg is welcome to meet this player, just wait to begin and has no right to select planecolor.
            if(msg.what == 0x200) {
//                for(int i = 0; i<radioGroupColor.getChildCount(); i++) {
//                    RadioButton radioButton = (RadioButton)radioGroupColor.getChildAt(i);
//                    if(radioButton.getText().toString().equals(planeColor)) {
//                        radioButton.setEnabled(false);
//                        Toast.makeText(UserSettingActivity.this,"Waiting to begin.",Toast.LENGTH_LONG).show();
//                    }
//                }
                Toast.makeText(UserSettingActivity.this,"Waiting to begin.",Toast.LENGTH_LONG).show();
                radioGroupColor.setEnabled(false);
                EditText edtName = (EditText)findViewById(R.id.edtName);
                edtName.setEnabled(false);

                btnEnter.setEnabled(false);

            }
            //you need to select again.
            if(msg.what == 0x401) {
                btnEnter.setEnabled(true);
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
        mPlayerIP = IPAdressHelper.getIPByWifi(this);//my IP

        //set the planeColor which the creater of the room has selected enable = false
        RadioGroup radioGroupColor = (RadioGroup)findViewById(R.id.radiogroupColor);
        for(int i = 0; i<radioGroupColor.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton)radioGroupColor.getChildAt(i);
            if(radioButton.getText().toString().equals(planeColor)) {
                radioGroupColor.clearCheck();
                for(int j = (i+1)%4;;j=(++j)%4) {
                    RadioButton radioButtonCheck = (RadioButton)radioGroupColor.getChildAt(j);
                    if(!radioButtonCheck.isChecked()) {
                        radioButtonCheck.setChecked(true);
                        break;
                    }
                }
                radioButton.setEnabled(false);

                break;
            }

        }

        //wait to begin and receive message.
        broascastGroupHelper = new BroascastGroupHelper(30000);
        broascastGroupHelper.joinGroup();
        broascastGroupHelper.setLoopback(true);
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


                    if (serliBroacastData.getTag().equals(WELCOME) || serliBroacastData.getTag().equals(REFUSE)) {
                        changeRadioBundle.putString("planeColor", serliBroacastData.getPlaneColor());
                        message.setData(changeRadioBundle);
                        message.what = 0x201;//the msg is to answer other players

                        //stop the enterRoomThread sending ENTERROOM message.
                        if (serliBroacastData.getPlayerIP().equals(mPlayerIP)) {
                            enterRoomThread.stopThread();

                            if (serliBroacastData.getTag().equals(WELCOME)) {
                                message.what = 0x200;//the msg is welcome to meet this player
                                //Toast.makeText(UserSettingActivity.this,"Waiting",Toast.LENGTH_LONG).show();
                                Log.e("doit", "receive " + WELCOME);
                                enterRoomThread.stopThread();
                            }
                            if (serliBroacastData.getTag().equals(REFUSE)) {
                                message.what = 0x401;//the msg is refuse to meet this player
                            }
                        }

                        handler.sendMessage(message);
                    }

                    if (serliBroacastData.getTag().equals(BEGIN)) {

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
                RadioGroup radioGroupColor = (RadioGroup)findViewById(R.id.radiogroupColor);
                EditText edtName = (EditText)findViewById(R.id.edtName);

                playerName = edtName.getText().toString();
                if(playerName.equals("")) {
                    Toast.makeText(UserSettingActivity.this, "请输入玩家姓名", Toast.LENGTH_SHORT).show();
                    return;
                }

                for(int i = 0; i <radioGroupColor.getChildCount(); i++) {
                    RadioButton r = (RadioButton) radioGroupColor.getChildAt(i);
                    if (r.isChecked()) {
                        planeColor = r.getText().toString();
                        break;
                    }
                }



                SerliBroacastData enterRoomData = new SerliBroacastData(ENTER_ROOM,roomIP,mPlayerIP,planeColor,playerName);
                enterRoomThread = new BroacastLuncherThread(broascastGroupHelper,enterRoomData.toString());
                enterRoomThread.start();
                btnEnter.setEnabled(false);//wait for check


            }
        });

    }
}
