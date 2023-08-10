package com.common.serial.kfc;

import android.content.Context;
import android.os.Build;
import android.os.Message;
import android.text.TextUtils;

import com.common.base.BeanInfo.PortFormat;
import com.common.base.MsgWhat;
import com.common.base.XssTrands;
import com.common.base.utils.XssData;
import com.common.base.utils.XssSavaData;
import com.common.base.utils.XssUtility;
import com.common.serial.SerialBase;


import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * FileName:串口通讯
 * Author: hua
 * Date: 2021/9/8 14:45
 * Description:
 */
public class serialtwo extends SerialBase {
    int currACTCmdFlow;


    public void openPort() {
        String port = "dev/ttyS3";
        int bote = XssSavaData.getInstance().getPortBoTe(1);
        logx("init", "port:  " + port + "   " + bote);
        portController.openSerialPort(port, bote);
    }

    public void portError() {
        XssTrands.getInstanll().sendMsgToUI(MsgWhat.ERRORCODE, MsgWhat.errorCode9002, 1, 0, "");
    }

    public String getData(String msg) {
        return msg;
    }

    public void trandsCmd(String cmd, String... parm) {
        sendPortCmd("20", parm[0]);

    }

    public void init(Context context) {
        super.init(context);
        Message message = masterHandler.obtainMessage();
        message.what = InitDevCaoZuo;
        message.arg1 = 1;
        masterHandler.sendMessageDelayed(message, 8 * 1000);
        sendQueryPoll(10);
    }


    public void deal05(String data) {
    }


    protected void dealRev(String cmdData) {
        XssTrands.getInstanll().sendMsgToUI(MsgWhat.PORTDATA, cmdData);
        String cmd = cmdData.substring(6, 8);
        switch (cmd) {
            case "A1":
                dealAl(cmdData);
                break;
        }
    }


    public void dealAl(String data) {
        data = data.toUpperCase();
        int x = 14;
        String result = data.substring(x, x = x + 2);
        if (result.equals("00")) {
        } else if (result.equals("AA")) {
        } else if (result.equals("F1") ||
                result.equals("F2") ||
                result.equals("F3") ||
                result.equals("FA") ||
                result.equals("FF")) {
        } else {

        }
    }

    public void queryAction() {
        sendPortCmd("21", "0");
    }

    long nextsendQureyACT;
    int nextQureyACT = 0;

    public void sendQureyACT(int count) {
        masterHandler.removeMessages(ACTCmdQuery);
        int time = 5;
        if (count == 100) {
            time = 10;
        } else if (count == 999) {
            time = 15;
        } else {
            if (nextQureyACT == 0) {
                time = 30;
            } else {
                time = 10;
            }
        }
        nextQureyACT = count;
        long inval = System.currentTimeMillis() - nextsendQureyACT;
        long temp = time * 100;
        Message msg = masterHandler.obtainMessage();
        msg.what = ACTCmdQuery;
        msg.arg1 = count;
        if (inval <= temp) {
            masterHandler.sendMessageDelayed(msg, temp - inval);
        } else {
            masterHandler.sendMessageDelayed(msg, temp);
        }

    }

    public void handlerData(Message msg) {
        switch (msg.what) {
            case ACTCmdQuery:
                nextsendQureyACT = System.currentTimeMillis();
                queryAction();
                sendQureyACT(msg.arg1);
                break;
            case StatesCmdQuery:
                sendQueryPoll(8);
                break;
            case InitDevCaoZuo:
                boolean b = XssSavaData.getInstance().getData(XssData.REBOOT_SCAN_FUWEI, false);
                if (b) {
                    if (msg.arg1 == 1) {
                        sendPortCmd(05, XssUtility.getHex(4), "0000" + "0000");
                    }
                }
                break;

        }
    }


    public String deal06(PortFormat portFormat) {

        String[] aar_parm = XssTrands.getInstanll().getActionAaar();
        if (aar_parm == null) {
            return "";
        }
        HashMap<Integer, String> hashMap = new HashMap();
        for (String s : aar_parm) {
            if (!TextUtils.isEmpty(s)) {
                int x = s.indexOf("~");
                if (x > 0) {
                    int local = Integer.parseInt(s.substring(0, x));
                    hashMap.put(local, s);
                }
            }

        }
        List<PortFormat.StatesActCmd> list = portFormat.getList();
        String msg = "";
        int currActS = 0;

        if (list != null && list.size() > 0) {
            msg = "";
            for (int i = 0; i < list.size(); i++) {
                String temp = "";
                PortFormat.StatesActCmd p = list.get(i);
                String act = hashMap.get(p.getCmd());
                String result = p.getCheck();
                String weight = "";
                switch (p.getCmd()) {
                    case 1:
                    case 2:
                    case 3:
                        weight = "     " + Integer.parseInt(p.getOther(), 16) + " g";
                        break;
                }

                if (result.equals("00")) {
                    temp = "00";
                } else if (result.equals("AA")) {
                    temp = "任务完成";
                } else if (result.equals("F1")) {
                    temp = "超时未完成";
                } else if (result.equals("F2")) {
                    temp = "电机堵转报错";
                } else if (result.equals("F3")) {
                    temp = "扫描篮子出错";
                } else if (result.equals("FA")) {
                    temp = "命令不会执行";
                } else if (result.equals("FF")) {
                    temp = "命令不合法";
                } else {
                    int flow = Integer.parseInt(result, 16);
                    if (flow > 0 && flow < 170) {
                        currActS = flow;
                        temp = "执行: " + flow;
                    }
                }
                if (!TextUtils.isEmpty(act)) {
                    msg += act + "   " + temp;
                    if (!TextUtils.isEmpty(weight)) {
                        msg += weight;
                    }
                    msg += "\n";
                }
            }
        } else {
            sendQureyACT(0);
        }
        currACTCmdFlow = currActS;
        return msg;
    }

