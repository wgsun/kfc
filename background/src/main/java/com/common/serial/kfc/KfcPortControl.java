package com.common.serial.kfc;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.common.base.BeanInfo.KfcOrder;
import com.common.base.BeanInfo.PortFormat;
import com.common.base.MsgWhat;
import com.common.base.XssEventInfo;
import com.common.base.XssTrands;
import com.common.base.utils.XssData;
import com.common.base.utils.XssUtility;
import com.hua.back.kfc.base.ErrorCodeControl;
import com.hua.back.kfc.base.YumControl;


import java.util.ArrayList;
import java.util.List;

import static com.common.base.MsgWhat.PORTDATA;


/**
 * FileName: KfcPortControl
 * Author: hua
 * Date: 2022/1/4 18:13
 * Description:    薯条站中枢控制
 */
public class KfcPortControl extends KfcUitlControl {


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case queryDropStats:
                    XssTrands.getInstanll().queryStatus();
                    break;
                case msgScanLan:
                    handler.removeMessages(msgScanLan);
                    isScanLanNeed = true;
                    XssTrands.getInstanll().actionHex(4, "00000000");
                    break;
                case heatYum:
                    sendHeatTime(8);
                    if (PowerStatus == 2) {
                        isScanLanNeed = true;
                        handler.removeMessages(msgScanLan);
                        if (status != 0) {
                            status = 0;//机器断电 bomStatus    改为 0
                        }
                    }
                    YumControl.getInstall().setHeatInterFace(PowerStatus, boxTemp);
                    break;
                case 1001:
                    dealBomStatus();
                    break;
            }
        }
    };

    //查询命令
    public void dealBomStatus() {
        if (XssTrands.getInstanll().isContinClick("1001", 10)) {
            handler.removeMessages(1001);
            sendBomStatus(5);
            return;
        }
        handler.removeMessages(1001);
        sendBomStatus(20);
        if (dorpState == enumFlow.two || dorpState == enumFlow.six) {
            if (status != 1) {
                status = 1;//dealBomStatus
            }
            sendBomQueryCmd(1);
        } else if (dorpState == enumFlow.three || dorpState == enumFlow.seven) {
            if (status != 2) {
                status = 2;//dealBomStatus
            }
            sendBomQueryCmd(2);
        } else {
            sendBomQueryCmd(status);
        }
    }

    public void scanLan() {
        handler.removeMessages(msgScanLan);
        handler.sendEmptyMessageDelayed(msgScanLan, 4 * 1000);
        isScanLanNeed = true;
        XssTrands.getInstanll().actionHex(4, "00000000");
    }

    /**
     * 发送心跳
     *
     * @param
     * @return
     * @author hua
     * @time 2022/7/14 15:56
     */
    public void sendHeatTime(int time) {
        if (time == 0) {
            handler.removeMessages(heatYum);
            handler.sendEmptyMessage(heatYum);
        } else {
            handler.sendEmptyMessageDelayed(heatYum, time * 1000);
        }

    }

    public static KfcPortControl getInstall() {
        if (kfcPortControl == null) {
            synchronized (KfcPortControl.class) {
                if (kfcPortControl == null) {
                    kfcPortControl = new KfcPortControl();
                }
            }
        }
        return kfcPortControl;

    }

    public void init(serialtwo serialtwo, serialOne bomSerial) {
        XssTrands.getInstanll().registerListener(m_vendListener);
        serialtow = serialtwo;
        this.Serialone = bomSerial;
        sendBomStatus(50);
        foodMap1.put(1, true);
        foodMap1.put(3, true);
        foodMap1.put(2, true);
        cleanFoodNotExsitMap();

        powerMap.put(1, 0l);
        powerMap.put(2, 0l);
        powerMap.put(3, 0l);


    }

    /**
     * 加载订单
     *
     * @param
     * @return
     * @author hua
     * @time 2022/1/18 14:17
     */
    public synchronized void shipOrer(String order, int unitNum, int weight, int type) {
        KfcOrder kfcOrder = new KfcOrder();
        kfcOrder.setWeight(weight);
        kfcOrder.setUnitNum(unitNum);
        kfcOrder.setOrder(order);
        kfcOrder.setType(type);
        kfcOrder.setFlow(0);
        int isMeat = isMeat(type);//0素，1荤
        kfcOrder.setIsmeat(isMeat);
        logx(" shipOrer:  " + "   weight=" + weight + "    " + getTypeName(kfcOrder.getType()) + "     " + order + "   isMeat:" + isMeat + "    chadanList.size=" + chadanList.size());
        if (type > 3) {
            kfcOrder.setType(type);
            chadanList.add(kfcOrder);
            AllList.add(0, kfcOrder);
        } else {
            AllList.add(kfcOrder);
        }
        XssTrands.getInstanll().sendQureyACT(999);
        sendSHow();
        addOrder();
    }

    public synchronized boolean isTwoOrder(String order) {
        if (TextUtils.isEmpty(order) || AllList.size() < 1) {
            return false;
        }
        for (int x = 0; x < AllList.size(); x++) {
            KfcOrder kfcOrder = AllList.get(x);
            if (kfcOrder != null) {
                if (order.equals(kfcOrder.getOrder())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param
     * @return
     * @author hua
     * @time 2022/2/14 11:53
     */
    public synchronized void addOrder() {
        if (isPower()) {
            XssTrands.getInstanll().sendMsgToUIDelay(MsgWhat.KFCStart, 0, 1, 1, 5000, "");
            return;
        }
        if (XssTrands.getInstanll().isContinClick("startWeight", 30)) {
            XssTrands.getInstanll().sendMsgToUIDelay(MsgWhat.KFCStart, 0, 1, 1, 3000, "");
            return;
        }
        XssTrands.getInstanll().sendMsgToUIDelay(MsgWhat.KFCStart, 0, 1, 1, 60 * 1000, "");
        if (!isCanWeight()) {
            return;
        }
        List<String> listFinish = new ArrayList<>();
        for (int x = 0; x < AllList.size(); x++) {
            KfcOrder kfcOrder = AllList.get(x);
            if (kfcOrder.getFlow() == 16) {
                listFinish.add(kfcOrder.getOrder());
            }
            if (kfcOrder.getFlow() < 1) {
                if (isExistFalut(kfcOrder)) {
                    listFinish.add(kfcOrder.getOrder());
                    XssTrands.getInstanll().sendMsgToUIDelay(MsgWhat.KFCStart, 0, 1, 1, 3 * 1000, "");
                    addFinishOrder(kfcOrder);
                    break;
                }
                int foodCmd = getTypeCmd(kfcOrder.getType());
                if (isWeightNoFood(foodCmd)) {
                    AllList.remove(x);
                    AllList.add(0, kfcOrder);
                    setFlow(kfcOrder, 1);
                    startMap.put(foodCmd, kfcOrder);
                    kfcOrder.setStartTime(System.currentTimeMillis());
                    serialtow.actionHex(kfcOrder.getType(), XssUtility.getHexLeng(kfcOrder.getWeight(), 4) + "0000");
                    break;
                }
            }
        }
        if (listFinish.size() > 0) {
            for (int x = AllList.size() - 1; x >= 0; x--) {
                if (AllList.get(x).getFlow() == 16) {
                    AllList.remove(x);
                }
            }
        }
    }

    /**
     * 判断是否有故障，可以下单
     *
     * @param
     * @return
     * @author hua
     * @time 2022/7/6 15:45
     */
    public boolean isExistFalut(KfcOrder kfcOrder) {
        int code = YumControl.getInstall().getErrorClz().getCanExeType(kfcOrder.getType());
        if (code > 0) {
            kfcOrder.setMidEnd(true);
            kfcOrder.setErrorCode(code);
            return true;
        }
        return false;
    }

    public boolean isExistBomFalut(KfcOrder kfcOrder) {
        int code = YumControl.getInstall().getErrorClz().getBomExeType();
        if (code > 0) {
            kfcOrder.setMidEnd(true);
            kfcOrder.setWeightNow(0);
            kfcOrder.setErrorCode(code);
            return true;
        }
        return false;
    }

    /**
     * 回篮
     *
     * @param
     * @return
     * @author hua
     * @time 2022/1/18 14:18
     */
    public boolean recvFoodBom(KfcBomInfo kfcBomInfo) {
        if (isrecv) {
            return false;
        }
        isrecv = true;
        boolean isNoChanDan = isNoChanDanDeal(kfcBomInfo);//是否不需要处理插单
        logx("recvFoodBom  isNoChanDan: " + isNoChanDan + "   isCanBomStatus: " + isCanBomStatus());
        if (isNoChanDan && isCanBomStatus()) {
            if (isScanLanNeed) {//
            } else if (isHasFoodAll(kfcBomInfo, true)) {
                status = 1;
                sendBomQueryCmd(1);
                sendBomStatus(0);
                dorpState = enumFlow.five;
                return true;
            }
        }
        if (!isNoChanDan) {
            status = 1;
            sendBomQueryCmd(1);
            sendBomStatus(0);
        }
        recvFood();//正常回篮
        return true;
    }


    /**
     * 回篮
     *
     * @param
     * @return
     * @author hua
     * @time 2022/1/18 14:18
     */
    public void recvFood() {
        dorpState = enumFlow.eight;
        lanNum++;
        rcvStatus = 0;
        serialtow.actionHex(9, "00000000");
        logx("cleanFault  bomSerial ");
        Serialone.cleanFault("000100" + "000000");
    }


    /**
     * 查询炸炉状态
     *
     * @param
     * @return
     * @author hua
     * @time 2022/1/18 13:58
     */
    synchronized void sendBomStatus(int time) {
        if (time == 0) {
            handler.removeMessages(1001);
            handler.sendEmptyMessageDelayed(1001, 20 * 100);
        } else {
            handler.sendEmptyMessageDelayed(1001, time * 100);
        }
    }


    /**
     * 返回参数订单状态处理
     *
     * @param
     * @return
     * @author hua
     * @time 2022/2/14 14:36
     */
    public void dealStatesActCmd(int dorpFlow, int food, int weigh) {
        KfcOrder kfcOrder = startMap.get(food);
        if (kfcOrder != null) {
            if (170 == dorpFlow) {
                if (kfcOrder.getFlow() == 1) {
                    if (weigh > 0) {
                        kfcOrder.setWeightNow(weigh);
                    } else if (kfcOrder.getWeightNow() < 10) {
                        kfcOrder.setWeightNow(weigh);
                    }
                    setFlow(kfcOrder, 2);
                    addOrder();
                } else {
                }
            } else {
                if (weigh > 0) {
                    kfcOrder.setWeightNow(weigh);
                }
            }
        }


    }


    /**
     * 是否没有插单订单需要处理
     *
     * @param
     * @return
     * @author hua
     * @time 2022/6/13 19:24
     */
    public synchronized boolean isNoChanDanDeal(KfcBomInfo kfcBomInfo) {
        boolean isNoChanDan = true;
        if (chadanList.size() == 0) {
            return isNoChanDan;
        }
        for (int x = 0; x < chadanList.size(); x++) {
            KfcOrder BoosOrder = chadanList.get(x);
            if (BoosOrder != null) {
                if (BoosOrder.getFlow() == 8) {
                    return false;
                }
                if (BoosOrder.getFlow() == 0) {
                    if (ishasFoods(kfcBomInfo, BoosOrder, true)) {
                        return false;
                    }

                }
            }
        }
        return isNoChanDan;

    }


    public synchronized boolean dealBoosOrder(KfcBomInfo kfcBomInfo) {
        if (chadanList.size() < 1) {
            return false;
        }

        if (isScanLanNeed) {//dealBoosOrder
            return false;
        }

        if (tempBoosOrder != null) {
            boolean isCanDanStatus = isCanChaDanBomStatus();
            if (tempBoosOrder.getFlow() == 8) {
                if (isExistBomFalut(tempBoosOrder)) {
                    addFinishOrder(tempBoosOrder);
                    return true;
                }
                if (!isCanDanStatus) {
                    return true;
                }
                if (ishasFoods(kfcBomInfo, tempBoosOrder, true)) {
                    setFlow(tempBoosOrder, 3);
                    dorpState = enumFlow.drop10;
                    XssTrands.getInstanll().actionHex(6, "01" + "000000");
                    return true;
                }
            } else if (tempBoosOrder.getFlow() == 3) {
                if (dorpState == enumFlow.drop10) {
                } else if (dorpState == enumFlow.drop11) {
                    if (bossKey != -1) {
                        return false;
                    }
                    int key = getkey(tempBoosOrder);
                    if (key == -1) {
                        return false;
                    }
                    KfcOrder kfcOrder = tempBoosOrder;
                    int type = kfcOrder.getType();
                    int isInser = type > 3 ? 1 : 0;
                    if (key == -1) {
                        return true;
                    }
                    int weight = kfcOrder.getWeightNow();
                    if (type == 2) {
                        weight = YumControl.getInstall().getWeight(kfcOrder.getWeightNow());
                    }
                    shipMap.put(key, kfcOrder);
                    status = 2;
                    sendBomQueryCmd(2);
                    sendBomStatus(0);
                    setFlow(kfcOrder, 10);
                    bossKey = key;
                    Serialone.ship(key, type, kfcOrder.getIsmeat(), isInser, weight);
                    tempBoosOrder = null;
                }
                return true;
            }
        } else {
            for (int x = 0; x < chadanList.size(); x++) {
                KfcOrder kfcOrder = chadanList.get(x);
                if (kfcOrder.getFlow() == 0) {
                    if (isExistFalut(kfcOrder)) {
                        addFinishOrder(kfcOrder);
                        return true;
                    }
                    if (ishasFoods(kfcBomInfo, kfcOrder, true)) {
                        if (isCanChaDanBomStatus()) {
                            status = 1;
                            sendBomQueryCmd(1);
                            sendBomStatus(0);
                            setFlow(kfcOrder, 8);
                            tempBoosOrder = kfcOrder;
                            return true;
                        }

                    }

                }
            }

        }
        return false;
    }


    public int getFlow(String result) {
        int dorpFlow = 0;
        if (result.equals("00")) {
            dorpFlow = 0;
        } else if (result.equals("AA")) {
            dorpFlow = 170;
        } else if (result.equals("F1") ||
                result.equals("F2") ||
                result.equals("F3") ||
                result.equals("FA") ||
                result.equals("FF")) {
            dorpFlow = -2;

        } else {
            int flow = Integer.parseInt(result, 16);
            dorpFlow = flow;
        }
        return dorpFlow;
    }


    private VendListener m_vendListener = new VendListener();

    private class VendListener implements XssTrands.VendEventListener {

        @Override
        public void VendEvent(XssEventInfo cEventInfo) {
            switch (cEventInfo.m_iEventID) {
                case MsgWhat.ERRORCODE:
                    int errorCodeMsg = cEventInfo.m_lParam1;
                    if (MsgWhat.errorCode9002 == errorCodeMsg) {
                        if (!listErrorOne.contains(errorCodeMsg)) {
                            if (listErrorTwo.contains(MsgWhat.errorCode9003)) {
                                setPowerError(true);
                            }
                            listErrorOne.add(errorCodeMsg);
                            if (AllList == null || AllList.size() < 1) {
                                return;
                            }
                            ErrorCodeControl codeControl = YumControl.getInstall().getErrorClz();
                            List<Integer> listIgnore = codeControl.getListIgnoreExeOrder();
                            List<Integer> listDorpCodeTypeAll = codeControl.getListDorpCode();
                            List<Integer> listErrorDorpExist = getListErrorDrop();
                            List<Integer> listErrorBomExist = getListErrorTwo();
                            for (int i = 0; i < AllList.size(); i++) {
                                KfcOrder kfcOrder = AllList.get(i);
                                if (kfcOrder != null && kfcOrder.getFlow() > 0) {
                                    if (listErrorBomExist != null && listErrorBomExist.size() > 0) {
                                        kfcOrder.setErrorCode(listErrorBomExist.get(0));
                                        YumControl.getInstall().setCallbackOrder(kfcOrder, boxTemp);
                                    } else if (kfcOrder.getFlow() < 10) {
                                        int code = 0;
                                        for (int index = 0; index < listErrorDorpExist.size(); index++) {
                                            int temp = listErrorDorpExist.get(index) % XssData.NumError10000;
                                            if (listDorpCodeTypeAll.contains(temp)) {
                                                List<Integer> listType = codeControl.getListTypeCode(kfcOrder.getType());
                                                if (listType.contains(temp)) {
                                                    code = listErrorDorpExist.get(index);
                                                    if (!listIgnore.contains(temp)) {
                                                        break;
                                                    }
                                                }
                                            } else {
                                                code = listErrorDorpExist.get(index);
                                                break;
                                            }
                                        }
                                        if (code > 0) {
                                            kfcOrder.setErrorCode(code);
                                            YumControl.getInstall().setCallbackOrder(kfcOrder, boxTemp);
                                        }

                                    }
                                }
                            }
                        }
                    } else if (MsgWhat.errorCode9003 == errorCodeMsg) {
                        if (!listErrorTwo.contains(errorCodeMsg)) {
                            if (listErrorOne.contains(MsgWhat.errorCode9002)) {
                                setPowerError(true);
                            }
                            listErrorTwo.add(errorCodeMsg);
                            if (AllList == null || AllList.size() < 1) {
                                return;
                            }
                            ErrorCodeControl codeControl = YumControl.getInstall().getErrorClz();
                            List<Integer> listIgnore = codeControl.getListIgnoreExeOrder();
                            List<Integer> listDorpCodeTypeAll = codeControl.getListDorpCode();
                            List<Integer> listErrorDorpExist = getListErrorDrop();
                            List<Integer> listErrorBomExist = getListErrorTwo();
                            for (int i = 0; i < AllList.size(); i++) {
                                KfcOrder kfcOrder = AllList.get(i);
                                if (kfcOrder != null && kfcOrder.getFlow() > 0) {
                                    if (listErrorBomExist != null && listErrorBomExist.size() > 0) {
                                        kfcOrder.setErrorCode(listErrorBomExist.get(0));
                                        YumControl.getInstall().setCallbackOrder(kfcOrder, boxTemp);
                                    } else if (kfcOrder.getFlow() < 10) {
                                        int code = 0;
                                        for (int index = 0; index < listErrorDorpExist.size(); index++) {
                                            int temp = listErrorDorpExist.get(index) % XssData.NumError10000;
                                            if (listDorpCodeTypeAll.contains(temp)) {
                                                List<Integer> listType = codeControl.getListTypeCode(kfcOrder.getType());
                                                if (listType.contains(temp)) {
                                                    code = listErrorDorpExist.get(index);
                                                    if (!listIgnore.contains(temp)) {
                                                        break;
                                                    }
                                                }
                                            } else {
                                                code = listErrorDorpExist.get(index);
                                                break;
                                            }
                                        }
                                        if (code > 0) {
                                            kfcOrder.setErrorCode(code);
                                            YumControl.getInstall().setCallbackOrder(kfcOrder, boxTemp);
                                        }

                                    }
                                }
                            }
                        }
                    }

                    break;
                case MsgWhat.KFCStart:
                    addOrder();
                    break;
                case PORTDATA:
                    XssTrands.getInstanll().sendMsgToUI(MsgWhat.SHOWERROR, cEventInfo.m_lParam4);
                    PortFormat portFormat = XssTrands.getInstanll().getPortFormat(cEventInfo.m_lParam4);
                    if (portFormat == null || TextUtils.isEmpty(portFormat.getCmd())) {
                        return;
                    }
                    String cmd = portFormat.getCmd();
                    int addr = portFormat.getAddr();
                    if (portFormat.isSettingErrcode()) {
                        int dropSize = listErrorOne.size();
                        listErrorOne.clear();
                        if (!listErrorTwo.contains(MsgWhat.errorCode9003)) {
                            setPowerError(false);
                        }
                        List<Integer> errorList = portFormat.getErrorList();
                        if (errorList != null && errorList.size() > 0) {
                            String tempData = "PORTDATA  ";
                            for (int index = 0; index < errorList.size(); index++) {
                                int dropCode = errorList.get(index);
                                listErrorOne.add(dropCode);
                                if (AllList == null || AllList.size() < 1) {
                                    return;
                                }
                                ErrorCodeControl codeControl = YumControl.getInstall().getErrorClz();
                                List<Integer> listIgnore = codeControl.getListIgnoreExeOrder();
                                List<Integer> listDorpCodeTypeAll = codeControl.getListDorpCode();
                                List<Integer> listErrorDorpExist = getListErrorDrop();
                                List<Integer> listErrorBomExist = getListErrorTwo();
                                for (int i = 0; i < AllList.size(); i++) {
                                    KfcOrder kfcOrder = AllList.get(i);
                                    if (kfcOrder != null && kfcOrder.getFlow() > 0) {
                                        if (listErrorBomExist != null && listErrorBomExist.size() > 0) {
                                            kfcOrder.setErrorCode(listErrorBomExist.get(0));
                                            YumControl.getInstall().setCallbackOrder(kfcOrder, boxTemp);
                                        } else if (kfcOrder.getFlow() < 10) {
                                            int code = 0;
                                            for (int listDorpindex = 0; listDorpindex < listErrorDorpExist.size(); listDorpindex++) {
                                                int temp = listErrorDorpExist.get(listDorpindex) % XssData.NumError10000;
                                                if (listDorpCodeTypeAll.contains(temp)) {
                                                    List<Integer> listType = codeControl.getListTypeCode(kfcOrder.getType());
                                                    if (listType.contains(temp)) {
                                                        code = listErrorDorpExist.get(listDorpindex);
                                                        if (!listIgnore.contains(temp)) {
                                                            break;
                                                        }
                                                    }
                                                } else {
                                                    code = listErrorDorpExist.get(listDorpindex);
                                                    break;
                                                }
                                            }
                                            if (code > 0) {
                                                kfcOrder.setErrorCode(code);
                                                YumControl.getInstall().setCallbackOrder(kfcOrder, boxTemp);
                                            }

                                        }
                                    }
                                }
                                tempData += dropCode + "=" + XssTrands.getInstanll().getErrCode(dropCode, false) + "    ";
                                YumControl.getInstall().setScanDealFace(dropCode, lanNum);
                            }
                            if (!TextUtils.isEmpty(printDropData) && tempData.equals(printDropData)) {
                                printDropData = "";
                            } else {
                                printDropData = tempData;
                                XssTrands.getInstanll().logd("KfcUitlControl", printDropData);
                            }
                        }
                        if (dropSize != listErrorOne.size()) {
                            sendHeatTime(0);
                        }
                    }
                    if ("80".equals(cmd)) {
                        portFormat80 = portFormat;
                        boxTemp = portFormat.getStatesDrop80().getTemp();
                        XssTrands.getInstanll().sendMsgToUI(MsgWhat.UPDATEStates, "");
                        foodMap1.put(1, 0 == portFormat.getStatesDrop80().getMeteChips());
                        foodMap1.put(2, 0 == portFormat.getStatesDrop80().getMeteChcken());
                        foodMap1.put(3, 0 == portFormat.getStatesDrop80().getMeteChips2());
                        if (portFormat.getStatesDrop80().isPower()) {
                            setTwoPower(true);
                            handler.sendEmptyMessageDelayed(queryDropStats, 2 * 1000);
                        } else {
                            setTwoPower(false);

                            setPowerError(false);
                        }
                        lanNum = portFormat.getStatesDrop80().getLanNum();
                    }
                    if (isScanLanNeed) {
                        List<PortFormat.StatesActCmd> list = portFormat.getList();
                        if (list != null && list.size() > 0) {
                            for (int x = 0; x < list.size(); x++) {
                                PortFormat.StatesActCmd statesActCmd = list.get(x);
                                int dorpFlow = getFlow(statesActCmd.getCheck());
                                int sudu = Integer.parseInt(statesActCmd.getOther(), 16);
                                if (statesActCmd.getCmd() == 4) {
                                    handler.removeMessages(msgScanLan);
                                    if (isScanLanNeed) {
                                        if (dorpFlow == 170) {
                                            isScanLanNeed = false;
                                            lanNum = portFormat.getLanNum();
                                            YumControl.getInstall().setScanDealFace(0, lanNum);
                                        }
                                    }
                                    return;
                                }
                                switch (statesActCmd.getCmd()) {
                                    case 1:
                                        dealStatesActCmd(dorpFlow, 1, sudu);
                                        break;
                                    case 2:
                                        dealStatesActCmd(dorpFlow, 2, sudu);
                                        break;
                                    case 3:
                                        dealStatesActCmd(dorpFlow, 3, sudu);
                                        break;
                                    case 4:
                                        if (dorpFlow == 170) {
                                            XssTrands.getInstanll().queryStatus();
                                        }
                                        break;
                                    case 5:
                                    case 7:
                                        if (dorpFlow == 170) {
                                            if (dorpState == enumFlow.two) {
                                                if (System.currentTimeMillis() - startMoveFoodTime > 9 * 1000) {
                                                    dorpState = enumFlow.three;
                                                    if (shipMap != null && shipMap.size() > 3) {
                                                        return;
                                                    }

                                                    if (TextUtils.isEmpty(currDorpOrder)) {
                                                        return;
                                                    }
                                                    if (dorpMap == null || dorpMap.size() < 1) {
                                                        return;
                                                    }
                                                    KfcOrder kfcOrder = dorpMap.get(currDorpOrder);
                                                    if (kfcOrder == null) {
                                                        return;
                                                    }
                                                    if (kfcOrder.getFlow() != 3) {
                                                        return;
                                                    }

                                                    int key = getkey(kfcOrder);
                                                    if (key > -1) {
                                                        int type = kfcOrder.getType();
                                                        int isInser = type > 3 ? 1 : 0;
                                                        if (key == -1) {
                                                            return;
                                                        }
                                                        int weight = kfcOrder.getWeightNow();
                                                        if (type == 2) {
                                                            weight = YumControl.getInstall().getWeight(kfcOrder.getWeightNow());
                                                        }
                                                        shipMap.put(key, kfcOrder);
                                                        status = 2;
                                                        sendBomQueryCmd(2);
                                                        sendBomStatus(0);
                                                        setFlow(kfcOrder, 10);
                                                        bossKey = key;
                                                        Serialone.ship(key, type, kfcOrder.getIsmeat(), isInser, weight);
                                                    } else {
                                                    }
                                                    addOrder();
                                                }
                                            } else if (dorpState == enumFlow.six) {
                                                if (System.currentTimeMillis() - startMoveFoodTime > 7 * 1000) {
                                                    dorpState = enumFlow.seven;
                                                    if (shipMap != null && shipMap.size() > 3) {
                                                        return;
                                                    }

                                                    if (TextUtils.isEmpty(currDorpOrder)) {
                                                        return;
                                                    }
                                                    if (dorpMap == null || dorpMap.size() < 1) {
                                                        return;
                                                    }
                                                    KfcOrder kfcOrder = dorpMap.get(currDorpOrder);
                                                    if (kfcOrder == null) {
                                                        return;
                                                    }
                                                    if (kfcOrder.getFlow() != 3) {
                                                        return;
                                                    }

                                                    int key = getkey(kfcOrder);
                                                    if (key > -1) {
                                                        int type = kfcOrder.getType();
                                                        int isInser = type > 3 ? 1 : 0;
                                                        if (key == -1) {
                                                            return;
                                                        }
                                                        int weight = kfcOrder.getWeightNow();
                                                        if (type == 2) {
                                                            weight = YumControl.getInstall().getWeight(kfcOrder.getWeightNow());
                                                        }
                                                        shipMap.put(key, kfcOrder);
                                                        status = 2;
                                                        sendBomQueryCmd(2);
                                                        sendBomStatus(0);
                                                        setFlow(kfcOrder, 10);
                                                        bossKey = key;
                                                        Serialone.ship(key, type, kfcOrder.getIsmeat(), isInser, weight);
                                                    } else {
                                                    }
                                                    addOrder();
                                                }
                                            }
                                        }
                                        break;
                                    case 6:
                                        if (dorpFlow == 170) {
                                            if (dorpState == enumFlow.drop10) {
                                                status = 1;
                                                sendBomQueryCmd(1);
                                                sendBomStatus(0);
                                                dorpState = enumFlow.drop11;
                                            } else if (dorpState == enumFlow.dorp12) {
                                                dorpState = enumFlow.drop13;
                                            }
                                        }

                                        break;
                                    case 9:
                                        if (dorpFlow == 170) {
                                            if (dorpState == enumFlow.eight) {
                                                dorpState = enumFlow.nine;
                                                rcvStatus = dorpFlow;
                                                addOrder();
                                            } else {
                                                rcvStatus = dorpFlow;
                                            }
                                        } else {
                                            rcvStatus = dorpFlow;
                                        }
                                        break;
                                }
                            }

                        } else {
                            XssTrands.getInstanll().sendQureyACT(0);
                        }
                    } else {
                        if (!isShipUI) {
                            if (isOrderExeing()) {

                            } else {
                                return;
                            }
                        }

                        List<PortFormat.StatesActCmd> list = portFormat.getList();
                        if (list != null && list.size() > 0) {
                            for (int x = 0; x < list.size(); x++) {
                                PortFormat.StatesActCmd statesActCmd = list.get(x);
                                int dorpFlow = getFlow(statesActCmd.getCheck());
                                int sudu = Integer.parseInt(statesActCmd.getOther(), 16);
                                if (statesActCmd.getCmd() == 4) {
                                    handler.removeMessages(msgScanLan);
                                    if (isScanLanNeed) {
                                        if (dorpFlow == 170) {
                                            isScanLanNeed = false;
                                            lanNum = portFormat.getLanNum();
                                            YumControl.getInstall().setScanDealFace(0, lanNum);
                                        }
                                    }
                                    return;
                                }
                                switch (statesActCmd.getCmd()) {
                                    case 1:
                                        dealStatesActCmd(dorpFlow, 1, sudu);
                                        break;
                                    case 2:
                                        dealStatesActCmd(dorpFlow, 2, sudu);
                                        break;
                                    case 3:
                                        dealStatesActCmd(dorpFlow, 3, sudu);
                                        break;
                                    case 4:
                                        if (dorpFlow == 170) {
                                            XssTrands.getInstanll().queryStatus();
                                        }
                                        break;
                                    case 5:
                                    case 7:
                                        if (dorpFlow == 170) {
                                            if (dorpState == enumFlow.two) {
                                                if (System.currentTimeMillis() - startMoveFoodTime > 9 * 1000) {
                                                    dorpState = enumFlow.three;
                                                    if (shipMap != null && shipMap.size() > 3) {
                                                        return;
                                                    }

                                                    if (TextUtils.isEmpty(currDorpOrder)) {
                                                        return;
                                                    }
                                                    if (dorpMap == null || dorpMap.size() < 1) {
                                                        return;
                                                    }
                                                    KfcOrder kfcOrder = dorpMap.get(currDorpOrder);
                                                    if (kfcOrder == null) {
                                                        return;
                                                    }
                                                    if (kfcOrder.getFlow() != 3) {
                                                        return;
                                                    }

                                                    int key = getkey(kfcOrder);
                                                    if (key > -1) {
                                                        int type = kfcOrder.getType();
                                                        int isInser = type > 3 ? 1 : 0;
                                                        if (key == -1) {
                                                            return;
                                                        }
                                                        int weight = kfcOrder.getWeightNow();
                                                        if (type == 2) {
                                                            weight = YumControl.getInstall().getWeight(kfcOrder.getWeightNow());
                                                        }
                                                        shipMap.put(key, kfcOrder);
                                                        status = 2;
                                                        sendBomQueryCmd(2);
                                                        sendBomStatus(0);
                                                        setFlow(kfcOrder, 10);
                                                        bossKey = key;
                                                        Serialone.ship(key, type, kfcOrder.getIsmeat(), isInser, weight);
                                                    } else {
                                                    }
                                                    addOrder();
                                                } else {
                                                }
                                            } else if (dorpState == enumFlow.six) {
                                                if (System.currentTimeMillis() - startMoveFoodTime > 9 * 1000) {
                                                    dorpState = enumFlow.seven;
                                                    if (shipMap != null && shipMap.size() > 3) {
                                                        return;
                                                    }

                                                    if (TextUtils.isEmpty(currDorpOrder)) {
                                                        return;
                                                    }
                                                    if (dorpMap == null || dorpMap.size() < 1) {
                                                        return;
                                                    }
                                                    KfcOrder kfcOrder = dorpMap.get(currDorpOrder);
                                                    if (kfcOrder == null) {
                                                        return;
                                                    }
                                                    if (kfcOrder.getFlow() != 3) {
                                                        return;
                                                    }

                                                    int key = getkey(kfcOrder);
                                                    if (key > -1) {
                                                        int type = kfcOrder.getType();
                                                        int isInser = type > 3 ? 1 : 0;
                                                        if (key == -1) {
                                                            return;
                                                        }
                                                        int weight = kfcOrder.getWeightNow();
                                                        if (type == 2) {
                                                            weight = YumControl.getInstall().getWeight(kfcOrder.getWeightNow());
                                                        }
                                                        shipMap.put(key, kfcOrder);
                                                        status = 2;
                                                        sendBomQueryCmd(2);
                                                        sendBomStatus(0);
                                                        setFlow(kfcOrder, 10);
                                                        bossKey = key;
                                                        Serialone.ship(key, type, kfcOrder.getIsmeat(), isInser, weight);
                                                    } else {
                                                    }
                                                    addOrder();
                                                } else {
                                                }
                                            }
                                        }
                                        break;
                                    case 6:
                                        if (dorpFlow == 170) {
                                            if (dorpState == enumFlow.drop10) {
                                                status = 1;
                                                sendBomQueryCmd(1);
                                                sendBomStatus(0);
                                                dorpState = enumFlow.drop11;
                                            } else if (dorpState == enumFlow.dorp12) {
                                                dorpState = enumFlow.drop13;
                                            }
                                        }

                                        break;
                                    case 9:
                                        if (dorpFlow == 170) {
                                            if (dorpState == enumFlow.eight) {
                                                dorpState = enumFlow.nine;
                                                rcvStatus = dorpFlow;
                                                addOrder();
                                            } else {
                                                if (rcvStatus == 170) {
                                                } else {
                                                }
                                                rcvStatus = dorpFlow;
                                            }
                                        } else {
                                            rcvStatus = dorpFlow;
                                        }
                                        break;
                                }
                            }

                        } else {
                            XssTrands.getInstanll().sendQureyACT(0);
                        }
                        sendSHow();
                    }

                    break;
                case MsgWhat.PORTDATA_BOM:
                    if (!isSetMeatPlain) {
                        setMeatPlain();
                    }
                    isSetMeatPlain = true;
                    KfcBomInfo temp = parsingBomData(cEventInfo.m_lParam4);
                    if (temp == null) {
                        return;
                    }
                    if (!listErrorOne.contains(MsgWhat.errorCode9002)) {
                        setPowerError(false);
                    }
                    int bomSize = listErrorTwo.size();
                    int bomIgnoreSize = listErrorthree.size();
                    listErrorTwo.clear();
                    listErrorthree.clear();
                    if (!TextUtils.isEmpty(temp.getErrcode())) {
                        int bomCode = Integer.parseInt(temp.getErrcode(), 16);
                        if (bomCode > 0) {
                            String tempData = "PORTDATA_BOM  code=" + bomCode + "   " + XssTrands.getInstanll().getErrCode(bomCode, true);
                            if (!TextUtils.isEmpty(printBomData) && tempData.equals(printBomData)) {
                                printBomData = "";
                            } else {
                                printBomData = tempData;
                                XssTrands.getInstanll().logd("KfcUitlControl", printBomData);
                            }
                            listErrorTwo.add(bomCode);
                            if (AllList == null || AllList.size() < 1) {
                                return;
                            }
                            ErrorCodeControl codeControl = YumControl.getInstall().getErrorClz();
                            List<Integer> listIgnore = codeControl.getListIgnoreExeOrder();
                            List<Integer> listDorpCodeTypeAll = codeControl.getListDorpCode();
                            List<Integer> listErrorDorpExist = getListErrorDrop();
                            List<Integer> listErrorBomExist = getListErrorTwo();
                            for (int i = 0; i < AllList.size(); i++) {
                                KfcOrder kfcOrder = AllList.get(i);
                                if (kfcOrder != null && kfcOrder.getFlow() > 0) {
                                    if (listErrorBomExist != null && listErrorBomExist.size() > 0) {
                                        kfcOrder.setErrorCode(listErrorBomExist.get(0));
                                        YumControl.getInstall().setCallbackOrder(kfcOrder, boxTemp);
                                    } else if (kfcOrder.getFlow() < 10) {
                                        int code = 0;
                                        for (int listDorpindex = 0; listDorpindex < listErrorDorpExist.size(); listDorpindex++) {
                                            int temp1 = listErrorDorpExist.get(listDorpindex) % XssData.NumError10000;
                                            if (listDorpCodeTypeAll.contains(temp1)) {
                                                List<Integer> listType = codeControl.getListTypeCode(kfcOrder.getType());
                                                if (listType.contains(temp1)) {
                                                    code = listErrorDorpExist.get(listDorpindex);
                                                    if (!listIgnore.contains(temp1)) {
                                                        break;
                                                    }
                                                }
                                            } else {
                                                code = listErrorDorpExist.get(listDorpindex);
                                                break;
                                            }
                                        }
                                        if (code > 0) {
                                            kfcOrder.setErrorCode(code);
                                            YumControl.getInstall().setCallbackOrder(kfcOrder, boxTemp);
                                        }

                                    }
                                }
                            }
                        }
                    }
                    if (!TextUtils.isEmpty(temp.getIgnoreErrcode())) {
                        int ignoreErrcode = Integer.parseInt(temp.getIgnoreErrcode(), 16);
                        if (ignoreErrcode > 0) {
                            listErrorthree.add(ignoreErrcode);
                            if (AllList == null || AllList.size() < 1) {
                                return;
                            }
                            ErrorCodeControl codeControl = YumControl.getInstall().getErrorClz();
                            List<Integer> listIgnore = codeControl.getListIgnoreExeOrder();
                            List<Integer> listDorpCodeTypeAll = codeControl.getListDorpCode();
                            List<Integer> listErrorDorpExist = getListErrorDrop();
                            List<Integer> listErrorBomExist = getListErrorTwo();
                            for (int i = 0; i < AllList.size(); i++) {
                                KfcOrder kfcOrder = AllList.get(i);
                                if (kfcOrder != null && kfcOrder.getFlow() > 0) {
                                    if (listErrorBomExist != null && listErrorBomExist.size() > 0) {
                                        kfcOrder.setErrorCode(listErrorBomExist.get(0));
                                        YumControl.getInstall().setCallbackOrder(kfcOrder, boxTemp);
                                    } else if (kfcOrder.getFlow() < 10) {
                                        int code = 0;
                                        for (int listDorpindex = 0; listDorpindex < listErrorDorpExist.size(); listDorpindex++) {
                                            int temp1 = listErrorDorpExist.get(listDorpindex) % XssData.NumError10000;
                                            if (listDorpCodeTypeAll.contains(temp1)) {
                                                List<Integer> listType = codeControl.getListTypeCode(kfcOrder.getType());
                                                if (listType.contains(temp1)) {
                                                    code = listErrorDorpExist.get(listDorpindex);
                                                    if (!listIgnore.contains(temp1)) {
                                                        break;
                                                    }
                                                }
                                            } else {
                                                code = listErrorDorpExist.get(listDorpindex);
                                                break;
                                            }
                                        }
                                        if (code > 0) {
                                            kfcOrder.setErrorCode(code);
                                            YumControl.getInstall().setCallbackOrder(kfcOrder, boxTemp);
                                        }

                                    }
                                }
                            }
                        }
                    }
                    if (listErrorTwo.size() != bomSize || listErrorthree.size() != bomIgnoreSize) {
                        sendHeatTime(0);
                    }
                    if (!isShipUI) {
                        if (isOrderExeing()) {

                        } else {
                            return;
                        }
                    }
                    if (temp == null) {
                        return;
                    }
                    if (!plain) {
                        plain = isLicenseFoode(temp.getLicense_meat());
                    }
                    if (!meat) {
                        meat = isLicenseFoode(temp.getLicense_plain());
                    }
                    if (TextUtils.isEmpty(temp.getState1()) || "00".equals(temp.getState1()) || TextUtils.isEmpty(temp.getZhalu1())) {//表示无订单

                    } else {
                        KfcOrder kfcOrder = null;
                        kfcOrder = shipMap.get(1);
                        if (kfcOrder != null) {
                            int bomId = XssUtility.getInt(temp.getZhalu1());
                            if (bomId > 0) {
                                kfcOrder.setBomId(bomId);
                            }
                            switch (temp.getState1()) {
                                case "01":
                                    setFlow(kfcOrder, 11);
                                    break;
                                case "02":
                                    setFlow(kfcOrder, 12);
                                    break;
                                case "03":
                                    setFlow(kfcOrder, 13);
                                    break;
                                case "04":
                                    setFlow(kfcOrder, 14);
                                    break;
                                case "05":
                                    setFlow(kfcOrder, 15);
                                    break;
                                case "06":
                                    if (kfcOrder.getFlow() != 16 && kfcOrder.getFlow() > 10) {
                                        addFinishOrder(kfcOrder);
                                        cleanChaDanList();
                                        shipMap.remove(1);
                                    }
                                    break;
                                case "E0":
                                    break;
                            }
                        }

                    }
                    if (TextUtils.isEmpty(temp.getState2()) || "00".equals(temp.getState2()) || TextUtils.isEmpty(temp.getZhalu2())) {//表示无订单

                    } else {
                        KfcOrder kfcOrder = null;
                        kfcOrder = shipMap.get(2);
                        if (kfcOrder != null) {
                            int bomId = XssUtility.getInt(temp.getZhalu2());
                            if (bomId > 0) {
                                kfcOrder.setBomId(bomId);
                            }
                            switch (temp.getState2()) {
                                case "01":
                                    setFlow(kfcOrder, 11);
                                    break;
                                case "02":
                                    setFlow(kfcOrder, 12);
                                    break;
                                case "03":
                                    setFlow(kfcOrder, 13);
                                    break;
                                case "04":
                                    setFlow(kfcOrder, 14);
                                    break;
                                case "05":
                                    setFlow(kfcOrder, 15);
                                    break;
                                case "06":
                                    if (kfcOrder.getFlow() != 16 && kfcOrder.getFlow() > 10) {
                                        addFinishOrder(kfcOrder);
                                        cleanChaDanList();
                                        shipMap.remove(2);
                                    }
                                    break;
                                case "E0":
                                    break;
                            }
                        } else {

                        }

                    }

                    if (TextUtils.isEmpty(temp.getState3()) || "00".equals(temp.getState3()) || TextUtils.isEmpty(temp.getZhalu3())) {//表示无订单

                    } else {
                        KfcOrder kfcOrder = null;
                        kfcOrder = shipMap.get(3);
                        if (kfcOrder != null) {
                            int bomId = XssUtility.getInt(temp.getZhalu3());
                            if (bomId > 0) {
                                kfcOrder.setBomId(bomId);
                            }
                            switch (temp.getState3()) {
                                case "01":
                                    setFlow(kfcOrder, 11);
                                    break;
                                case "02":
                                    setFlow(kfcOrder, 12);
                                    break;
                                case "03":
                                    setFlow(kfcOrder, 13);
                                    break;
                                case "04":
                                    setFlow(kfcOrder, 14);
                                    break;
                                case "05":
                                    setFlow(kfcOrder, 15);
                                    break;
                                case "06":
                                    if (kfcOrder.getFlow() != 16 && kfcOrder.getFlow() > 10) {
                                        addFinishOrder(kfcOrder);
                                        cleanChaDanList();
                                        shipMap.remove(3);
                                    }
                                    break;
                                case "E0":
                                    break;
                            }
                        }
                    }

                    if (TextUtils.isEmpty(temp.getState4()) || "00".equals(temp.getState4()) || TextUtils.isEmpty(temp.getZhalu4())) {//表示无订单

                    } else {
                        KfcOrder kfcOrder = null;
                        kfcOrder = shipMap.get(4);
                        if (kfcOrder != null) {
                            int bomId = XssUtility.getInt(temp.getZhalu4());
                            if (bomId > 0) {
                                kfcOrder.setBomId(bomId);
                            }
                            switch (temp.getState4()) {
                                case "01":
                                    setFlow(kfcOrder, 11);
                                    break;
                                case "02":
                                    setFlow(kfcOrder, 12);
                                    break;
                                case "03":
                                    setFlow(kfcOrder, 13);
                                    break;
                                case "04":
                                    setFlow(kfcOrder, 14);
                                    break;
                                case "05":
                                    setFlow(kfcOrder, 15);
                                    break;
                                case "06":
                                    if (kfcOrder.getFlow() != 16 && kfcOrder.getFlow() > 10) {
                                        addFinishOrder(kfcOrder);
                                        cleanChaDanList();
                                        shipMap.remove(4);
                                    }
                                    break;
                                case "E0":
                                    break;
                            }
                        }
                    }
                    int index = 0;
                    int[] aar = new int[4];
                    aar[index++] = XssUtility.getInt(temp.getState1());
                    aar[index++] = XssUtility.getInt(temp.getState2());
                    aar[index++] = XssUtility.getInt(temp.getState3());
                    aar[index++] = XssUtility.getInt(temp.getState4());
                    if (bossKey == -1) {

                    } else {
                        if (bossKey > 0 && bossKey - 1 < aar.length && 2 == aar[bossKey - 1]) {
                            if (dorpState == enumFlow.drop11) {
                                dorpState = enumFlow.dorp12;
                                XssTrands.getInstanll().actionHex(6, "04" + "000000");
                                status = 0;//插单降落，状态重装
                                bossKey = -1;
                            } else if (dorpState == enumFlow.three
                                    || dorpState == enumFlow.seven) {
                                bossKey = -1;
                                status = 0;//移动完成，状态重置
                                dorpState = enumFlow.four;
                                if (currDorpOrder != null) {
                                    dorpMap.remove(currDorpOrder);
                                    currDorpOrder = "";
                                }
                            }
                        }
                    }

                    if ("01".equals(temp.getRevState())) {
                        if (status != 0) {
                            status = 0;//避免冲突
                        }
                        isrecv = false;
                    } else {
                        if ("02".equals(temp.getRevState())) {
                            if (recvFoodBom(temp)) {
                                return;
                            }
                        } else {
                            isrecv = false;
                        }
                        if (dorpState == enumFlow.one) {
                            int moveType = -1;
                            if (ishasFoods(temp, 3, false)) {
                                moveType = comChiken(temp, 3, false);

                            } else if (ishasFoods(temp, 1, false)) {
                                moveType = comChiken(temp, 1, false);
                            } else if (ishasFoods(temp, 2, false)) {
                                moveType = 2;
                            }
                            if (moveType != -1) {
                                KfcOrder kfcOrder = startMap.get(moveType);
                                if (kfcOrder == null) {
                                    return;
                                }
                                if (weight != null && weight.equals(kfcOrder.getOrder())) {
                                    return;
                                }
                                if (isExistBomFalut(kfcOrder)) {
                                    XssTrands.getInstanll().sendMsgToUIDelay(MsgWhat.KFCStart, 0, 1, 1, 3 * 1000, "");
                                    addFinishOrder(kfcOrder);
                                    return;
                                }
                                setFlow(kfcOrder, 3);
                                weight = kfcOrder.getOrder();
                                kfcOrder.setYouzhaTime(System.currentTimeMillis());
                                if (lanNum > 0) {
                                    lanNum--;
                                }
                                dorpState = enumFlow.two;
                                currDorpOrder = kfcOrder.getOrder();
                                dorpMap.put(currDorpOrder, kfcOrder);
                                startMoveFoodTime = System.currentTimeMillis();
                                int local = 0;
                                switch (kfcOrder.getType()) {
                                    case 1:
                                        break;
                                    case 2:
                                        local = 1;
                                        break;
                                    case 3:
                                        local = 2;
                                        break;
                                }
                                serialtow.actionHex(5, "0" + local + "000000");

                            }
                        } else if (dorpState == enumFlow.five) {
                            int moveType = -1;
                            if (ishasFoods(temp, 3, true)) {
                                moveType = comChiken(temp, 3, true);

                            } else if (ishasFoods(temp, 1, true)) {
                                moveType = comChiken(temp, 1, true);
                            } else if (ishasFoods(temp, 2, true)) {
                                moveType = 2;
                            }
                            if (moveType != -1) {
                                KfcOrder kfcOrder = startMap.get(moveType);
                                if (kfcOrder == null) {
                                    status = 0;//startMoveFood 订单丢失，直接回蓝
                                    recvFood();
                                    return;
                                }
                                if (weight != null && weight.equals(kfcOrder.getOrder())) {
                                    status = 0;//startMoveFood 重复订单
                                    recvFood();
                                    return;
                                }
                                if (isExistBomFalut(kfcOrder)) {
                                    XssTrands.getInstanll().sendMsgToUIDelay(MsgWhat.KFCStart, 0, 1, 1, 3 * 1000, "");
                                    addFinishOrder(kfcOrder);
                                    status = 0;//出现故障，直接回蓝
                                    recvFood();
                                    return;
                                }
                                setFlow(kfcOrder, 3);
                                weight = kfcOrder.getOrder();
                                kfcOrder.setYouzhaTime(System.currentTimeMillis());
                                Serialone.cleanFault("000100" + "000000");
                                dorpState = enumFlow.six;
                                currDorpOrder = kfcOrder.getOrder();
                                dorpMap.put(currDorpOrder, kfcOrder);
                                startMoveFoodTime = System.currentTimeMillis();
                                int local = 0;
                                switch (kfcOrder.getType()) {
                                    case 1:
                                        break;
                                    case 2:
                                        local = 1;
                                        break;
                                    case 3:
                                        local = 2;
                                        break;
                                }
                                serialtow.actionHex(7, "0" + local + "000000");
                            } else {
                                status = 0;//落料订单出现异常故障，直接回蓝
                                recvFood();
                            }
                        } else if (dealBoosOrder(temp)) {//处理插单，确认是否执行其他操作
                            cleanChaDanList();
                        } else {
                            if (isScanLanNeed) {//setBomStates
                                return;
                            }
                            if (isCanBomStatus()) {
                                if (isHasFoodAll(temp, false)) {
                                    status = 1;
                                    sendBomQueryCmd(1);
                                    sendBomStatus(0);
                                    dorpState = enumFlow.one;

                                }
                            }

                        }
                    }

                    sendSHow();
                    break;

            }
        }

    }


    /**
     * folw 状态描述
     *
     * @param
     * @return
     * @author hua
     * @time 2022/6/17 11:24
     */
    public String getFlow(int type) {
        String msg = "" + type;
        return msg;
    }

    public void logx(String msg) {
        XssTrands.getInstanll().logd("kfcPortControl", msg);
    }


}
