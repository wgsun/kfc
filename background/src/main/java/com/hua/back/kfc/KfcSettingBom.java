package com.hua.back.kfc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.common.base.MsgWhat;
import com.common.base.XssEventInfo;
import com.common.base.XssTrands;
import com.common.base.utils.ToastUtils;
import com.common.base.utils.XssData;
import com.common.base.utils.XssSavaData;
import com.common.base.utils.XssUtility;
import com.common.serial.kfc.KfcBomInfo;
import com.common.serial.kfc.KfcPortControl;
import com.hua.back.base.ui.ButtonEditSelectD;
import com.hua.back.common.act.BaseBackActivity;

import com.tcn.background.R;


import java.util.HashMap;

import androidx.annotation.Nullable;

/**
 * FileName: KfcSetting
 * Author: hua
 * Date: 2021/9/10 15:00
 * Description:k
 */
public class KfcSettingBom extends BaseBackActivity {
    private ButtonEditSelectD bd_action;
    private ButtonEditSelectD bd_set_parm;
    private ButtonEditSelectD bd_query_parm;
    public TextView tvMsg;
    private ButtonEditSelectD bd_querystate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        XssTrands.getInstanll().registerListener(m_vendListener);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        XssTrands.getInstanll().unregisterListener(m_vendListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        logx("onResume");
        KfcPortControl.getInstall().setShipUI(false);

    }

    @Override
    protected void onPause() {
        super.onPause();
        logx("onPause");
        KfcPortControl.getInstall().setShipUI(true);

    }

    @Override
    public int getlayout() {
        return R.layout.pram_setting_kfc_bom;
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.serial_clean) {
                XssTrands.getInstanll().cleanFault2();
            } else if (v.getId() == R.id.btn_ship) {
           /*     String data = edSlot.getText().toString();
                if (XssUtility.isDigital(data)) {
                    XssTrands.getInstanll().ship(Integer.parseInt(data), "00", edData.getText().toString());
                }*/
            } else if (v.getId() == R.id.serial_drive_info) {
                XssTrands.getInstanll().queryDriveInfo2();

            } else if (v.getId() == R.id.serial_drive_setting) {
            } else if (v.getId() == R.id.serial_query) {
                XssTrands.getInstanll().queryStatus2("00");
            }
        }
    };


    public void setTv(String msg) {
        if (tvMsg == null) {
            return;
        }
        msg = msg.toUpperCase();
        SpannableString spanString = new SpannableString(msg);
//再构造一个改变字体颜色的Span
        ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor("#00ff00"));
        ForegroundColorSpan span1 = new ForegroundColorSpan(Color.parseColor("#ff0000"));
