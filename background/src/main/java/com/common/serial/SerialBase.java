package com.common.serial;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;


import com.common.base.BeanInfo.PortFormat;
import com.common.base.MsgWhat;
import com.common.base.XssTrands;
import com.common.base.utils.XssData;
import com.common.base.utils.XssSavaData;
import com.common.base.utils.XssUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android_serialport_api.PortSerialPortController;
import android_serialport_api.SerialBeanInfo;


/**
 * FileName: SerialBase
 * Author: hua
 * Date: 2021/9/9 14:25
 * Description:
 */
public abstract class SerialBase {
    int m_iSN;

    protected PortSerialPortController portController;
    protected int ID = 01;//驱动板组号
    int cmd21 = 33;
    int cmd00 = 0;
    protected int count;
    protected final int ReuseCmd = 1001;
    protected final int NextCmdData = 1002;
    protected final int ACTCmdQuery = 1003;
    protected final int InitDevCaoZuo = 1004;
    protected final int StatesCmdQuery = 1005;
    protected final int dealRevCmd = 1006;
    int errorCuont = 0;//重复发送数据的问题
    protected Context mContext;
    List<SendSerialBean> writeList = new ArrayList<>();
    List<SendSerialBean> writeCacheList = new ArrayList<>();
    boolean isDataRev = true;


    long beforeSerialTime;

    protected Handler masterHandler;

    protected int water = 0;


    public void addSN() {
        m_iSN++;
        if (m_iSN >= 256) {
            m_iSN = 0;
        }
    }


    public void cleanFault() {
        cleanFault("");
    }

    public void cleanFault(String hex) {
        logx("cleanFault" , hex);
        sendPortCmd(02, hex);
    }

    public void init(Context context) {
        mContext = context;
        masterHandler = new MasterHandler();
        portController = new PortSerialPortController();
        openPort();
        portController.setHandler(masterHandler);
    }

    public void portError() {
    }

    public void openPort() {
    }

    public synchronized void writeData(String msg) {
        int cmd = getCmd(msg);
        if (cmd < 0) {
            return;
        }
        SendSerialBean serialBean = new SendSerialBean(msg, cmd, 0);
        writeCacheList.add(serialBean);
        if (isDataRev) {
            sendMasterHandler(0);//writeData
        }
    }

    public synchronized void writeDataBefore() {
        if (!isDataRev) {
            long invalTime = System.currentTimeMillis() - beforeSerialTime;
            if (invalTime < 2000) {
                return;
            } else {
                int size = writeList.size();
                if (size > 0) {
                    logx("writeDataBefore 数据未回复 inval=" + invalTime + " writeList=" + writeList.size() + "  CacheList=" + writeCacheList.size());
                    SendSerialBean serialBean = writeList.get(size - 1);
                    writeList.remove(size - 1);
                    if (serialBean.getAgainCount() < 2) {
                        logx("writeDataBefore 命令重发 cmd=" + Integer.toHexString(serialBean.getCmd()) + " data=" + serialBean.getData() + "  time=" + serialBean.getTime() + "    invalBefore=" + (System.currentTimeMillis() - serialBean.getTime()) + "  Count=" + serialBean.getAgainCount());
                        writeDataBefore(serialBean, -1);
                        return;
                    } else {
                        portError();
                        logx("writeDataBefore 命令丢失 cmd=" + Integer.toHexString(serialBean.getCmd()) + " data=" + serialBean.getData() + "  time=" + serialBean.getTime() + "    invalBefore=" + (System.currentTimeMillis() - serialBean.getTime()) + "  Count=" + serialBean.getAgainCount());
                    }
                    if (size > 1) {
                        serialBean = writeList.get(0);
                        writeList.remove(0);
                        logx("writeDataBefore 数据异常 cmd=" + Integer.toHexString(serialBean.getCmd()) + " data=" + serialBean.getData() + "  time=" + serialBean.getTime() + "    invalBefore=" + (System.currentTimeMillis() - serialBean.getTime()) + "  Count=" + serialBean.getAgainCount());
                    }
                }

            }
        }
        if (writeCacheList.size() == 0) {
        } else if (writeCacheList.size() > 0) {
            writeDataBefore(writeCacheList.get(0), 0);
        }
    }

