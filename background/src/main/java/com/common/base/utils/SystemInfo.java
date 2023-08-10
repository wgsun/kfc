package com.common.base.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;

public class SystemInfo {
    static SystemInfo systemInfo;
    public final String HIDE_STATUSBAR_CMD = "su -c service call activity 42 s16 com.android.systemui";
    private final String CMD_CPU_INFO = "top -n 1";
    private final String CMD_REBOOT = "su -c reboot";

    /**
     * 支持工控  四信
     * @param 
     * @return 
     * @author hua
     * @time 2022/2/21 9:52
     */
    public void rebootDevice(Context m_context ) {
        Intent intent = new Intent("com.fourfaith.reboot");
        m_context.sendBroadcast(intent);
        return;
    }

/**
 *   隐藏导航栏  true 隐藏状态栏和导航栏 false 显示导航栏和状态栏
 * @param 
 * @return 
 * @author hua
 * @time 2022/2/21 10:01
 */
    public void hideNavKey(Context m_context,boolean ishide) {
        Intent intent=new Intent();
        intent.setAction("android.intent.action.hidenavigation");
        String result=ishide? "1" : "0";
        Log.d("xxx", "hideNavKey: "+result);
        intent.putExtra("enable",ishide);
        m_context.sendBroadcast(intent);
    }


    public static SystemInfo getInstance() {
        if (systemInfo == null) {
            synchronized (SystemInfo.class) {
                if (systemInfo == null) {
                    systemInfo = new SystemInfo();
                }
            }
        }
        return systemInfo;
    }

    /*
     *第一行：User 35%, System 13%, IOW 0%, IRQ 0% // CPU占用率
     *第二行:User 109 + Nice 0 + Sys 40 + Idle 156 + IOW 0 + IRQ 0 + SIRQ 1 = 306
     */
    public String getCPURunInfo() {
        String line = "";
        InputStream is = null;
        Runtime runtime = Runtime.getRuntime();
        Process proc = null;
        try {
            proc = runtime.exec(CMD_CPU_INFO);
            is = proc.getInputStream();
            InputStreamReader mInputStreamReader = new InputStreamReader(is);
            // 换成BufferedReader
            BufferedReader buf = new BufferedReader(mInputStreamReader);
            do {
                line = buf.readLine();
                // 前面有几个空行
                if ((line != null) && line.startsWith("User")) {
                    // 读到第一行时，我们再读取下一行
                    //line = buf.readLine();
                    break;
                }
            } while (true);
            if (is != null) {
                buf.close();
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    public String getTotalUsage() {
        String strInfo = getCPURunInfo();
        String[] CPUusr = strInfo.split("%");
        String[] CPUusage = CPUusr[0].split("User");
        String[] SYSusage = CPUusr[1].split("System");
        String strCPUusage = CPUusage[1].trim();
        String strSYSusage = SYSusage[1].trim();
        Float fTotalUsage = Float.valueOf(strCPUusage) + Float.valueOf(strSYSusage);
        String strTotalUsage = String.valueOf(fTotalUsage);

        return strTotalUsage;
    }

    public String getUserUsage() {
        String strInfo = getCPURunInfo();
        String[] CPUusr = strInfo.split("%");
        String[] CPUusage = CPUusr[0].split("User");
        String strCPUusage = CPUusage[1].trim();

        return strCPUusage;
    }

    public String getSysUsage() {
        String strInfo = getCPURunInfo();
        String[] CPUusr = strInfo.split("%");
        String[] SYSusage = CPUusr[1].split("System");
        String strSYSusage = SYSusage[1].trim();

        return strSYSusage;
    }

    /**
     * SDCARD是否存
     */
    public boolean externalMemoryAvailable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取手机内部剩余存储空间
     *
     * @return
     */
    public long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize / 1024 / 1024;
    }

    /**
     * 获取手机内部总的存储空间
     *
     * @return
     */
    public long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize / 1024 / 1024;
    }

    /**
     * 获取SDCARD剩余存储空间
     *
     * @return
     */
    public long getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize / 1024 / 1024;
        } else {
            return -1;
        }
    }

    /**
     * 获取SDCARD总的存储空间
     *
     * @return
     */
    public long getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize / 1024 / 1024;
        } else {
            return -1;
        }
    }

    public void rebootDevice() {
        try {
            Runtime.getRuntime().exec(CMD_REBOOT);
        } catch (IOException e) {
        }
    }

    public void hideBottomUIMenu() {
        try {
            if (Build.VERSION.SDK_INT <= 17) {
                String ProcID = "42";
                Process proc = Runtime.getRuntime().exec(HIDE_STATUSBAR_CMD);
                proc.waitFor();
            }
        } catch (Exception ex) {

        }

    }

    /**
     * 这是使用adb shell命令来获取mac地址的方式
     *
     * @return
     */
    public String getMac() {
        String macSerial = null;
        String str = "";

        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        return macSerial;
    }

    public void execCmd(String cmd) {
        Process process;
        try {
            process = Runtime.getRuntime().exec(cmd);
            process.waitFor();
        } catch (Exception e) {
        }
    }

    public void hideBar() {
        String ProcID = "79";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            ProcID = "42"; // ICS AND NEWER
        }
        try {
            execCmd("service call activity " + ProcID + " s16 com.android.systemui");  //志强通达
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showBar() {
        try {
            execCmd("am startservice -n com.android.systemui/.SystemUIService");   //志强通达
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exec(String cmd) {
        OutputStream os = null;
        try {
            if (os == null) {
                os = XssUtility.getBoardSU().getOutputStream();
            }
            os.write(cmd.getBytes());
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void simulateKey(int keyCode) {
        exec("input keyevent " + keyCode + "\n");
    }


    //ryd 3399
    public String execRootCmd(String cmd) {
        String result = "";
        if (Build.VERSION.SDK_INT < 25) {  //7.1.1
            return result;
        }
        DataOutputStream dos = null;
        DataInputStream dis = null;

        try {
            Process p = XssUtility.getBoardSU();// 经过Root处理的android系统即有su命令

            // Process p =   Runtime.getRuntime().exec(new String[]{"/system/xbin/su","-c", cmd});

            dos = new DataOutputStream(p.getOutputStream());
            dis = new DataInputStream(p.getInputStream());

            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            String line = null;
            while ((line = dis.readLine()) != null) {
                result += line;
            }
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}
