package com.hua.back.kfc;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hua.back.common.act.BaseBackActivity;

import com.tcn.background.R;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;

/**
 * FileName: KfcBackAct
 * Author: hua
 * Date: 2021/8/31 14:39
 * Description:
 */
public abstract class KfcBackAct extends BaseBackActivity {

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }




    public abstract void setOnClickListener(View view);


}
