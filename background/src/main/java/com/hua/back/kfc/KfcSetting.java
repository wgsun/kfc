package com.hua.back.kfc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.common.base.BeanInfo.PortFormat;
import com.common.base.MsgWhat;
import com.common.base.XssEventInfo;
import com.common.base.XssTrands;
import com.common.base.utils.ToastUtils;
import com.common.base.utils.XssData;
import com.common.base.utils.XssSavaData;
import com.common.base.utils.XssUtility;
import com.common.serial.kfc.KfcPortControl;
import com.hua.back.base.ui.ButtonEditSelectD;
import com.hua.back.common.act.BaseBackActivity;
import com.tcn.background.R;


import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

import static com.common.base.MsgWhat.PORTDATA;


/**
 * FileName: KfcSetting
 * Author: hua
 * Date: 2021/9/10 15:00
 * Description:
 */
public class KfcSetting extends BaseBackActivity {
    private ButtonEditSelectD bd_action;
    private ButtonEditSelectD bd_set_parm;
    private ButtonEditSelectD bd_query_parm;
    public TextView tvMsg;
    private EditText edSlot;
    private EditText edData;
    private KfcTrandsDeal kfcTrandsDeal;
    TextView tvWeight;
    private ButtonEditSelectD bd_query_zc;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        XssTrands.getInstanll().registerListener(m_vendListener);
        init();
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
    protected void onDestroy() {
        super.onDestroy();
        XssTrands.getInstanll().unregisterListener(m_vendListener);
        if (kfcTrandsDeal != null) {
            kfcTrandsDeal.deInt();
            kfcTrandsDeal = null;
        }
    }

    boolean isQuery = false;

