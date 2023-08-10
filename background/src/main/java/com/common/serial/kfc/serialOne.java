package com.common.serial.kfc;

import android.text.TextUtils;

import com.common.base.MsgWhat;
import com.common.base.XssTrands;
import com.common.base.utils.XssData;
import com.common.base.utils.XssSavaData;
import com.common.base.utils.XssUtility;
import com.common.serial.SendSerialBean;
import com.common.serial.SerialBase;

/**
 * FileName: KfcPort
 * Author: hua
 * Date: 2021/9/8 14:45
 * Description:
 */
public class serialOne extends SerialBase {
    public void openPort() {
        String port = "dev/ttyS4";
        int bote = XssSavaData.getInstance().getPortBoTe(1);
        logx("init", "port:  " + port + "   " + bote);
        portController.openSerialPort(port, bote);
    }

    public void portError() {
        XssTrands.getInstanll().sendMsgToUI(MsgWhat.ERRORCODE, MsgWhat.errorCode9003, 2, 0, "");
    }

    public void queryStatu(int states) {
        sendPortCmd(00, XssUtility.getHexLeng(states, 2));
    }

    /**
     * 开始油炸
     * @param
     * @return
     * @author hua
     * @time 2022/6/14 10:29
     */
    public void ship(int key, int type, int isMeat, int insert, int weight) {
        String hex = "01050919";
        String msg = XssSavaData.getInstance().getData(XssData.FOODZhaCheng + type);
        if (TextUtils.isEmpty(msg) || msg.startsWith("000000")) {
            switch (type) {
                case 1:
                    msg = "2-6-10-25";
                    break;
                case 2:
                    msg = "1-5-9-25";
                    break;
                case 3:
                    msg = "3-7-11-25";
                    break;
                case 4:
                    msg = "13-17-21-25";
                    break;
                case 5:
                    msg = "4-8-12-25";
                    break;
                case 6:
                    msg = "13-17-21-25";
                    break;
            }
            XssSavaData.getInstance().savaData(XssData.FOODZhaCheng + type, msg);
        }

        if (!TextUtils.isEmpty(msg)) {
            String temp = "";
            String[] aar = msg.split("-");
            temp += getZChex(1, 4, aar);
            temp += getZChex(5, 8, aar);
            temp += getZChex(9, 12, aar);
            temp += getZChex(25, 28, aar);
            if (!"00000000".equals(temp)) {
                hex = temp;
            }
        }
        logx("ship "," type=" + type + "  key=" + key + " FOODZhaCheng  msg= " + msg + " hex=" + hex + " isMeat=" + isMeat + " insert=" + insert + "  weight=" + weight);
//        0-其它（插单或其它)； 1-鸡块； 2-直薯条；3-波纹薯条；  此处与其他地方不一样
        int temp = type;
        switch (type) {
            case 1:
                temp = 2;
                break;
            case 2:
                temp = 1;
                break;
            case 6:
                temp = 2;
                break;
        }

        String data = getHex4(key) + XssUtility.getHexLeng(insert, 2) + XssUtility.getHex(temp) + XssUtility.getHexLeng(isMeat, 2) + hex + "09" + XssUtility.getHexLeng(weight, 4);
        setDataPort(01, data);
    }


    public String getZChex(int start, int end, String[] aar) {
        if (aar != null) {
            for (int x = start; x <= end; x++) {
                for (int y = 0; y < aar.length; y++) {
                    if (String.valueOf(x).equals(aar[y])) {
                        if (x > 16) {
                            return Integer.toHexString(x);
                        } else {
                            return "0" + Integer.toHexString(x);
                        }
                    }
                }
            }
            for (int x = 12 + start; x <= 12 + end; x++) {
                for (int y = 0; y < aar.length; y++) {
                    if (String.valueOf(x).equals(aar[y])) {
                        if (x > 16) {
                            return Integer.toHexString(x);
                        } else {
                            return "0" + Integer.toHexString(x);
                        }
                    }
                }
            }
        }
        if (start > 16) {
            return Integer.toHexString(start);
        } else {
            return "0" + Integer.toHexString(start);
        }
    }


    public void setParamHex(int local, String parm) {
        int num = 0;
        if (!TextUtils.isEmpty(parm)) {
            num = parm.length() / 4;
        }
        logx("setParamHex", "local: " + local + "   num=" + num + "     parm=" + parm);
        setData0304(04, num, getHex4(local), parm);
    }


