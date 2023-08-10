package com.common.serial.kfc;

import com.common.base.BeanInfo.KfcOrder;
import com.common.base.BeanInfo.PortFormat;
import com.common.base.MsgWhat;
import com.common.base.XssTrands;
import com.common.base.utils.XssData;
import com.common.base.utils.XssSavaData;
import com.common.base.utils.XssUtility;
import com.google.gson.Gson;
import com.hua.back.kfc.base.ErrorCodeControl;
import com.hua.back.kfc.base.YumControl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * FileName: KfcUitlControl
 * Author: hua
 * Date: 2022/6/24 17:19
 * Description:
 */
public class KfcUitlControl {
    int rcvStatus = 0;//回篮流程
    boolean plain = false;
    boolean meat = false;
    final int heatYum = 1002;
    final int msgScanLan = 1003;
    final int queryDropStats = 1004;
    String printDropData;
    String printBomData;

    enum enumFlow {//薯条站流程
        Zero,
        one, two, three,
        four, five, six, seven,
        eight, nine,
        drop10, drop11,
        dorp12, drop13,
    }

    List<KfcOrder> AllList = new ArrayList<>();
    List<Integer> listErrorOne = new ArrayList<>();
    List<Integer> listErrorTwo = new ArrayList<>();
    List<Integer> listErrorthree = new ArrayList<>();
    int boxTemp = -99;
    HashMap<Integer, Boolean> foodMap1 = new HashMap<>();//是否缺料的状态  0有料  1 缺料
    HashMap<Integer, Boolean> foodMap2 = new HashMap<>();//判断食物是否无货 true  表示无货

    PortFormat portFormat80;
    static KfcPortControl kfcPortControl;
    serialtwo serialtow;
    serialOne Serialone;
    enumFlow dorpState = enumFlow.Zero;
    boolean isrecv;
    int lanNum;
    boolean isShipUI = true;
    String currDorpOrder;
    public boolean isScanLanNeed = true;
    public boolean isSetMeatPlain = false;
    List<KfcOrder> chadanList = new ArrayList<>();
    int status;
    Map<Integer, KfcOrder> shipMap = new HashMap<>();
    Map<Integer, KfcOrder> startMap = new HashMap<>();
    Map<String, KfcOrder> dorpMap = new HashMap<>();
    Map<Integer, Long> powerMap = new HashMap<>();
    KfcOrder tempBoosOrder;//插单处理
    int bossKey = -1;
    String weight;
    long startMoveFoodTime;
    int[] orderAar = new int[]{1, 2, 3};

    public boolean isMeat() {
        return meat;
    }

    /**
     * 清除订单
     *
     * @param
     * @return
     * @author hua
     * @time 2022/1/18 14:16
     */
    public synchronized void cleanOrder() {
        logx("cleanOrder");
        chadanList.clear();
        if (AllList.size() > 0) {
            for (int x = 0; x < AllList.size(); x++) {
                KfcOrder kfcOrder = AllList.get(x);
                if (kfcOrder.getFlow() < 16) {
                    if (kfcOrder.getErrorCode() / XssData.NumError10000 != 1) {
                        kfcOrder.setErrorCode(MsgWhat.errorCode9017);
                    }
                    kfcOrder.setMidEnd(true);
                    addFinishOrder(kfcOrder);
                }
            }
        }
        AllList.clear();

        sendSHow();
        deviceInit();

    }

    public void cleanFoodNotExsitMap() {
        foodMap2.put(1, false);
        foodMap2.put(3, false);
        foodMap2.put(2, false);
    }

    public void deviceInit() {
        cleanFoodNotExsitMap();
        shipMap.clear();
        startMap.clear();
        dorpMap.clear();
        status = 0;//清除订单
        currDorpOrder = "";
        tempBoosOrder = null;
        bossKey = -1;
        dorpState = enumFlow.Zero;
        tempBoosOrder = null;//插单处理
        weight = "";
    }

    public void setShipUI(boolean shipUI) {
        isShipUI = shipUI;
    }


    public int comChiken(KfcBomInfo kfcBomInfo, int type, boolean isrecv) {
        KfcOrder kfcChild = startMap.get(2);
        KfcOrder kfcorder = startMap.get(type);
        int moveType = type;
        if (ishasFoods(kfcBomInfo, 2, isrecv)) {
            if (kfcorder.getStartTime() - kfcChild.getStartTime() >= 10 * 1000) {
                moveType = 2;
            } else {

                moveType = type;
            }
        } else {
            moveType = type;
        }
        return moveType;
    }

