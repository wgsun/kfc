package com.hua.back.common.act;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.os.IBinder;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.common.base.MsgWhat;
import com.common.base.XssTrands;
import com.common.base.utils.XssData;
import com.hua.back.base.ui.ButtonEditSelectD;
import com.hua.back.common.KeyboardBuilder;
import com.tcn.background.R;


import androidx.annotation.Nullable;

/**
 * FileName: BaseBackActivity
 * Author: hua
 * Date: 2021/8/17 9:34
 * Description:
 */
public abstract class BaseBackActivity extends Activity {
    protected KeyboardBuilder builder;
    protected TextView tvTitle;

    protected void bindKey(int Rid) {
        KeyboardView keyboardView = (KeyboardView) findViewById(Rid);
        if (keyboardView != null) {
            builder = new KeyboardBuilder(this, keyboardView, R.xml.keys_layout);
        }
    }


    protected void registerEditText(EditText ed) {
        if (builder != null)
            builder.registerEditText(ed);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getlayout());

        String msgTitle=getIntent().getStringExtra(XssData.BgTitle);
        tvTitle = findViewById(R.id.tv_title);
        if (!TextUtils.isEmpty(msgTitle)) {
            if (tvTitle != null) {
                tvTitle.setText(msgTitle);
            }
        }
        Button btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    back();
                }
            });
        }
        Button btn_exit = findViewById(R.id.btn_exit);
        if (btn_exit != null) {
            btn_exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent home = new Intent(Intent.ACTION_MAIN);
                    home.addCategory(Intent.CATEGORY_HOME);
                    startActivity(home);
                    XssTrands.getInstanll().hideNavKey(false);
                    finish();
                }
            });
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        if (me.getAction() == MotionEvent.ACTION_DOWN) {  //把操作放在用户点击的时候
            View v = getCurrentFocus();      //得到当前页面的焦点,ps:有输入框的页面焦点一般会被输入框占据
            if (isShouldHideKeyboard(v, me)) { //判断用户点击的是否是输入框以外的区域
                hideKeyboard(v.getWindowToken());   //收起键盘
            }
        }
        return super.dispatchTouchEvent(me);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {  //判断得到的焦点控件是否包含EditText
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],    //得到输入框在屏幕中上下左右的位置
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击位置如果是EditText的区域，忽略它，不收起键盘。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略
        return false;
    }

    public void setBDStates(ButtonEditSelectD bd, String name, String exe, ButtonEditSelectD.ButtonListener listener) {
        if (bd != null) {
            bd.setButtonType(ButtonEditSelectD.BUTTON_TYPE_SELECT_SECOND_INPUT_QUERY);
            bd.setButtonName(name);
            bd.setButtonQueryText(exe);
            bd.setButtonNameTextSize(XssTrands.getInstanll().getFitScreenSize(20));
            bd.setButtonQueryTextColor("#ffffff");
            bd.setButtonDisplayTextColor("#4e5d72");
            bd.setButtonQueryTextSize(XssTrands.getInstanll().getFitScreenSize(20));
            bd.setInputTypeInput(InputType.TYPE_CLASS_NUMBER);
            bd.setButtonListener(listener);
            bd.hideSele2();
            bd.showInput(false);
            bd.showInput2(false);
        }
    }


    /**
     * 获取InputMethodManager，隐藏软键盘
     * @param token
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    public void back() {

        finish();
    }

    public abstract int getlayout();
}
