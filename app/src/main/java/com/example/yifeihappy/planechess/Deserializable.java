package com.example.yifeihappy.planechess;

import java.util.StringTokenizer;

/**
 * Created by yifeihappy on 16-4-22.
 */
public class Deserializable {
    private StringTokenizer stringTokenizer = null;
    SerliBroacastData serliBroacastData = null;

    SerliBroacastData deSerliBroacastData(byte [] msg) {
        stringTokenizer = new StringTokenizer(new String(msg),",");
        String tag = stringTokenizer.nextToken();
        String roomIP = stringTokenizer.nextToken();
        String playerIP = stringTokenizer.nextToken();
        String planeColor = stringTokenizer.nextToken();
        String playerName = stringTokenizer.nextToken();
        String next = stringTokenizer.nextToken();
        serliBroacastData = new SerliBroacastData(tag,roomIP,playerIP,planeColor,playerName,next);
        return  serliBroacastData;
    }

}
