//package com.hua.back.kfc.base;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.text.TextUtils;
//
//import com.common.base.XssTrands;
//import com.common.base.utils.XssData;
//import com.common.base.utils.XssSavaData;
//import com.common.base.utils.XssUtility;
//import com.common.logger.MuhuaLog;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import com.common.CashService;
//import com.hua.back.kfc.KfcSetting;
//import com.tcn.background.R;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//
///**
// * FileName: YumControl
// * Author: hua
// * Date: 2022/4/13 13:03
// * Description:
// */
//public class YumControl2 implements YumInterFace {
//    private static YumControl2 yumControl2NOW;
//    ErrorCode errorCode;
//    String RESULT = "result";
//    String MESSAGE = "message";
//    String DATA = "data";
//    String temp = "-18";
//    int demoCode;
//    Map<Integer, YumDemoBean> demoMap = new HashMap<>();
//    Map<String, YumOrderInfo> orderMap = new HashMap<>();
//    List<YumOrderInfo> historyList = new ArrayList<>();
//    OrderResultCallback callback;
//    boolean isOpenCash = false;
//    private YumResultCallback heatResultCallback;
//
//    public static YumControl2 getInstall() {
//
//        if (yumControl2NOW == null) {
//
//            synchronized (YumControl2.class) {
//                if (yumControl2NOW == null) {
//                    yumControl2NOW = new YumControl2();
//                }
//            }
//        }
//        return yumControl2NOW;
//
//    }
//
//    public synchronized String setTestData(int sort, int code, String message, String... data) {
//        logx("setTestData: " + sort + "  " + code + "   " + message + "  " + getStrAAr(data));
//        demoCode = code;
//        YumDemoBean yumDemoBean = new YumDemoBean(sort, code, message, data);
//        demoMap.put(sort, yumDemoBean);
//        if (sort == 2) {
//            setCallback2(code, message, data);
//        } else if (sort == 14) {
//            setCallback14(code, message, data);
//
//        }
//        JsonObject jsonObject = new JsonObject();
//        jsonObject.addProperty(RESULT, "0");
//        jsonObject.addProperty(MESSAGE, "0");
//        return jsonObject.toString();
//    }
//
//    public synchronized String getDemoData(int sort) {
//        YumDemoBean demoBean = demoMap.get(sort);
//        if (demoBean == null) {
//            return null;
//        }
//        String[] parm = demoBean.getData();
//        String data = "";
//        JsonObject jsonObject = new JsonObject();
//        switch (sort) {
//            case 1:
//            case 2:
//            case 8:
//                data = getJsonString(demoBean.getCode(), demoBean.getMessage());
//                break;
//            case 4:
//                temp = demoBean.getMessage();
//                jsonObject.addProperty("status", "1");
//                data = getJsonString(demoBean.getCode(), "0", jsonObject, demoBean.getMessage());
//                break;
//            case 5:
//            case 6:
//                if (parm != null && parm.length >= 1) {
//                    jsonObject.addProperty("status", parm[0]);
//                    data = getJsonString(demoBean.getCode(), demoBean.getMessage(), jsonObject);
//                }
//                break;
//            case 3:
//                if (parm != null && parm.length >= 3) {
//                    HashMap<String, Integer> temp = new HashMap<>();
//                    temp.put("1", 0);
//                    temp.put("2", 1);
//                    temp.put("3", 2);
//                    JsonArray jsonArray = new JsonArray();
//                    for (String entry : orderMap.keySet()) {
//                        YumOrderInfo yumOrderInfo = orderMap.get(entry);
//                        if (yumOrderInfo != null) {
//                            JsonObject j2 = new JsonObject();
//                            j2.addProperty("orderId", yumOrderInfo.getOrderId());
//                            j2.addProperty("status", parm[temp.get(yumOrderInfo.getFryerId())]);
//                            j2.addProperty("fryerId", yumOrderInfo.getFryerId());
//                        }
//                    }
//                    return getJsonString(0, "", jsonArray);
//                } else {
//                    logx("getDemoData:   3 parm数组长度不对");
//                }
//                break;
//            case 7:
//                if (parm != null && parm.length >= 3) {
//                    JsonArray jsonArray = new JsonArray();
//                    JsonObject j1 = new JsonObject();
//                    j1.addProperty("fryerType", "0");
//                    j1.addProperty("status", parm[0]);
//                    j1.addProperty("fryerId", "1");
//                    JsonObject j2 = new JsonObject();
//                    j2.addProperty("fkryerType", "0");
//                    j2.addProperty("status", parm[1]);
//                    j2.addProperty("fryerId", "2");
//
//                    JsonObject j3 = new JsonObject();
//                    j3.addProperty("fryerType", "0");
//                    j3.addProperty("status", parm[2]);
//                    j3.addProperty("fryerId", "3");
//                    jsonArray.add(j1);
//                    jsonArray.add(j2);
//                    jsonArray.add(j3);
//                    return getJsonString(0, "", jsonArray);
//                } else {
//                    logx("getDemoData:   3 parm数组长度不对");
//                }
//                break;
//            case 10:
//                if (parm != null && parm.length >= 0) {
//                    JsonArray jsonArray = new JsonArray();
//                    for (String s : parm) {
//                        int c = XssUtility.getInt(s);
//                        if (c > 0) {
//                            c = c % 10000;
//                            JsonObject j1 = new JsonObject();
//                            j1.addProperty("code", s);
//                            j1.addProperty("msg", errorCode.getCodeMsg(c));
//                            jsonArray.add(j1);
//                        }
//
//                    }
//
//                    return getJsonString(demoBean.getCode(), errorCode.getCodeMsg(demoBean.getCode()), jsonArray);
//                } else {
//                    data = getJsonString(demoBean.getCode(), errorCode.getCodeMsg(demoBean.getCode()));
//                }
//                break;
//        }
//        return data;
//    }
//
//    private void logx(String msg) {
//        XssTrands.getInstanll().LoggerDebug(this.getClass().getSimpleName(), msg);
//    }
//
//    public void logReturun(String msg, String data) {
//        logx(msg + "   return: " + data);
//    }
//
//    @Override
//    public synchronized String createOrder(Context context, int weight, int num, int type, String orderId) {
//        if (orderMap.size() >= 3) {
//        }
//        YumOrderInfo yumOrderInfo = new YumOrderInfo();
//        yumOrderInfo.setOrderId(orderId);
//        yumOrderInfo.setNum(num);
//        yumOrderInfo.setWeight(weight);
//        yumOrderInfo.setType(type);
//        String test = getDemoData(1);

