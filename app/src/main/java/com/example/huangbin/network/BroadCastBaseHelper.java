package com.example.huangbin.network;

/**
 * 广播的基类，使用UDP
 * Created by Huangbin on 2016/4/8.
 */
public abstract  class BroadCastBaseHelper {

    /**
     * 接收数据的监听器
     */
    protected BroadCastBaseHelper.OnReceiveMsgListener mListener;

    public void setOnReceiveMsgListener(OnReceiveMsgListener listener){
        this.mListener=listener;
    }

    /**
     * 销毁socket
     */
    public abstract void destory();

    /**
     * 收到数据后，交给监听器的数据结构
     */
    public class BroadCastBaseMsg{
        public String ip;
        public int port;
        public byte[] msg;
    }

    /**
     * 接收到数据的监听器
     */
    public interface OnReceiveMsgListener{
        void onReceive(BroadCastBaseMsg msg);
    }

}
