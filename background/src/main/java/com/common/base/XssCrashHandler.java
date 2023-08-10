package com.common.base;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


import com.common.logger.MuhuaLog;

import java.io.PrintWriter;
import java.io.StringWriter;

public class XssCrashHandler implements Thread.UncaughtExceptionHandler {
    public static final String TAG = "TcnCrashHandler";

    //系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    //CrashHandler实例
    private static XssCrashHandler INSTANCE = new XssCrashHandler();
    //程序的Context对象
    private Context mContext;

    /** 保证只有一个CrashHandler实例 */
    public XssCrashHandler() {
    }

    /** 获取CrashHandler实例 ,单例模式 */
    public static XssCrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        if (null == mContext) {
            mContext = context;
            //获取系统默认的UncaughtException处理器
            mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
            //设置该CrashHandler为程序的默认处理器
            Thread.setDefaultUncaughtExceptionHandler(this);
        }
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        ex.printStackTrace(writer); // 打印到输出流
        String exception =stringWriter.toString();
        MuhuaLog.getInstance().LoggerError("ComponentController",TAG,"uncaughtException", "exception: "+exception);
        if (!handleException(ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {

            }
            //退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
            if (mContext != null) {
                mContext.startService(new Intent(mContext, XssCrashHandler.class));
            }
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        //使用Toast来显示异常信息
        Toast.makeText(mContext, "ComponentController handleException ex: "+ex, Toast.LENGTH_SHORT).show();

        return true;
    }
}
