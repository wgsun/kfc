package com.common;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;

import com.common.logger.MuhuaLog;


/**
 * FileName: CompreService
 * Author: hua
 * Date: 2021/11/18 10:56
 *
 * Description:
 */
public class CompreService  extends Service {
    private static final String TAG = "CompreService";

    @Override
    public void onCreate() {
        super.onCreate();
        }

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MuhuaLog.getInstance().LoggerInfo("base",TAG,"onStartCommand", "onStartCommand");


//		flags = Service.START_STICKY;
//		this.bindService(new Intent(ComVendService.this, AppVendService.class), conn, Context.BIND_IMPORTANT);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MuhuaLog.getInstance().LoggerInfo("base",TAG,"onDestroy", "onDestroy");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        MuhuaLog.getInstance().LoggerInfo("base",TAG,"onUnbind", "intent: "+intent);
        return super.onUnbind(intent);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    @Override
    public IBinder onBind(Intent intent) {
        MuhuaLog.getInstance().LoggerInfo("base",TAG,"onBind", "onBind: "+intent);

        return null;
    }


}