    public synchronized void writeDataBefore(SendSerialBean serialBean, int cacheIndex) {
        String msg = serialBean.getData();
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        int cmd = serialBean.getCmd();
        long invalTime = System.currentTimeMillis() - beforeSerialTime;
        if (invalTime < 500) {
            sendMasterHandler(2);
            return;
        }
        if (cacheIndex >= 0) {
            writeCacheList.remove(cacheIndex);
        }
        isDataRev = false;
        if (serialBean.getAgainCount() < 1) {
            serialBean.setOneTime(System.currentTimeMillis());
        }
        serialBean.setTime(System.currentTimeMillis());
        serialBean.setAgainCount(serialBean.getAgainCount() + 1);
        if (cmd21 == serialBean.getCmd() || cmd00 == serialBean.getCmd()) {//移除多余的缓存数据
            if (writeCacheList.size() > 1) {
                for (int index = writeCacheList.size() - 1; index >= 0; index--) {
                    SendSerialBean temp = writeCacheList.get(index);
                    if (msg.equals(temp.getData())) {
                        writeCacheList.remove(index);
                    }
                }
            }
        }
        writeList.add(serialBean);
        XssTrands.getInstanll().sendMsgToUI(MsgWhat.NEXTDATA, "");
        if (msg.startsWith("02000421")
                || msg.startsWith("02000300")
                || msg.startsWith("0200040001")) {
            logx("send--> " + msg + "     cmd=" + Integer.toHexString(serialBean.getCmd()) + " time=" + invalTime + " writeList=" + writeList.size() + "  CacheList=" + writeCacheList.size());
        } else {
            logx("send--> " + msg + "     cmd=" + Integer.toHexString(serialBean.getCmd()) + "  time=" + invalTime + " writeList=" + writeList.size() + "  CacheList=" + writeCacheList.size());
        }
        send(msg);
    }

    public int getCmd(String msg) {
        if (TextUtils.isEmpty(msg) || msg.length() < 7) {
            return -1;
        }
        String cmd = msg.substring(6, 8);
        int x = Integer.parseInt(cmd, 16);
        if (x >= 128) {
            x = x - 128;
        }
        return x;
    }


    public void send(String msg) {
        beforeSerialTime = System.currentTimeMillis();
        portController.writeData(XssUtility.hexStringToBytes(msg));
    }


    public void sendMasterHandler(int time) {
        if (time == 0) {
            masterHandler.removeMessages(ReuseCmd);
            masterHandler.sendEmptyMessage(ReuseCmd);
        } else {
            masterHandler.sendEmptyMessageDelayed(ReuseCmd, time * 100);
        }
    }


    public synchronized void dealSendmap(boolean isRev, String msg) {
        int cmd = getCmd(msg);
        if (isRev) {
            for (int x = 0; x < writeList.size(); x++) {
                SendSerialBean serialBean = writeList.get(x);
                if (serialBean != null) {
                    if (cmd == serialBean.getCmd()) {
                        writeList.remove(x);
                        return;
                    }
                }
            }
        }
    }



    public String getData(String msg) {
        return msg;
    }

    class MasterHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SerialBeanInfo.SERIAL_PORT_RECEIVE_DATA:
                    String data = getData((String) msg.obj);
                    portController.clean();
                    revert(data, true);
                    break;
                case ReuseCmd:
                    sendMasterHandler(5);
                    writeDataBefore();
                    break;

