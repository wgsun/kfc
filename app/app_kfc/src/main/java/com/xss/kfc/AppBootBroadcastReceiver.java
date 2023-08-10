package com.xss.kfc;

import android.content.Context;
import android.content.Intent;

import com.common.base.XssTrands;


public class AppBootBroadcastReceiver extends BootBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if ((null == context) || (null == intent)) {
            return;
        }
        XssTrands.getInstanll().LoggerDebug("AppBootBroadcastReceiver",   "onReceive getAction: " + intent.getAction());
        m_context = context.getApplicationContext();
        if (action_boot.equals(intent.getAction())) {
            startApp(m_context);
        }
    }
}

