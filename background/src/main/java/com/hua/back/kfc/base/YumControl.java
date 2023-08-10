package com.hua.back.kfc.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.common.base.BeanInfo.KfcOrder;
import com.common.base.MsgWhat;
import com.common.base.XssTrands;
import com.common.base.utils.XssData;
import com.common.base.utils.XssSavaData;
import com.common.base.utils.XssUtility;
import com.common.logger.MuhuaLog;
import com.common.serial.kfc.KfcPortControl;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.common.CashService;
import com.google.gson.JsonParser;
import com.hua.back.common.act.MeauSetting;
import com.hua.back.kfc.db.KfcErrorInfo;
import com.hua.back.kfc.db.KfcSqlControl;
import com.tcn.background.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * FileName: YumControl
 * Author: hua
 * Date: 2022/4/13 13:03
 * Description:
 */
public class YumControl {
    String ver = "v20220901_01";
    String TAG = "YumControl";
    boolean isAAr = true;
    int scanError;
    private static YumControl yumControlNOW;
    ErrorCodeControl errorClz;
    //    Map<Integer, String> orderPrintMap = new HashMap<>();

    List<String> printList = new ArrayList<>();
    String RESULT = "result";
    String MESSAGE = "message";
    String solution = "solution";
    String temperature = "temperature";
    String DATA = "data";
    String heatDataPrint = "0";
    List<Integer> listErrorRecord = new ArrayList<>();
    List<Integer> listErrorRecordTwo = new ArrayList<>();
    int errorRecordTwoCont = 0;
    List<KfcOrder> historyList = new ArrayList<>();
    OrderResultCallback orderCallback;
    boolean isOpenCash = false;
    private YumResultCallback scanBasket;
    boolean isYumScanLan = false;
    boolean isDeviceBootInit = false;
    private YumResultCallback heatCallback;
    private YumResultCallback deviceBootInterFace;

    public static YumControl getInstall() {

        if (yumControlNOW == null) {

            synchronized (YumControl.class) {
                if (yumControlNOW == null) {
                    yumControlNOW = new YumControl();
                }

            }
        }
        return yumControlNOW;

    }

    public boolean isAAr() {
        return isAAr;
    }


    private void logx(String msg) {
        XssTrands.getInstanll().LoggerDebug(TAG, msg);
    }

    public void logReturun(String msg, String data) {
        logx(msg + "  return: " + data);
    }

    public synchronized String createOrder(Context context, int weight, int num, int type, String orderId) {
        type = getType(type);
        int code = 0;
        logx("createOrder  weight=" + weight + "  num=" + num +
                "  type=" + type + "  orderId: " + orderId + "   isScanLan: " + KfcPortControl.getInstall().isScanLanNeed);
        if (KfcPortControl.getInstall().isScanLanNeed) {
            code = MsgWhat.errorCode9014;
        } else if (!KfcPortControl.getInstall().isMeat() && !KfcPortControl.getInstall().isMeat()) {
            code = MsgWhat.errorCode9020;
        } else if (TextUtils.isEmpty(orderId) || orderId.length() > 50) {
            code = MsgWhat.errorCode9001;
        } else {
            code = errorClz.getCanExeType(type);
            if (code == 0) {
                if (type < 1 || type > XssData.FOODSELES.length) {
                    code = MsgWhat.errorCode9008;
                } else {
                    if (type < 4) {
                        if (weight < 1 || weight > 99999
                                || num < 0) {
                            code = MsgWhat.errorCode9001;
                        } else {
                            if (!KfcPortControl.getInstall().isCanWeight()) {
                                code = MsgWhat.errorCode9015;
                            } else {
                                if (!KfcPortControl.getInstall().isCanAddOrder(type)) {
                                    code = MsgWhat.errorCode9007;
                                }
                            }
                        }
                    } else {
                        if (!KfcPortControl.getInstall().isCanBoosOrder(type)) {
                            code = MsgWhat.errorCode9007;
                        }
                    }
                }

            }
        }
        String rd = "";
        if (code == 0) {
            rd = getJsonString(0);
            if (KfcPortControl.getInstall().isTwoOrder(orderId)) {
                logx("createOrder: 重复订单： " + orderId);
            } else {
                logReturun("createOrder", rd + "   orderId=" + orderId + "   " + KfcPortControl.getInstall().getTypeName(type));
                getKfcPortControl().shipOrer(orderId, num, weight, type);
            }
        } else {
            rd = getJsonString(code);
            boolean isPrint = true;
            String temp = rd + "   orderId=" + orderId;
            if (XssUtility.isContains(printList, temp)) {
                isPrint = false;
            } else {
                printList.add(temp);
            }
            if (isPrint) {
                logReturun("createOrder", temp + "     printList=" + printList.size());
            }
        }
        return rd;
    }


