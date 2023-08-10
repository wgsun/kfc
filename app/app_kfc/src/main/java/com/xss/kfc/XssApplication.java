package com.xss.kfc;

import android.app.Activity;
import android.app.Application;
import android.content.Context;


import com.hua.back.kfc.base.YumControl;

import java.util.List;

import androidx.multidex.MultiDex;

/**
 * Created by 10295 on 2017/12/5 0005
 */

public class XssApplication extends Application {

    private Activity m_MainAct = null;
    private Activity m_CurrentAct = null;
    private List<Activity> activities;
    protected static Context context;


    @Override
    public void onCreate() {
        super.onCreate();
        YumControl.getInstall().init(this);
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

}
