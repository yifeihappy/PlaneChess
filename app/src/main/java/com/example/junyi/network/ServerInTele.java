package com.example.junyi.network;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;

/**
 * Created by Timer on 2016/4/17.
 */

/**
 * 保存网络信息的类
 * data是数据信息
 * from表示从哪个玩家发来的信息
 */
class NetMsg {
    String data;
    byte from;
    public NetMsg(String s, byte c) {
        this.data = s;
        this.from = c;
    }
}

/**
 * 管理接收到的信息的队列
 */
class MsgQueue {
    LinkedList<NetMsg> mq;

    public MsgQueue() {
        mq = new LinkedList<>();
    }

    /**
     * 判断队列是否为空
     * @return
     * true：为空
     * false：不为空
     */
    public boolean isempty() {
        return mq.isEmpty();
    }

    /**
     * 从队列中获取数据，当队列中没有信息时会阻塞，直到收到新的数据
     * @return
     * 返回队列中最先要处理的数据
     */
    public NetMsg getData() {
        synchronized (mq) {
            if(mq.isEmpty()) {
                try {
                    //如果mq为空，释放mq锁，并进入阻塞
                    mq.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return mq.pollFirst();
        }
    }

    /**
     * 每当接收到数据时，把数据加入到队列
     * @param data：要加入的数据
     */
    public void addData(NetMsg data) {
        synchronized (mq) {
            mq.addLast(data);
            mq.notify();
        }
    }
}

/**
 * 在手机上的服务器通信类，存储了一个房间里四个玩家（电脑也算玩家的话）的网络连接
 * 其中index为0是表示房主，网络连接的数据为空
 * 如果index为2的玩家是电脑玩家，则网络连接数据为空
 */
public class ServerInTele {
    static final int PORT = 1188;
    protected ServerSocket server = null;
    protected Integer n = 0;
    protected Socket clientsocket[] = new Socket[4];
    protected PrintWriter out[] = new PrintWriter[4];
    protected ServerReadThread thread[] = new ServerReadThread[4];
    protected Thread accptthread = null;
    MsgQueue msg = new MsgQueue();

    protected void init() {
        for(int i=0; i<4; i++) {
            clientsocket[i] = null;
        }
        out[0] = null;
        thread[0] = null;
        n = 1;
    }

    public ServerInTele(ServerSocket server) {
        this.server = server;
        init();
    }

    /**
     * 会新建ServerSocket
     * @throws IOException
     */
    public ServerInTele() throws IOException {
        server = new ServerSocket(PORT);
        init();
    }

    /**
     * 新建一个线程监听客户端发来的连接请求，每当有连接建立，会新建一个线程监听发送来的数据
     * 并将一条from为0x60，data为n的数据加入消息队列
     */
    public void accept() {
        accptthread = new Thread() {
            @Override
            public void run() {
                try {
                    while (n < 4) {
                        if (this.isInterrupted()) {
                            throw new InterruptedException("accept thread has been interrupter");
                        }
                        synchronized (n) {
                            clientsocket[n] = server.accept();
                            out[n] = new PrintWriter(new BufferedWriter(new OutputStreamWriter
                                    (clientsocket[n].getOutputStream())), true);
                            thread[n] = new ServerReadThread(ServerInTele.this, clientsocket[n]);
                            NetMsg m = new NetMsg(n.toString(), (byte) 0x60);
                            msg.addData(m);
                            n++;
                        }
                    }
                } catch (IOException e) {
                    if (e instanceof SocketException) {
                        //Socket is not bound or Socket output is shutdown orSocket input is shutdown
                        if ("Socket output is shutdown".equals(e.getMessage())) {
                            close(n--);
                        } else if ("Socket input is shutdown".equals(e.getMessage())) {
                            close(n--);
                        } else {
                            //socket未连接
                        }
                    }
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    this.interrupt();
                }
            }
        };
        accptthread.start();
    }

    /**
     * 添加一个电脑玩家，它的网络连接信息为空
     * @return
     * true：成功
     * false：失败
     */
    public boolean addAi() {
        synchronized (n) {
            if(n<4) {
                clientsocket[n] = null;
                out[n] = null;
                thread[n] = null;
                n++;
                if(n==4) {
                    accptthread.interrupt();
                }
                return true;
            }
            return false;
        }
    }

    /**
     * 向指定index的玩家发送数据
     * @param index 指定玩家索引
     * @param data 发送的数据
     */
    public void send(int index, NetMsg data) {
        if(index != 0){
            if (clientsocket[index] != null) {
                out[index].println(data.from + data.data);
            }
        }
    }

    /**
     * 给所有玩家发送数据
     * @param data 发送的数据
     */
    public void sendToAll(NetMsg data) {
        for(int i = 1; i<n; i++) {
            if(clientsocket[i] != null) {
                out[i].println(data.from + data.data);
            }
        }
    }

    /**
     * 获得队列中接受到的数据
     * @return
     */
    public NetMsg getData() {
        return msg.getData();
    }

    /**
     * 关闭指定玩家的网络连接
     * @param index 指定玩家的索引
     */
    public void close(int index){
        out[index] = null;
        if(thread[index] != null) {
            thread[index].interrupt();
        }
        try {
            clientsocket[index].close();
        } catch (IOException e) {
            //关闭连接失败，或者连接已经关闭
            clientsocket[index] = null;
            e.printStackTrace();
        }
    }

    /**
     * 关闭所有玩家的网络连接
     * @throws IOException
     */
    public void closeAll(){
        for (int index = 1; index < n; index++) {
            out[index] = null;
            if(thread[index] != null) {
                thread[index].interrupt();
            }
            try {
                clientsocket[index].close();
            } catch (IOException e) {
                //关闭连接失败，或者连接已经关闭
                clientsocket[index] = null;
                e.printStackTrace();
            }
        }
        try {
            server.close();
        } catch (IOException e) {
            //关闭客户端失败，或者客户端已经关闭
            server = null;
            e.printStackTrace();
        }
    }
}