    /**
     * 2薯条站实际生产状态返回，监听方式，实时返回（监听各订单制作进度信息）
     *
     * @param
     * @return
     * @author hua
     * @time 2022/7/12 17:24
     */
    public void setOrderResultCallback(Context context, OrderResultCallback callback) {
        logx("setOrderResultCallback");
        this.orderCallback = callback;
    }

    /**
     * //   3获取当前设备生产状态
     *
     * @param
     * @return
     * @author hua
     * @time 2022/7/12 17:24
     */
    public String getProductionStatus(Context context) {
        logx("getProductionStatus");
        Map<Integer, KfcOrder> map = getKfcPortControl().getShipMap();
        Map<Integer, KfcOrder> fryMap = new HashMap<>();
        for (Integer x : map.keySet()) {
            KfcOrder kfcOrder = map.get(x);
            if (kfcOrder != null && kfcOrder.getBomId() > 0) {
                fryMap.put(kfcOrder.getBomId(), kfcOrder);
            }
        }
        JsonArray jsonArray = new JsonArray();
        for (int x = 1; x < 4; x++) {
            KfcOrder kfc = fryMap.get(x);
            JsonObject j1 = new JsonObject();
            if (kfc != null && (kfc.getFlow() == 13 || kfc.getFlow() == 14)) {
                j1.addProperty("orderId", kfc.getOrder());
                j1.addProperty("status", "1");
            } else {
                j1.addProperty("orderId", "");
                j1.addProperty("status", "0");
            }
            j1.addProperty("fryerId", "" + x);
            jsonArray.add(j1);
        }
        String data = getJsonString(0, jsonArray);
        logReturun("getProductionStatus", data);
        return data;
    }

    /**
     * 4获取当前设备原料状态（低物料信息）,判断物料是否充足
     *
     * @param
     * @return
     * @author hua
     * @time 2022/7/12 17:24
     */
    public String getMaterialStatus(Context context) {
        logx("getMaterialStatus");
        String data = getJsonString(errorClz.getMaterialStatusErrorCode(), null);
        logReturun("getMaterialStatus", data);
        return data;

    }

    /**
     * 5获取当前设备桁架状态   可用/不可用
     *
     * @param
     * @return
     * @author hua
     * @time 2022/7/12 17:24
     */
    public String getTrussStatus(Context context) {
        logx("getTrussStatus");
        int code = errorClz.getTrussStatusErrorCode();
        String data = getJsonString(code, null);
        logReturun("getTrussStatus", data);
        return data;
    }

    //    6 获取当前设备保温槽状态    可用/不可用
    public String getHeatStatus(Context context) {
        logx("getHeatStatus");
        int code = errorClz.getgetHeatStatusErrorCode();
        String data = getJsonString(code);
        logReturun("getHeatStatus", data);
        return data;
    }

