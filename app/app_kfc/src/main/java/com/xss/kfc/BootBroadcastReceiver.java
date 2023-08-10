package com.xss.kfc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 描述： 849877446@qq.com
 * 作者：muhua
 */
public class BootBroadcastReceiver extends BroadcastReceiver {

    protected static final String action_boot = "android.intent.action.BOOT_COMPLETED";
    private static final int START_COMMAND = 1;
    protected Context m_context = null;



    @Override



    public void onReceive(Context context, Intent intent) {

        if ((null == context) || (null == intent)) {
            return;
        }

    }

    private long CalculationTime(String beginTiem, String endTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
        long hours = 0;

        try {


            Date begin = simpleDateFormat.parse(beginTiem);
            Date end = simpleDateFormat.parse(endTime);
            long diff = end.getTime() - begin.getTime();
            hours = diff / (1000 * 60 * 60);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return hours;
    }


    //启动应用
    protected void startApp(Context context) {

        Intent start = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.CUPCAKE) {
            start = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        }
        context.startActivity(start);

    }
}

