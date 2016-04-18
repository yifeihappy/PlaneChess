package com.example.junyi.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by Timer on 2016/4/13.
 */

/**
 * 服务器监听接收数据的线程
 */
public class ServerReadThread extends Thread {
    private ServerInTele server = null;     //手机服务器的引用
    private Socket socket = null;           //对应的客户端的socket
    private BufferedReader in = null;       //对应socket的input流

    ServerReadThread(ServerInTele ser, Socket s) throws IOException {
        this.server = ser;
        this.socket = s;
        this.in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        this.start();
    }

    @Override
    public void run() {
        try {
            while(true) {
                String str = in.readLine();
                byte ch = (byte) str.charAt(0);
                String info = str.substring(1);
                NetMsg m = new NetMsg(info, (byte) (ch&0x03));
                synchronized (server.msg) {
                    server.msg.addData(m);
                }
                if((ch&0x80) != 0x00) {
                    server.sendToAll(m);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