                default:
                    handlerData(msg);
                    break;
            }
        }
    }

    public void handlerData(Message msg) {
    }


    public synchronized void revert(String cmdData, boolean isone) {
        if (TextUtils.isEmpty(cmdData)) {
            return;
        }
        cmdData = cmdData.toUpperCase();
        if (cmdData.length() < 18) {
            logx("revert<--: " + cmdData + "   数据长度不对");
            return;
        }
        if (cmdData.startsWith("FF")) {
            logx("revert<--: " + cmdData + "   数据错误");
            return;
        }
        String temp = cmdData.substring(0, cmdData.length() - 4) + "0000";
        byte[] aar = XssUtility.hexStringToBytes(temp);
        CrcUtil.getCrc(aar);
        temp = XssUtility.bytesToHexString(aar);
        if (!cmdData.equalsIgnoreCase(temp)) {
            XssTrands.getInstanll().sendMsgToUI(MsgWhat.TestDATA, "数据验证不对: " + cmdData);
            logx("revert<--: " + cmdData + "   数据验证不对  " + temp);
            return;
        }
        isDataRev = true;
        dealSendmap(true, cmdData);
        logx("revert<--: " + cmdData + "    inval=" + (System.currentTimeMillis() - beforeSerialTime));
        addSN();
        dealRev(cmdData);

//        XssTrands.getInstance().sendMsgToUI(MsgWhat.SERIALDATA, msg);
    }

    protected void dealRev(String cmdData) {
    }

    public void dealAl(String data) {

    }

    public void deal05(String data) {

    }

    public void deal80(PortFormat data) {

    }

    public String deal80Msg(PortFormat portFormat) {
        return "";
    }

    public void queryStatus() {
        sendPortCmd(00);
    }

    public void queryStatus(String hex) {
        sendPortCmd(00, hex);
    }

    public void queryOther(int type, String... parm) {
    }

    public void queryDriveInfo() {
        sendPortCmd(9);
    }

    public void sendPortCmd(int cmd, String... pram0) {
        switch (cmd) {
            case 00:
                setDataPort(00, "");
                break;
            case 01:
                if (pram0.length >= 2) {
                    setDataPort(05, pram0[0] + pram0[1]);
                }
                break;
            case 02:
                setDataPort(02, "");
                break;
            case 03:
                setData0304(03, 1, pram0[0], "");
                break;
            case 04:
                if (pram0.length >= 2) {
                    setData0304(04, 1, pram0[0], pram0[1]);
                }
                break;
            case 05:
                if (pram0.length > 1) {
                    XssTrands.getInstanll().sendMsgToUI(MsgWhat.CMDSTATS06, 0, 1, 1, "正在执行中");
                    sendQureyACT(100);
                    setDataPort(05, pram0[0] + pram0[1]);
                }
                break;
            case 06:
                setDataPort(6, "00");

                break;
            case 9:
                setDataPort(9, "");
                break;
        }
    }

    public void sendPortCmd(String cmd, String... pram0) {
        String data = "";
        if (pram0 != null && pram0.length > 0) {
            data = pram0[0];
        }
        setDataPort(cmd, data);
    }

    public void sendQureyACT(int count) {
    }

    public void setData0304(int cmd, int num, String addr, String data) {
        setDataPort(cmd, addr + XssUtility.getHex(num) + data);
    }

    public void setData05(int cmd, String addr, String data) {
     /*   StringBuffer sb = new StringBuffer();
        sb.append(getHex(cmd));
        sb.append(getHex(ID));
        sb.append(getHex(water));
        sb.append(addr);
        sb.append(data);
        int len = sb.length() / 2;
        sb.insert(0, "02" + getHex(len));
        sb.append("030000");
        byte[] aar = XssUtility.hexStringToBytes(sb.toString());
        CrcUtil.getCrc(aar);
        writeData(XssUtility.bytesToHexString(aar));*/
    }

    public void setDataPort(String cmd, String data) {

        StringBuffer sb = new StringBuffer();
        sb.append(cmd);
        sb.append(XssUtility.getHex(ID));
        sb.append(XssUtility.getHex(water));
        if (!TextUtils.isEmpty(data)) {
            if (data.length() == 1) {
                sb.append("0" + data);
            } else {
                sb.append(data);
            }
        } else {
        }

        int len = sb.length() / 2;
        sb.insert(0, "02" + getHex4(len));
        sb.append("030000");
        byte[] aar = XssUtility.hexStringToBytes(sb.toString());
        CrcUtil.getCrc(aar);
        writeData(XssUtility.bytesToHexString(aar));
    }

    public void setDataPort(int cmd, String data) {
        setDataPort(XssUtility.getHex(cmd), data);
    }

    public void queryParam(int local, String... parm) {
        logx("queryParam", "local: " + local);
        sendPortCmd(03, getHex4(local));

    }

    public void setParam(int local, String... parm) {
        logx("setParam", "local: " + local + "   " + parm[0]);
        sendPortCmd(04, getHex4(local), getHex4(Integer.parseInt(parm[0])));

    }

    public void setParamHex(int local, String parm) {
        logx("setParamHex", "local: " + local + "   " + parm);
        sendPortCmd(04, getHex4(local), parm);

    }

    public void action(int local, String... parm) {

        int len = 0;
        String temp = null;
        if (!TextUtils.isEmpty(parm[0])) {
            len = Integer.parseInt(parm[0]);
            temp = parm[0];
        }
        logx("action", "local: " + local + "   " + temp);

        sendPortCmd(05, XssUtility.getHex(local), XssUtility.getHexLeng(len, 8));
    }


    public void trandsCmd(String cmd, String... parm) {
    }

    public void actionHex(int local, String parm) {
        actionHex(XssUtility.getHex(local), parm);
    }

    public void actionHex(String hexLocal, String parm) {
        logx("actionHex", "local: " + hexLocal + "   " + parm);
        sendPortCmd(05, hexLocal, parm);
    }

    public String getHex4(int intdata) {
        return XssUtility.getHexLeng(intdata, 4);
    }


    public void ship(String order, int... parm) {
    }


    public void loadGoods(int... parm) {
        StringBuffer sb = new StringBuffer();
        if (parm == null || parm.length < 1) {
            return;
        }
        for (int x = 0; x < parm.length; x++) {
            sb.append(XssUtility.getHex(parm[x]));
        }
        queryOther(2, sb.toString());
    }


    public void ship(int slot, String mode, String data) {
        if (TextUtils.isEmpty(data)) {
            data = "00000000";
        } else {
            while (data.length() < 8) {
                data = "0" + data;
            }
        }
        sendPortCmd(01, XssUtility.getHex(slot), data);
    }

    public void logx(String fun, String msg) {
        XssTrands.getInstanll().logd(getName() , fun + ": " + msg);
    }

    public void logx(String msg) {
        XssTrands.getInstanll().logd(getName() , msg);
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }
    public String deal06(PortFormat portFormat) {
        return "";
    }


}
