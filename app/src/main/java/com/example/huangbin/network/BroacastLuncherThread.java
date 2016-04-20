package com.example.huangbin.network;

import android.content.Context;

/**
 * Created by Huangbin on 2016/4/19.
 */
public class BroacastLuncherThread implements Runnable {
    private  volatile boolean stopThread=true;
    private  BroascastGroupHelper mBroacastGrouperHelper;
    private   Context mCoontext;
    private   String mSelfIP;
    private Object mLock=new Object();
    /**
     * @param helper BroascastGroupHelper第一个发起者
     * @param ip     ip一定不能为空
     */
    public BroacastLuncherThread(BroascastGroupHelper helper,String ip) {
       this.mBroacastGrouperHelper=helper;
        this.mSelfIP=ip;
    }

    public  void stopThread(){
        synchronized (mLock) {
            this.stopThread = false;
        }
    }
    @Override
    public void run() {
        synchronized (mLock) {
            while (stopThread) {
                mBroacastGrouperHelper.sendMsg(mSelfIP);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
