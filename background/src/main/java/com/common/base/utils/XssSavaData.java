package com.common.base.utils;

import android.content.Context;
import android.text.TextUtils;

import com.common.base.MsgWhat;

import net.grandcentrix.tray.AppPreferences;


public class XssSavaData {
    private static XssSavaData m_Instance = null;
    public Context m_context = null;
    private AppPreferences mTrayPreferences;

    public static synchronized XssSavaData getInstance() {
        if (null == m_Instance) {
            m_Instance = new XssSavaData();
        }
        return m_Instance;
    }

    public void init(Context context) {
        m_context = context;
        mTrayPreferences = new AppPreferences(context);
    }

    public String getMacId() {
        String id = mTrayPreferences.getString(XssData.MACID, "");
        return id;
    }

    /**
     * 1   c出菜机   2   薯条站
     *
     * @param
     * @return
     * @author hua
     * @time 2021/10/26 16:15
     */
    public String getMacType() {
        String id = mTrayPreferences.getString(XssData.MACTYPEKEY,  XssData.ChuCaiJi);
        return id;
    }

 /**
     * 1   c出菜机   2   薯条站
     *
     * @param
     * @return
     * @author hua
     * @time 2021/10/26 16:15
     */
    public void setMacType(String macTypr) {
        savaData(XssData.MACTYPEKEY,macTypr);
    }

    public boolean isChuCaiJi() {
//        return XssData.ChuCaiJi .equals(getMacType());
        return false;
    }

    //  1: 主板串口  3.Rfid 串口
    public String getPortData(int x) {
        String port = "";
        switch (x) {
            case 1:
                port = getData(XssData.portaar[0]);
                if (TextUtils.isEmpty(port)) {
                    port = "/dev/ttyS1";
                }
                break;
            case 2:
                port = getData(XssData.portaar[2]);

                break;
            case 3:
                port = getData(XssData.portaar[4]);
                if (TextUtils.isEmpty(port)) {
                    port = "/dev/ttyS3";
                }
                break;
        }

        return port;
    }

    //  1: 主板串口
    public int getPortBoTe(int x) {
        int bote = 0;
        switch (x) {
            case 1:
                getData(XssData.portaar[1], 0);
                break;
            case 2:
                break;
            case 3:
                break;
        }
        if (bote == 0) {
            bote = 19200;
        }
        return bote;
    }

    public void savaData(String key, String vul) {
        mTrayPreferences.put(key, vul);
    }

    public void savaData(String key, int vul) {
        mTrayPreferences.put(key, vul);
    }

    public void savaData(String key, boolean vul) {
        mTrayPreferences.put(key, vul);
    }

    public String getData(String key) {
        return getData(key, "");

    }

    public String getData(String key, String defaul) {
        String id = mTrayPreferences.getString(key, defaul);
        return id;
    }

    public int getData(String key, int defaul) {
        int id = mTrayPreferences.getInt(key, defaul);
        return id;
    }

    public boolean getData(String key, boolean defaul) {
        boolean id = mTrayPreferences.getBoolean(key, defaul);
        return id;
    }

    public boolean isTimiingReboot() {
        return getData(XssData.B_TimingReboot, false);
    }

    public int getTimingReboot() {
        return getData(XssData.TimingReboot, 0);
    }

    public void savaTimiingReboot(boolean isreboot) {
        savaData(XssData.B_TimingReboot, isreboot);
    }
}

