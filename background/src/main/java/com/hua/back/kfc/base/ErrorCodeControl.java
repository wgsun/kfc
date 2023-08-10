package com.hua.back.kfc.base;

import android.content.Context;
import android.text.TextUtils;

import com.common.base.MsgWhat;
import com.common.base.utils.XssData;
import com.common.serial.kfc.KfcPortControl;
import com.tcn.background.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * FileName: EerorCode
 * Author: hua
 * Date: 2022/5/16 19:50
 * Description:
 */
public class ErrorCodeControl {
    HashMap<Integer, String> errorCodeMap = new HashMap<>();
    HashMap<Integer, String> errorWayMap = new HashMap<>();

    public void init(Context context) {
        String[] errCode = context.getResources().getStringArray(R.array.kfc_error_other);
        String[] errCode1 = context.getResources().getStringArray(R.array.kfc_errorcode_dorp);
        String[] errCode2 = context.getResources().getStringArray(R.array.kfc_errorcode_bom);
        initKfcErrCode(errCode, 0, errorCodeMap, errorWayMap);
        initKfcErrCode(errCode1, XssData.NumErrorDrop, errorCodeMap, errorWayMap);
        initKfcErrCode(errCode2, XssData.NumErrorBoom, errorCodeMap, errorWayMap);
    }


    public String getCodeMsg(int code) {
        if (code == 0) {
            return "";
        }
        code = code % 10000;
        Object o = errorCodeMap.get(code);
        if (o != null) {
            return o.toString();
        }
        if (code > 0) {
            return code + "";
        }
        return "";
    }

    public String getCodeWay(int code) {
        if (code == 0) {
            return "";
        }
        code = code % 10000;
        Object o = errorWayMap.get(code);
        if (o != null) {
            return o.toString();
        }
        if (code > 0) {
            return code + "";
        }
        return "";
    }

    public List<Integer> getListDorpCode() {
        List<Integer> list = new ArrayList<>();
        list.add(1010);
        list.add(1011);
        list.add(1012);

        list.add(1101);
        list.add(1102);
        list.add(1103);

        list.add(1301);
        list.add(1302);
        list.add(1303);
        return list;
    }


    /**
     * 是否回调点击清除的故障
     *
     * @param
     * @return
     * @author hua
     * @time 2022/7/28 14:31
     */
    public List<Integer> getListCleanRevDorpCode() {
        List<Integer> list = new ArrayList<>();
        list.add(1010);
        list.add(1011);
        list.add(1012);

        list.add(1301);
        list.add(1302);
        list.add(1303);
        return list;
    }

    /**
     * 机器是否可以正常通讯
     *
     * @param sele 0,所以 ，1冰箱   2炸炉
     * @return
     * @author hua
     * @time 2022/7/12 9:22
     */
    public int getCommCode(int sele) {
        List<Integer> list = KfcPortControl.getInstall().getErrorCode();
        List<Integer> listFail = new ArrayList<>();
        switch (sele) {
            case 1:
                listFail.add(MsgWhat.errorCode9002 % XssData.NumError10000);
                break;
            case 2:
                listFail.add(MsgWhat.errorCode9003 % XssData.NumError10000);
                break;
            default:
                listFail.add(MsgWhat.errorCode9002 % XssData.NumError10000);
                listFail.add(MsgWhat.errorCode9003 % XssData.NumError10000);
                break;
        }
        if (list == null || list.size() < 1) {
            return 0;
        }
        for (int x = 0; x < list.size(); x++) {
            int temp = list.get(x) % XssData.NumError10000;
            if (listFail.contains(temp)) {
                return list.get(x);
            }
        }
        return 0;
    }

    /**
     * des 扫描篮子的故障
     *
     * @param
     * @return
     * @author hua
     * @time 2022/7/13 17:46
     */
    public List<Integer> getListTypeScanLan() {
        List<Integer> tempList = new ArrayList<>();
        tempList.add(1020);
        tempList.add(1200);
        tempList.add(1201);
        tempList.add(1202);
        tempList.add(1203);
        tempList.add(1204);
        tempList.add(9001);
        tempList.add(9002);
        tempList.add(9003);
        return tempList;
    }


    /**
     * @param
     * @return
     * @author hua
     * @time 2022/7/28 18:55
     */
    public List<Integer> getListTypeCode(int type) {
        List<Integer> tempList = new ArrayList<>();
        switch (type) {
            case 1:
                tempList.add(1010);
                tempList.add(1101);
                tempList.add(1301);
                break;
            case 2:
                tempList.add(1012);
                tempList.add(1102);
                tempList.add(1302);
                break;
            case 3:
                tempList.add(1011);
                tempList.add(1103);
                tempList.add(1303);
                break;
        }
        return tempList;
    }

    /**
     * 针对炸炉故障添加
     *
     * @param
     * @return
     * @author hua
     * @time 2022/8/4 11:37
     */
    public int getTrandsCodes(int code) {
/*        switch (code) {
            case 12002:
            case 12003:
            case 12004:
            case 2002:
            case 2003:
            case 2004:
                code = 1000 + code;
                break;
        }*/
        return code;
    }

