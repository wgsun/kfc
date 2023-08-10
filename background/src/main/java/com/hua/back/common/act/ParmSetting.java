package com.hua.back.common.act;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.common.CustomTimePickerDialog;
import com.common.base.XssTrands;
import com.common.base.utils.XssData;
import com.common.base.utils.XssSavaData;
import com.hua.back.base.ui.ButtonEdit;
import com.hua.back.base.ui.ButtonEditSelectD;
import com.hua.back.base.ui.ButtonSwitch;
import com.tcn.background.R;


import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * FileName: ParmSetting
 * Author: hua
 * Date: 2021/8/16 16:05
 * Description:
 */
public class ParmSetting extends BaseBackActivity {
    private CustomTimePickerDialog mTimePickerDialog;
    String TAG = this.getClass().getSimpleName();
    int singleitem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTimingrebot();
        initreboot();
        initDebugScan();
        setNum();
    }

    @Override
    public int getlayout() {
        return R.layout.menu_parmsetting;
    }

    public void setNum() {
//        ButtonEditSelectD bd_set_Num = (ButtonEditSelectD) findViewById(R.id.bd_set_num);
        ButtonEditSelectD bd_set_Num =null;
        if (bd_set_Num == null) {
            return;
        }
        setBDStates(bd_set_Num, "设置单次重量", "设置", new ButtonEditSelectD.ButtonListener() {
            @Override
            public void onClick(View v, int buttonId) {
                int id = v.getId();
//                if (R.id.bd_set_num == id) {
                if (true) {
                    //0~转发驱动板指令{1,2,#}查询货道      //解释：0代表参数ID。   1,2 表示第一和第二个要传入的参数，#代表要输入的值(如果是设置参数，这个#去掉)
                    if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
//                        String strParamSecond = bd.getButtonEditInputText();
//                        String strParamSecond2 = bd.getButtonEditInput2Text();
                    } else if (ButtonEditSelectD.BUTTON_ID_SELECT_SECOND == buttonId) {
                        String[] foodName = new String[]{"直薯", "鸡块", "波纹"};
                        showSelectDialog(5, "请选择动作地址", bd_set_Num.getButtonEditSecond(), "", foodName);
                    }

                }
            }
        });
    }

    protected void showSelectDialog(final int type, String title, final TextView v, String selectData, final String[] str) {
        if (null == str) {
            return;
        }
        int checkedItem = -1;
        if ((selectData != null) && (selectData.length() > 0)) {
            for (int i = 0; i < str.length; i++) {
                if (str[i].equals(selectData)) {
                    checkedItem = i;
                    break;
                }
            }
        }
        singleitem = 0;

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setSingleChoiceItems(str, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                singleitem = which;
            }
        });
        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                v.setText(str[singleitem]);
            }
        });
        builder.setNegativeButton(getString(R.string.cancle), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();

    }


    public void initTimingrebot() {
        final List<ButtonEdit> listView = new ArrayList<>();
        ButtonSwitch btnTimging = findViewById(R.id.pay_swith_timing);
        btnTimging.setButtonName("回到桌面");
        btnTimging.setVisibility(View.VISIBLE);
        btnTimging.setSwitchState(XssSavaData.getInstance().isTimiingReboot());
        btnTimging.setButtonListener(new ButtonSwitch.ButtonListener() {
            @Override
            public void onSwitched(View v, boolean isSwitchOn) {
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
    /*            XssSavaData.getInstance().savaTimiingReboot(isSwitchOn);
                isShowView(isSwitchOn, listView);*/
            }
        });
        final ButtonEdit parm = (ButtonEdit) findViewById(R.id.pay_parm0_timing);
        parm.setButtonType(ButtonEdit.BUTTON_TYPE_SELECT);
        parm.setButtonName("机器重启时间");
        parm.setButtonText(getTime(XssSavaData.getInstance().getTimingReboot()));
        parm.setButtonListener(new ButtonEdit.ButtonListener() {
            @Override
            public void onClick(View v, int buttonId) {
                showTimePickerDialog(parm);
            }
        });
        listView.add(parm);
        isShowView(XssSavaData.getInstance().isTimiingReboot(), listView);
    }

    public void initreboot() {
        final List<ButtonEdit> listView = new ArrayList<>();
        ButtonSwitch btnTimging = findViewById(R.id.pay_swith_reboot);
        btnTimging.setButtonName("立即重启机器");
        btnTimging.setVisibility(View.VISIBLE);
        btnTimging.setSwitchState(false);
        btnTimging.setButtonListener(new ButtonSwitch.ButtonListener() {
            @Override
            public void onSwitched(View v, boolean isSwitchOn) {
                XssTrands.getInstanll().reboot();
            }
        });

    }

    public void initDebugScan() {
        ButtonSwitch btnTimging = findViewById(R.id.pay_swith_debug_scan);
        btnTimging.setButtonName("开机复位与扫描");
        btnTimging.setVisibility(View.VISIBLE);
        btnTimging.setSwitchState(XssSavaData.getInstance().getData(XssData.REBOOT_SCAN_FUWEI, false));
        btnTimging.setButtonListener(new ButtonSwitch.ButtonListener() {
            @Override
            public void onSwitched(View v, boolean isSwitchOn) {
                XssSavaData.getInstance().savaData(XssData.REBOOT_SCAN_FUWEI, isSwitchOn);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimePickerDialog != null) {
            mTimePickerDialog.deInit();
            mTimePickerDialog = null;
        }
    }


    String getTime(int time) {
        int hour = time / 100;
        int minute = time % 100;
        String strTime = hour > 9 ? hour + "：" : "0" + hour + "：";
        strTime = minute > 9 ? strTime + minute : strTime + "0" + minute;
        return strTime;
    }


    void showTimePickerDialog(final ButtonEdit parm) {
        if (mTimePickerDialog == null) {
            mTimePickerDialog = new CustomTimePickerDialog(this, new CustomTimePickerDialog.TimePickerDialogInterface() {
                @Override
                public void positiveListener() {
                    int hour = mTimePickerDialog.getHour();
                    int minute = mTimePickerDialog.getMinute();
                    String strTime = hour > 9 ? hour + "：" : "0" + hour + "：";
                    strTime = minute > 9 ? strTime + minute : strTime + "0" + minute;
                    XssTrands.getInstanll().LoggerDebug(TAG, "showTimePickerDialog: " + strTime);
                    int time1 = hour * 100 + minute;
                    parm.setButtonText(strTime);
                    XssSavaData.getInstance().savaData(XssData.TimingReboot, time1);

                }

                @Override
                public void negativeListener() {

                }
            });
        }
        mTimePickerDialog.showTimePickerDialog();
    }

    void isShowView(boolean isSwitchOn, List<ButtonEdit> listView) {
        if (isSwitchOn) {
            if (listView.size() > 0) {
                for (int x = 0; x < listView.size(); x++) {
                    ButtonEdit edit = listView.get(x);
                    edit.setVisibility(View.VISIBLE);
                }
            }
        } else {
            if (listView.size() > 0) {
                for (int x = 0; x < listView.size(); x++) {
                    ButtonEdit edit = listView.get(x);
                    edit.setVisibility(View.GONE);
                }
            }
        }
    }

}