    /**
     * 7获取当前设备炸锅状态
     *
     * @param
     * @return
     * @author hua
     * @time 2022/7/12 17:23
     */
    public String getFryerStatus(Context context) {
        Map<Integer, KfcOrder> map = getKfcPortControl().getShipMap();
        Map<Integer, KfcOrder> fryMap = new HashMap<>();
        for (Integer x : map.keySet()) {
            KfcOrder kfcOrder = map.get(x);
            if (kfcOrder != null && kfcOrder.getBomId() > 0) {
                fryMap.put(kfcOrder.getBomId(), kfcOrder);
            }
        }
        JsonArray jsonArray = new JsonArray();
        for (int x = 1; x < 4; x++) {
            KfcOrder kfc = fryMap.get(x);
            JsonObject j1 = new JsonObject();
            if (kfc == null) {
                j1.addProperty("status", "0");
            } else if (kfc.getFlow() == 13 || kfc.getFlow() == 14) {
                j1.addProperty("status", "1");

            } else {
                j1.addProperty("status", "0");
            }
            if (x == 3) {
                j1.addProperty("fryerType", "0");//0-荤锅、1-素锅
            } else {
                j1.addProperty("fryerType", "1");//0-荤锅、1-素锅

            }
            j1.addProperty("fryerId", "" + x);
            jsonArray.add(j1);
        }
        String data = getJsonString(0, jsonArray);
        logReturun("getFryerStatus", data);
        return data;
    }

    /**
     * dec8  设置当前设备荤素炸锅
     *
     * @param context：Android上下文环境 设置当前设备荤素炸锅
     * @return [{
     * "fryerId":1,//炸锅Id
     * "fryerType":1,//炸锅类型，0-荤锅、1-素锅   (串口协议中是0素，1荤)
     * }]
     * @author hua
     * @time 2022/4/18 10:39
     */
    public String setFryerInfo(Context context, String message) {
        logx("setFryerInfo:   message=" + message);
        int code = getErrorClz().getCommCode(0);
        if (code > 0) {

        } else {
            HashMap<Integer, Integer> hashMap = new HashMap<>();
            hashMap.put(1, -1);
            hashMap.put(2, -1);
            hashMap.put(3, -1);
            JsonElement jsonElement = new JsonParser().parse(message);
            if (jsonElement != null) {
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                if (jsonArray != null) {
                    Iterator it = jsonArray.iterator();
                    while (it.hasNext()) {
                        JsonElement e = (JsonElement) it.next();
                        int fryerId = XssUtility.getJsonDataAsInt(e, "fryerId");
                        int fryerType = XssUtility.getJsonDataAsInt(e, "fryerType");
                        if (fryerType < 0 || fryerType > 1) {
                            code = MsgWhat.errorCode9001;
                            break;
                        }
                        hashMap.put(fryerId, fryerType);
                    }
                }
            } else {
                code = MsgWhat.errorCode9001;
            }
            int org = XssSavaData.getInstance().getData(XssData.FOODMeatPlain, XssData.FOODMeatPlainv);
            int temp = 0;
            if (hashMap.get(3) >= 0) {
                temp = getFryerVlaue(hashMap.get(3)) * 100;
            } else {
                temp = (org / 100) * 100;
            }

            if (hashMap.get(2) >= 0) {
                temp += getFryerVlaue(hashMap.get(2)) * 10;
            } else {
                temp += ((org % 100) / 10) * 10;
            }

            if (hashMap.get(1) >= 0) {
                temp += getFryerVlaue(hashMap.get(1));
            } else {
                temp += org % 10;
            }
            XssSavaData.getInstance().savaData(XssData.FOODMeatPlain, temp);
            KfcPortControl.getInstall().setMeatPlain();
        }

        String data = getJsonString(code);
        logReturun("setFryerInfo", data);
        return data;
    }

    /**
     * 9  设置当前设备自动模式或手动模式
     *
     * @param
     * @return
     * @author hua
     * @time 2022/7/12 17:23
     */
    public String setWorkMode(Context context, int mode) {
        logx("setFryerInfo:   setWorkMode    mode=" + mode);
        String data = getJsonString(0);
        logReturun("setWorkMode", data);
        return data;
    }

    /**
     * 10 检测设备是否可用，生产指令下发前，硬件设备自检，判断是否可以制作订单
     *
     * @param
     * @return
     * @author hua
     * @time 2022/7/12 17:22
     */
    public String checkDevice(Context context) {
        String msg = "";
        List<Integer> checkDeviceParm = KfcPortControl.getInstall().getErrorCode();
        String data = "";
        if (checkDeviceParm != null && checkDeviceParm.size() > 0) {
            JsonArray jsonArray = new JsonArray();
            for (int x = 0; x < checkDeviceParm.size(); x++) {
                int code = checkDeviceParm.get(x);
                JsonObject j1 = new JsonObject();
                j1.addProperty("code", getYumcode(code));
                j1.addProperty("msg", errorClz.getCodeMsg(code));
                jsonArray.add(j1);
            }
            data = getJsonString(0, jsonArray);
        } else {
            data = getJsonString(0);
        }
        logReturun("checkDevice", data);
        return data;
    }

