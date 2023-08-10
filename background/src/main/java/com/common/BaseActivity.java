package com.common;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;


/**
 * FileName: BaseActivity
 * Author: hua
 * Date: 2021/8/9 9:10
 * Description:
 */
public class BaseActivity extends Activity {
    private MPermissionHelper permissionHelper;
    PermissInterFace permissInterFace;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         permissInterFace=new PermissInterFace();
        permissionHelper = new MPermissionHelper(this);
        permissionHelper.requestPermission(permissInterFace,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        );
    }

    class  PermissInterFace implements MPermissionHelper.PermissionCallBack{
        @Override
        public void permissionRegisterSuccess(String... permissions) {

        }

        @Override
        public void permissionRegisterError(String... permissions) {

        }
    }



    @Override
    protected void onDestroy() {
        if (permissionHelper != null) {
            permissionHelper.destroy();
        }
        super.onDestroy();
    }

    public void test() {
        Bundle bundle= new Bundle();
        bundle.clear();
        if (permissInterFace != null) {

        }
    }


}