//将这个Span应用于指定范围的字体
        spanString.setSpan(span, 6, 8, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        if (msg.length() > 18) {
            spanString.setSpan(span1, 12, msg.length() - 6, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
//设置给EditText显示出来
        tvMsg.setText(spanString);
    }


    public void init() {
        findViewById(R.id.serial_clean).setOnClickListener(mClickListener);
        findViewById(R.id.serial_drive_info).setOnClickListener(mClickListener);
        findViewById(R.id.serial_drive_setting).setOnClickListener(mClickListener);
        findViewById(R.id.serial_query).setOnClickListener(mClickListener);

        tvMsg = findViewById(R.id.tv_msg);

        bd_querystate = (ButtonEditSelectD) findViewById(R.id.bd_querystate);
        setBDStates(bd_querystate, "查询状态（0x00）", "查询");


        bd_action = (ButtonEditSelectD) findViewById(R.id.bd_action);
        setBDStates(bd_action, "执行动作命令（0x05）", "执行");

        bd_query_parm = (ButtonEditSelectD) findViewById(R.id.bd_query_parm);
        setBDStates(bd_query_parm, "查询参数（0x03）", "设置");

        bd_set_parm = (ButtonEditSelectD) findViewById(R.id.bd_set_parm);
        setBDStates(bd_set_parm, "设置参数（0x04）", "设置");
    }


    public void setBDStates(ButtonEditSelectD bd, String name, String exe) {
        if (bd != null) {
            bd.setButtonType(ButtonEditSelectD.BUTTON_TYPE_SELECT_SECOND_INPUT_QUERY);
            bd.setButtonName(name);
            bd.setButtonQueryText(exe);
            bd.setButtonNameTextSize(XssTrands.getInstanll().getFitScreenSize(20));
            bd.setButtonQueryTextColor("#ffffff");
            bd.setButtonDisplayTextColor("#4e5d72");
            bd.setButtonQueryTextSize(XssTrands.getInstanll().getFitScreenSize(16));
            bd.setInputTypeInput(InputType.TYPE_CLASS_NUMBER);
            bd.setButtonListener(m_ButtonEditClickListener);
            bd.hideSele2();
            bd.showInput(false);
            bd.showInput2(false);
        }
    }

    String[] aar_action, aar_parm;
    protected ButtonEditClickListener m_ButtonEditClickListener = new ButtonEditClickListener();

    protected class ButtonEditClickListener implements ButtonEditSelectD.ButtonListener {
        @Override
        public void onClick(View v, int buttonId) {
            if (null == v) {
                return;
            }
            int id = v.getId();
            if (R.id.bd_querystate == id) {
                if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
                    int local = getLocal(bd_querystate);
                    XssTrands.getInstanll().queryStatus2(XssUtility.getnumTwo(local + ""));
                } else if (ButtonEditSelectD.BUTTON_ID_SELECT_SECOND == buttonId) {
                    String[] aAar = new String[]{"0~无下料", "1~下料中", "2~下料完成并等待抓取",};
                    showSelectDialog(0, "下料状态", bd_querystate.getButtonEditSecond(), "", aAar);
                }
            }
            if (R.id.bd_action == id) {
                //0~转发驱动板指令{1,2,#}查询货道      //解释：0代表参数ID。   1,2 表示第一和第二个要传入的参数，#代表要输入的值(如果是设置参数，这个#去掉)
                if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
                    startParm(5, bd_action);

                } else if (ButtonEditSelectD.BUTTON_ID_SELECT_SECOND == buttonId) {
                    if (aar_action == null)
                        aar_action = getResources().getStringArray(R.array.kfc_action_bom);
                    showSelectDialog(5, "请选择动作地址", bd_action.getButtonEditSecond(), "", aar_action);
                } else if (ButtonEditSelectD.BUTTON_ID_SELECT == buttonId) {
                    showSelectDialog(-1, setActionhint, bd_action.getButtonEdit(), "", getActAar(setActionType));
                }

            } else if (R.id.bd_set_parm == id) {
                //0~转发驱动板指令{1,2,#}查询货道      //解释：0代表参数ID。   1,2 表示第一和第二个要传入的参数，#代表要输入的值(如果是设置参数，这个#去掉)
                if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
                    startParm(4, bd_set_parm);
                } else if (ButtonEditSelectD.BUTTON_ID_SELECT_SECOND == buttonId) {
                    if (aar_parm == null)
                        aar_parm = getResources().getStringArray(R.array.kfc_set_parm_bom);
                    showSelectDialog(4, "请选择参数地址", bd_set_parm.getButtonEditSecond(), "", aar_parm);
                } else if (ButtonEditSelectD.BUTTON_ID_SELECT == buttonId) {
                    if (aar_parm == null)
                        aar_parm = getResources().getStringArray(R.array.kfc_set_parm_bom);
                    showSelectDialog(-1, setParmhint, bd_set_parm.getButtonEdit(), "", getsetParmAar(setParmType));
                }

            } else if (R.id.bd_query_parm == id) {
                //0~转发驱动板指令{1,2,#}查询货道      //解释：0代表参数ID。   1,2 表示第一和第二个要传入的参数，#代表要输入的值(如果是设置参数，这个#去掉)
                if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
                    startParm(3, bd_query_parm);
                } else if (ButtonEditSelectD.BUTTON_ID_SELECT_SECOND == buttonId) {
                    if (aar_parm == null)
                        aar_parm = getResources().getStringArray(R.array.kfc_set_parm_bom);
                    showSelectDialog(-1, "请选择参数地址", bd_query_parm.getButtonEditSecond(), "", aar_parm);
                }

            } else {

            }
        }
    }


    int singleitem = 0;

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
                if (type == 4) {
                    int local = getLocal(bd_set_parm);
                    switch (local) {
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                            setParmType = local;
                            bd_set_parm.showInput(false);
                            bd_set_parm.showSele2("设置荤素", "设置荤素");
                            setParmhint = "0素，1荤";
                            break;
                        default:
                            bd_set_parm.hideSele2();
                            bd_set_parm.setHint("参数值");
                            bd_set_parm.showInput2(false);

                            break;
                    }
                } else if (type == 5) {
                    int local = getLocal(bd_action);
                    setActionType = local;
                    bd_action.showInput(true);
                    bd_action.showInput2(false);
                    bd_action.hideSele2();

                    switch (local) {
                        case 18:
                        case 19:
                        case 20:
                        case 22:
                            bd_action.showInput(false);
                            bd_action.showSele2("位置选择", "油炸位置");
                            setActionhint = "位置选择";
                            break;
                        case 9:
                        case 10:
                            bd_action.hideSele2();
                            bd_action.showInput(false);
                            bd_action.showInput2(false);
                            break;
                        default:
                            setActionhint = "位置选择";
                            bd_action.showSele2("位置选择", "油炸位置");
                            bd_action.setHint("数值填写");
                            break;


                    }
                }
            }
        });
        builder.setNegativeButton(getString(R.string.cancle), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }


    protected void startParm(int type, ButtonEditSelectD bd) {
        String strParamSecond = bd.getButtonEditInputText();
        String strParamSecond2 = bd.getButtonEditInput2Text();
        String sele = getLocal(bd.getButtonEditText()) + "";

        int local = getLocal(bd);
        switch (type) {
            case 3:
                XssTrands.getInstanll().queryParam(local, strParamSecond);
                break;
            case 4:
                bd_set_parm.setButtonDisplayText("");
                setPrarm(local, sele, strParamSecond, strParamSecond2);
                break;
            case 5:
                action(local, sele, strParamSecond, strParamSecond2);
                break;
        }
    }

    public void setPrarm(int local, String sele, String msg, String msg2) {
        logx("setPrarm  local: " + local + "sele:  " + sele + "  msg：" + msg + "  msg2： " + msg2);
        if (TextUtils.isEmpty(msg)) {
            msg = "0";
        }
        if (TextUtils.isEmpty(msg2)) {
            msg2 = "0";
        }
        if (TextUtils.isEmpty(sele)) {
            sele = "0";
        }
//        D-- 22-07-02-14:29:53  KfcBomSerial: send--> 0200080401000001010001038e92      time=1739   writeList:1
//     D-- 22-07-02-14:29:53  KfcBomSerial: revert<--: 020008840000000101001203B43F
        if (local <= 4) {
            HashMap<Integer, Integer> hashMap = new HashMap<>();
            hashMap.put(1, -1);
            hashMap.put(2, -1);
            hashMap.put(3, -1);
            hashMap.put(1, -1);
            hashMap.put(2, -1);
            hashMap.put(3, -1);
            hashMap.put(local, Integer.parseInt(sele));
            int org = XssSavaData.getInstance().getData(XssData.FOODMeatPlain, XssData.FOODMeatPlainv);
            int temp = 0;
            if (hashMap.get(3) >= 0) {
                temp = hashMap.get(3) * 100;
            } else {
                temp = (org / 100) * 100;
            }

            if (hashMap.get(2) >= 0) {
                temp += hashMap.get(2) * 10;
            } else {
                temp += ((org % 100) / 10) * 10;
            }

            if (hashMap.get(1) >= 0) {
                temp += hashMap.get(1);
            } else {
                temp += org % 10;
            }
            XssSavaData.getInstance().savaData(XssData.FOODMeatPlain, temp);
            KfcPortControl.getInstall().setMeatPlain();
//            XssTrands.getInstanll().setParamHex2(local, "00" +XssUtility. getnumTwo(sele));
        } else {
            XssTrands.getInstanll().setParamHex2(local, XssUtility.getHexLeng(Integer.parseInt(msg), 4));

        }
    }

    public void action(int local, String sele, String msg, String msg2) {
        if (TextUtils.isEmpty(sele)) {
            sele = "0";
        }
        if (TextUtils.isEmpty(msg)) {
            msg = "0";
        }
        if (TextUtils.isEmpty(msg2)) {
            msg2 = "0";
        }
        logx("action  local: " + local + "  sele：" + sele + "  msg：" + msg + "  msg2： " + msg2);

        String space = "0000";
        switch (local) {
            case 9:
            case 10:
                XssTrands.getInstanll().actionHex2(local, "00000000");
                break;
            case 18:
            case 19:
            case 20:
            case 22:
                XssTrands.getInstanll().actionHex2(local, getnumFour(msg) + "0000");

                break;
            default:
                XssTrands.getInstanll().actionHex2(local, getnumFour(sele) + getnumFour(msg));

                break;


        }

    }


    public void toast(String msg) {
        ToastUtils.showToast(this, msg);
    }


    public int getLocal(ButtonEditSelectD bd) {
        return getLocal(bd.getButtonEditTextSecond());
    }

    public int getLocal(String strParam) {
        if (TextUtils.isEmpty(strParam)) {
            return -1;
        }
        int x = strParam.indexOf("~");
        int local = -1;
        if (x > 0) {
            local = Integer.parseInt(strParam.substring(0, x));
        } else {
            local = Integer.parseInt(strParam);
        }
        return local;
    }


    public String getnumFour(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return "0000";
        }
        return XssUtility.getHexLeng(Integer.parseInt(msg), 4);
    }

    private VendListener m_vendListener = new VendListener();

    private class VendListener implements XssTrands.VendEventListener {

        @Override
        public void VendEvent(XssEventInfo cEventInfo) {
            switch (cEventInfo.m_iEventID) {
                case MsgWhat.PORTDATA_BOM:
                    KfcBomInfo temp = KfcPortControl.getInstall().parsingBomData(cEventInfo.m_lParam4);
                    if (temp != null) {
                        logx("PORTDATA: " + temp.toString());
                    }
                    setTv(cEventInfo.m_lParam4);
                    break;
                case MsgWhat.NEXTDATA:
                    tvMsg.setText("");
                    break;
            }


        }
    }

    int setParmType;
    int setActionType;
    String setParmhint;
    String setActionhint;

    public String[] getsetParmAar(int local) {
        String[] a = null;
        switch (local) {
            case 1:
            case 2:
            case 3:
            case 4:
                a = new String[]{"0", "1"};
                break;
        }
        return a;
    }

    public String[] getActAar(int local) {
        String[] a = null;
        switch (local) {
            case 11:
                a = new String[]{"0~回原点", "1~炸炉1", "2~炸炉2", "3~炸炉3",
                        "4~炸炉4", "5~插单", "6~倒篮", "7~回篮", "8~左移d_l值", "9~右移d_l值"};
                break;
            case 12:
                a = new String[]{"0~回原点", "1~正常提篮", "2~插单提篮", "3~滤油",
                        "4~烹炸", "5~插单结束", "6~倒篮", "8~上移d_l值", "9~下移d_l值"};
                break;
            case 13:
                a = new String[]{"0~回原点", "1~正常提篮", "2~插单提篮", "3~转运位",
                        "4~滤油", "5~烹炸", "6~直薯倒料", "8~前移d_l值", "9~后移值"};
                break;
            case 14:
                a = new String[]{"0~回原点", "1~倒篮位", "8~顺时针移d_l值", "9~逆时针移d_l值"};
                break;
            case 15:
                a = new String[]{"0~回原点", "1~夹紧位", "8~夹紧移d_l值", "9~放构移d_l值"};
                break;
            case 16:
                a = new String[]{"0~回原点", "1~直薯位", "2~波纹薯条位", "8~前移d_l值", "9~后移d_l值"};
                break;
            case 17:
                a = new String[]{"0~回原点", "1~推料位", "8~左移d_l值", "9~右移d_l值"};
                break;
            case 18:
                a = new String[]{"0~关闭", "1~开启"};
                break;
            case 19:
                a = new String[]{"0~直薯", "1~鸡块", "2~波纹薯条"};
                break;
            case 20:
                a = new String[]{"0~素", "1~荤"};
                break;
            case 21:
                a = new String[]{"0~炸炉随机，分类随机", "1~炸炉1分料1", "1~炸炉2分料2", "1~炸炉3分料3"};
                break;
            case 22:
                a = new String[]{"0~清除声音", "1~关闭电源"};
                break;

        }
        return a;

    }

    public void setActHint(String msg) {
        bd_action.setButtonDisplayText(msg);
    }

    public void logx(String msg) {
        XssTrands.getInstanll().logd("KfcSettingBom", msg);
    }
}