    /**
     * 添加错误代码
     * @param
     * @return
     */
    public void addErrorCode(List<Integer> allList, List<Integer> list, int type) {
        if (list.size() > 0) {
            for (int x = 0; x < list.size(); x++) {
                int code = list.get(x);
                if (code > 0) {
                    if (code < 1000) {
                        code = type + code;
                    }
                    if (code < 10000) {
                        if (YumControl.getInstall().getErrorClz().hideError(code)) {
                            code = XssData.NumError20000 + code;
                        } else {
                            code = XssData.NumError10000 + code;
                        }
                    }
                    if (!allList.contains(code)) {
                        allList.add(code);
                    }
                }
            }
        }
    }

    public synchronized void addOrder() {

    }

    public boolean ishasFoods(KfcBomInfo kfcBomInfo, int type, boolean isrecv) {
        KfcOrder kfcOrder = startMap.get(type);
        if (kfcOrder == null) {
            return false;
        }
        if (kfcOrder != null && kfcOrder.getFlow() > 2) {
            startMap.remove(type);
            addOrder();
            return false;
        }
        int flow = kfcOrder.getFlow();//实际重量为0，则不处理}
        if (flow != 2) {
            return false;
        }
        if (flow == 2 && kfcOrder.getWeightNow() < 10) {
            addFinishOrder(kfcOrder);
            startMap.remove(type);
            addOrder();
            return false;
        }
        if (flow > 3) {
            return false;
        }
        if (!isrecv) {
            if (lanNum < 1) {
                if (shipMap.size() < 2) {
                    XssTrands.getInstanll().queryStatus();
                }
                return false;
            }
        }
        return ishasFoods(kfcBomInfo, kfcOrder, false);
    }
/**
 * 订单完成
 * @param
 * @return
 * @author hua
 */
    public void addFinishOrder(KfcOrder kfcOrder) {
        kfcOrder.setEndTime(System.currentTimeMillis());
        if (!YumControl.getInstall().isAAr()) {
            PaxLog.getInstall().writeLog(new Gson().toJson(kfcOrder));
        } else {
            logx(kfcOrder.toString());
        }
        setFlow(kfcOrder, 16);
        if (kfcOrder.getType() < 4) {
            getYumControl().addhistoryList(kfcOrder);
        }

    }

    public boolean ishasFoods(KfcBomInfo kfcBomInfo, KfcOrder kfcOrder, boolean isChanDan) {
        if (kfcOrder == null) {
            return false;
        }
        boolean ishas = false;
        if (kfcOrder.getIsmeat() == 1) {
            ishas = isLicenseFoode(kfcBomInfo.getLicense_meat());
        } else {
            ishas = isLicenseFoode(kfcBomInfo.getLicense_plain());
        }
        return ishas;
    }

    public boolean isLicenseFoode(String cmd) {
        return "01".equals(cmd);
    }

    /**
     * 更新界面ui
     *
     * @param
     * @return
     * @author hua
     * @time 2022/1/18 14:18
     */
    public void sendSHow() {
        XssTrands.getInstanll().sendMsgToUI(MsgWhat.SHIPORDERINFO, 0, 1, 1, "正在执行中");
    }


    public String getLogOrder(KfcOrder kfcOrder) {
        if (kfcOrder == null) {
            return "getLogOrder  kfcOrder == null";
        }
        return kfcOrder.toString();
    }

    public void logx(String msg) {
        XssTrands.getInstanll().logd(this.getClass().getSimpleName(), msg);
    }

    public Map<Integer, KfcOrder> getShipMap() {
        return shipMap;
    }

    public String getTypeName(int type) {
        if (type - 1 < XssData.FOODSELES.length && type - 1 >= 0) {
            return XssData.FOODSELES[type - 1];
        }
        return "插单商品:" + type;
    }


    public PortFormat getPortFormat80() {
        return portFormat80;
    }



    public boolean isCanAddOrder(int type) {
        if (type > 3) {
            return true;
        }
        if (AllList == null || AllList.size() < 1) {
            return true;
        }
        for (int x = 0; x < AllList.size(); x++) {
            KfcOrder kfcOrder = AllList.get(x);
            if (kfcOrder.getType() == type) {
                if (kfcOrder.getFlow() < 3) {
                    return false;
                }
            }

        }
        return true;
    }

    public boolean isCanBoosOrder(int type) {
        if (type < 4) {
            return true;
        }
        if (chadanList == null || chadanList.size() < 1) {
            return true;
        }
        boolean isChaDan = true;//改为判断插单处理
        for (int x = 0; x < chadanList.size(); x++) {
            KfcOrder kfcOrder = chadanList.get(x);
            if (kfcOrder.getFlow() == 0) {
                return false;
            }
        }
        return isChaDan;
    }


    public List<KfcOrder> getAllList() {
        return AllList;
    }

    public synchronized void cleanChaDanList() {
        for (int x = chadanList.size() - 1; x >= 0; x--) {
            KfcOrder kfcOrder = chadanList.get(x);
            if (kfcOrder.getFlow() > 15) {
                chadanList.remove(x);
            }
        }
    }

