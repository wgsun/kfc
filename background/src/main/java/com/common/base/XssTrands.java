package com.common.base;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import com.common.base.BeanInfo.PortFormat;
import com.common.base.utils.SystemInfo;
import com.common.base.utils.XssData;
import com.common.base.utils.XssHandleUtils;
import com.common.base.utils.XssUtility;

import com.common.logger.MuhuaLog;
import com.common.serial.SerialBase;
import com.common.serial.kfc.serialOne;
import com.common.serial.kfc.serialtwo;
import com.common.serial.kfc.KfcPortControl;
import com.hua.back.kfc.base.YumControl;
import com.hua.back.kfc.db.KfcSqlControl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

import android_serialport_api.SerialPortFinder;

public class XssTrands {
    private static final String TAG = "XssTrands";
    private static XssTrands m_Instance = null;
    private Context m_context = null;
    private static XssControl mXssControl = null;
    SerialBase serialBase;
    SerialBase serialBase2;
    Handler XssControlHander = null;
    Map<String, Long> clickMap = new HashMap<>();
    private String[] aar_parm;
    public Context mContext;

    public static synchronized XssTrands getInstanll() {
        if (null == m_Instance) {
            m_Instance = new XssTrands();
        }
        return m_Instance;
    }

    public void setXssControlHander(Handler hander) {
        XssControlHander = hander;
    }

    public void init(Context context, int errId, int actarray) {
        m_context = context;
        XssCrashHandler.getInstance().init(context);
        KfcSqlControl.getInstall().init(m_context);
        try {
            String[] errCode = context.getResources().getStringArray(errId);
            aar_parm = context.getResources().getStringArray(actarray);
            serialBase = new serialtwo();
            serialBase2 = new serialOne();
            KfcPortControl.getInstall().init((serialtwo) serialBase, (serialOne) serialBase2);
            serialBase2.init(m_context);
        } catch (Exception e) {
            e.printStackTrace();
            logd(this.getClass().getSimpleName(), "init  Exception: " + e.toString());
        }

        serialBase.init(m_context);
    }

    public boolean isContinClick(String key) {
        return isContinClick(key, 7);
    }

    public boolean isContinClick(String key, long decTime) {
        Object o = clickMap.get(key);
        if (o != null) {
            long currTime = (long) o;
            if (System.currentTimeMillis() - currTime < decTime * 100) {
                return true;
            }
        }
        clickMap.put(key, System.currentTimeMillis());
        return false;
    }

    public void startWorkThread() {
        if (null != mXssControl) {
            mXssControl.quit();
            mXssControl = null;
        }
        mXssControl = new XssControl(m_context, "XssControl");
        mXssControl.start();
    }

    public void sendMqttMsg(int topc, String msg) {
        XssHandleUtils.sendMessage(XssControlHander, MsgWhat.SERVER_MQTT, topc, 30000, msg);

    }

    public void stopWorkThread() {
        if (mXssControl != null) {
            mXssControl.quit();
            mXssControl = null;
        }
    }

    private final CopyOnWriteArrayList<VendEventListener> m_Callbacks = new CopyOnWriteArrayList<VendEventListener>();

    public void registerListener(VendEventListener callback) {
        synchronized (m_Callbacks) {
            if (null == callback) {
                return;
            }

            if (!(m_Callbacks.contains(callback))) {
                m_Callbacks.add(callback);
            }
        }
    }

    public void unregisterListener(VendEventListener callback) {
        synchronized (m_Callbacks) {
            if (null == callback) {
                return;
            }
            if (m_Callbacks.contains(callback)) {
                m_Callbacks.remove(callback);
            }

        }
    }

    private void sendNotifyToUI(XssEventInfo cEventInfo) {
        synchronized (m_Callbacks) {
            for (VendEventListener c : m_Callbacks) {
                c.VendEvent(cEventInfo);
            }
        }
    }

    // VendEventListener interface
    public interface VendEventListener {
        public void VendEvent(XssEventInfo cEventInfo);
    }