    public void sendQueryPoll(int time) {
        masterHandler.sendEmptyMessageDelayed(StatesCmdQuery, time * 1000);
        if (!XssTrands.getInstanll().isContinClick("sendQueryPoll", 20)) {
            queryStatus();
        }
    }

    public void deal80(PortFormat portFormat) {
        PortFormat.StatesDrop80 info = new PortFormat.StatesDrop80();
        info.setTemp(Integer.parseInt(portFormat.getData().substring(0, 2), 16) - 40);
        String box = new StringBuffer(XssUtility.byteArrToBinStr(portFormat.getData().substring(4, 8))).toString();
        if (portFormat.getData().length() == 24) {
            info.setwShuTiao(Integer.parseInt(portFormat.getData().substring(8, 12), 16));
            info.setwBoWei(Integer.parseInt(portFormat.getData().substring(12, 16), 16));
            info.setwChcken(Integer.parseInt(portFormat.getData().substring(16, 20), 16));
            info.setLanNum(Integer.parseInt(portFormat.getData().substring(20, 22), 16));
            boolean power = false;
            if (portFormat.getData().substring(22, 24).equals("55")) {
                power = true;
            }
            info.setPower(power);
        }
        int x = 0;
        info.setMeteIceBox(Integer.parseInt(box.substring(x, x = x + 1)));
        info.setMeteChips2(Integer.parseInt(box.substring(x, x = x + 1)));
        info.setMeteChcken(Integer.parseInt(box.substring(x, x = x + 1)));
        info.setMeteChips(Integer.parseInt(box.substring(x, x = x + 1)));
        info.setLocalChaDan(gettwoInt(box.substring(x, x = x + 2)));
        info.setLocalBasket(gettwoInt(box.substring(x, x = x + 2)));
        info.setLocalScan(gettwoInt(box.substring(x, x = x + 2)));
        info.setLocalChip2(gettwoInt(box.substring(x, x = x + 2)));
        info.setLocalChcken(gettwoInt(box.substring(x, x = x + 2)));
        info.setLocalChips(gettwoInt(box.substring(x, x = x + 2)));
        portFormat.setStatesDrop80(info);
    }

    //1111 0101 0010 1010
    public String deal80Msg(PortFormat portFormat) {
        if (portFormat == null || portFormat.getStatesDrop80() == null) {
            return "";
        }
        PortFormat.StatesDrop80 info = portFormat.getStatesDrop80();
        String span = "\n";
        StringBuffer sb = new StringBuffer("");
        sb.append("冰箱温度：" + info.getTemp() + " ℃" + span);
        sb.append(getLocalMsg(info.getLocalChips(), "直薯条") + span);
        sb.append(getLocalMsg(info.getLocalChcken(), "鸡块") + span);
        sb.append(getLocalMsg(info.getLocalChip2(), "波纹薯条") + span);
        sb.append(getLocalMsg(info.getLocalScan(), "扫描在", "右边", "左边", "中间位置") + span);
        sb.append(getLocalMsg(info.getLocalBasket(), "挂篮推杆", "缩回状态", "推出状态", "中间位置") + span);
        sb.append(getLocalMsg(info.getLocalChaDan(), "插单推杆", "缩回状态", "推出状态", "中间位置") + span);
        sb.append(getMeatMsg(info.getMeteChips(), "直薯条", "缺料", "有料") + span);
        sb.append(getMeatMsg(info.getMeteChips2(), "波纹", "缺料", "有料") + span);
        sb.append(getMeatMsg(info.getwChcken(), "鸡块", "缺料", "有料") + span);
        sb.append(getMeatMsg(info.getMeteIceBox(), "冰箱门", "开", "关") + span);
        sb.append("直薯条重量 " + info.getwShuTiao() + span);
        sb.append("鸡块薯条重量 " + info.getwChcken() + span);
        sb.append("波纹重量 " + info.getwBoWei() + span);
        return sb.toString();

    }

    public String getMeatMsg(int sele, String name, String one, String two) {
        String msg = sele == 1 ? name + one : name + two;
        return msg;
    }

    public String getLocalMsg(int local, String name) {
        String msg = "";
        switch (local) {
            case 0:
                msg = name + "关门位";
                break;
            case 1:
                msg = name + "开门位";
                break;
            case 2:
                msg = name + "中间位置";
                break;
        }
        return msg;
    }

    public String getLocalMsg(int local, String name, String msg1, String msg2, String msg3) {
        String msg = "";

        switch (local) {
            case 0:
                msg = name + msg1;
                break;
            case 1:
                msg = name + msg2;
                break;
            case 2:
                msg = name + msg3;
                break;
        }
        return msg;
    }

    public int gettwoInt(String s) {
        switch (s) {
            case "00":
                return 0;
            case "01":
                return 1;
            case "10":
                return 2;
        }
        return 0;
    }

    public String getName() {
        return "KfcDropSerial";
    }


}