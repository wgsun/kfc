package com.common;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;

import com.common.base.XssTrands;
import com.common.base.service.ComVendService;
import com.common.logger.MuhuaLog;


import java.io.PrintWriter;
import java.io.StringWriter;

import androidx.annotation.Nullable;

public class CashService extends ComVendService {
	private static final String TAG = "CashService";
	private Thread.UncaughtExceptionHandler m_UncaughHandler = null;


	public CashService() {
		super();
	}

	@Override
	public void onCreate() {
		super.onCreate();
/*		String AppName = XssTrands.getInstanll().getAppName(getApplicationContext());
		XssTrands.getInstanll().logd("VendApplication", "AppName: " + AppName);*/
		m_UncaughHandler = new Thread.UncaughtExceptionHandler() {
			public void uncaughtException(Thread thread, Throwable ex) {
				//任意一个线程异常后统一的处理
				StringWriter stringWriter = new StringWriter();
				PrintWriter writer = new PrintWriter(stringWriter);
				ex.printStackTrace(writer); // 打印到输出流
				String exception =stringWriter.toString();

				MuhuaLog.getInstance().LoggerInfo("TcnPay",TAG,"onCreate", "setDefaultUncaughtExceptionHandler exception: "+exception);
				stopSelf();
//				startApp(CashService.this);
			}
		};
		////捕捉异常，并将具体异常信息写入日志中
		Thread.setDefaultUncaughtExceptionHandler(m_UncaughHandler);
		MuhuaLog.getInstance().LoggerDebug("TcnPay",TAG,"onCreate",  "onCreate");

		XssTrands.getInstanll().startWorkThread();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		MuhuaLog.getInstance().LoggerDebug("TcnPay",TAG,"onStartCommand",  "onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		MuhuaLog.getInstance().LoggerDebug("TcnPay",TAG,"onDestroy",  "onDestroy");
		m_UncaughHandler = null;
		Thread.setDefaultUncaughtExceptionHandler(null);
		XssTrands.getInstanll().stopWorkThread();
	}

	//启动应用
	private void startApp(Context context) {
		Intent start = null;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.CUPCAKE) {
			start = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
		}
		context.startActivity(start);
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}



}