    public boolean isCanBomStatus() {
        boolean b = dorpState == enumFlow.four
                || dorpState == enumFlow.nine
                || dorpState == enumFlow.drop13
                || dorpState == enumFlow.Zero;
        return b;
    }

    public boolean isCanChaDanBomStatus() {
        boolean isCan = (dorpState == enumFlow.eight && (rcvStatus >= 7));
        if (isCan) {
            return isCan;
        }
        boolean b = dorpState == enumFlow.four
                || dorpState == enumFlow.nine

                || dorpState == enumFlow.drop13
                || dorpState == enumFlow.Zero;
        return b;
    }

    /**
     * 设备初始化
     *
     * @param
     * @return
     * @author hua
     * @time 2022/7/25 17:21
     */
    public void deviceBootInit() {
        cleanOrder();
        scanLan();
    }

    public void scanLan() {
    }

    public int getTypeCmd(int type) {
        int cmd = type;
        switch (type) {
            case 1:
                cmd = 1;
                break;
            case 2:
                cmd = 2;
                break;
            case 3:
                cmd = 3;
                break;
        }
        return cmd;
    }

    public synchronized boolean isWeightNoFood(int type) {
        KfcOrder kfcOrder = startMap.get(type);
        boolean isFood = true;
        if (kfcOrder != null) {
            if (kfcOrder.getFlow() == 1 || kfcOrder.getFlow() == 2 || kfcOrder.getFlow() == 3) {
                isFood = false;
            } else {
                startMap.remove(type);
            }
        }
        return isFood;
    }


    public int getkey(KfcOrder kfcOrder) {
        if (bossKey > -1) {
            return -1;
        }
        int key = -1;
        if (shipMap.size() > 0) {
            Set<Integer> set = shipMap.keySet();
            for (int x = 0; x < orderAar.length; x++) {
                if (!set.contains(orderAar[x])) {
                    key = orderAar[x];
                    break;
                }
            }
        } else {
            key = 1;
        }
        XssTrands.getInstanll().sendQureyACT(999);
        return key;
    }

    public synchronized void dealKfcOrder(int pos, boolean isDeal) {
        if (pos >= AllList.size()) {
            return;
        }
        KfcOrder kfcOrder = AllList.get(pos);
        if (AllList.size() == 1) {
            if (isDeal) {
                AllList.remove(pos);
            }
            return;
        }
        AllList.remove(pos);
        for (int x = 0; x < AllList.size(); x++) {
            if (AllList.get(x).getFlow() == 0 && AllList.get(x).getType() < 4) {
                if (!isDeal) {
                    AllList.add(x, kfcOrder);
                }
                break;
            }
        }
    }


