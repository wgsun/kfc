package com.common.serial.kfc;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.common.base.BeanInfo.KfcOrder;
import com.common.base.XssTrands;
import com.common.base.utils.FileUtils;
import com.common.base.utils.Utils;
import com.google.gson.Gson;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PaxLog {
    String TAG = "PaxLog";
    static PaxLog paxLog;
    final String PAXLOG_PATH = "/PaxLog";
    final static String monthDir = "yyyyMM";
    private HandlerThread m_HandlerThreadServer;
    private Handler sonWritekHandler;
    final int data_write = 1;
    final int data_detele = 2;
    final int data_read = 3;

    public static synchronized PaxLog getInstall() {
        if (paxLog == null) {
            paxLog = new PaxLog();
        }
        return paxLog;
    }

    public void writeLog(final String data) {
        if (sonWritekHandler == null) {
            synchronized (this) {
                if (sonWritekHandler == null) {
                    init();
                }
            }
        }
        Message msg = new Message();
        msg.what = data_write;
        msg.obj = data;
        sonWritekHandler.sendMessage(msg);
    }

    public String getCurrPath() {
        String path = Utils.getExternalStorageDirectory() + PAXLOG_PATH + "/" + getDirName(true);
        return path;
    }

    private synchronized void startLog(String data) {
        String str =data + "\r\n";
        XssTrands.getInstanll().logd(TAG,"startLog:  "+data);
        File file = createFile(getCurrPath(), getDirName(false) + ".txt");
        if (file != null) {
            FileWriter fw = null;
            try {
                // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
                fw = new FileWriter(file, true);
                fw.write(str);
                fw.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fw != null) {
                    try {
                        fw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 读取文本文件
     * @param fileName
     * @return
     */
    public synchronized String readFile(String filePath, String fileName) {
        if (null == fileName) {
            return null;
        }
        List<KfcOrder> kfcOrderList = new ArrayList<>();
        if (TextUtils.isEmpty(filePath)) {
            filePath = "";
        }
        String mStrRootPath = Utils.getExternalStorageDirectory();
        if (!filePath.startsWith(mStrRootPath)) {
            filePath = mStrRootPath + "/" + filePath;
        }
        String mfile = filePath + "/" + fileName;
        StringBuffer sb = new StringBuffer();
        File file = new File(mfile);
        if (file == null || !file.exists() || file.isDirectory()) {
            Log.e("file", "readFile return.");
            return null;
        }
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line = "";
            Gson gson = new Gson();
            while ((line = br.readLine()) != null) {
                if (!TextUtils.isEmpty(line)) {
                    line = line.trim();
                    if (line.startsWith("{") && line.endsWith("}")) {
                        KfcOrder kfcOrder = gson.fromJson(line, KfcOrder.class);
                        kfcOrderList.add(0,kfcOrder);
                        sb.append(line);
                    }
                }
            }

        } catch (Exception e) {
            XssTrands.getInstanll().logd(TAG, "readFile  历史数据获取失败");
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    public void cleanDIr() {
        FileUtils.deleteDir(getCurrPath());
        FileUtils.deleteDir( "/mnt/sdcard/muhuaLog/Log");
    }
    //删除多余的文件
    public synchronized void dealPlusFile() {
        Date dNow = new Date();   //当前时间
        List<String> list = new ArrayList();
        list.add(getDirName(true));
        list.add(getBeforTime(dNow, -1));
        list.add(getBeforTime(dNow, -2));
        for (int x = 0; x < list.size(); x++) {
            XssTrands.getInstanll().LoggerDebug("PaxLog", "dealPlusFile: " + list.get(x));
        }
        File fileDir = new File(Utils.getExternalStorageDirectory() + PAXLOG_PATH);
        if (fileDir.exists()) {
            if (fileDir.isDirectory()) {
                File[] files = fileDir.listFiles();
                if (files != null && files.length > 0) {
                    for (int i = 0; i < files.length; i++) {
                        File file = files[i];
                        if (!list.contains(file.getName())) {
                            deteFile(file);
                        }
                    }
                }
            } else {
                fileDir.delete();
            }
        }
    }

    public void init() {
        m_HandlerThreadServer = new HandlerThread("WirteHandlerThread");
        m_HandlerThreadServer.start();
        // 消息处理的操作
//设置了两种消息处理操作,通过msg来进行识别
// 消息1
        sonWritekHandler = new Handler(m_HandlerThreadServer.getLooper()) {
            @Override
            // 消息处理的操作
            public void handleMessage(Message msg) {
                //设置了两种消息处理操作,通过msg来进行识别
                switch (msg.what) {
                    // 消息1
                    case data_write:
                        startLog((String) msg.obj);
                        readFile(getCurrPath(), getDirName(false) + ".txt");
                        break;
                    default:
                    case data_detele:
                        dealPlusFile();
                        break;
                    case data_read:
                        break;
                }
            }
        };
        sonWritekHandler.sendEmptyMessageDelayed(data_detele, 10000);

    }

    //删除文件及以下文件夹
    void deteFile(File delFile) {
        XssTrands.getInstanll().LoggerDebug("PaxLog", "deteFile: " + delFile.getName());
//        FileOperation.instance().deleteAllFile();
        try {
            if (delFile == null || !delFile.exists()) {
                return;
            }
            if (delFile.isDirectory()) {
                File[] files = delFile.listFiles();
                if (files != null && files.length > 0) {
                    for (int i = 0; i < files.length; i++) {
                        deteFile(files[i]);
                    }

                }
            }
            Log.d("PaxLog", "deteFile: " + delFile.delete());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String getBeforTime(Date dNow, int month) {
        Date dBefore = new Date();
        Calendar calendar = Calendar.getInstance(); //得到日历
        calendar.setTime(dNow);//把当前时间赋给日历
        calendar.add(calendar.MONTH, month);  //设置为前month月
        dBefore = calendar.getTime();   //得到前month月的时间
        SimpleDateFormat sdf = new SimpleDateFormat(monthDir); //设置时间格式
        String strMonth = sdf.format(dBefore);
        return strMonth;    //格式化前month月的时间
    }

    String getDirName(boolean isFileDir) {
        String str;
        if (isFileDir) {
            str = monthDir;
        } else {
            str = "yyyyMMdd";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(str);
        return sdf.format(System.currentTimeMillis());
    }

    String getCurrentTime() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(System.currentTimeMillis());
    }

    File createFile(String filePath, String fileName) {
        String mDirPath = filePath;
        try {
            File mDir = new File(mDirPath.trim());
            if (!mDir.exists()) {
                mDir.mkdirs();
            }
            String mFilePath = mDirPath + "/" + fileName;
            File mFile = new File(mFilePath.trim());
            if (!mFile.exists()) {
                mFile.createNewFile();
            }
            return mFile;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


}
