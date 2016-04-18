package com.example.junyi.network;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Timer on 2016/4/13.
 */

/**
 * 客户端通信类，负责与服务器的通信，和通过服务器与其他客户端通信
 */
public class Client {
    private InetAddress addr;       //服务器地址
    private int port;               //服务器端口
    private Socket socket = null;
    private ClientReadThread thread = null;
    private PrintWriter out = null;
    MsgQueue msg = new MsgQueue();

    /**
     * 初始化Client并新建一个线程监听服务器发送来的数据
     * @param inet 服务器地址
     * @param p 服务器端口
     * @throws IOException
     */
    Client(InetAddress inet, int p) throws IOException {
        this.addr = inet;
        this.port = p;
        this.socket = new Socket(this.addr, this.port);
        this.thread = new ClientReadThread(this);
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter
                (socket.getOutputStream())), true);
    }

    /**
     * 初始化Client并新建一个线程监听服务器发送来的数据
     * @param s 与服务器连接的socket
     * @throws IOException
     */
    Client(Socket s) throws IOException {
        this.socket = s;
        this.addr = s.getInetAddress();
        this.port = s.getPort();
        this.thread = new ClientReadThread(this);
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter
                (socket.getOutputStream())), true);
    }

    /**
     * 发送数据到服务器（注意，数据不会发送到任何客户端上）
     * @param data 发送的数据
     */
    public void sendToServer(NetMsg data) {
        out.println(data.from + data.data);
    }

    /**
     * 发送数据到所有客户端和服务器
     * @param data 发送的数据
     */
    public void sendToAll(NetMsg data) {
        out.println((data.from|0x80) + data.data);
    }

    /**
     * 获得该客户端消息队列中的数据
     * @return
     */
    public NetMsg getData() {
        return msg.getData();
    }

    public Socket getSocket() {
        return socket;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public InetAddress getAddr() {
        return addr;
    }

    public void setAddr(InetAddress addr) {
        this.addr = addr;
    }


}
