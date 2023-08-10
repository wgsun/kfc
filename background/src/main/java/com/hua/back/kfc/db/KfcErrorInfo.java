package com.hua.back.kfc.db;

/**
 * FileName: KfcErrorInfo
 * Author: hua
 * Date: 2022/8/17 15:31
 * Description:
 */
public class KfcErrorInfo {
    int key;
    int code;
    int date;
    int hour;
    int status;
    int count;
    String start;
    String end;
    String time;
    int INT_OTHER;
    String OHTERDATA1;
    String OHTERDATA2;

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getINT_OTHER() {
        return INT_OTHER;
    }

    public void setINT_OTHER(int INT_OTHER) {
        this.INT_OTHER = INT_OTHER;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getOHTERDATA1() {
        return OHTERDATA1;
    }

    public void setOHTERDATA1(String OHTERDATA1) {
        this.OHTERDATA1 = OHTERDATA1;
    }

    public String getOHTERDATA2() {
        return OHTERDATA2;
    }

    public void setOHTERDATA2(String OHTERDATA2) {
        this.OHTERDATA2 = OHTERDATA2;
    }

    @Override
    public String toString() {
        return "KfcErrorInfo{" +
                "key=" + key +
                ", code=" + code +
                ", date=" + date +
                ", hour=" + hour +
                ", status=" + status +
                ", count=" + count +
                ", time='" + time + '\'' +
                '}';
    }
}
