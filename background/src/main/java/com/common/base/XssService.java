package com.common.base;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

/**
 * FileName: XssServer
 * Author: hua
 * Date: 2021/11/11 8:56
 * Description:
 */
public class XssService extends Service {

    private static final String TAG = "XssServer";


    @Override
    public void onCreate() {
        super.onCreate();
        XssTrands.getInstanll().LoggerDebug(TAG, "TcnService onCreate()");
        XssTrands.getInstanll().startWorkThread();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        XssTrands.getInstanll().LoggerDebug(TAG, "TcnService onStartCommand() flags: "+flags+" startId:"+startId);
        flags = Service.START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        XssTrands.getInstanll().LoggerDebug(TAG, "TcnService onBind()");
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        XssTrands.getInstanll().LoggerDebug(TAG, "TcnService onUnbind()");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        XssTrands.getInstanll().LoggerDebug(TAG, "TcnService onDestroy()");
        XssTrands.getInstanll().stopWorkThread();
    }
}