    /**
     * 11 用于开机后的设备初始化
     *
     * @param
     * @return
     * @author hua
     * @time 2022/7/13 9:23
     */
    public String init(Context context) {
        MuhuaLog.getInstance().initLog(context);
        XssSavaData.getInstance().init(context);
        XssSavaData.getInstance().setMacType(XssData.ShuTiaoZhan);
        XssTrands.getInstanll().init(context, R.array.kfc_errorcode_dorp, R.array.kfc_action);

        logx(" init  ver:   " + ver);
        openCashService(context);
        errorClz = new ErrorCodeControl();
        errorClz.init(context);
        listErrorRecordTwo.clear();
        listErrorRecord.clear();

        return getJsonString(0);
    }

    /**
     * 12 跳转到设备底层驱动调试界面
     *
     * @param
     * @return
     * @author hua
     * @time 2022/7/13 9:24
     */
    public String openSetting(Activity context) {
        logx("openSetting");

        Intent intent = new Intent(context, MeauSetting.class);
        context.startActivity(intent);
        return getJsonString(0);
    }

    /**
     * 14 告警消除
     *
     * @param warningType 0--全部故障，1--落料故障，2--烹炸故障（默认为0全部故障）
     * @return
     * @author hua
     * @time 2022/6/13 9:28
     */
    public String dismissWarning(Context context, int warningType, String errerCode) {
        logx("dismissWarning:   warningType=" + warningType + "   errerCode=" + errerCode);
        int code = getErrorClz().getCommCode(0);
        if (code > 0) {
        } else {
            int orgCode = XssUtility.getInt(errerCode);
            if (orgCode > 0) {
                List<Integer> listDrop = errorClz.getListCleanRevDorpCode();
                if (listDrop.contains(orgCode % XssData.NumError10000)) {
                    XssTrands.getInstanll().cleanFault();
                    listErrorRecordTwo.clear();
                    errorRecordTwoCont = 0;
                }
            } else {
           /*     switch (warningType) {
                    case 0:
                        XssTrands.getInstanll().cleanFault();
                        XssTrands.getInstanll().cleanFault2();
                        break;
                    case 1:
                        XssTrands.getInstanll().cleanFault();
                        break;
                    case 2:
                        XssTrands.getInstanll().cleanFault2();
                        break;
                    default:
                        code = MsgWhat.errorCode9001;
                        break;
                }*/
            }
        }

        String data = getJsonString(code);
        logReturun("dismissWarning", data);
        return data;
    }

    /**
     * 15薯条机硬件扫描篮子
     *
     * @param
     * @return
     * @author hua
     * @time 2022/7/13 17:11
     */
    public void scanBasket(Context context, YumResultCallback callback) {
        logx("scanBasket  callback=" + callback);
        int code = getErrorClz().getCommCode(2);
        isYumScanLan = true;
        scanBasket = callback;
        if (code > 0) {
            setScanDealFace(code, 0);
        } else {
            XssTrands.getInstanll().toast("开始扫描篮子", 0);
            KfcPortControl.getInstall().scanLan();
        }
    }

    /**
     * 16 薯条机心跳状态返回，返回告警列表、设备开机状态、设备是否可以下单（区分荤素锅是否可以下单）
     *
     * @return
     * @param-
     * @author hua
     * @time 2022/7/14 11:32
     */
    public void setHeatCallback(Context context, YumResultCallback callback) {
        logx("setHeatCallback  callback=" + callback);
        heatCallback = callback;
        KfcPortControl.getInstall().sendHeatTime(5);
    }

