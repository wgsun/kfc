package com.common.base.service;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;


import com.common.logger.MuhuaLog;

import java.lang.ref.WeakReference;

import androidx.annotation.Nullable;

public class AppVendService extends Service {

    private static final String TAG = "AppVendService";
    private static final int REPLY_MSG_ID = 2;
    private boolean mServiceConnected = false;


    @Override
    public void onCreate() {
        super.onCreate();
        MuhuaLog.getInstance().LoggerInfo("ComponentController", TAG, "onCreate", "onCreate");
    }

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MuhuaLog.getInstance().LoggerInfo("ComponentController", TAG, "onStartCommand", "onStartCommand");
        flags = Service.START_STICKY;
        this.bindService(new Intent(AppVendService.this, ComVendService.class), conn, Context.BIND_IMPORTANT);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MuhuaLog.getInstance().LoggerInfo("ComponentController", TAG, "onDestroy", "onDestroy");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        MuhuaLog.getInstance().LoggerInfo("ComponentController", TAG, "onUnbind", "intent: " + intent);
        return super.onUnbind(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        MuhuaLog.getInstance().LoggerInfo("ComponentController", TAG, "onBind", "intent: " + intent);
        return mMessenger.getBinder();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /*************************    接收Com进程发过来的消息  start           ***************************/


    private final Messenger mMessenger = new Messenger(new ServerServiceHandler(this));


    private static class ServerServiceHandler extends Handler {
        private final WeakReference<AppVendService> mService;

        public ServerServiceHandler(AppVendService service) {
            mService = new WeakReference<AppVendService>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    }

    /*************************    接收Com进程发过来的消息  end           ***************************/

    //用于向Service端发送消息的Messenger
    private Messenger mBoundServiceMessenger = null;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "tcn---AppService onServiceDisconnected name: " + name);
            mServiceConnected = false;
            mBoundServiceMessenger = null;
            AppVendService.this.startService(new Intent(AppVendService.this,
                    ComVendService.class));
            AppVendService.this.bindService(new Intent(AppVendService.this,
                    ComVendService.class), conn, Context.BIND_IMPORTANT);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "tcn---AppService onServiceConnected name: " + name + " service: " + service);
            mServiceConnected = true;
            mBoundServiceMessenger = new Messenger(service);
//            AppToComControl.getInstance().init(mBoundServiceMessenger, true);
        }
    };

    public static void sendAidl(int wath, Bundle mBundle) {
        if (null == mBundle) {
            return;
        }

    }
}
