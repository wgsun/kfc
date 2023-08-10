package com.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.common.logger.MuhuaLog;


public class BootBroadcastReceiver extends BroadcastReceiver {

    protected static final String action_boot = "android.intent.action.BOOT_COMPLETED";
    private static final int START_COMMAND = 1;
    private Context m_context = null;


    @Override
    public void onReceive(Context context, Intent intent) {
        if ((null == context) || (null == intent)) {
            return;

        }
        MuhuaLog.getInstance().LoggerDebug("ComponentBoard", "BootBroadcastReceiver", "onReceive", "getAction: " + intent.getAction());
        m_context = context.getApplicationContext();
        if (action_boot.equals(intent.getAction())) {
            startApp(m_context);

//            //启动服务与主板进行通讯
//            Intent intentVend = new Intent(m_context, ComVendService.class);
//            m_context.startService(intentVend);
//
//            Intent m_intent_Service = new Intent(m_context, AppVendService.class);

        }


    }



    //启动应用
    private void startApp(Context context) {
        //            m_context.startService(m_intent_Service);

        Intent start = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.CUPCAKE) {
            start = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        }
        context.startActivity(start);
    }
}