    /**
     * 17 薯条机设备开机后，机器初始化操作，初始化成功，返回初始化完成和篮子数量
     *
     * @param
     * @return
     * @author hua
     * @time 2022/7/14 11:34
     */
    public void deviceBootInit(Context context, YumResultCallback callback) {
        int code = getErrorClz().getCommCode(0);
        logx("deviceBootInit  callback=" + callback + "   " + code);
        deviceBootInterFace = callback;
        isDeviceBootInit = true;
        XssTrands.getInstanll().toast("开始初始化中", 0);
        KfcPortControl.getInstall().deviceBootInit();
        KfcSqlControl.getInstall().deleteErrorCode(getDate() + 1);
        KfcSqlControl.getInstall().deleteErrorCode(getDate() - 1);
    }

    /**
     * 18薯条站一类告警和二类告警统计信息返回，返回当日统计数据（设备关机后不在统计范围，只统计设备
     * 异常告警信息）
     *
     * @param
     * @return
     * @author hua
     * @time 2022/8/26 16:46
     */
    public String getTodayErrorInfo(Context context, int type, int hour) {
        logx("getTodayErrorInfo type=" + type + " hour=" + hour);
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        List<KfcErrorInfo> list = null;
        list = KfcSqlControl.getInstall().queryErrorInfo(getDate(), hour);

        if (list != null && list.size() > 1) {
            for (int x = 0; x < list.size(); x++) {
                KfcErrorInfo kfcErrorInfo = list.get(x);
                int code = kfcErrorInfo.getCode();
                if (type == code / XssData.NumError10000) {
                    JsonObject j1 = new JsonObject();
                    j1.addProperty("code", code);
                    j1.addProperty("message", errorClz.getCodeMsg(code));
                    j1.addProperty("time", kfcErrorInfo.getTime());
                    jsonArray.add(j1);
                } else {
                }

            }
        }
        jsonObject.add("error", jsonArray);
        String data = jsonObject.toString();
        logReturun("getTodayErrorInfo ", data);
        return data;
    }

    /**
     * 订单接口的回调
     *
     * @param
     * @return
     * @author hua
     * @time 2022/8/4 11:14
     */
    public void setCallbackOrder(KfcOrder yumOrderInfo, int boxTemp) {
        if (TextUtils.isEmpty(yumOrderInfo.getOrder())) {
            logx("setCallbackOrder   订单不能为null: ");
            return;
        }
        int foodType = yumOrderInfo.getType();
        int status = getFlowReturn(yumOrderInfo.getFlow());
        if (yumOrderInfo.isMidEnd()) {
            status = -99;
        } else {
            if (status == yumOrderInfo.getYumStates()) {
                return;
            } else if (status < yumOrderInfo.getYumStates()) {
                logx("setCallbackOrder 状态异常：" + yumOrderInfo.toString() + " status= " + status);
            }
        }
        yumOrderInfo.setYumStates(status);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("orderId", yumOrderInfo.getOrder());
        jsonObject.addProperty("weight", yumOrderInfo.getWeight() + "");
        if (foodType == 2) {
            jsonObject.addProperty("realWeight", getWeight(yumOrderInfo.getWeightNow()));
        } else {
            jsonObject.addProperty("realWeight", yumOrderInfo.getWeightNow());
        }
        jsonObject.addProperty("realNum", yumOrderInfo.getUnitNum() + "");
        jsonObject.addProperty("num", yumOrderInfo.getUnitNum() + "");
        jsonObject.addProperty("type", getType(foodType) + "");
        jsonObject.addProperty("fryerId", yumOrderInfo.getBomId() + "");
        jsonObject.addProperty("status", String.valueOf(status));
        int code = yumOrderInfo.getErrorCode();
 /*       if (code > 0) {
        } else {
            List<Integer> listBoMIgnore = KfcPortControl.getInstall().getListErrorBoMIgnore();
            if (listBoMIgnore != null && listBoMIgnore.size() > 0) {
                code = listBoMIgnore.get(0);
            }
        }*/
        String data = getJsonString(code, errorClz.getCodeMsg(code), errorClz.getCodeWay(code), jsonObject, boxTemp + "");
        logReturun("setCallbackOrder", data);
/*        String temp = orderPrintMap.get(foodType);
        if (!TextUtils.isEmpty(temp) && temp.equals(data) && yumOrderInfo.getFlow() < 15) {
            orderPrintMap.put(foodType, "");
        } else {
            logReturun("setCallbackOrder", data);
            orderPrintMap.put(foodType, data);
        }*/
        if (orderCallback == null) {
            return;
        }
        orderCallback.onSuccess(0, data);
    }