    public void sendPortCmd(int cmd, String... pram0) {
        switch (cmd) {
            case 00:
                if (pram0 != null && pram0.length > 0) {
                    setDataPort(00, pram0[0]);
                }
                break;
            case 01:
                if (pram0.length >= 2) {
                    setDataPort(05, pram0[0] + pram0[1]);
                }
                break;
            case 02:
                if (pram0 != null && pram0.length > 0) {
                    setDataPort(02, pram0[0]);
                } else {
                    setDataPort(02, "");
                }
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
                    logx("油炸命令： " + pram0[0] + pram0[1]);
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

    protected void dealRev(String cmdData) {
        XssTrands.getInstanll().sendMsgToUI(MsgWhat.PORTDATA_BOM, cmdData);
    }


    /**
     * 解析炸炉
     *
     * @param
     * @return
     * @author hua
     * @time 2022/1/18 14:24
     */
    public synchronized KfcBomInfo parsingBomData(String data) {
        if (TextUtils.isEmpty(data)) {
            return null;
        }
        int leng = data.length();
        if (leng < 22) {
            return null;
        }

        KfcBomInfo kfcBomInfo = null;
        data = data.toUpperCase();
        String cmd = data.substring(6, 8);
        if ("80".equals(cmd)) {
            kfcBomInfo = new KfcBomInfo();
            int x = 2;
            kfcBomInfo.setLeng(data.substring(x, x = x + 4));

            kfcBomInfo.setCmd(data.substring(x, x = x + 2));
            kfcBomInfo.setId(data.substring(x, x = x + 2));
            kfcBomInfo.setSn(data.substring(x, x = x + 2));
            kfcBomInfo.setState(data.substring(x, x = x + 2));
            kfcBomInfo.setLicense_plain(data.substring(x, x = x + 2));
            kfcBomInfo.setLicense_meat(data.substring(x, x = x + 2));
            kfcBomInfo.setRevState(data.substring(x, x = x + 2));
            kfcBomInfo.setInsertState(data.substring(x, x = x + 2));
            kfcBomInfo.setState1(data.substring(x, x = x + 2));
            kfcBomInfo.setState2(data.substring(x, x = x + 2));
            kfcBomInfo.setState3(data.substring(x, x = x + 2));
            kfcBomInfo.setState4(data.substring(x, x = x + 2));
            kfcBomInfo.setZhalu1(data.substring(x, x = x + 2));
            kfcBomInfo.setZhalu2(data.substring(x, x = x + 2));
            kfcBomInfo.setZhalu3(data.substring(x, x = x + 2));
            kfcBomInfo.setZhalu4(data.substring(x, x = x + 2));
            kfcBomInfo.setErrcode(data.substring(x, x = x + 4));
            kfcBomInfo.setIgnoreErrcode(data.substring(x, x = x + 4));

            return kfcBomInfo;
        } else if ("84".equals(cmd)) {
  /*
            int x = 2;
            kfcBomInfo.setLeng(data.substring(x, x = x + 4));
            kfcBomInfo.setCmd(data.substring(x, x = x + 2));
            kfcBomInfo.setId(data.substring(x, x = x + 2));
          kfcBomInfo.setSn(data.substring(x, x = x + 2));
            kfcBomInfo.setState(data.substring(x, x = x + 2));
            kfcBomInfo.setLicense_plain(data.substring(x, x = x + 2));
            kfcBomInfo.setLicense_meat(data.substring(x, x = x + 2));
            kfcBomInfo.setRevState(data.substring(x, x = x + 2));
            kfcBomInfo.setInsertState(data.substring(x, x = x + 2));
            kfcBomInfo.setState1(data.substring(x, x = x + 2));
            kfcBomInfo.setState2(data.substring(x, x = x + 2));
            kfcBomInfo.setState3(data.substring(x, x = x + 2));
            kfcBomInfo.setState4(data.substring(x, x = x + 2));
            kfcBomInfo.setZhalu1(data.substring(x, x = x + 2));
            kfcBomInfo.setZhalu2(data.substring(x, x = x + 2));
            kfcBomInfo.setZhalu3(data.substring(x, x = x + 2));
            kfcBomInfo.setZhalu4(data.substring(x, x = x + 2));
            kfcBomInfo.setErrcode(data.substring(x, x = x + 4));
            kfcBomInfo.setIgnoreErrcode(data.substring(x, x = x + 4));
            return kfcBomInfo;*/
        }
        return null;
    }

    public String getName() {
        return "KfcBomSerial";
    }
}