    private final Handler m_cEventHandlerForUI = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            handleMessageToUI(msg.what, (Bundle) msg.obj);
        }
    };

    private volatile int m_iEventIDTemp = -1;
    private volatile int m_ilParam1 = -1;

    public void handleMessageToUI(int what, Bundle bundle) {
        Bundle msgBundle = bundle;

        int iEventID = msgBundle.getInt("eID");
        int lParam1 = msgBundle.getInt("lP1");
        int lParam2 = msgBundle.getInt("lP2");
        long lParam3 = msgBundle.getLong("lP3");
        String lParam4 = msgBundle.getString("lP4");
        notifyUI(iEventID, lParam1, lParam2, lParam3, lParam4);
    }

    private void notifyUI(int iEventID, int lParam1, int lParam2, long lParam3, String lParam4) {
        XssEventInfo cEventInfo = new XssEventInfo();

        cEventInfo.SetEventID(iEventID);
        cEventInfo.SetlParam1(lParam1);
        cEventInfo.SetlParam2(lParam2);
        cEventInfo.SetlParam3(lParam3);
        cEventInfo.SetlParam4(lParam4);
        sendNotifyToUI(cEventInfo);
    }

    private void notifyUI(int iEventID, int lParam1, int lParam2, long lParam3, String
            lParam4, Object lParam5) {
        XssEventInfo cEventInfo = new XssEventInfo();

        cEventInfo.SetEventID(iEventID);
        cEventInfo.SetlParam1(lParam1);
        cEventInfo.SetlParam2(lParam2);
        cEventInfo.SetlParam3(lParam3);
        cEventInfo.SetlParam4(lParam4);
        cEventInfo.SetlParam5(lParam5);
        sendNotifyToUI(cEventInfo);
    }


    public void sendMsgToUI(int iEventID, int lParam1, int lParam2, long lParam3, String
            lParam4) {

        if ((m_iEventIDTemp == iEventID) && (m_ilParam1 == lParam1)) {
            XssHandleUtils.removeMessages(m_cEventHandlerForUI, iEventID);
        }

        m_iEventIDTemp = iEventID;
        m_ilParam1 = lParam1;

        Bundle msgBundle = new Bundle();

        msgBundle.putInt("eID", iEventID);
        msgBundle.putInt("lP1", lParam1);
        msgBundle.putInt("lP2", lParam2);
        msgBundle.putLong("lP3", lParam3);
        msgBundle.putString("lP4", lParam4);
        XssHandleUtils.sendMessage(m_cEventHandlerForUI, iEventID, -1, -1, msgBundle);
    }

    public void sendMsgToUIDelay(int iEventID, int lParam1, int lParam2, long lParam3, long delayMillis, String lParam4) {
        Bundle msgBundle = new Bundle();

        msgBundle.putInt("eID", iEventID);
        msgBundle.putInt("lP1", lParam1);
        msgBundle.putInt("lP2", lParam2);
        msgBundle.putLong("lP3", lParam3);
        msgBundle.putString("lP4", lParam4);
        XssHandleUtils.removeMessages(m_cEventHandlerForUI, iEventID);
        XssHandleUtils.sendMsgDelayed(m_cEventHandlerForUI, iEventID, -1, delayMillis, msgBundle);
    }

    public void sendMsgToUI(int iEventID, String
            lParam4) {
        int lParam1 = 1;
        if ((m_iEventIDTemp == iEventID) && (m_ilParam1 == lParam1)) {
            XssHandleUtils.removeMessages(m_cEventHandlerForUI, iEventID);
        }

        m_iEventIDTemp = iEventID;

        Bundle msgBundle = new Bundle();

        msgBundle.putInt("eID", iEventID);
        msgBundle.putInt("lP1", lParam1);
        msgBundle.putInt("lP2", 1);
        msgBundle.putLong("lP3", 1);
        msgBundle.putString("lP4", lParam4);
        XssHandleUtils.sendMessage(m_cEventHandlerForUI, iEventID, -1, -1, msgBundle);
    }


    /**
     * 获取当前进程的名字
     *
     * @return 返回进程的名字
     */
    public String getAppName(Context context) {
        int pid = android.os.Process.myPid(); // Returns the identifier of this process
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List list = activityManager.getRunningAppProcesses();
        Iterator i = list.iterator();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pid) {
                    // 根据进程的信息获取当前进程的名字
                    return info.processName;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 没有匹配的项，返回为null
        return null;
    }


    public void stopAct() {
        XssTrands.getInstanll().actionHex(255, "00000000");

    }


    private Vector<SerialPortFinder.Driver> mDrivers = null;

    Vector<SerialPortFinder.Driver> getDrivers() throws IOException {
        if (mDrivers == null) {
            mDrivers = new Vector<SerialPortFinder.Driver>();
            LineNumberReader r = new LineNumberReader(new FileReader(
                    "/proc/tty/drivers"));
            String l;
            while ((l = r.readLine()) != null) {
                // Issue 3:
                // Since driver name may contain spaces, we do not extract
                // driver name with split()
                String drivername = l.substring(0, 0x15).trim();
                String[] w = l.split(" +");
                if ((w.length >= 5) && (w[w.length - 1].equals("serial"))) {
                    Log.d(TAG, "Found new driver " + drivername + " on "
                            + w[w.length - 4]);
                    mDrivers.add(new SerialPortFinder.Driver(drivername, w[w.length - 4]));
                }
            }
            r.close();
        }
        return mDrivers;
    }

    public String[] getAllDevices() {
        Vector<String> devices = new Vector<String>();
        // Parse each driver
        Iterator<SerialPortFinder.Driver> itdriv;
        try {
            itdriv = getDrivers().iterator();
            while (itdriv.hasNext()) {
                SerialPortFinder.Driver driver = itdriv.next();
                Iterator<File> itdev = driver.getDevices().iterator();
                while (itdev.hasNext()) {
                    String device = itdev.next().getName();
                    String value = String.format("%s (%s)", device,
                            driver.getName());
                    if (!TextUtils.isEmpty(value)) {
                        if ((device.contains("ttyS")) || (device.contains("ttyO")) || (device.contains("ttymxc"))
                                || (device.contains("ttyES")) || (device.contains("ttysWK")) || (device.contains("ttyCOM"))
                                || (device.contains("ttyHSL")) || (device.contains("ttyXRUSB")) || (device.contains("ttyWK"))
                                || (device.contains("ttyRK")) || (device.contains("ttys"))) {
                            devices.add(value);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return devices.toArray(new String[devices.size()]);
    }

    public String[] getAllDevicesPath() {
        Vector<String> devices = new Vector<String>();
        // Parse each driver
        Iterator<SerialPortFinder.Driver> itdriv;
        try {
            itdriv = getDrivers().iterator();
            while (itdriv.hasNext()) {
                SerialPortFinder.Driver driver = itdriv.next();
                Iterator<File> itdev = driver.getDevices().iterator();
                while (itdev.hasNext()) {
                    String device = itdev.next().getAbsolutePath();
                    if (!TextUtils.isEmpty(device)) {
                        if ((device.contains("ttyS")) || (device.contains("ttyO")) || (device.contains("ttymxc"))
                                || (device.contains("ttyES")) || (device.contains("ttysWK")) || (device.contains("ttyCOM"))
                                || (device.contains("ttyHSL")) || (device.contains("ttyXRUSB")) || (device.contains("ttyWK"))
                                || (device.contains("ttyRK"))) {
                            devices.add(device);
                        }

                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return devices.toArray(new String[devices.size()]);
    }


    private String getTopActivityInfo(Context context) {
        String packageName = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    packageName = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            packageName = tasks.get(0).processName;
        }
        return packageName;
    }


    public void reboot() {
        LoggerDebug("XssTrands", "reboot 重启机器");
        SystemInfo.getInstance().rebootDevice(m_context);
    }

    public void hideNavKey(boolean ishide) {
        LoggerDebug("XssTrands", "hideNavKey 隐藏导航栏：" + ishide);
        if (true) {
            return;
        }
        if (m_context == null) {
            LoggerDebug("XssTrands", "hideNavKey 隐藏导航栏 m_context == null  ：" + ishide);

            return;
        }
        SystemInfo.getInstance().hideNavKey(m_context, ishide);
    }

    public void logd(String clazz, String msg) {
        MuhuaLog.getInstance().LoggerDebug(clazz, msg);

    }

    public void LoggerDebug(String tag, String msg) {
        logd(tag, msg);
    }

    public void LoggerInfo(String tag, String msg) {
        MuhuaLog.getInstance().LoggerInfo("App--", tag, "", msg);
    }

    public void LoggerError(String tag, String msg) {
        MuhuaLog.getInstance().LoggerError("App--", tag, "", msg);
    }


    public void cleanFault() {
        if (serialBase != null)
            serialBase.cleanFault();
    }

    public void cleanFault2() {
        if (serialBase2 != null)
            serialBase2.cleanFault("010101" + "010101");
    }

    public void queryStatus() {
        if (serialBase != null)
            serialBase.queryStatus();
    }

    public void queryStatus2(String hex) {
        if (serialBase2 != null)
            serialBase2.queryStatus(hex);
    }


    public void queryDriveInfo() {
        if (serialBase != null)
            serialBase.queryDriveInfo();
    }

    public void queryDriveInfo2() {
        if (serialBase2 != null)
            serialBase2.queryDriveInfo();
    }

    public void queryParam(int local, String... parm) {
        if (serialBase != null)
            serialBase.queryParam(local, parm);
    }

    public void queryParam2(int local, String... parm) {
        if (serialBase2 != null)
            serialBase2.queryParam(local, parm);
    }

    public void queryOther(int type, String... parm) {
        if (serialBase != null)
            serialBase.queryOther(type, parm);
    }

    public void setParam(int local, String... parm) {
        if (serialBase != null)
            serialBase.setParam(local, parm);
    }

    public void setParamHex(int local, String parm) {
        if (serialBase != null)
            serialBase.setParamHex(local, parm);
    }

    public void setParamHex2(int local, String parm) {
        if (serialBase2 != null)
            serialBase2.setParamHex(local, parm);
    }

    public void action(int local, String... parm) {
        if (serialBase != null)
            serialBase.action(local, parm);
    }


    public void actionHex(int local, String parm) {
        if (serialBase != null)
            serialBase.actionHex(local, parm);
    }

    public void actionHex2(int local, String parm) {
        if (serialBase2 != null)
            serialBase2.actionHex(local, parm);
    }

    public void actionHex(String local, String parm) {
        if (serialBase != null)
            serialBase.actionHex(local, parm);
    }

    public void ship(int slot, String mode, String data) {
        if (serialBase != null)
            serialBase.ship(slot, mode, data);
    }

    public void loadGoods(int... parm) {
        if (serialBase != null)
            serialBase.loadGoods(parm);
    }

    public void ship(String order, int... parm) {
        if (serialBase != null)
            serialBase.ship(order, parm);
    }

    public int getFitScreenSize(int defaultSize) {
        return defaultSize;
    }

    public boolean isDigital(String data) {
        if ((null == data) || (data.length() < 1)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[0-9]*$");
        return pattern.matcher(data).matches();
    }


    public void initKfcErrCode(String[] errCode, int wan, HashMap<Integer, String> hashMap) {
        for (String s : errCode) {
            if (!TextUtils.isEmpty(s)) {
                s.trim();
                int x = s.indexOf("~");
                if (x > 0 && x < s.length() - 1) {
                    hashMap.put(wan + Integer.parseInt(s.substring(0, x)), s.substring(x + 1));
                }
            }
        }
    }

    public String getErrCode(String error, boolean isBom) {
        String msg = "";
        if (TextUtils.isEmpty(error)) {
        } else {
            int code = Integer.parseInt(error, 16);
            return getErrCode(code, isBom);
        }
        return msg;
    }

    /**
     * @param type 0信息展示  1错误提示
     * @return
     * @author hua
     * @time 2022/7/20 11:25
     */
    public void toast(String msg, int type) {
        sendMsgToUI(MsgWhat.SHOWTOAST, type, 0, 0, msg);

    }

    public String getErrCode(int code, boolean isBom) {
        String msg = "";
        if (code == 0) {
            return "";
        } else {
            code += isBom ? XssData.NumErrorBoom : XssData.NumErrorDrop;
        }
        msg = YumControl.getInstall().getCodemsg(code);
        return msg;
    }


    public String[] getActionAaar() {
        return aar_parm;
    }


    public void trandsCmd(String cmd, String... parm) {
        if (serialBase != null)
            serialBase.trandsCmd(cmd, parm);
    }

    public String getCmd80Msg(PortFormat portFormat) {
        if (serialBase != null) {
            return serialBase.deal80Msg(portFormat);
        }
        return "";
    }

    public void sendQureyACT(int count) {
        if (serialBase != null) {
            serialBase.sendQureyACT(count);
        }
    }

    public boolean isAppForeground(Context m_context) {
        boolean bRet = false;
        ActivityManager am = (ActivityManager) m_context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);

        if (tasks != null && tasks.size() > 0) {
            String topActivityInfo = getTopActivityInfo(m_context);
            if (TextUtils.isEmpty(topActivityInfo)) {
                topActivityInfo = tasks.get(0).topActivity.getPackageName();
            } else if (!topActivityInfo.equals(m_context.getPackageName())) {
                LoggerDebug(TAG, "isAppForeground 前台正在运行程序包名： " + topActivityInfo);
            }
            // LoggerDebug(TAG, "isAppForeground topActivity: " + topActivity.getPackageName() + " == pack" + m_context.getPackageName() + " skin packName " + TcnShareUseData.getInstance().getSkinAppPackName());
            if (topActivityInfo.equals(m_context.getPackageName())) {
                bRet = true;
            } else {
                LoggerDebug(TAG, "isAppForeground 程序前台运行程序： " + topActivityInfo);
            }
        } else {
            LoggerDebug(TAG, "isAppForeground 程序前台运行出错 tasks == null ");
         /*   if (main_activity_Status == TcnSavaData.MAIN_Activity.ONUNLOCK) {
                return false;
            }
            if (main_activity_Status != TcnSavaData.MAIN_Activity.ONDESTORY) {
                return true;
            }*/
        }

        return bRet;
    }

    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    public PortFormat getPortFormat(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return null;
        }
        int leng = msg.length();
        if (leng < 22) {
            return null;
        }
        msg = msg.toUpperCase();
        PortFormat portFormat = new PortFormat();
        int x = 2;
        portFormat.setLeng(msg.substring(x, x = x + 4));
        portFormat.setCmd(msg.substring(x, x = x + 2));
        portFormat.setId(msg.substring(x, x = x + 2));
        portFormat.setSn(msg.substring(x, x = x + 2));
        switch (portFormat.getCmd()) {
            case "80":
                try {
                    portFormat.setState(msg.substring(x, x = x + 2));
                    portFormat.setData(msg.substring(x, x = x + 24));
                    portFormat.setErrcode(msg.substring(leng - 18, leng - 6));
                    if (serialBase != null) {
                        serialBase.deal80(portFormat);
                    }
                } catch (Exception e) {
                    logd(TAG, "80错误   " + e.toString());
                }

                break;
            case "83":
            case "84":
                portFormat.setAddr(Integer.parseInt(msg.substring(x, x = x + 4), 16));
                portFormat.setNum(msg.substring(x, x = x + 2));
                if (x < leng - 6) {
                    portFormat.setData(msg.substring(x, leng - 6));
                }
                break;
            case "A0":

                break;
            case "85":
                portFormat.setErrcode(msg.substring(leng - 18, leng - 6));
                break;
            case "A1":
                portFormat.setState(msg.substring(x, x = x + 2));
//                portFormat.setErrcode(msg.substring(x, x = x + 4));  故障代码位置改变
                int tastNum = Integer.parseInt(msg.substring(x, x = x + 2));
                List<PortFormat.StatesActCmd> list = new ArrayList<>();
                if (tastNum > 0) {
                    if (msg.length() > tastNum * 8 + x) {
                        for (int i = 0; i < tastNum; i++) {
                            PortFormat.StatesActCmd p = new PortFormat.StatesActCmd();
                            p.setCmd(Integer.parseInt(msg.substring(x, x = x + 2), 16));
                            p.setCheck(msg.substring(x, x = x + 2));
                            p.setOther(msg.substring(x, x = x + 4));
                            list.add(p);
                        }
                    } else {
                        logd(TAG, "数据解析失败，任务长度不够 tastNum= " + tastNum
                                + "  msg.length= " + msg.length() + "   x=" + x + "    msg=" + msg);
                    }
                } else {
                    portFormat.setAddr(Integer.parseInt(msg.substring(x, x = x + 2), 16));
                    portFormat.setCmdCheck((msg.substring(x, x = x + 2)));
                }
                portFormat.setList(list);
                portFormat.setLanNum(Integer.parseInt(msg.substring(x, x = x + 2), 16));
                portFormat.setErrcode(msg.substring(leng - 18, leng - 6));
                String error = XssTrands.getInstanll().getErrCode(portFormat.getErrcodeOne(), false);
                if (!TextUtils.isEmpty(error)) {
                    sendMsgToUI(MsgWhat.SHOWERROR, error);
                } else {
                    sendMsgToUI(MsgWhat.SHOWERROR, serialBase.deal06(portFormat) + "\n" + msg);
                    if (!TextUtils.isEmpty(nextError)) {
                        sendMsgToUI(MsgWhat.SHOWERROR, "");
                    }
                }
                break;
        }
        return portFormat;
    }


    String nextError;

    public String deal06(PortFormat data) {
        if (serialBase != null) {
            return serialBase.deal06(data);
        }
        return "";
    }

    public int getEdNum(EditText editText) {
        int num = MsgWhat.ERR_INT;
        if (editText == null) {
            return num;
        }
        String s = editText.getText().toString().trim();
        if (!TextUtils.isEmpty(s) && XssUtility.isDigital(s)) {
            num = Integer.parseInt(s);
        }
        return num;

    }


}