    @Override
    public int getlayout() {
        return R.layout.pram_setting_kfc;
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.serial_clean) {
                XssTrands.getInstanll().cleanFault();
            } else if (v.getId() == R.id.serial_stopact) {
                XssTrands.getInstanll().stopAct();
            } else if (v.getId() == R.id.btn_ship) {
                String data = edSlot.getText().toString();
                if (XssUtility.isDigital(data)) {
                    XssTrands.getInstanll().ship(Integer.parseInt(data), "00", edData.getText().toString());
                }
            } else if (v.getId() == R.id.serial_drive_info) {
                XssTrands.getInstanll().queryDriveInfo();

            } else if (v.getId() == R.id.serial_drive_setting) {
            } else if (v.getId() == R.id.serial_query) {
                isQuery = true;
                XssTrands.getInstanll().queryStatus();
            }
        }
    };



    public String getboxResult(String data, int b) {
        return "0".equals(data.substring(b, b + 1)) ? "箱空" : "没空";
    }

    public void setTv(String msg) {
        if (tvMsg == null) {
            return;
        }
        msg = msg.toUpperCase();
        showParm(msg);
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


    public void showParm(String msg) {
//        020014A0
//        0000
//        5501
//        3C
//        0400
//        B0DEFDFF
//        3A6A
//        0302F5
        PortFormat portFormat = XssTrands.getInstanll().getPortFormat(msg);
        if (portFormat == null) {
            return;
        }
        String cmd = portFormat.getCmd();
        int addr = portFormat.getAddr();
        if (msg.length() < 22) {
            return;
        }
        String data = msg.substring(18, 22);
        if ("A1".equalsIgnoreCase(cmd)) {
            setActHint(XssTrands.getInstanll().deal06(portFormat));
        } else if ("A0".equals(cmd)) {
            int index = msg.indexOf("3C0400");
            if (index > 10 && msg.length() > index + 18) {
                String dataw = msg.substring(index + 6, index + 14);
                String temp = dataw.substring(6, 8) + dataw.substring(4, 6) + dataw.substring(2, 4) + dataw.substring(0, 2);

                if (temp.startsWith("FF")) {

                    logx("showParm: 数据过大 dataw=" + dataw);
                    return;

                }

                int weight = Integer.parseInt(temp, 16) / 100;
                logx("showParm:  weight=" + weight + "   dataw=" + dataw);
                tvWeight.setTextColor(Color.parseColor("#000000"));

                tvWeight.setText(weight + " g");
            } else if (msg.contains("3E0400")) {
                tvWeight.setTextColor(Color.parseColor("#000000"));

                tvWeight.setText("0 g");
            }
        } else if ("04".equals(cmd)) {
            switch (addr) {
                case 101:
                case 102:
                case 103:
                    bd_set_parm.setButtonDisplayText(Integer.parseInt(data) + "g");
                    break;
            }
        } else if ("80".equals(cmd)) {
            String hint = XssTrands.getInstanll().getCmd80Msg(portFormat);

            if (!TextUtils.isEmpty(hint)) {
                if (isQuery) {
                    isQuery = false;
                }

            }
        } else if ("83".equals(cmd)) {
            int leng = msg.length();
            switch (addr) {
                case 14:
                case 15:
                case 16:
                case 22:
                    int one = Integer.parseInt(msg.substring(leng - 10, leng - 8), 16);
                    int two = Integer.parseInt(msg.substring(leng - 8, leng - 6), 16);
                    bd_query_parm.setButtonDisplayText(one + "\n" + two);
                    break;
                default:
                    int sudu = Integer.parseInt(msg.substring(leng - 10, leng - 6), 16);
                    bd_query_parm.setButtonDisplayText(sudu + "");
                    break;
            }

        }


    }

    public void init() {
        findViewById(R.id.serial_clean).setOnClickListener(mClickListener);
        findViewById(R.id.serial_drive_info).setOnClickListener(mClickListener);
        findViewById(R.id.serial_drive_setting).setOnClickListener(mClickListener);
        findViewById(R.id.serial_query).setOnClickListener(mClickListener);
        findViewById(R.id.serial_stopact).setOnClickListener(mClickListener);

        tvMsg = findViewById(R.id.tv_msg);
        edSlot = findViewById(R.id.kfc_setting_ship_slot);
        edData = findViewById(R.id.kfc_setting_ship_data);
        tvWeight = findViewById(R.id.tv_trands_weight);

        initTrands(R.id.btn_trand_cmd_ed0, R.id.btn_trand_cmd_btn0);
        initTrands(R.id.btn_trand_cmd_ed1, R.id.btn_trand_cmd_btn1);
        initTrands(R.id.btn_trand_cmd_ed2, R.id.btn_trand_cmd_btn2);
        listTrandsed.add((TextView) findViewById(R.id.btn_trand_cmd_ed3));
        listTrandsed.add((TextView) findViewById(R.id.btn_trand_cmd_ed4));
        listTrandsed.get(0).setText("1");
        findViewById(R.id.btn_trand_cmd_exe).setOnClickListener(trandslistener);
        View v = findViewById(R.id.ll_trands_2);
        kfcTrandsDeal = new KfcTrandsDeal(v, listTrandsed, this);

        bd_action = (ButtonEditSelectD) findViewById(R.id.bd_action);
        setBDStates(bd_action, "执行动作命令（0x05）", "执行");

        bd_query_parm = (ButtonEditSelectD) findViewById(R.id.bd_query_parm);
        setBDStates(bd_query_parm, "查询参数（0x03）", "设置");

        bd_set_parm = (ButtonEditSelectD) findViewById(R.id.bd_set_parm);
        setBDStates(bd_set_parm, "设置参数（0x04）", "设置");

        bd_query_zc = (ButtonEditSelectD) findViewById(R.id.bd_query_zc);
        setBDStates(bd_query_zc, "设置炸程", "设置");
        bd_query_zc.showInput(false);
        bd_query_zc.showInput2(false);
        bd_query_zc.showSele2("选择食品", "选择炸程");
    }


    public void setBDStates(ButtonEditSelectD bd, String name, String exe) {
        setBDStates(bd, name, exe, m_ButtonEditClickListener);

    }


    String[] aar_action, aar_parm, aar_trands_cmd, aar_trands_addr;
    protected ButtonEditClickListener m_ButtonEditClickListener = new ButtonEditClickListener();

    protected class ButtonEditClickListener implements ButtonEditSelectD.ButtonListener {
        @Override
        public void onClick(View v, int buttonId) {
            if (null == v) {

                return;
            }
            int id = v.getId();
            if (R.id.bd_query_zc == id) {
                //0~转发驱动板指令{1,2,#}查询货道      //解释：0代表参数ID。   1,2 表示第一和第二个要传入的参数，#代表要输入的值(如果是设置参数，这个#去掉)
                if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
//                    startParm(5, bd_action);
                    String sele = bd_query_zc.getButtonEditSecond().getText().toString();
                    String msg = bd_query_zc.getButtonEdit().getText().toString();
                    logx("bd_query_zc  onClick: " + sele + "     " + msg);
                    if (!TextUtils.isEmpty(sele) && !TextUtils.isEmpty(msg)) {
                        int x = getIntData(sele.substring(0, 1));
                        if (x > -1) {
                            XssSavaData.getInstance().savaData(XssData.FOODZhaCheng + x, msg);
                            ;
                        }
                    }
                } else if (ButtonEditSelectD.BUTTON_ID_SELECT_SECOND == buttonId) {
                    showSelectDialog(9, "对应食品", bd_query_zc.getButtonEditSecond(), "", XssData.FOODSELES);

                } else if (ButtonEditSelectD.BUTTON_ID_SELECT == buttonId) {
                    showBifDialog(bd_query_zc.getButtonEdit());
                }

            }
            if (R.id.bd_action == id) {
                //0~转发驱动板指令{1,2,#}查询货道      //解释：0代表参数ID。   1,2 表示第一和第二个要传入的参数，#代表要输入的值(如果是设置参数，这个#去掉)
                if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
                    startParm(5, bd_action);

                } else if (ButtonEditSelectD.BUTTON_ID_SELECT_SECOND == buttonId) {
                    if (aar_action == null)
                        aar_action = getResources().getStringArray(R.array.kfc_action);
                    showSelectDialog(5, "请选择动作地址", bd_action.getButtonEditSecond(), "", aar_action);
                } else if (ButtonEditSelectD.BUTTON_ID_SELECT == buttonId) {

                    showSelectDialog(-1, setActionhint, bd_action.getButtonEdit(), "", getSetParmAar(setActionType));
                }

            } else if (R.id.bd_set_parm == id) {
                //0~转发驱动板指令{1,2,#}查询货道      //解释：0代表参数ID。   1,2 表示第一和第二个要传入的参数，#代表要输入的值(如果是设置参数，这个#去掉)
                if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
                    startParm(4, bd_set_parm);
                } else if (ButtonEditSelectD.BUTTON_ID_SELECT_SECOND == buttonId) {
                    if (aar_parm == null)
                        aar_parm = getResources().getStringArray(R.array.kfc_set_parm_drop);
                    showSelectDialog(4, "请选择参数地址", bd_set_parm.getButtonEditSecond(), "", aar_parm);
                } else if (ButtonEditSelectD.BUTTON_ID_SELECT == buttonId) {
                    if (aar_parm == null)
                        aar_parm = getResources().getStringArray(R.array.kfc_set_parm_drop);
                    showSelectDialog(-1, setParmhint, bd_set_parm.getButtonEdit(), "", getSetParmAar(setParmType));
                }

            } else if (R.id.bd_query_parm == id) {
                //0~转发驱动板指令{1,2,#}查询货道      //解释：0代表参数ID。   1,2 表示第一和第二个要传入的参数，#代表要输入的值(如果是设置参数，这个#去掉)
                if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
                    startParm(3, bd_query_parm);
                } else if (ButtonEditSelectD.BUTTON_ID_SELECT_SECOND == buttonId) {
                    if (aar_parm == null)
                        aar_parm = getResources().getStringArray(R.array.kfc_set_parm_drop);
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
                if (type == 20) {
                    if (singleitem > -1) {
                        kfcTrandsDeal.seleTrand(str[singleitem]);
                    }
                } else if (type == 9) {
                    int x = getIntData(str[singleitem].substring(0, 1));
                    if (x > -1) {
                        bd_query_zc.getButtonEdit().setText(XssSavaData.getInstance().getData(XssData.FOODZhaCheng + x));
                    }
                } else if (type == 4) {
                    int local = getLocal(bd_set_parm);
                    bd_set_parm.hideSele2();
                    if (local < 10) {
                        bd_set_parm.setHint("电机速度10~1000");
                        bd_set_parm.showInput2(false);
                    } else if (local < 12) {
                        bd_set_parm.setHint("电机速度10~3000");
                        bd_set_parm.showInput2(false);
                    } else if (local < 14) {
                        bd_set_parm.setHint("电机速度10~1000");
                        bd_set_parm.showInput2(false);
                    } else if (local < 16) {
                        bd_set_parm.setHint("减速值0~100", "停止值0~15");
                        bd_set_parm.showInput2(true);
                    } else if (local == 16) {
                        bd_set_parm.setHint("鸡块误差等级0~15", "鸡块单块重量10~255");
                        bd_set_parm.showInput2(true);
                    } else if (local == 17) {
                        bd_set_parm.setHint("篮子数量0~3");
                        bd_set_parm.showInput2(false);
                    } else if (local == 18) {
                        bd_set_parm.setHint("延时时间0~65000");
                        bd_set_parm.showInput2(false);
                    } else if (local == 22) {
                        bd_set_parm.setHint("鸡块减速值", "鸡块停止值"
                        );
                        bd_set_parm.showInput2(true);
                    } else if (local == 101 || local == 103) {
                        bd_set_parm.setHint("单份重量（g）");
                        bd_set_parm.showInput2(false);
                    } else if (local == 102) {
                        bd_set_parm.setHint("单份数量（块）");
                        bd_set_parm.showInput2(false);
                    }
                } else if (type == 5) {
                    int local = getLocal(bd_action);
                    setActionType = local;
                    bd_action.showInput(true);
                    bd_action.showInput2(false);
                    bd_action.hideSele2();
                    switch (local) {
                        case 1:
                        case 2:
                            bd_action.setHint("下料重量多少g");
                            bd_action.showInput2(false);
                            break;
                        case 3:
                            bd_action.setHint("下料多少块");
                            bd_action.showInput2(false);
                            break;
                        case 4:
                        case 8:
                        case 9:
                        case 10:
                        case 11:
                        case 12:
                        case 13:
                        case 255:
                            bd_action.showInput(false);
                            bd_action.showInput2(false);
                            break;

                        case 5:
                        case 7:
                            bd_action.showInput(false);
                            bd_action.showSele2("下料类型", "下料类型");
                            setActionhint = "下料类型";
                            break;
                        case 6:
                            bd_action.showInput(false);
                            bd_action.showSele2("插单命令", "油炸位置");
                            setActionhint = "插单命令到几号油炸位（1-3推出，4回落）";
                            break;
                        case 102:
                        case 104:
                        case 106:
                            setActionhint = "0停止，1：旋转，2反转";
                            setActionType = 102;
                            bd_action.showSele2("旋转电机控制", "电机转动时间(ms)");
                            break;
                        case 101:
                        case 103:
                        case 105:
                            setAction100(local, "1：开门，0关门", "门控开关");
                            bd_action.setButtonEditInputText("500");
                            bd_action.showInput2(false);
                            bd_action.getButtonEdit().setText("1");
                            break;
                        case 107:
                            setAction100(local, "1：左移，0右移", "电机控制");
                            break;
                        case 108:
                        case 109:
                            setAction100(local, "1：推出，0缩回", "电机控制");
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


    protected void setAction100(int local, String msg, String hint2) {
        setActionhint = msg;
        setActionType = 101;
        bd_action.showSele2(hint2, msg);
        if (local == 107) {
            bd_action.setHint("电机速度10~3000");
        } else {
            bd_action.setHint("电机速度10~1000");
        }
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
                setPrarm(local, strParamSecond, strParamSecond2);
                break;
            case 5:
                action(local, sele, strParamSecond, strParamSecond2);
                break;
        }
    }

    public void setPrarm(int local, String msg, String msg2) {
        logx("setPrarm  local: " + local + "  msg：" + msg + "  msg2： " + msg2);
        if (TextUtils.isEmpty(msg)) {
            msg = "0";
        }
        if (TextUtils.isEmpty(msg2)) {
            msg2 = "0";
        }
        switch (local) {
            case 101:
            case 102:
            case 103:
                if (XssUtility.isNumeric(msg)) {
                    XssSavaData.getInstance().savaData(XssData.ShuTiaoSignNum + local, Integer.parseInt(msg));
                }
                break;
            case 14:
            case 15:
            case 16:
            case 22:
                XssTrands.getInstanll().setParamHex(local, XssUtility.getnumTwo(msg) +XssUtility. getnumTwo(msg2));
                break;
            default:
                XssTrands.getInstanll().setParam(local, msg);
                break;
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
        logx("action  local: " + local + "  msg：" + msg + "  msg2： " + msg2);

        String space = "0000";
        switch (local) {
            case 1:
            case 2:
            case 3:
                XssTrands.getInstanll().actionHex(local, XssUtility.getnumFour(msg) + "0000");
                break;
            case 4:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 255:
                XssTrands.getInstanll().actionHex(local, space + space);
                bd_action.showInput(false);
                bd_action.showInput2(false);
                break;
            case 5:
            case 7:
                XssTrands.getInstanll().actionHex(local, XssUtility.getnumTwo(sele) + XssUtility.getnumFour(msg) + "00");
                break;
            case 6:
                XssTrands.getInstanll().actionHex(local, XssUtility.getnumTwo(sele) + "00" + space);
                break;
            case 101:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
                XssTrands.getInstanll().actionHex(local, XssUtility.getnumTwo(sele) + XssUtility.getnumFour(msg) + "00");
                break;
        }
    }

    public boolean isDataB(String msg, int min, int max) {
        if (XssUtility.isDigital(msg)) {
            int d = Integer.parseInt(msg);
            if (d >= min && d <= max) {
                return true;
            }
        }
        return false;
    }

    public void toast(String msg) {
        ToastUtils.showToast(this, msg);
    }

    public String getDataTwo(String msg) {
        int sx = getIntData(msg);
        if (sx > -1 && sx < 65536) {
            return XssUtility.getHexLeng(sx, 4);
        }
        return "";
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

    public int getIntData(String msg) {
        int x = -99;
        if (XssUtility.isDigital(msg)) {
            x = Integer.parseInt(msg);
        }
        return x;
    }



    private VendListener m_vendListener = new VendListener();

    private class VendListener implements XssTrands.VendEventListener {

        @Override
        public void VendEvent(XssEventInfo cEventInfo) {
            switch (cEventInfo.m_iEventID) {
                case PORTDATA:
                    setTv(cEventInfo.m_lParam4);
                    break;
                case MsgWhat.CMDSTATS06:
                    if (cEventInfo.m_lParam1 == 1) {
//                        setActHint(kfcTrandsDeal.deal06(cEventInfo.m_lParam4));
                    } else {
                        if (!TextUtils.isEmpty(cEventInfo.m_lParam4)) {
                            setActHint(cEventInfo.m_lParam4);
                        }
                    }
                    break;
                case MsgWhat.NEXTDATA:
                    tvMsg.setText("");
                    break;
            }


        }
    }

    List<TextView> listTrandsed = new ArrayList<>();
    List<TextView> listTrandsbtn = new ArrayList<>();
    TrandsListener trandslistener = new TrandsListener();

    public void initTrands(int id1, int id2) {
        TextView tv = findViewById(id1);
        TextView tv2 = findViewById(id2);
        tv2.setOnClickListener(trandslistener);
        listTrandsed.add(tv);
        listTrandsbtn.add(tv2);

    }

    class TrandsListener implements View.OnClickListener {


        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn_trand_cmd_exe) {
                tvWeight.setTextColor(Color.parseColor("#33333333"));
                kfcTrandsDeal.trandsCmd();
            } else if (id == R.id.btn_trand_cmd_btn0) {
                String[] trandsAddr = new String[]{"1", "2", "3"};
                showSelectDialog(4, "传感器地址", listTrandsed.get(0), trandsAddr[0], trandsAddr);
            } else if (id == R.id.btn_trand_cmd_btn1) {
                if (aar_trands_cmd == null)
                    aar_trands_cmd = getResources().getStringArray(R.array.kfc_weght);
                showSelectDialog(20, "传感器指令码", listTrandsed.get(1), aar_trands_cmd[0], aar_trands_cmd);
            } else if (id == R.id.btn_trand_cmd_btn2) {
                if (aar_trands_addr == null)
                    aar_trands_addr = getResources().getStringArray(R.array.kfc_weght_AD);
                showSelectDialog(21, "内存变量地址", listTrandsed.get(2), aar_trands_addr[0], aar_trands_addr);
            }
        }
    }

    /**
     * 列表Dialog
     */
    private void showListDialog() {
        final String[] items = {"我是1", "我是2", "我是3"};

        AlertDialog.Builder listDialog = new AlertDialog.Builder(KfcSetting.this);
        listDialog.setTitle("我就是个列表Dialog");
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(KfcSetting.this, "点击了" + items[which], Toast.LENGTH_SHORT).show();
            }
        });
        listDialog.show();
    }

    int setParmType;
    int setActionType;
    String setParmhint;
    String setActionhint;

    public String[] getSetParmAar(int type) {
        String[] a = null;
        switch (type) {
            case 1:
            case 101:
                a = new String[]{"0", "1"};
                break;
            case 102:
                a = new String[]{"0", "1", "2"};
                break;
            case 2:
                a = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
                break;
            case 5:
            case 7:
                a = new String[]{"0~直薯条", "1~鸡块", "2~波纹薯条"};
                break;
            case 6:
                a = new String[]{"1", "2", "3", "4"};
                break;

        }
        return a;

    }

    public void setActHint(String msg) {
        bd_action.setButtonDisplayText(msg);
    }

    public void logx(String msg) {
        XssTrands.getInstanll().logd("KfcSetting", msg);

    }

    final String[] ss = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"
            , "16", "17", "18", "19", "20", "21", "22", "23", "24"};
    boolean[] bomSele = new boolean[ss.length];

    public void showBifDialog(EditText editText) {
        for (int x = 0; x < ss.length; x++) {
            bomSele[x] = false;
        }
        AlertDialog.Builder DuoItem = new AlertDialog.Builder(this);
        DuoItem.setTitle("选择对应炸程");
        DuoItem.setMultiChoiceItems(ss, bomSele, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                bomSele[which] = isChecked;
            }
        });
        DuoItem.setPositiveButton("提交", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String s = "";
                for (int x = 0; x < bomSele.length; x++) {
                    if (bomSele[x]) {
                        s += ss[x] + "-";
                    }
                }
                if (!TextUtils.isEmpty(s) && s.endsWith("-")) {
                    s = s.substring(0, s.length() - 1);
                    editText.setText(s);
                }
            }
        });
        DuoItem.create().show();

    }
}