//        String rd = "";
//        if (orderMap.size() < 3) {
//            bomid++;
//            if (bomid > 3) {
//                bomid = 1;
//            }
//            yumOrderInfo.setFryerId(bomid);
//            orderMap.put(yumOrderInfo.getOrderId(), yumOrderInfo);
//        } else {
//            rd = getJsonString(19007, errorCode.getCodeMsg(19007), null);
//            logReturun("createOrder", "orderMap: " + orderMap.size());
//            logReturun("createOrder", rd);
//            return rd;
//        }
//        if (!TextUtils.isEmpty(test)) {
//            logReturun("createOrder", test);
//            return test;
//        }
//        rd = getJsonString(0, "", null);
//        logReturun("createOrder", rd);
//        return rd;
//    }
//
//    int bomid = 0;
//
//    private synchronized void startBoom() {
////        startPoll();
//
//
//    }
//
//    boolean isPoll = false;
//
//
//    public String getStrAAr(String... parm) {
//        if (parm == null || parm.length < 1) {
//            return "";
//        }
//        String s = "";
//        for (int x = 0; x < parm.length; x++) {
//            s += parm[x] + "  ";
//        }
//        return s;
//    }
//
//    public String getMapKey(Map<String, YumOrderInfo> map) {
//        if (map == null) {
//            return "";
//        }
//        String str = "";
//        for (String s : map.keySet()) {
//            YumOrderInfo yumOrderInfo = map.get(s);
//            str += "order=" + s + ":   " + yumOrderInfo.getStatus() + "\n";
//        }
//        return str;
//    }
//
//    private void setCallback2(int code, String meesage, String... parm) {
//        YumOrderInfo yumOrderInfo = null;
//        if (meesage != null) {
//            yumOrderInfo = orderMap.get(meesage);
//        }
//        if (yumOrderInfo == null || parm == null || parm.length < 2) {
//            logx("setCallback2: " + getMapKey(orderMap));
//            String data = getJsonString(code, errorCode.getCodeMsg(code), null, temp);
//            callback.onSuccess(0, data);
//            return;
//        }
//        int status = XssUtility.getInt(parm[0]);
//        String orderid = meesage;
//        if (status >= 5) {
//            logx("setCallback2: orderid=" + orderid + "    status=" + status);
//            orderMap.remove(orderid);
//        }
//        yumOrderInfo.setStatus(status + 1);
//        JsonObject jsonObject = new JsonObject();
//        jsonObject.addProperty("orderId", yumOrderInfo.getOrderId());
//        jsonObject.addProperty("weight", yumOrderInfo.getWeight() + "");
//        if (status < 2) {
//            jsonObject.addProperty("realWeight", yumOrderInfo.getWeight());
//            jsonObject.addProperty("realNum", yumOrderInfo.getNum() + "");
//        } else {
//            jsonObject.addProperty("realWeight", parm[1] + "");
//            jsonObject.addProperty("realNum", yumOrderInfo.getRealNum() + "");
//        }
//        jsonObject.addProperty("num", yumOrderInfo.getNum() + "");
//        jsonObject.addProperty("type", yumOrderInfo.getType() + "");
//        jsonObject.addProperty("fryerId", yumOrderInfo.getFryerId() + "");
//        jsonObject.addProperty("status", status + "");
//        String data = getJsonString(code, errorCode.getCodeMsg(code), jsonObject, temp);
//        logReturun("callback", data);
//
//        callback.onSuccess(0, data);
//
//    }
//
//
//    private void setCallback14(int code, String meesage, String... parm) {
//        JsonObject jsRight = new JsonObject();
//        JsonObject jsleft = new JsonObject();
//        jsleft.addProperty("heatId", "1");
//        jsRight.addProperty("heatId", "2");
//        int typeLeft = 0;
//        long typeRight = 0;
//        long timeLeft = 0;
//        long timeright = 0;
//
//        if (historyList.size() < 1) {
//
//        } else if (historyList.size() < 2) {
//            YumOrderInfo yumOrderInfo = historyList.get(0);
//            typeLeft = yumOrderInfo.getType();
//            timeLeft = yumOrderInfo.getEndTime();
//        } else {
//            YumOrderInfo yumOrderInfol = historyList.get(historyList.size() - 1);
//            typeLeft = yumOrderInfol.getType();
//            timeLeft = yumOrderInfol.getEndTime();
//
//            YumOrderInfo yumOrderInfoR = historyList.get(historyList.size() - 2);
//            typeRight = yumOrderInfoR.getType();
//            timeright = yumOrderInfoR.getEndTime();
//        }
//        jsleft.addProperty("time", timeLeft);
//        jsleft.addProperty("type", typeLeft);
//
//        jsRight.addProperty("time", timeright);
//        jsRight.addProperty("type", typeRight);
//        JsonArray jsonArray = new JsonArray();
//        jsonArray.add(jsleft);
//        jsonArray.add(jsRight);
//
//
//        String data = getJsonString(0, "", jsonArray);
//        logReturun("callback", data);
//        if (heatResultCallback != null)
//            heatResultCallback.onSuccess(data);
//
//    }
//
//
//    @Override
//    public void setOrderResultCallback(Context context, OrderResultCallback callback) {
//        logx("setOrderResultCallback");
//
//        this.callback = callback;
///*                 "orderId":"",//订单号
//                    "weight":1,//计划油炸总重量（薯条为克，鸡块为块）
//                    "num":1,//计划数量
//                    "realWeight":1,//计划油炸总重量（薯条为克，鸡块为块）(落料和实际生产的重量，默认为计划值)
//                    "realNum":1,//实际数量（默认为计划值）
//                    "type":1,//类型（1-薯条、2-波纹薯条、3-黄金鸡块、4-红豆派、5-LTO）
//                    "status":1,//状态类型（1-物料确认完成、2-落料完成、3-烹炸完成、4-转移完成、5-订单制作完成）*/
//
////        callback.onSuccess(0,);
//    }
//
//
//    @Override
//    public String getProductionStatus(Context context) {
//        logx("getProductionStatus");
//        String test = getDemoData(3);
//        if (!TextUtils.isEmpty(test)) {
//            logReturun("getProductionStatus", test);
//            return test;
//        }
//        JsonArray jsonArray = new JsonArray();
//        JsonObject j1 = new JsonObject();
//        j1.addProperty("orderId", "111111");
//
//        j1.addProperty("status", "1");
//        j1.addProperty("fryerId", "1");
//        jsonArray.add(j1);
//        return getJsonString(0, "", jsonArray);
//    }
//
//
//    @Override
//    public String getMaterialStatus(Context context) {
//        logx("getMaterialStatus");
//        String test = getDemoData(4);
//        if (!TextUtils.isEmpty(test)) {
//            logReturun("getMaterialStatus", test);
//            return test;
//        }
//        JsonObject j1 = new JsonObject();
//        j1.addProperty("status", "1");
//        return getJsonString(0, "", j1);
//
//    }
//
//    @Override
//    public String getTrussStatus(Context context) {
//        logx("getTrussStatus");
//        String test = getDemoData(5);
//        if (!TextUtils.isEmpty(test)) {
//            return test;
//        }
//        JsonObject j1 = new JsonObject();
//        j1.addProperty("status", "1");
//        return getJsonString(0, "", j1);
//    }
//
//    @Override
//    public String getHeatStatus(Context context) {
//        logx("getHeatStatus");
//
//        String test = getDemoData(6);
//        if (!TextUtils.isEmpty(test)) {
//            logReturun("getHeatStatus", test);
//            return test;
//        }
//        JsonObject j1 = new JsonObject();
//        j1.addProperty("status", "1");
//
//        return getJsonString(0, "", j1);
//    }
//
//    @Override
//    public String getFryerStatus(Context context) {
//        logx("getFryerStatus");
//
//        String test = getDemoData(7);
//        if (!TextUtils.isEmpty(test)) {
//            logReturun("getFryerStatus", test);
//            return test;
//        }
//        JsonArray jsonArray = new JsonArray();
//        JsonObject j1 = new JsonObject();
//        j1.addProperty("fryerId", "1");
//        j1.addProperty("status", "1");
//        j1.addProperty("fryerType", "1");
//        jsonArray.add(j1);
//        return getJsonString(0, "", jsonArray);
//    }
//
//    /**
//     * @param context：Android上下文环境 mode：0--自动模式，1--手动模式
//     * @return [{
//     * "fryerId":1,//炸锅Id
//     * "fryerType":1,//炸锅类型，0-荤锅、1-素锅
//     * }]
//     * @author hua
//     * @time 2022/4/18 10:39
//     */
//    @Override
//    public String setFryerInfo(Context context, String message) {
//        logx("setFryerInfo:   message=" + message);
//
//        String test = getDemoData(8);
//        if (!TextUtils.isEmpty(test)) {
//            logReturun("setFryerInfo", test);
//            return test;
//        }
//        JsonArray jsonArray = new JsonArray();
//        JsonObject j1 = new JsonObject();
//        j1.addProperty("fryerId",
//                "1");
//        j1.addProperty("fryerType", "0");
//        jsonArray.add(j1);
//        return getJsonString(0, "", jsonArray);
//    }
//
//    @Override
//    public String setWorkMode(Context context, int mode) {
//        logx("setFryerInfo:   setWorkMode    mode=" + mode);
//
//        String test = getDemoData(9);
//        if (!TextUtils.isEmpty(test)) {
//            logReturun("setWorkMode", test);
//
//            return test;
//        }
//        return getJsonString(0, "");
//    }
//
//
//    @Override
//    public String checkDevice(Context context) {
//        logx("checkDevice");
//        String test = getDemoData(10);
//        if (!TextUtils.isEmpty(test)) {
//            logReturun("checkDevice", test);
//            return test;
//        }
//        return getJsonString(0, "");
//    }
//
//    public void openSetting(Activity context) {
//        logx("openSetting");
//
//        Intent intent = new Intent(context, KfcSetting.class);
//        context.startActivity(intent);
//    }
//
//    /**
//     * 14 实时返回两个保温槽状态
//     *
//     * @param
//     * @return
//     * @author hua
//     * @time 2022/6/13 9:28
//     */
//    public void setHeatResultCallback(Context context, YumResultCallback callback) {
//        logx("setHeatResultCallback:   callback=" + callback);
//        heatResultCallback = callback;
//    }
//
//    public void init(Context context) {
//
//        MuhuaLog.getInstance().initLog(context);
//        XssSavaData.getInstance().init(context);
//        XssSavaData.getInstance().setMacType(XssData.ShuTiaoZhan);
//        XssTrands.getInstanll().init(context, R.array.kfc_errorcode, R.array.kfc_action);
//        logx(" init:   ");
//        orderMap.clear();
//        openCashService(context);
//        errorCode = new ErrorCode();
//        errorCode.init(context);
//
//
//    }
//
//    public void openCashService(Context context) {
//        boolean b = XssTrands.getInstanll().isAppForeground(context);
//        logx(" openCashService:   " + b + "    isOpenCash: " + isOpenCash);
//        if (isOpenCash) {
//            return;
//        }
//        if (b) {
//            isOpenCash = true;
//            Intent m_intent_Service = new Intent(context, CashService.class);
//            context.startService(m_intent_Service);
//        }
//    }
//
//    public String getJsonString(int code, String meessage) {
//        return getJsonString(code, meessage, null);
//    }
//
//    public String getJsonString(int code, String meessage, JsonElement dataJson) {
//        return getJsonString(code, meessage, dataJson, "");
//    }
//
//    public String getJsonString(int code, String meessage, JsonElement dataJson, String temperature) {
//        JsonObject jsonObject = new JsonObject();
//        jsonObject.addProperty(RESULT, String.valueOf(code));
//        jsonObject.addProperty(MESSAGE, meessage);
//        if (!TextUtils.isEmpty(temperature)) {
//            jsonObject.addProperty("temperature", temperature);
//        }
//        if (dataJson != null) {
//            jsonObject.add(DATA, dataJson);
//        }
//
//        return jsonObject.toString();
//    }
//
//
//}
