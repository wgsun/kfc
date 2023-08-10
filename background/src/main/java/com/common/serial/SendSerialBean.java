package com.common.serial;

/**
 * FileName: SendSerialBean
 * Author: hua
 * Date: 2021/9/9 16:11
 * Description:
 */
public class SendSerialBean {
    private long time;
    private String data;
    private int cmd;
    private int type;
    public int againCount;
    public long oneTime;


    public SendSerialBean( String data, int cmd, int type) {
        this.data = data;
        this.type = type;
        this.cmd = cmd;
        againCount=0;
    }

    public long getOneTime() {
        return oneTime;
    }

    public void setOneTime(long oneTime) {
        this.oneTime = oneTime;
    }

    public int getAgainCount() {
        return againCount;
    }

    public void setAgainCount(int againCount) {
        this.againCount = againCount;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public long getTime() {
        return time;
    }


    public void setTime(long time) {
        this.time = time;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


}
