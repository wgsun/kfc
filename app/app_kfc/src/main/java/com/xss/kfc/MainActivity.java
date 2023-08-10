package com.xss.kfc;

import android.os.Bundle;
import android.util.DisplayMetrics;


import com.common.base.XssTrands;
import com.hua.back.kfc.base.YumControl;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    ServingUi servingUi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        getYumControl().openCashService(this);
        servingUi = ServingUi.getInstall();
        servingUi.onCreate(this);
        DisplayMetrics dm = getResources().getDisplayMetrics();

        int screenHeight = dm.heightPixels;
        int screenw = dm.widthPixels;
//        XssTrands.getInstanll().hideNavKey(true);
        XssTrands.getInstanll().LoggerDebug("MainAct", "onCreate: " + screenHeight + " " + screenw);
//        startActivity(new Intent(MainActivity.this, MeauSetting.class));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        XssTrands.getInstanll().LoggerDebug("MainAct", "onDestroy: ");
        if (servingUi != null)
            servingUi.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (servingUi != null)
            servingUi.onResume();
        XssTrands.getInstanll().LoggerDebug("MainAct", "onResume: ");

    }

    @Override
    protected void onPause() {
        super.onPause();
        XssTrands.getInstanll().LoggerDebug("MainAct", "onPause: ");
        if (servingUi != null)
            servingUi.onPause();
    }
    public YumControl getYumControl() {
        return   YumControl.getInstall();
    }

}
