package com.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.common.base.XssTrands;

public class HuaBroadReceiver extends BroadcastReceiver {
    private static final String TAG = "TcnMediaReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Uri uri = intent.getData();
        String path = null;
        if ((null == action) || (null == uri)) {
            XssTrands.getInstanll().LoggerError(TAG, "onReceive Error: action or uri is null");
            return;
        }
        XssTrands.getInstanll().LoggerDebug(TAG, "onReceive uri: " + uri + "  uri.getScheme(): " + uri.getScheme());
        if (uri.getScheme().equals("file")) {
            path = uri.getPath();
        }
        XssTrands.getInstanll().LoggerDebug(TAG,
                "TcnMediaReceiver intent.getAction(): " + intent.getAction());
        if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {    //  file:///storage/68AE-0A7B
//			if (TcnShareUseData.getInstance().getOpenCopyLog()) {

//			}


        } else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {

        } else if (action.equals(Intent.ACTION_MEDIA_SCANNER_STARTED)) {

        } else if (action.equals(intent.ACTION_MEDIA_REMOVED) ||
                action.equals(intent.ACTION_MEDIA_BAD_REMOVAL) || action.equals(intent.ACTION_MEDIA_EJECT)) {

        } else if (action.equals(Intent.ACTION_MEDIA_SCANNER_FINISHED)) {

        } else {
            XssTrands.getInstanll().LoggerError(TAG, "unknown action:(" + action + ")");
        }
    }

}
