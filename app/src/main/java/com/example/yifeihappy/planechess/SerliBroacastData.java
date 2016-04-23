package com.example.yifeihappy.planechess;

import java.io.Serializable;

/**
 * Created by yifeihappy on 16-4-22.
 */
public class SerliBroacastData implements Serializable{
    private static final long serialVersionUID = 1L;
    private String tag;
    private String roomIP;
    private String playerIP;
    private String planeColor;
    private String playerName;
    private String next;

    public String getTag() {
        return tag;
    }

    public String getRoomIP() {
        return roomIP;
    }

    public String getPlayerIP() {
        return  playerIP;
    }

    public String getPlaneColor() {
        return planeColor;
    }

    public String getNext() {
        return  next;
    }

    public String getPlayerName() {
        return  playerName;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setRoomIP(String roomIP) {
        this.roomIP = roomIP;
    }

    public void setPlayerIP(String playerIP) {
        this.playerIP = playerIP;
    }

    public void setPlaneColor(String planeColor) {
        this.planeColor = planeColor;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public SerliBroacastData(String tag,String roomIP,String playerIP,String planeColor,String playerName,String next) {
        setTag(tag);
        setRoomIP(roomIP);
        setPlayerIP(playerIP);
        setPlaneColor(planeColor);
        setPlayerName(playerName);
        setNext(next);
    }
    public SerliBroacastData(String tag,String roomIP,String playerIP,String planeColor,String playerName) {
        setTag(tag);
        setRoomIP(roomIP);
        setPlayerIP(playerIP);
        setPlaneColor(planeColor);
        setPlayerName(playerName);
        setNext("FALSE");
    }
    public SerliBroacastData() {

    }

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(tag+",");
        stringBuffer.append(roomIP+",");
        stringBuffer.append(playerIP+",");
        stringBuffer.append(planeColor+",");
        stringBuffer.append(playerName+",");
        stringBuffer.append(next);

        return  stringBuffer.toString();
    }
}
