package com.common.logger;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by muhua on 2021/08/02.
 */
public class MuhuaLog {
    private volatile static MuhuaLog m_Instance = null;
    boolean isDbug = false;
    int logSize = 1024 * 1024;
    public static String logname = "/muhuaLog/Log";
    public static String mFilePath = "/mnt/sdcard/muhuaLog/Log";
    //    private static String mCpyPath = "/mnt/sdcard/cpy";//复制日志 ，暂时不用
    public static final String LOG_FILENAME_PRI_COMMON = "log-";


    public static synchronized MuhuaLog getInstance() {
        if (null == m_Instance) {
            synchronized (MuhuaLog.class) {
                if (null == m_Instance) {
                    m_Instance = new MuhuaLog();
                }
            }
        }
        return m_Instance;
    }

    public void initLog(Context context) {
        if (isDbug) {
            return;
        }

        logFileCheck();
        initLogNew(context);
    }

    public boolean carFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                File file1 = new File(path);
                file1.mkdir();
                return file1.mkdirs();

            } catch (Exception e) {
                Log.d("MuhuaLog", "carFile: " + e.getMessage());
                e.printStackTrace();
                return false;

            }
        }
        return true;
    }

    private static final String PATH_SDCARD = "/mnt/sdcard";

    /**
     * 获取U盘或是sd卡的路径
     *
     * @return
     */
    public static String getEsd() {
        String dir = new String();
        try {
            File sdcardDir = Environment.getExternalStorageDirectory();
            if (sdcardDir.exists()) {
                return sdcardDir.getAbsolutePath();
            }
            File file = new File(PATH_SDCARD);
            if (file.exists() && file.isDirectory()) {
                dir = PATH_SDCARD;
                return dir;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                if (line.contains("secure")) continue;
                if (line.contains("asec")) continue;

                if (line.contains("fat")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        dir = columns[1];
                    }
                }
            }

            br.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dir;
    }

    private void initLogNew(Context context) {

        String path = getEsd() + logname;
        mFilePath = getEsd() + File.separator + "muhuaLog/Log";
//        mCpyPath = getEsd() + File.separator + "muhuaLog/cpy";
//        String path = Environment.getExternalStorageDirectory().getPath() + logname;

        Log.d("MuhuaLog", "initLogNew: " + mFilePath + "   =" + carFile(mFilePath));
//        Log.d("MuhuaLog", "initLogNew: " + mCpyPath + "   =" + carFile(mCpyPath));

        ALog.Config config = ALog.init(context)
                .setLogSwitch(true)// 设置log总开关，包括输出到控制台和文件，默认开
                .setConsoleSwitch(true)// 设置是否输出到控制台开关，默认开
                .setGlobalTag("")// 设置log全局标签，默认为空
                // 当全局标签不为空时，我们输出的log全部为该tag，
                // 为空时，如果传入的tag为空那就显示类名，否则显示tag
                .setLogHeadSwitch(false)// 设置log头信息开关，默认为开
                .setLog2FileSwitch(true)// 打印log时是否存到文件的开关，默认关
                .setDir(path)// 当自定义路径为空时，写入应用的/cache/log/目录中
                .setFilePrefix("log")// 当文件前缀为空时，默认为"util"，即写入文件为"util-MM-dd.txt"
                .setBorderSwitch(false)// 输出日志是否带边框开关，默认开
                .setSingleTagSwitch(true)
                .setConsoleFilter(ALog.V)// log的控制台过滤器，和logcat过滤器同理，默认Verbose
                .setFileFilter(ALog.V)// log文件过滤器，和logcat过滤器同理，默认Verbose
                .setSaveDays(7)//保存天数
                .setStackDeep(1);// log栈深度，默认为1
        //   ALog.d(config.toString());
    }


    /*
     * component: 组件名    className:文件名    function: 方法名称
     *
     */
    public void LoggerDebug(String component, String className, String function, String msg) {
        if (isDbug) {
            Log.d(component, className + " " + function + " " + msg);
            return;
        }
        ALog.dTag(component, className + " " + function + " " + msg);
    }


    public void LoggerDebug(String className, String msg) {
        if (isDbug) {
            Log.d(className, msg);
            return;
        }
        ALog.dTag(className, msg);
    }

    public void LoggerInfo(String component, String className, String function, String msg) {
        if (isDbug) {
            Log.d(component, className + " " + function + " " + msg);
            return;
        }
        ALog.iTag(component, className + " " + function + " " + msg);
    }

    /*
     * component: 组件名    className:文件名    function: 方法名称
     *
     */
    public void LoggerError(String component, String className, String function, String msg) {
        if (isDbug) {
            Log.e(component, className + " " + function + " " + msg);
            return;
        }
        ALog.eTag(component, className + " " + function + " " + msg);
    }


    /**
     * 描述：复制源文件(可以是目录)到目的地址 函数名：copyFile
     *
     * @param：sourceFile 源文件
     * @param：targetFile 目的地址
     * @author song 2017-02-05
     */
    private boolean copyFile(String sourceFile, String targetFileFolder, String targetFileNmae) {
        boolean bRet = false;
        FileInputStream in = null;
        FileOutputStream out = null;
        try {

            File targetFolder = new File(targetFileFolder);
            if (!targetFolder.exists() || !targetFolder.isDirectory()) { //如果文件夹不存在 则建立新文件夹
                targetFolder.mkdirs();
            }

            File mSource = new File(sourceFile);

            if (!mSource.isFile() || mSource.isHidden() || mSource.getName().equals("LOST.DIR")) {
                return bRet;
            }

            in = new FileInputStream(mSource);
            out = new FileOutputStream(new File(targetFileFolder + "/" + targetFileNmae));
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
            bRet = true;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bRet;
    }


    public boolean copyFile(File sourceFile, String targetFileFolder, String targetFileNmae) {
        File folder = new File(targetFileFolder);
        LoggerDebug("ComponentBoard", "FileOperation", "copyFile",
                "sourceFile :" + sourceFile + " targetFileFolder: " + targetFileFolder + " targetFileNmae: " + targetFileNmae);

        if (!folder.exists()) {
            folder.mkdirs();
            LoggerDebug("ComponentBoard", "FileOperation", "copyFile", "!folder.exists()");
            return false;
        }
        File targetFile = new File(folder, targetFileNmae);
        FileInputStream in = null;
        FileOutputStream out = null;

        try {

            targetFile.createNewFile();
            if (sourceFile == null | !sourceFile.isFile()) {
                LoggerDebug("ComponentBoard", "FileOperation", "copyFile", "sourceFile == null | !sourceFile.isFile()");

                return false;
            }

            in = new FileInputStream(sourceFile);
            out = new FileOutputStream(targetFile);
            byte[] buffer = new byte[1024];

            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
        } catch (Exception var25) {
            var25.printStackTrace();
            LoggerDebug("ComponentBoard", "FileOperation", "copyFile", "copyFile e:" + var25);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException var24) {
                    var24.printStackTrace();
                    LoggerDebug("ComponentBoard", "FileOperation", "copyFile", "copyFile e2:" + var24);
                }
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException var23) {
                    var23.printStackTrace();
                    LoggerDebug("ComponentBoard", "FileOperation", "copyFile", "copyFile e3:" + var23);
                }
            }

        }

        return true;
    }

    //返回多少M总共的空间
    private long getTotalSpace() {
        long blockSize = -1;
        long totalBlocks = -1;
        long avaibleBlocks = -1;

        StatFs statFs = new StatFs("/mnt/sdcard");

        /*
         * Build.VERSION.SDK_INT:获取当前系统版本的等级
         * Build.VERSION_CODES.JELLY_BEAN_MR2表示安卓4.3，也就是18，这里直接写18也可以
         * 因为getBlockSizeLong()等三个方法是安卓4.3以后才有的，所以这里需要判断当前系统版本
         * 补充一个知识点：所有储存设备都被分成若干块，每一块都有固定大小。
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            // 获取块的数量
            blockSize = statFs.getBlockSizeLong();
            // 获取一共有多少块
            totalBlocks = statFs.getBlockCountLong();
            // 可以活动的块
//			avaibleBlocks = statFs.getAvailableBlocksLong();
        } else {
            /*
             * 黑线说明这三个API已经过时了。但是为了兼容4.3一下的系统，我们需要写上
             */
            blockSize = statFs.getBlockSize();
            totalBlocks = statFs.getBlockCount();
//			avaibleBlocks = statFs.getAvailableBlocks();

        }

        long totalSpace = (totalBlocks * blockSize / 1024) / 1024;     //获取多少M

        return totalSpace;
    }

    private volatile boolean m_bLogChecked = false;

    //OnVendLoop 文件大于1g的时候 1。删除不是log的文件 2删除之前的log文件
    public void logFileCheck() {
        if (m_bLogChecked) {
            return;
        }
        m_bLogChecked = true;
        File folder = new File(mFilePath);
        if (folder.exists() && folder.isDirectory()) {
            try {
                int maxM = 800;
                long iTotalSpace = getTotalSpace();
                if (iTotalSpace > 5000) {
                    maxM = 800;
                } else if (iTotalSpace > 3000) {
                    maxM = 600;
                } else if (iTotalSpace > 2000) {
                    maxM = 200;
                } else {
                    maxM = 100;
                }

                long size = getFolderSize(folder);
//                LoggerDebug("ComponentBoard", "logFileCheck", "size", "size:" + size);
                if (size > logSize * maxM) { //删除无关文件  800M
                    File[] files = folder.listFiles();
                    for (File f : files) {
                        if (f.isDirectory()) {
                            deleteDirAndFile(f);
                        } else if (f.getName().contains(LOG_FILENAME_PRI_COMMON)) {
                            f.delete();
                        } else {
                            f.delete();
                        }
                    }
                }
                long size1 = getFolderSize(folder); //删除无关文件后再判断大小  800M
//                LoggerDebug("ComponentBoard", "logFileCheck", "size1", "size1:" + size1);
                if (size1 > logSize * maxM) {
                    String[] files = folder.list();
                    Arrays.sort(files, new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            return o1.compareTo(o2);
                        }
                    });
                    if (files == null)
                        return;
                    if (files.length >= 25) {
                        for (int i = 0; i < 10; i++)
                            new File(files[i]).delete();
                    } else if (files.length >= 15) {
                        for (int i = 0; i < 5; i++)
                            new File(files[i]).delete();
                    } else if (files.length >= 10) {
                        for (int i = 0; i < 3; i++)
                            new File(files[i]).delete();
                    } else {
                        if (files.length > 1) {
                            new File(files[0]).delete();
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private boolean deleteDirAndFile(File dir) {
        if (null != dir && dir.exists()) {
            if (dir.isDirectory()) {
                String[] children = dir.list();

                for (int i = 0; i < children.length; ++i) {
                    boolean success = this.deleteDirAndFile(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }

            return dir.delete();
        } else {
            return false;
        }
    }


    public void clearLogCheckFlag() {
        m_bLogChecked = false;
    }

    private long getFileSize(File f) throws Exception {// 取得文件大小

        if (f.exists()) {
            return f.length();
        }
        return 0;
    }

    // 取得文件夹大小
    private long getFolderSize(File f) throws Exception {
        long size = 0;

        File flist[] = f.listFiles();
        if (flist == null) {
            return 0;
        }
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFolderSize(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }


}