    public int getDate() {
        String time = XssUtility.getTime14B();
        return Integer.parseInt(time.substring(6, 8));
    }

    /**
     * 16  心跳接口的回调
     * "powerStatus":"0",//开机状态，0--正常，1--薯条机设备开机
     * "meatCreateOrderStatus":"0", // 是否可以向荤锅下发订单，0--可以创建订单，1--不可以创建订单
     * "vegetableCreateOrderStatus":"0" //是否可以向素锅下发订单，0--可以创建订单，1--不可以创建订
     * 单
     *
     * @param
     * @return
     * @author hua
     * @time 2022/7/14 11:36
     */
    public void setHeatInterFace(int powerStatus, int boxTemp) {
        if (heatCallback == null) {
            logReturun("setHeatInterFace", "heatCallback == null");
            return;
        }
        JsonArray jsonArray = new JsonArray();
        List<Integer> heatList = KfcPortControl.getInstall().getErrorCode();

        if (heatList == null || heatList.size() < 1) {
            JsonObject j1 = new JsonObject();
            j1.addProperty("code", "0");
            j1.addProperty("message", "");
            jsonArray.add(j1);
        } else {
            if (powerStatus == 2) {
                listErrorRecord.clear();
                listErrorRecordTwo.clear();
                errorRecordTwoCont = 0;
                int code = MsgWhat.errorCode9018;
                if ((!heatList.contains(MsgWhat.errorCode9003)) || (!heatList.contains(MsgWhat.errorCode9002))) {
                    code = MsgWhat.errorCode9019;
                }
                JsonObject j1 = new JsonObject();
                j1.addProperty("code", code);
                j1.addProperty(MESSAGE, errorClz.getCodeMsg(code));
                j1.addProperty("solution", errorClz.getCodeWay(code));
                jsonArray.add(j1);
            } else {
                List<Integer> listDorpCode = errorClz.getListDorpCode();
                int errNum = 0;
          /*      boolean isNoshuTiao = errorClz.isNoFoods(1, list);
                boolean isNoshuJiKuai = errorClz.isNoFoods(2, list);
                boolean isNoBoWei = errorClz.isNoFoods(3, list);*/
                for (int x = 0; x < heatList.size(); x++) {
                    int code = heatList.get(x);
                    if (code == MsgWhat.errorCode9002 || code == MsgWhat.errorCode9003) {
                        logx("串口通讯故障  code=" + code + "  " + errorClz.getCodeMsg(code));
//                    } else if (errorClz.isCanNoFoods(code, isNoshuTiao, isNoshuJiKuai, isNoBoWei)) {
                    } else {
                        if (code>XssData.NumError10000&&code<XssData.NumError20000) {
                            if (!listErrorRecord.contains(code)) {
                                listErrorRecord.add(code);
                                KfcSqlControl.getInstall().addErrorCode(code);
                            } else {
                                logx("listErrorRecord: "+listErrorRecord.size());
                            }
                        } else if (listDorpCode.contains(code % XssData.NumError10000)) {
                            if (!listErrorRecordTwo.contains(code)) {
                                listErrorRecordTwo.add(code);
                                errorRecordTwoCont = 0;
                                KfcSqlControl.getInstall().addErrorCode(code);
                            } else {
                                logx("listErrorRecordTwo: "+listErrorRecordTwo.size());

                            }
                        }
                        JsonObject j1 = new JsonObject();
                        j1.addProperty("code", code);
                        j1.addProperty(MESSAGE, errorClz.getCodeMsg(code));
                        j1.addProperty("solution", getSolution(code));
                        errNum++;
                        jsonArray.add(j1);
                    }

                }
                errorRecordTwoCont++;
                if (errorRecordTwoCont > 350) {
                    errorRecordTwoCont = 0;
                    listErrorRecordTwo.clear();
                }
                if (errNum < 1) {
                    JsonObject j1 = new JsonObject();
                    j1.addProperty("code", "0");
                    j1.addProperty(MESSAGE, errorClz.getCodeMsg(0));
                    j1.addProperty("solution", getSolution(0));
                    jsonArray.add(j1);
                }
            }
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("error", jsonArray);
        jsonObject.addProperty("powerStatus", powerStatus == 2 ? 2 : 0);
/*        jsonObject.addProperty("meatCreateOrderStatus", KfcPortControl.getInstall().isMeat() ? 0 : 1);//0--可以创建订单，1--不可以创建订单
        jsonObject.addProperty("vegetableCreateOrderStatus", KfcPortControl.getInstall().isPlain() ? 0 : 1);    */
        jsonObject.addProperty("meatCreateOrderStatus", 0);//0--可以创建订单，1--不可以创建订单
        jsonObject.addProperty("vegetableCreateOrderStatus", 0);
        String data = jsonObject.toString();
        if (data.equals(heatDataPrint)) {
            heatDataPrint = "0";
        } else {
            heatDataPrint = data;
            logReturun("setHeatInterFace", data);
        }
        if (heatCallback != null) {
            heatCallback.onSuccess(data);
        }

    }


    /**
     * 15  17 设备初始化与扫描篮子接口的回调
     *
     * @param
     * @return
     * @author hua
     * @time 2022/7/14 11:35
     */

    public void setScanDealFace(int code, int lanNum) {
        if (lanNum > 2) {
            scanError = 0;
        }
        boolean isBooInit = true;
        if (deviceBootInterFace == null || !isDeviceBootInit) {
            isBooInit = false;
        }
        if (!isBooInit) {
            if (scanBasket == null || !isYumScanLan) {
                return;
            }
        }
        if (code > 0) {
            List<Integer> list = errorClz.getListTypeScanLan();
            int temp = code % XssData.NumError10000;
            if (!list.contains(temp)) {
                return;
            }
            code = temp + XssData.NumError10000;
        } else {
            if (lanNum < 3) {
                code = MsgWhat.errorCode11205;
                scanError = code;
            } else {
                scanError = 0;
            }
        }
        String data = getJsonString(code);
        logReturun("setScanDealFace " + " isBooInit=" + isBooInit + " ", data);
        if (isBooInit) {
            isDeviceBootInit = false;
            deviceBootInterFace.onSuccess(data);
        } else {
            isYumScanLan = false;
            scanBasket.onSuccess(data);
        }

    }

    public int getWeight(int weight) {
        int x = weight * 1000 / XssData.childBilie;
        if (x % 10 >= 5) {
            x = x + 10;
        }
        return x / 10;
    }


    public String getCodemsg(int code) {
        if (errorClz != null) {
            return errorClz.getCodeMsg(code);
        }
        return "";
    }

    int getYumcode(int code) {
        if (errorClz == null) {
            return code;
        }
        return errorClz.getTrandsCodes(code);
    }

/*    //        1  称重  2称重完成； 3，移动       8  插单开始处理
//        //  0x01转运中； 0x02烹炸中； 0x03滤油中
//        //0x04回蓝中； 0x05已完成； 0xE0故障*/


    //    型（1-物料确认完成、2-落料完成、3-烹炸完成、4-转移完成、5-订单制作完成    -99-  订单中止）
    public int getFlowReturn(int flow) {
        int s = flow;
        switch (flow) {
            case 1:
                s = 1;
                break;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 10:
            case 11:
            case 12:
            case 13:
                s = 2;
                break;
            case 14:
                s = 3;
                break;
            case 15:
                s = 4;
                break;
            case 16:
                s = 5;
                break;
            default:
                s = 0;
                break;
        }
        return s;
    }


    public synchronized void addhistoryList(KfcOrder kfcOrder) {
        if (historyList.size() > 3) {
            historyList.remove(historyList.size() - 1);
        }
        historyList.add(0, kfcOrder);
        setCallback14();
    }

    public String getSolution(int code) {
        String msg = errorClz.getCodeWay(code);
        if (TextUtils.isEmpty(msg) && code / XssData.NumError10000 == 1) {
            msg = "关机-->开机";
        }
        return msg;
    }

    public int getType(int type) {
        switch (type) {
            case 2:
                return 3;
            case 3:
                return 2;
        }
        return type;
    }

    /**
     * 保温槽提示接口，目前已废弃
     *
     * @param
     * @return
     * @author hua
     * @time 2022/7/13 17:54
     */
    private void setCallback14() {
    /*    JsonObject jsRight = new JsonObject();
        JsonObject jsleft = new JsonObject();
        jsleft.addProperty("heatId", "1");
        jsRight.addProperty("heatId", "2");
        int typeLeft = 0;
        long typeRight = 0;
        long timeLeft = 0;
        long timeright = 0;

        if (historyList.size() < 1) {

        } else if (historyList.size() < 2) {
            KfcOrder yumOrderInfo = historyList.get(0);
            typeLeft = getType(yumOrderInfo.getType());
            timeLeft = yumOrderInfo.getEndTime();
        } else {
            KfcOrder yumOrderInfol = historyList.get(historyList.size() - 1);
            typeLeft = getType(yumOrderInfol.getType());
            timeLeft = yumOrderInfol.getEndTime();

            KfcOrder yumOrderInfoR = historyList.get(historyList.size() - 2);
            typeRight = getType(yumOrderInfoR.getType());
            timeright = yumOrderInfoR.getEndTime();
        }
        jsleft.addProperty("time", timeLeft);
        jsleft.addProperty("type", typeLeft);

        jsRight.addProperty("time", timeright);
        jsRight.addProperty("type", typeRight);
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(jsleft);
        jsonArray.add(jsRight);


//        logReturun("setCallback14", data);
*//*        if (heatResultCallback != null)
            heatResultCallback.onSuccess(data);*//*
         */
    }

    public ErrorCodeControl getErrorClz() {
        return errorClz;
    }

    public void openCashService(Context context) {
        boolean b = XssTrands.getInstanll().isAppForeground(context);
        logx(" openCashService:   " + b + "    isOpenCash: " + isOpenCash);
        if (isOpenCash) {
            return;
        }
        if (b) {
            isOpenCash = true;
            Intent m_intent_Service = new Intent(context, CashService.class);
            context.startService(m_intent_Service);
        }
    }


    public String getJsonString(int code) {
        return getJsonString(code, null);
    }

    public String getJsonString(int code, JsonElement dataJson) {
        return getJsonString(code, errorClz.getCodeMsg(code), errorClz.getCodeWay(code), dataJson, "");
    }

    public int getScanError() {
        return scanError;
    }

    public String getJsonString(int code, String mees, String way, JsonElement dataJson, String temp) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(RESULT, String.valueOf(getYumcode(code)));
        jsonObject.addProperty(MESSAGE, mees);
        jsonObject.addProperty(solution, way);
        if (!TextUtils.isEmpty(temp)) {
            jsonObject.addProperty(temperature, temp);
        }
        if (dataJson != null) {
            jsonObject.add(DATA, dataJson);
        }

        return jsonObject.toString();
    }


    private KfcPortControl getKfcPortControl() {
        return KfcPortControl.getInstall();
    }


    private JsonArray getData(String strJSON) {

        JsonArray mJsonArray = null;

        if ((null == strJSON) || (strJSON.length() <= 0)) {
            return mJsonArray;
        }

        JsonObject json = new JsonParser().parse(strJSON).getAsJsonObject();
        mJsonArray = json.get("Data").getAsJsonArray();

        return mJsonArray;
    }

    public int getFryerVlaue(int x) {
        return x == 0 ? 1 : 0;
    }

    public void showHint(String message, String msg) {
        YumDemoBean yumDemoBean = new Gson().fromJson(message, YumDemoBean.class);
        if (yumDemoBean.getResult() == 0) {
            XssTrands.getInstanll().toast(msg, 0);
        } else {
            XssTrands.getInstanll().toast(yumDemoBean.getResult() + "  " + yumDemoBean.getMessage(), 1);
        }
    }

}
