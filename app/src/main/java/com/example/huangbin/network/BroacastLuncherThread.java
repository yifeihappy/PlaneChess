package com.example.huangbin.network;

import android.content.Context;
import android.util.Log;

/**
 * Created by Huangbin on 2016/4/19.
 */
public class BroacastLuncherThread extends Thread {
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
                Log.e("doit","broast ip"+mSelfIP);
                try {
                    mLock.wait(1000);
                    //Thread.sleep(1000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
