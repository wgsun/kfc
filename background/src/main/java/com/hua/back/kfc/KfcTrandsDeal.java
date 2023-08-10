package com.hua.back.kfc;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import com.common.base.XssTrands;
import com.common.base.utils.ToastUtils;
import com.common.base.utils.XssUtility;
import com.common.serial.CrcUtil;

import java.util.List;

/**
 * FileName: KfcTrandsDeal
 * Author: hua
 * Date: 2021/12/1 10:00
 * Description:
 */
public class KfcTrandsDeal {
    List<TextView> listTrandsed = null;
    Context context;
    View view3;

    public void seleTrand(String data) {
        String cmd = getLocal(data);
        switch (cmd) {
            case "3C":
                showView(view3, true);
                showView(listTrandsed.get(3), false);
                showView(listTrandsed.get(4), false);
                break;
            case "3E":
            case "41":
                showView(view3, false);
                showView(listTrandsed.get(3), false);
                showView(listTrandsed.get(4), false);
                break;
            case "50"://必须是数字
            case "51":
                showView(view3, false);
                showView(listTrandsed.get(4), false);
                showView(listTrandsed.get(3), true);
                setHint(listTrandsed.get(3), "请填入重量");
                listTrandsed.get(3).setFocusable(true);
                listTrandsed.get(3).setEnabled(true);
                break;
            case "0C":
                showView(view3, false);
                showView(listTrandsed.get(4), false);
                showView(listTrandsed.get(3), true);
                setHint(listTrandsed.get(3), "请填入波特率");
                listTrandsed.get(3).setFocusable(true);
                listTrandsed.get(3).setEnabled(true);
                break;
            case "54":
                setHint(listTrandsed.get(3), "分度值设置");
                showView(view3, false);
                listTrandsed.get(3).setFocusable(true);
                listTrandsed.get(3).setEnabled(true);

                showView(listTrandsed.get(3), true);
                showView(listTrandsed.get(4), false);
                break;
            case "55":
                showView(view3, false);
                listTrandsed.get(3).setFocusable(true);
                listTrandsed.get(4).setFocusable(true);
                listTrandsed.get(3).setEnabled(true);
                listTrandsed.get(4).setEnabled(true);

                showView(listTrandsed.get(3), true);
                showView(listTrandsed.get(4), true);
                setHint(listTrandsed.get(3), "开机自动清零");
                setHint(listTrandsed.get(4), "零点跟踪");
                break;
        }
    }

    public void trandsCmd() {
        String[] datas = new String[listTrandsed.size()];
        for (int x = 0; x < listTrandsed.size(); x++) {
            String temp = getData(listTrandsed.get(x));
            datas[x] = getLocal(temp);
        }
        String cmd = getHexLengTwo(datas[1]);
        String data = getHexLengTwo(datas[0]) + cmd;
        if (TextUtils.isEmpty(cmd) || TextUtils.isEmpty(data)) {
            toast("请先填入数据");
            return;
        }
        switch (cmd) {
            case "3C":
                if (TextUtils.isEmpty(datas[2])) {
                    toast("请填入内存地址");
                    return;
                }
                data = data + "0400" + getHexLengTwo(datas[2]) + "000100";
                break;
            case "3E":
            case "41":
                data = data + "0000";
                break;
            case "50"://必须是数字
            case "51":
                if (!XssUtility.isDigital(datas[3])) {
                    toast("请正确填入重量");
                    return;
                }
                data = data + "0400" + getIntToHexLow(datas[3]);
                break;

            case "0C":
                if (!XssUtility.isDigital(datas[3])) {
                    toast("请正确填入波特率");
                    return;
                }
                int wight = Integer.parseInt(datas[3]);
                if (wight > 65534) {
                    toast("波特率不能超过65535");
                    return;
                }
                String hex = Integer.toHexString(wight);
                while (hex.length() < 4) {
                    hex = hex + "0";
                }
                data = data + "0400" + hex.substring(2, 4) + hex.substring(0, 2) + "0000";
                break;
            case "54":
                if (TextUtils.isEmpty(datas[3])) {
                    toast("请先填入数据");
                    return;
                }
                if (datas[3].length() > 2) {
                    toast("请填入一个字节的数据");
                    return;
                }
                data = data + "0100" + getHexLengTwo(datas[3]);
                break;
            case "55":
                if (TextUtils.isEmpty(datas[3]) || TextUtils.isEmpty(datas[4])) {
                    toast("请先填入数据");
                    return;
                }
                if (datas[3].length() > 2 || datas[4].length() > 2) {
                    toast("请填入一个字节的数据");
                    return;
                }
                data = data + "0200" + getHexLengTwo(datas[3]) + getHexLengTwo(datas[4]);
                break;

        }

        byte[] aar = XssUtility.hexStringToBytes(data + "0000");
        CrcUtil.getCrcTrands(aar);
        Log.d("KfcTrandsDeal", "trandsCmd: " + XssUtility.bytesToHexString(aar));
        XssTrands.getInstanll().trandsCmd("0", XssUtility.bytesToHexString(aar));
    }

    public String getData(TextView tv) {
        String data = "";
        if (tv != null) {
            if (tv.getText() != null) {
                data = tv.getText().toString().trim();
            }
        }
        return data;
    }

    public void setHint(TextView tv, String msg) {

        tv.setHint(msg);
    }

    public void showView(View v, boolean isShow) {
        if (v != null) {
            v.setVisibility(isShow ? View.VISIBLE : View.GONE);
        }

    }

    public String getLocal(String strParam) {
        if (TextUtils.isEmpty(strParam)) {
            return "";
        }
        int x = strParam.indexOf("~");
        if (x < 1) {
            return strParam;
        }
        strParam = strParam.substring(0, x).toUpperCase();
        if (strParam.startsWith("0X")) {
            strParam = strParam.substring(2);
        }
        return strParam;
    }

    public KfcTrandsDeal(View view, List<TextView> list, Context context) {
        listTrandsed = list;
        this.context = context;
        view3 = view;
    }


    public void deInt() {
        listTrandsed = null;
        context = null;
        view3 = null;
    }

    public void toast(String msg) {
        ToastUtils.show(context, msg);
    }


    public static String getHexLengTwo(String data) {
        return XssUtility.getHexLengHex(data, 2);

    }

    public static String getIntToHexLow(String data) {
        if (TextUtils.isEmpty(data)) {
            return "00000000";
        }
        String hex = Integer.toHexString(Integer.parseInt(data) * 100);

        hex = XssUtility.getHexLengHex(hex, 8);

        return hex.substring(6, 8) + hex.substring(4, 6) + hex.substring(2, 4) + hex.substring(0, 2);
    }




    public void logx(String msg) {
        XssTrands.getInstanll().logd("KfcTrandsDeal", msg);
    }


}