    //    设备原料状态故障
    public int getMaterialStatusErrorCode() {
        List<Integer> parms = KfcPortControl.getInstall().getErrorCode();
        List<Integer> listCode = new ArrayList<>();
        listCode.add(1301);
        listCode.add(1302);
        listCode.add(1303);
        listCode.add(1101);
        listCode.add(1102);
        listCode.add(1103);
        if (parms != null && parms.size() > 0) {
            for (int x = 0; x < parms.size(); x++) {
                int code = parms.get(x) % 10000;
                if (listCode.contains(code)) {
                    return parms.get(x);
                }
            }
        }
        return 0;
    }

    //    获取当前设备桁架状态
    public int getTrussStatusErrorCode() {
        List<Integer> parms = KfcPortControl.getInstall().getErrorCode();
        List<Integer> listCode = new ArrayList<>();
        for (int x = 2001; x < 2037; x++) {
            listCode.add(1301);

        }
        if (parms != null && parms.size() > 0) {
            for (int x = 0; x < parms.size(); x++) {
                int code = parms.get(x) % 10000;
                if (listCode.contains(code)) {
                    return parms.get(x);
                }
            }
        }
        return 0;
    }

    //    获取当前设备保温槽状态
    public int getgetHeatStatusErrorCode() {
        List<Integer> listCode = new ArrayList<>();
        listCode.add(2009);
        listCode.add(2017);
        listCode.add(2025);
        return getCode(listCode);
    }


    public int getCode(List<Integer> listCode) {
        List<Integer> parms = KfcPortControl.getInstall().getErrorCode();
        if (parms != null && parms.size() > 0) {
            for (int x = 0; x < parms.size(); x++) {
                int code = parms.get(x) % 10000;
                if (listCode.contains(code)) {
                    return parms.get(x);
                }
            }
        }
        return 0;
    }

    public void initKfcErrCode(String[] errCode, int Xian, HashMap<Integer, String> errorMap, HashMap<Integer, String> wayMap) {
        for (String s : errCode) {
            if (!TextUtils.isEmpty(s)) {
                s.trim();
                int x = s.indexOf("~");
                if (x > 0 && x < s.length() - 1) {
                    int code = Xian + Integer.parseInt(s.substring(0, x));
                    code = code % 10000;
                    String msg = s.substring(x + 1);
                    int way = msg.indexOf("~");
                    if (way > 0 && way < msg.length() - 1) {
                        errorMap.put(code, msg.substring(0, way));
                        wayMap.put(code, msg.substring(way + 1));
                    } else {
                        errorMap.put(code, msg);
                        wayMap.put(code, "");
                    }
                }
            }
        }
    }

    /**
     * 需要不进行处理的异常代码  落料模块
     *
     * @param
     * @return
     * @author hua
     * @time 2022/6/20 15:50
     */
    public boolean hideError(int code) {
        code = code % XssData.NumError10000;
        ArrayList<Integer> list = new ArrayList();
        list.add(1010);
        list.add(1011);
        list.add(1012);

        list.add(1101);
        list.add(1102);
        list.add(1103);

        list.add(1301);
        list.add(1302);
        list.add(1303);

        list.add(2700);
        list.add(2701);
        return list.contains(code);
    }


    /**
     * 是否可以跑的故障
     *
     * @param
     * @return
     * @author hua
     * @time 2022/7/12 17:35
     */
    public int getCanExeType(int type) {
        List<Integer> listErrorBoMEx = KfcPortControl.getInstall().getListErrorTwo();
        if (listErrorBoMEx != null && listErrorBoMEx.size() > 0) {
            return listErrorBoMEx.get(0);
        }
        int code = 0;
        List<Integer> listDorpCode = getListDorpCode();
        List<Integer> listCanExeOrder = getListIgnoreExeOrder();
        List<Integer> listCodeExist = KfcPortControl.getInstall().getListErrorDrop();
        List<Integer> listTypeCode = getListTypeCode(type);
        if (listCodeExist != null && listCodeExist.size() > 0) {
            for (int i = 0; i < listCodeExist.size(); i++) {
                int tempCode = listCodeExist.get(i) % XssData.NumError10000;
                if (listCanExeOrder.contains(tempCode)) {
                } else if (listDorpCode.contains(tempCode)) {
                    if (listTypeCode.contains(tempCode)) {
                        return listCodeExist.get(i);
                    }
                } else {
                    return listCodeExist.get(i);
                }
            }
        }
        return code;
    }

    public int getBomExeType() {
        List<Integer> listErrorBoMEx = KfcPortControl.getInstall().getListErrorTwo();
        if (listErrorBoMEx != null && listErrorBoMEx.size() > 0) {
            return listErrorBoMEx.get(0);
        }
        List<Integer> listDorpCode = getListDorpCode();
        List<Integer> listCanExeOrder = getListIgnoreExeOrder();
        List<Integer> listCodeExist = KfcPortControl.getInstall().getListErrorDrop();
        if (listCodeExist != null && listCodeExist.size() > 0) {
            for (int i = 0; i < listCodeExist.size(); i++) {
                int tempCode = listCodeExist.get(i) % XssData.NumError10000;
                if (listCanExeOrder.contains(tempCode)) {
                } else if (listDorpCode.contains(tempCode)) {
                } else {
                    return listCodeExist.get(i);
                }
            }
        }
        return 0;
    }

    //执行订单可以忽略的故障
    public ArrayList<Integer> getListIgnoreExeOrder() {
        ArrayList<Integer> aar = new ArrayList();
        aar.add(1101);
        aar.add(1102);
        aar.add(1103);

        aar.add(2700);
        aar.add(2701);
        return aar;
    }
}
