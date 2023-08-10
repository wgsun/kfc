package com.common.base;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.common.base.utils.XssData;
import com.common.base.utils.XssHandleUtils;

import java.util.Timer;
import java.util.TimerTask;
public class XssControl extends HandlerThread {
    private static final String TAG = "XssControl";


    private static XssConHandler m_xssConHandler = null;
    private static Handler m_serverReciveHandler = null;

    private volatile String m_strTemp = "";


    private Context m_context = null;

    private Timer m_UpdatePayTimer = null;
    private TimerTask m_UpdatePayTimerTask = null;


    public XssControl(Context context, String name) {
        super(name);
        m_context = context;
    }

    public XssControl(String name) {
        super(name);
    }

    @Override
    protected void onLooperPrepared() {
        XssTrands.getInstanll().LoggerDebug(TAG, "onLooperPrepared()");
        initialize();
        super.onLooperPrepared();
    }

    @Override
    public void run() {
        XssTrands.getInstanll().LoggerDebug(TAG, "run()");
        super.run();
    }

    @Override
    public boolean quit() {
        XssTrands.getInstanll().LoggerDebug(TAG, "quit()");
        deInitialize();
        return super.quit();
    }

    private void initialize() {
        m_xssConHandler = new XssConHandler();
        XssTrands.getInstanll().setXssControlHander(m_xssConHandler);
       OnTimer();
    }


    private void OnTimer() {
        String strTime = XssHandleUtils.getTime(XssData.YEAR_HM);
        XssHandleUtils.sendMsgDelayed(m_xssConHandler, MsgWhat.UPDATE_TIME, -1, 30000, null);
        XssTrands.getInstanll().sendMsgToUI(MsgWhat.UPDATE_TIME, -1, -1, -1, strTime);
    }


    public void deInitialize() {
        if (m_xssConHandler != null) {
            m_xssConHandler.removeCallbacksAndMessages(null);
            m_xssConHandler = null;
        }


        m_context = null;
    }


    // 自动更新回调函数

    private class XssConHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MsgWhat.UPDATE_TIME:
                    OnTimer();
                    break;
            }

        }
    }

}