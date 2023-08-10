package com.common.base.utils;

import android.os.Handler;
import android.os.Message;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * FileName: XssUtils
 * Author: hua
 * Date: 2021/8/17 15:38
 * Description:
 */
public class XssHandleUtils {



    public static void removeMessages(Handler h, int what) {
        if (h != null) {
            h.removeMessages(what);
        }
    }

    /**
     * 根据格式获取时间 "yyyyMMddHHmmss"
     * @return
     */
    public static String getTime(String dataFormat) {
        SimpleDateFormat dateFormat=new SimpleDateFormat(dataFormat);
        return dateFormat.format(new Date(System.currentTimeMillis()));
    }


    public static boolean sendMessage(Handler h, int what, int arg1, int arg2, Object obj) {
        if (null == h) {
            return false;
        }
        Message msg = h.obtainMessage();
        msg.what	= what;
        msg.arg1	= arg1;
        msg.arg2	= arg2;
        msg.obj		= obj;
        return h.sendMessage(msg);
    }


    public static boolean sendMessageDelayed(Handler h, int what, int arg1, long delayMillis, Object obj) {
        if (null == h) {
            return false;
        }
        Message msg = h.obtainMessage();
        msg.what	= what;
        msg.arg1	= arg1;
        msg.obj		= obj;
        return h.sendMessageDelayed(msg, delayMillis);
    }

    public static boolean sendMsgDelayed(Handler h, int what, int arg1, long delayMillis, Object obj) {
        if (null == h) {
            return false;
        }

        Message msg = h.obtainMessage();

        msg.what	= what;
        msg.arg1	= arg1;
        msg.obj		= obj;

        return h.sendMessageDelayed(msg, delayMillis);
    }

    public static boolean sendMsgDelayed(Handler h, int what, int arg1, int arg2, long delayMillis, Object obj) {
        if (null == h) {
            return false;
        }

        Message msg = h.obtainMessage();

        msg.what	= what;
        msg.arg1	= arg1;
        msg.arg2	= arg2;
        msg.obj		= obj;

        return h.sendMessageDelayed(msg, delayMillis);
    }

    public static boolean sendMsg(Handler h, int what, int arg1, int arg2, Object obj) {
        if (null == h) {
            return false;
        }

        //h.removeMessages(what);

        Message msg = h.obtainMessage();

        msg.what	= what;
        msg.arg1	= arg1;
        msg.arg2	= arg2;
        msg.obj		= obj;

        return h.sendMessage(msg);
    }

}