    public boolean isOrderExeing() {
        if (AllList == null && AllList.size() < 1) {
            return false;
        }
        for (int x = 0; x < AllList.size(); x++) {
            KfcOrder kfcOrder = AllList.get(x);
            if (kfcOrder != null) {
                if (kfcOrder.getFlow() > 0 || kfcOrder.getFlow() < 16) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isHasFoodAll(KfcBomInfo kfcBomInfo, boolean isRev) {
        boolean b = false;
        if (startMap.size() > 0) {
            if (ishasFoods(kfcBomInfo, 2, isRev)) {
                return true;
            }
            if (ishasFoods(kfcBomInfo, 1, isRev)) {
                return true;
            }
            if (ishasFoods(kfcBomInfo, 3, isRev)) {
                return true;
            }
        }
        return b;
    }


    /**
     * 进行荤素判断  //0素，1荤
     *
     * @param
     * @return
     * @author hua
     * @time 2022/6/13 16:12
     */
    public int isMeat(int type) {
        if (type == 2 || type == 5) {
            return 1;
        }
        return 0;
    }


    public KfcBomInfo parsingBomData(String data) {
        return Serialone.parsingBomData(data);
    }

    public YumControl getYumControl() {
        return YumControl.getInstall();
    }

    public void sendBomQueryCmd(int states) {
        if (Serialone != null) {
            Serialone.queryStatu(states);
        }
    }

    /**
     * 设置流程
     *
     * @param
     * @return
     * @author hua
     * @time 2022/6/17 11:26
     */
    public void setFlow(KfcOrder kfcOrder, int flow) {
        kfcOrder.setFlow(flow);
        getYumControl().setCallbackOrder(kfcOrder, boxTemp);
    }

    public boolean isCanWeight() {
        if (lanNum < 1 && shipMap.size() == 0) {
            return false;
        }
        return true;
    }

    public void setMeatPlain() {
        int value = XssSavaData.getInstance().getData(XssData.FOODMeatPlain, XssData.FOODMeatPlainv);
        int two = (value % 100) / 10;
        int three = value / 100;
        int one = value % 10;
        XssTrands.getInstanll().logd("KfcUitlControl", "setMeatPlain  value = " + value);
        XssTrands.getInstanll().setParamHex2(1, XssUtility.getHexLeng(one, 4)
                + XssUtility.getHexLeng(two, 4) + XssUtility.getHexLeng(three, 4));

    }

    /**
     * 得到机器所有的故障
     *
     * @param
     * @return
     * @author hua
     * @time 2022/7/6 14:03
     */
    public List<Integer> getErrorCode() {
        List<Integer> list = getListErrorDrop();
        addErrorCode(list, listErrorTwo, XssData.NumErrorBoom);
        addErrorCode(list, listErrorthree, XssData.NumErrorBoom);
        if (PowerStatus == 2) {
            list.add(0, MsgWhat.errorCode9018);
        } else {
            if (boxTemp > -12) {
                list.add(MsgWhat.errorCode9021);
            }
        }
        return list;
    }

    /**
     * 是否断电
     *
     * @param
     * @return
     * @author hua
     * @time 2022/8/11 16:43
     */
    public boolean isPower() {
        return PowerStatus == 2;
    }

    /**
     * 得到油炸部分的故障
     *
     * @param
     * @return
     * @author hua
     * @time 2022/7/6 14:03
     */
    public synchronized List<Integer> getListErrorDrop() {
        List<Integer> list = new ArrayList<>();

        if (isScanLanNeed) {
            list.add(MsgWhat.errorCode9014);
        } else {
            int code = YumControl.getInstall().getScanError();
            if (code > 0) {
                list.add(code);
            }
        }

        List<Integer> listErrorTemp = new ArrayList<>();
        boolean isNoZhiFu = false;
        boolean isNoBowen = false;
        boolean isNoChild = false;

        for (int x = 0; x < listErrorOne.size(); x++) {
            int codeTemp = listErrorOne.get(x);
            if (codeTemp == 10) {
                isNoZhiFu = dealNoExsit(codeTemp, 1, 301, listErrorTemp);
            } else if (codeTemp == 11) {
                isNoBowen = dealNoExsit(codeTemp, 3, 303, listErrorTemp);
            } else if (codeTemp == 12) {
                isNoChild = dealNoExsit(codeTemp, 2, 302, listErrorTemp);
            } else {
                listErrorTemp.add(codeTemp);
            }
        }
        if (!foodMap1.get(1)) {
            if (!isNoZhiFu) {
                listErrorOne.add(101);
            }
        }

        if (!foodMap1.get(2)) {
            if (!isNoChild) {
                listErrorOne.add(102);
            }
        }
        if (!foodMap1.get(3)) {
            if (!isNoBowen) {
                listErrorOne.add(103);
            }
        }
        addErrorCode(list, listErrorTemp, XssData.NumErrorDrop);
        return list;
    }

    public boolean dealNoExsit(int code, int type, int noFoodCode, List<Integer> list) {
        if (!foodMap1.get(type)) {
            list.add(0, noFoodCode);
            if (foodMap2.get(type)) {

            } else {
                foodMap2.put(type, true);
            }
            return true;
        } else if (foodMap2.get(type)) {
            list.add(0, noFoodCode);
            return true;
        } else {
            list.add(code);
        }
        return false;
    }

    /**
     * 得到油炸部分的故障
     *
     * @param
     * @return
     * @author hua
     * @time 2022/7/6 14:03
     */
    public List<Integer> getListErrorTwo() {
        List<Integer> list = new ArrayList<>();
        addErrorCode(list, listErrorTwo, XssData.NumErrorBoom);
        return list;
    }



    int PowerStatus = 2;

    public void sendHeatTime(int time) {

    }

    public void setTwoPower(boolean isReboot) {
        if (isReboot) {
            if (PowerStatus == 0) {
                cleanOrder();
                PowerStatus = 2;//setTwoPower  机器临时断电
                sendHeatTime(0);
            }
        } else {
            PowerStatus = 0;
        }

    }

    public void setPowerError(boolean isError) {
        if (isError) {
            if (PowerStatus != 2) {
                PowerStatus = 2;
                cleanOrder();
                powerMap.put(2, System.currentTimeMillis());
                logx("setPowerError  机器断电，数据通讯断开");
                sendHeatTime(0);
            }
        } else {
            if (PowerStatus == 2) {
                PowerStatus = 1;
                powerMap.put(1, System.currentTimeMillis());
                long time = powerMap.get(2);
                logx("setPowerError  机器上电  数据恢复通讯 上次断电时间：" + XssUtility.getTimeShow(time));
                sendHeatTime(0);
            } else if (PowerStatus == 1) {

            } else {

            }
        }

    }
}
