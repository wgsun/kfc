package com.common.base.service;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;


import com.common.logger.MuhuaLog;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;

public class ComVendService extends Service {
	private static final String TAG = "ComVendService";



	@Override
	public void onCreate() {
		super.onCreate();
		Context mContext = getApplicationContext();
		CrashHandlerCom.getInstance().init(mContext);
		MuhuaLog.getInstance().LoggerInfo("base",TAG,"onCreate 11","  getAppName : "+getAppName(mContext));
	}

	@SuppressLint("WrongConstant")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		MuhuaLog.getInstance().LoggerInfo("base",TAG,"onStartCommand", "onStartCommand");
//		flags = Service.START_STICKY;
		this.bindService(new Intent(ComVendService.this, AppVendService.class), conn, Context.BIND_IMPORTANT);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
//		m_UncaughHandler = null;
		Thread.setDefaultUncaughtExceptionHandler(null);
		MuhuaLog.getInstance().LoggerInfo("base",TAG,"onDestroy", "onDestroy");
	}

	@Override
	public boolean onUnbind(Intent intent) {
		MuhuaLog.getInstance().LoggerInfo("base",TAG,"onUnbind", "intent: "+intent);
		return super.onUnbind(intent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		MuhuaLog.getInstance().LoggerInfo("base",TAG,"onBind", "intent: "+intent);
		return mMessenger.getBinder();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Context mContext = getApplicationContext();
//		CC.obtainBuilder("ComponentBoard").setContext(mContext).setActionName("init").build().call();
	}

	/*************************    接收APP进程发过来的消息  start           ***************************/
	private final Messenger mMessenger = new Messenger(new ComServiceHandler(this));

	private static class ComServiceHandler extends Handler {
		private final WeakReference<ComVendService> mService;
		public ComServiceHandler(ComVendService service){
			mService = new WeakReference<ComVendService>(service);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Log.i(TAG,"tcn---ComVendService handleMessage APP_SEND_SELECT_GOODS "+" what: "+msg.what+" currentThread: "+Thread.currentThread()+" msg: "+msg.getData().getString("msg"));

		}
	}


	/*************************    接收APP进程发过来的消息  end           ***************************/



	//用于向Service端发送消息的Messenger
	private Messenger mBoundServiceMessenger = null;
	private ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i(TAG,"tcn---ComVendService onServiceDisconnected name: "+name);
		/*	mBoundServiceMessenger = null;
			ComVendService.this.startService(new Intent(ComVendService.this,
					AppVendService.class));
			ComVendService.this.bindService(new Intent(ComVendService.this,
					AppVendService.class), conn, Context.BIND_IMPORTANT);*/
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i(TAG,"tcn---ComVendService onServiceConnected name: "+name+" service: "+service);
			mBoundServiceMessenger = new Messenger(service);
//			ComToAppControl.getInstance().init(mBoundServiceMessenger,true);
		}
	};


	public String getAppName(Context context)
	{
		int pid = android.os.Process.myPid(); // Returns the identifier of this process
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List list = activityManager.getRunningAppProcesses();
		Iterator i = list.iterator();
		while (i.hasNext())
		{
			ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
			try
			{
				if (info.pid == pid)
				{
					// 根据进程的信息获取当前进程的名字
					return info.processName;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		// 没有匹配的项，返回为null
		return null;
	}
}
