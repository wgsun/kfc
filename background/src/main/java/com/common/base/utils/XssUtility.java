package com.common.base.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import com.common.logger.MuhuaLog;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.core.content.FileProvider;

public class XssUtility {
    private static final String PATH_USB = "/mnt/usb_storage";
    private static final String PATH_SDCARD = "/mnt/sdcard";

    public static boolean sendEmptyMsg(Handler h, int what) {
        if (null == h) {
            return false;
        }

        h.removeMessages(what);

        return h.sendEmptyMessage(what);
    }


    public static String getHex(int intdata) {
        String hex = Integer.toHexString(intdata);
        if (hex.length() == 1) {
            return 0 + hex;
        } else if (hex.length() == 3) {
            return 0 + hex;
        }
        return hex;
    }

    public static String getnumTwo(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return "00";
        }
        return XssUtility.getHexLeng(Integer.parseInt(msg), 2);
    }

    public static String getnumFour(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return "0000";
        }
        return XssUtility.getHexLeng(Integer.parseInt(msg), 4);
    }

    public static String getHexLeng(int data, int leng) {
        String hex = Integer.toHexString(data);
        if (hex.length() > leng) {
            return "";
        }
        while (hex.length() < leng) {
            hex = 0 + hex;
        }
        return hex;

    }

    public static String getHexLengHex(String hex, int leng) {
        if (TextUtils.isEmpty(hex)) {
            return hex;
        }
        if (hex.length() > leng) {
            return hex;
        }
        while (hex.length() < leng) {
            hex = 0 + hex;
        }
        return hex;

    }

    public static String getJsonData(JsonElement json, String msg) {
        JsonElement jsonElement = json.getAsJsonObject().get(msg);
        if (jsonElement != null) {
            return jsonElement.getAsString();
        }
        return "";
    }

    public static JsonElement getJsonObject(JsonElement json, String msg) {
        JsonElement jsonElement = json.getAsJsonObject().get(msg);
        if (jsonElement != null) {
            return jsonElement.getAsJsonObject();
        }
        return null;
    }

    public static int getJsonDataAsInt(JsonElement json, String msg) {
        try {
            if (json == null) {
                return -1;
            }
            JsonElement jsonElement = json.getAsJsonObject().get(msg);
            if (jsonElement != null) {
                return jsonElement.getAsInt();
            }
        } catch (Exception e) {
            MuhuaLog.getInstance().LoggerDebug("", "XssUtility", "getJsonDataAsInt", "e: " + e.toString());
        }

        return -1;
    }

    public static boolean getJsonDataAsBoole(JsonElement json, String msg) {
        try {
            JsonElement jsonElement = json.getAsJsonObject().get(msg);
            if (jsonElement != null) {
                return jsonElement.getAsBoolean();
            }
        } catch (Exception e) {
            MuhuaLog.getInstance().LoggerDebug("", "XssUtility", "getJsonDataAsInt", "e: " + e.toString());
        }

        return false;
    }


    /**
     *      * 判断一个字符串是否为url
     *      * @param str String 字符串
     *      * @return boolean 是否为url
     *      * @author peng1 chen
     *      *
     **/
    public static boolean isURL(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
//转换为小写
        String regex = "^(https?|http|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Pattern patt = Pattern.compile(regex);
        Matcher matcher = patt.matcher(str.toLowerCase());

        return matcher.matches();
    }


    public static JsonObject parserJsonObject(JsonElement jsonElement) {
        if (jsonElement != null) {
            return jsonElement.getAsJsonObject();
        }
        return null;
    }

    public static boolean sendEmptyMessageDelayed(Handler h, int what, long delayMillis) {
        if (null == h) {
            return false;
        }

        h.removeMessages(what);

        return h.sendEmptyMessageDelayed(what, delayMillis);
    }

    public static boolean sendMsgDelayed(Handler h, int what, int arg1, long delayMillis, Object obj) {
        if (null == h) {
            return false;
        }

        Message msg = h.obtainMessage();

        msg.what = what;
        msg.arg1 = arg1;
        msg.obj = obj;

        return h.sendMessageDelayed(msg, delayMillis);
    }

    public static boolean sendMsgDelayed(Handler h, int what, int arg1, int arg2, long delayMillis, Object obj) {
        if (null == h) {
            return false;
        }

        Message msg = h.obtainMessage();

        msg.what = what;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        msg.obj = obj;

        return h.sendMessageDelayed(msg, delayMillis);
    }

    public static boolean sendMsg(Handler h, int what, int arg1, int arg2, Object obj) {
        if (null == h) {
            return false;
        }

        //h.removeMessages(what);

        Message msg = h.obtainMessage();

        msg.what = what;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        msg.obj = obj;

        return h.sendMessage(msg);
    }

    public static boolean sendMsgAtFrontOfQueue(Handler h, int what, int arg1, int arg2, Object obj) {
        if (null == h) {
            return false;
        }

        h.removeMessages(what);

        Message msg = h.obtainMessage();

        msg.what = what;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        msg.obj = obj;

        return h.sendMessageAtFrontOfQueue(msg);
    }

    public static void removeMessages(Handler h, int what) {
        if (h != null) {
            h.removeMessages(what);
        }
    }

    /**
     * 根据格式获取时间 "yyyyMMddHHmmss"
     *
     * @return
     */
    public static String getTime(String dataFormat) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(dataFormat);
        return dateFormat.format(new Date(System.currentTimeMillis()));
    }

    public static String getTime14B() {

        return getTime14B(System.currentTimeMillis());
    }

    //20220727145523
    public static String getTime14B(long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(new Date(time));
    }

    public static boolean isContains(List<String> list, String data) {
        if (list == null || list.size() < 1 || TextUtils.isEmpty(data)) {
            return false;
        }
        for (int x = 0; x < list.size(); x++) {
            if (data.equals(list.get(x))) {
                return true;
            }
        }
        return false;
    }

    public static String getTimeShow(long time) {
        if (time < 1000) {
            return "";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date(time));
    }

    public static String getIP() {

        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }


    /**
     * @param decimalSource
     * @return String
     * @Description: 十进制转换成二进制 ()
     */
    public static String decimalToBinary(int decimalSource) {
        BigInteger bi = new BigInteger(String.valueOf(decimalSource));    //转换成BigInteger类型
        return bi.toString(2);    //参数2指定的是转化成X进制，默认10进制
    }

    /**
     * @param binarySource
     * @return int
     * @Description: 二进制转换成十进制
     */
    public static int binaryToDecimal(String binarySource) {
        BigInteger bi = new BigInteger(binarySource, 2);    //转换为BigInteger类型
        return Integer.parseInt(bi.toString());        //转换成十进制
    }

    //判断字符串是否包含中文
    public static boolean isContainChinese(String str) {

        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    //写一个方法实现：把一个十进制的数转换成为16进制的数
    public static String deciToHexData(long a) {
        String str = "";
        //1:用a去除以16，得到商和余数
        long sun = a / 16;
        int yuShu = (int) (a % 16);
        str = "" + shuZhiToZhiMu(yuShu);
        while (sun > 0) {
            //2：继续用商去除以16，得到商和余数
            yuShu = (int) (sun % 16);
            sun = sun / 16;
            //3：如果商为0，那么就终止
            //4：把所有的余数倒序排列
            str = shuZhiToZhiMu(yuShu) + str;
        }
        return str;
    }

    private static String shuZhiToZhiMu(int a) {
        switch (a) {
            case 10:
                return "A";
            case 11:
                return "B";
            case 12:
                return "C";
            case 13:
                return "D";
            case 14:
                return "E";
            case 15:
                return "F";
        }
        return "" + a;
    }

    /**
     * 判断是否是含小数
     *
     * @param data
     * @return
     */
    public static boolean isContainDeciPoint(String data) {
        boolean bRet = false;
        if ((null == data) || (data.length() < 1)) {
            return bRet;
        }
        try {
            // Pattern pattern = Pattern.compile("^[0-9]+\\.{0,1}[0-9]{0,2}$");
            int indexP = data.indexOf(".");
            if (indexP > 0) {
                int lastIndexOf = data.lastIndexOf(".");
                if (lastIndexOf > indexP) {
                    return bRet;
                }
                data = data.replace(".", "");
                bRet = isDigital(data);
            }
        } catch (Exception e) {

        }

        return bRet;
    }

    /**
     * 判断是否全部由数字组成
     *
     * @param str
     * @return
     */
    public static boolean isDigital(String str) {
        if ((null == str) || (str.length() < 1)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[0-9]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 判断是否为数字(正负数都行)
     *
     * @param str 需要验证的字符串
     * @return
     */
    public static boolean isNumeric(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * @param fileName
     * @return byte[]
     */
    public static byte[] readFile(String fileName) {
        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;
        byte[] data = null;
        try {
            fis = new FileInputStream(fileName);
            byte[] buffer = new byte[8 * 1024];
            int readSize = -1;
            baos = new ByteArrayOutputStream();
            while ((readSize = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, readSize);
            }
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (baos != null)
                    baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;

    }

    /**
     * 读取文本文件
     *
     * @param fileName
     * @return
     */
    public static String readFile(String filePath, String fileName) {
        if (null == fileName) {
            return null;
        }
        if (TextUtils.isEmpty(filePath)) {
            filePath = "";
        }
//    String mStrRootPath = Utils.getExternalStorageDirectory();
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
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("file", "readFile FileNotFoundException e: " + e);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("file", "readFile IOException e: " + e);
        } catch (Exception e) {

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

    /**
     * 读取文本文件
     *
     * @param filePathAndName
     * @return
     */
    public static String readFileData(String filePathAndName) {
        if (TextUtils.isEmpty(filePathAndName)) {
            return null;
        }


        StringBuffer sb = new StringBuffer();
        File file = new File(filePathAndName);
        if (file == null || !file.exists() || file.isDirectory()) {
            Log.e("file", "readFile return.");
            return null;
        }
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("file", "readFile FileNotFoundException e: " + e);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("file", "readFile IOException e: " + e);
        } catch (Exception e) {

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

    /**
     * @param data     数据
     * @param path     路径
     * @param fileName 文件名
     * @return true成功 false失败
     */
    public static boolean writeToSdcard(byte[] data, String path, String fileName) {
        FileOutputStream fos = null;
        try {
            //判断有没有文件夹
            File filePath = new File(path);
            if (!filePath.exists()) {
                //创建文件夹
                filePath.mkdirs();
            }

            //判断有没有同名的文件
            File file = new File(path + fileName);
            //有的话，删除
            if (file.exists()) {
                file.delete();
            }
            //写文件
            fos = new FileOutputStream(file);
            fos.write(data);
            fos.flush();
            return true;
            //		}

        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 十六进制转换字符串
     *
     * @param (:[616C6B])
     * @return String 对应的字符串
     */
    public static String hexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;

        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    public static String getAmountSum(String amount1, String amount2) {
        if (TextUtils.isEmpty(amount1)) {
            amount1 = "0";
        }

        if (TextUtils.isEmpty(amount2)) {
            amount2 = "0";
        }

        BigDecimal mBigDecimalAmount1 = new BigDecimal(amount1);
        BigDecimal mBigDecimalAmount2 = new BigDecimal(amount2);

        String amount = (mBigDecimalAmount1.add(mBigDecimalAmount2)).toString();

        return amount;
    }

    /**
     * 判断是否正确的价格格式
     *
     * @param str
     * @return
     * @author hua
     */
    public static boolean isPriceFormat(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^(0|[1-9][0-9]{0,9})(\\.[0-9]{1,2})?$");
        return pattern.matcher(str).matches();
    }

    /**
     * 把字节数组转换成16进制字符串
     *
     * @param bArray
     * @return
     */
    public static String bytesToHexString(byte[] bArray, int byteCount) {
        String stmp = "";
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < byteCount; i++) {
            stmp = Integer.toHexString(bArray[i] & 0xFF);
            sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
        }
        return sb.toString().toUpperCase().trim();
    }

    public static String bytesToHexString(Byte[] src) {

        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }


    /* Convert byte[] to hex string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
     * @param src byte[] data
     * @return hex string
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static String bytesToHexString(byte src) {
        StringBuilder stringBuilder = new StringBuilder("");
        int v = src & 0xFF;
        String hv = Integer.toHexString(v);
        if (hv.startsWith("0x") || hv.startsWith("0X")) {
            hv = hv.substring(2);
        }
        if (hv.length() < 2) {
            stringBuilder.append(0);
        }
        stringBuilder.append(hv);
        return stringBuilder.toString();
    }


    /**
     * Convert hex string to byte[]
     *
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        if (hexString.startsWith("0x") || hexString.startsWith("0X")) {
            hexString = hexString.substring(2);
        }
        if (hexString.length() == 1) {
            hexString = "0" + hexString;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    public static byte[] ascStringToBytes(String ascString) {
        if (ascString == null || ascString.equals("")) {
            return null;
        }
        char[] chars = ascString.toCharArray();
        StringBuffer hex = new StringBuffer();
        String hexSingle = null;
        for (int i = 0; i < chars.length; i++) {
            hexSingle = Integer.toHexString((int) chars[i]);
            if (hexSingle.length() == 1) {
                hexSingle = "0" + hexSingle;
            }
            hex.append(hexSingle);
        }

        byte[] bRetBytes = hexStringToBytes(hex.toString());

        return bRetBytes;
    }

    /**
     * Convert hex string to byte[]
     *
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte hexStringToByte(String hexString) {
        byte bRet = (byte) 0xFF;
        if (hexString == null || hexString.equals("")) {
            return bRet;
        }
        if (hexString.startsWith("0x") || hexString.startsWith("0X")) {
            hexString = hexString.substring(2);
        }
        if (hexString.length() == 1) {
            hexString = "0" + hexString;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        if (d.length > 0) {
            bRet = d[0];
        }

        return bRet;
    }

    /**
     * Convert char to byte
     *
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static String getChecksumHex(String hexdata) {
        if (hexdata == null || hexdata.equals("")) {
            return "";
        }
        if (hexdata.startsWith("0x") || hexdata.startsWith("0X")) {
            hexdata = hexdata.substring(2);
        }
        if (hexdata.length() == 1) {
            hexdata = "0" + hexdata;
        }

        int total = 0;
        int len = hexdata.length();
        int num = 0;
        while (num < len) {
            String s = hexdata.substring(num, num + 2);
            total += Integer.parseInt(s, 16);
            num = num + 2;
        }
        /**
         * 用256求余最大是255，即16进制的FF
         */
        int mod = total % 256;
        String hex = Integer.toHexString(mod);
        if (hex.startsWith("0x") || hex.startsWith("0X")) {
            hex = hex.substring(2);
        }
        len = hex.length();
        // 如果不够校验位的长度，补0,这里用的是两位校验
        if (len < 2) {
            hex = "0" + hex;
        }
        return hex.toUpperCase();
    }


    public static String getCheckXOR(String hexdata) {
        String str1 = "00";
        String str2 = "00";
        int iLength = hexdata.length();
        BigInteger big2 = new BigInteger(str2, 16);
        for (int i = 0; i < iLength / 2; i++) {
            str1 = hexdata.substring(i * 2, (i + 1) * 2);
            BigInteger big1 = new BigInteger(str1, 16);
            big2 = big1.xor(big2);
        }
        String ret = big2.toString(16);
        if (ret.length() == 1) {
            ret = "0" + ret;
        }
        return ret;
    }

    public static String getCheckXor(String strData) {
        byte[] datas = strData.getBytes();
        byte temp = datas[0];

        for (int i = 1; i < datas.length; i++) {
            temp ^= datas[i];
        }
        String iRet = String.valueOf((int) temp);
        return iRet;
    }

    public static int byte2IntByStream(byte[] bdata) throws IOException {

        int len = bdata.length;
        byte[] rb = new byte[len];
        for (int i = 0; i < rb.length; i++) {
            rb[i] = bdata[len - 1 - i];
        }

        ByteArrayInputStream in = new ByteArrayInputStream(rb);
        int result = in.read();
        in.close();

        return result;
    }


    //有符号
    public static short hex4StringToDecimal(String hex4Data) {
        if ((null == hex4Data) || (hex4Data.length() != 4) || (hex4Data.contains("0x")) || (hex4Data.contains("0X"))) {
            return -1;
        }
        int ret = Integer.parseInt(hex4Data, 16);
        ret = ((ret & 0x8000) > 0) ? (ret - 0x10000) : (ret);
        return (short) ret;
    }

    //有符号,一个字节
    public static short hex2StringToDecimal(String hex2Data) {
        if ((null == hex2Data) || (hex2Data.length() != 2) || (hex2Data.contains("0x")) || (hex2Data.contains("0X"))) {
            return -1;
        }
        int ret = Integer.parseInt(hex2Data, 16);
        ret = ((ret & 0x80) > 0) ? (ret - 0x100) : (ret);
        return (short) ret;
    }

    public static long hexStringToDecimal(String hexData) {
        if (hexData == null || hexData.length() < 1) {
            throw new RuntimeException("字符串不合法");
        }
        long sum = 0;
        int iLength = hexData.length();
        for (int i = 0; i < iLength; i++) {
            long iData = 1;
            String tmp = hexData.substring(i, i + 1);
            if ("A".equalsIgnoreCase(tmp)) {
                iData = 10;
            } else if ("B".equalsIgnoreCase(tmp)) {
                iData = 11;
            } else if ("C".equalsIgnoreCase(tmp)) {
                iData = 12;
            } else if ("D".equalsIgnoreCase(tmp)) {
                iData = 13;
            } else if ("E".equalsIgnoreCase(tmp)) {
                iData = 14;
            } else if ("F".equalsIgnoreCase(tmp)) {
                iData = 15;
            } else if ("0".equals(tmp)) {
                iData = 0;
            } else if ("1".equals(tmp)) {
                iData = 1;
            } else if ("2".equals(tmp)) {
                iData = 2;
            } else if ("3".equals(tmp)) {
                iData = 3;
            } else if ("4".equals(tmp)) {
                iData = 4;
            } else if ("5".equals(tmp)) {
                iData = 5;
            } else if ("6".equals(tmp)) {
                iData = 6;
            } else if ("7".equals(tmp)) {
                iData = 7;
            } else if ("8".equals(tmp)) {
                iData = 8;
            } else if ("9".equals(tmp)) {
                iData = 9;
            } else {

            }
            for (int j = 0; j < (iLength - i - 1); j++) {
                iData = iData * 16;
            }
            sum = sum + iData;
        }
        return sum;
    }

    public static BigInteger hexStringToBigInteger(String hexData) {
        if (hexData == null || hexData.length() < 1) {
            throw new RuntimeException("字符串不合法");
        }
        BigInteger sum = BigInteger.valueOf(0);
        int iLength = hexData.length();
        for (int i = 0; i < iLength; i++) {
            BigInteger iData = BigInteger.valueOf(1);
            String tmp = hexData.substring(i, i + 1);
            if ("A".equalsIgnoreCase(tmp)) {
                iData = BigInteger.valueOf(10);
            } else if ("B".equalsIgnoreCase(tmp)) {
                iData = BigInteger.valueOf(11);
            } else if ("C".equalsIgnoreCase(tmp)) {
                iData = BigInteger.valueOf(12);
            } else if ("D".equalsIgnoreCase(tmp)) {
                iData = BigInteger.valueOf(13);
            } else if ("E".equalsIgnoreCase(tmp)) {
                iData = BigInteger.valueOf(14);
            } else if ("F".equalsIgnoreCase(tmp)) {
                iData = BigInteger.valueOf(15);
            } else if ("0".equals(tmp)) {
                iData = BigInteger.valueOf(0);
            } else if ("1".equals(tmp)) {
                iData = BigInteger.valueOf(1);
            } else if ("2".equals(tmp)) {
                iData = BigInteger.valueOf(2);
            } else if ("3".equals(tmp)) {
                iData = BigInteger.valueOf(3);
            } else if ("4".equals(tmp)) {
                iData = BigInteger.valueOf(4);
            } else if ("5".equals(tmp)) {
                iData = BigInteger.valueOf(5);
            } else if ("6".equals(tmp)) {
                iData = BigInteger.valueOf(6);
            } else if ("7".equals(tmp)) {
                iData = BigInteger.valueOf(7);
            } else if ("8".equals(tmp)) {
                iData = BigInteger.valueOf(8);
            } else if ("9".equals(tmp)) {
                iData = BigInteger.valueOf(9);
            } else {

            }
            for (int j = 0; j < (iLength - i - 1); j++) {
                iData = iData.multiply(BigInteger.valueOf(16));
            }
            sum = sum.add(iData);
        }
        return sum;
    }

    /**
     * MD5加密方法
     *
     * @param str
     * @param encoding       default UTF-8
     * @param no_Lower_Upper 0,1,2 0：不区分大小写，1：小写，2：大写
     * @return MD5Str
     */
    public static String getMD5(String str, String encoding, int no_Lower_Upper) {
        if (null == encoding) {
            encoding = "utf-8";
        }
        StringBuffer sb = new StringBuffer();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(str.getBytes(encoding));
            for (int i = 0; i < array.length; i++) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
                        .toUpperCase().substring(1, 3));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (no_Lower_Upper == 0) {
            return sb.toString();
        }
        if (no_Lower_Upper == 1) {
            return sb.toString().toLowerCase();
        }
        if (no_Lower_Upper == 2) {
            return sb.toString().toUpperCase();
        }
        return null;
    }

    /**
     * 字符串转换为16进制字符串
     *
     * @param s
     * @return
     */
    public static String stringToHexString(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch);
            if (s4.length() == 1) {
                s4 = "0" + s4;
            }
            str = str + s4;
        }
        return str;
    }


    /**
     * 普通字符转换成16进制字符串
     *
     * @param str
     * @return
     */
    public static String str2HexStr(String str) {
        byte[] bytes = str.getBytes();
        // 如果不是宽类型的可以用Integer
        BigInteger bigInteger = new BigInteger(1, bytes);
        return bigInteger.toString(16);
    }


    /**
     * 16进制字符串转换为字符串
     *
     * @param s
     * @return
     */
    public static String hexStringToString(String s, String charsetName) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(
                        s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, charsetName);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    /**
     * 将字符串编码成16进制数字,适用于所有字符（包括中文）
     */
    public static String encodeToHexString(String data) {
        if (null == data) {
            return null;
        }
        String HEX_STRING_TABLE = "0123456789ABCDEF";
        //根据默认编码获取字节数组
        byte[] bytes = data.getBytes();
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        //将字节数组中每个字节拆解成2位16进制整数
        for (int i = 0; i < bytes.length; i++) {
            sb.append(HEX_STRING_TABLE.charAt((bytes[i] & 0xf0) >> 4));
            sb.append(HEX_STRING_TABLE.charAt((bytes[i] & 0x0f) >> 0));
        }
        return sb.toString();
    }

    /**
     * 将字符串编码成16进制数字,适用于所有字符（包括中文）
     */
    public static String encodeToHexStringUpperCase(String data) {
        if (null == data) {
            return null;
        }
        String HEX_STRING_TABLE = "0123456789ABCDEF";
        //根据默认编码获取字节数组
        byte[] bytes = data.getBytes();
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        //将字节数组中每个字节拆解成2位16进制整数
        for (int i = 0; i < bytes.length; i++) {
            sb.append(HEX_STRING_TABLE.charAt((bytes[i] & 0xf0) >> 4));
            sb.append(HEX_STRING_TABLE.charAt((bytes[i] & 0x0f) >> 0));
        }
        return sb.toString().toUpperCase();
    }

    /**
     * 将16进制数字解码成字符串,适用于所有字符（包括中文）
     */
    public static String hexStringDecode(String hexString) {
        if ((null == hexString) || (hexString.length() < 1)) {
            return null;
        }
        String HEX_STRING_TABLE = "0123456789ABCDEF";
        ByteArrayOutputStream baos = new ByteArrayOutputStream(hexString.length() / 2);
        //将每2位16进制整数组装成一个字节
        for (int i = 0; i < hexString.length(); i += 2) {
            baos.write((HEX_STRING_TABLE.indexOf(hexString.charAt(i)) << 4 | HEX_STRING_TABLE.indexOf(hexString.charAt(i + 1))));
        }
        return new String(baos.toByteArray());
    }

    /**
     * 将10进制转化为62进制
     *
     * @param number
     * @param length 转化成的62进制长度，不足length长度的话高位补0，否则不改变什么
     * @return
     */
    public static String _10_to_62(long number, int length) {
        char[] char62Set = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
        Long rest = number;
        Stack<Character> stack = new Stack<Character>();
        StringBuilder result = new StringBuilder(0);
        while (rest != 0) {
            stack.add(char62Set[new Long((rest - (rest / 62) * 62)).intValue()]);
            rest = rest / 62;
        }
        for (; !stack.isEmpty(); ) {
            result.append(stack.pop());
        }
        int result_length = result.length();
        StringBuilder temp0 = new StringBuilder();
        for (int i = 0; i < length - result_length; i++) {
            temp0.append('0');
        }

        return temp0.toString() + result.toString();

    }

    /**
     * 将10进制转化为62进制
     *
     * @param number
     * @param length 转化成的62进制长度，不足length长度的话高位补0，否则不改变什么
     * @return
     */
    public static String _10_to_62H(long number, int length) {
        char[] char62Set = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
        Long rest = number;
        Stack<Character> stack = new Stack<Character>();
        StringBuilder result = new StringBuilder(0);
        while (rest != 0) {
            stack.add(char62Set[new Long((rest - (rest / 62) * 62)).intValue()]);
            rest = rest / 62;
        }
        for (; !stack.isEmpty(); ) {
            result.append(stack.pop());
        }
        int result_length = result.length();
        StringBuilder temp0 = new StringBuilder();
        for (int i = 0; i < length - result_length; i++) {
            temp0.append('-');
        }
        return result.toString() + temp0.toString();  // 后面补“-”

    }

    /**
     * 将10进制转化为62进制
     *
     * @param number
     * @param length 转化成的62进制长度，不足length长度的话高位补0，否则不改变什么
     * @return
     */
    public static String _10Big_to_62H(BigInteger number, int length) {
        char[] char62Set = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
        BigInteger rest = number;
        Stack<Character> stack = new Stack<Character>();
        StringBuilder result = new StringBuilder(0);
        while (!rest.equals(BigInteger.ZERO)) {
            BigInteger index = rest.subtract((rest.divide(BigInteger.valueOf(62))).multiply(BigInteger.valueOf(62)));
            stack.add(char62Set[index.intValue()]);
            rest = rest.divide(BigInteger.valueOf(62));
        }
        for (; !stack.isEmpty(); ) {
            result.append(stack.pop());
        }
        int result_length = result.length();
        StringBuilder temp0 = new StringBuilder();
        for (int i = 0; i < length - result_length; i++) {
            temp0.append('-');
        }
        return result.toString() + temp0.toString();  // 后面补“-”

    }

    public static boolean isTcnImage(String filename) {
        if (null == filename) {
            return false;
        }
        boolean bRet = false;
        String mFilename = filename.toLowerCase();
        if (mFilename.endsWith(".jpg") || mFilename.endsWith(".jpeg") || mFilename.endsWith(".png") || mFilename.endsWith(".bmp")) {
            bRet = true;
        }
        return bRet;
    }


    //其中 -c 1为发送的次数，1为表示发送1次，-w 表示发送后等待响应的时间。
    private static boolean startPing(String ip) {
        boolean success = false;
        Process p = null;

        try {
            p = Runtime.getRuntime().exec("ping -c 1 -i 0.2 -W 1 " + ip);
            int status = p.waitFor();
            if (status == 0) {
                success = true;
            } else {
                success = false;
            }
        } catch (IOException e) {
            success = false;
        } catch (InterruptedException e) {
            success = false;
        } finally {
            p.destroy();
        }

        return success;
    }

    /**
     * 获取U盘或是sd卡的路径
     *
     * @return
     */
    public static String getExternalStorageDirectory() {

        String dir = new String();

        try {
            File file = new File(XssData.PATH_SDCARD);
            if (file.exists() && file.isDirectory()) {
                dir = XssData.PATH_SDCARD;
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

    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }

    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); //删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isFileExit(String fileName) {
        boolean bRet = false;

        if ((fileName == null) || (fileName.length() < 1)) {
            return bRet;
        }
        try {
            String mStrRootPath = getExternalStorageDirectory();
            if (!fileName.startsWith(mStrRootPath)) {
                fileName = mStrRootPath + "/" + fileName;
            }
            File file = new File(fileName);
            if (file == null || !file.exists() || file.isDirectory()) {
                return bRet;
            }
            bRet = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bRet;
    }

    public static String getInstallApkPath(String apkName) {
        String apkPath = null;
        try {
            String mStrRootPath = getExternalStorageDirectory();

            File file = new File(mStrRootPath);
            if (!file.exists()) {
                return apkPath;
            }
            File[] files = file.listFiles();

            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    String fileName = files[i].getName();
                    if (fileName.contains(apkName)) {
                        apkPath = mStrRootPath;
                        if (apkPath.endsWith("/")) {
                            apkPath = apkPath + fileName;
                        } else {
                            apkPath = apkPath + "/" + fileName;
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return apkPath;
    }

    public static boolean deleteApk(String apkName) {
        boolean bRet = false;
        try {
            String mStrRootPath = getExternalStorageDirectory();

            File file = new File(mStrRootPath);
            if (!file.exists()) {
                return bRet;
            }
            File[] files = file.listFiles();

            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    String fileName = files[i].getName();
                    if (fileName.contains(apkName)) {
                        files[i].delete();
                        bRet = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bRet;
    }

    public static boolean deleteFile(String fileUrl) {
        boolean bRet = false;
        try {
            File file = new File(fileUrl);
            if (!file.exists()) {
                return bRet;
            }
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bRet;
    }

    public static void exec(String cmd) {
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

    public static void simulateKey(int keyCode) {
        exec("input keyevent " + keyCode + "\n");
    }

    public void goBack() {
        simulateKey(KeyEvent.KEYCODE_BACK);
    }


    /**
     * @param src 16进制字符串
     * @return 字节数组
     * @throws
     * @Title:hexString2Bytes
     * @Description:16进制字符串转字节数组
     */
    public static byte[] hexString2Bytes(String src) {
        int l = src.length() / 2;
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            ret[i] = (byte) Integer.valueOf(src.substring(i * 2, i * 2 + 2), 16).byteValue();
        }
        return ret;
    }


    /**
     * 16进制转换为二进制字符串
     **/
    public static String byteArrToBinStr(String hex) {
        if (TextUtils.isEmpty(hex)) {
            return "00000000";
        }
        if (hex.length() % 2 != 0) {
            hex = 0 + hex;
        }
        return byteArrToBinStr(hexString2Bytes(hex));
    }

    /**
     * byte数组转换为二进制字符串,每个字节以","隔开
     **/
    public static String byteArrToBinStr(byte[] b) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            String data = Long.toString(b[i] & 0xff, 2);
            while (data.length() < 8) {
                data = "0" + data;
            }
            result.append(data);
        }
        return result.toString();
    }


    /**
     * 检测网络是否连接
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    public static boolean isNetConnected(Context context) {

        if (null == context) {
            return false;
        }

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            NetworkInfo[] infos = cm.getAllNetworkInfo();
            if (infos != null) {
                for (NetworkInfo ni : infos) {
                    if (ni.isConnected() && ni.isAvailable()) {
                        return true;
                    }
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Network[] networks = cm.getAllNetworks();
                NetworkInfo networkInfo;
                for (Network mNetwork : networks) {
                    networkInfo = cm.getNetworkInfo(mNetwork);
                    if ((networkInfo != null) && (networkInfo.getState() != null)) {
                        if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                            return true;
                        }
                    }

                }
            } else {
                //noinspection deprecation
                NetworkInfo[] info = cm.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() != null) {
                            if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                                return true;
                            }
                        }

                    }
                }
            }
        }

        return false;
    }

    public static boolean isJsonNull(JsonObject jsonObject, String key) {
        boolean bRet = false;
        if (null == jsonObject) {
            bRet = true;
            return bRet;
        }
        if (!jsonObject.has(key)) {
            bRet = true;
        } else {
            JsonElement mJsonElement = jsonObject.get(key);
            if ((null == mJsonElement) || (mJsonElement.equals("null"))) {
                bRet = true;
            } else {
                if ("null".equals(mJsonElement.toString())) {
                    bRet = true;
                }
            }
        }
        return bRet;
    }

    //msm8953
    public static String getAndroidBoard() {
        String androidBoard = Build.BOARD;
        return androidBoard;
    }

    //判断工控是不是msm8953
    public static boolean isAndroidBoardMSM8953() {
        boolean bRet = false;
        if ((Build.BOARD).contains("msm8953")) {
            bRet = true;
        }

        return bRet;
    }

    // su替换
    public static Process getBoardSU() {
        try {
            if (judeFileExists(new File("/system/bin/ubiot")) || judeFileExists(new File("/system/xbin/ubiot")))
//			if (TcnUtility.isAndroidBoardMSM8953())
            {
                return Runtime.getRuntime().exec("ubiot");
            } else {

            }
            return Runtime.getRuntime().exec("su");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean judeFileExists(File file) {
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    @SuppressLint("MissingPermission")
    public static int getNetWorkType(Context context) {
        int type = -1;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == cm) {
            return type;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            @SuppressLint("MissingPermission") Network[] networks = cm.getAllNetworks();
            NetworkInfo networkInfo;
            if (null == networks) {
                return type;
            }

            for (int i = 0; i < networks.length; i++) {
                Network mNetwork = networks[i];
                networkInfo = cm.getNetworkInfo(mNetwork);
                if ((networkInfo != null) && (networkInfo.getState() != null) && (networkInfo.getState().equals(NetworkInfo.State.CONNECTED))) {

                    if ((Build.BOARD).contains("msm8953")) {
                        if (ConnectivityManager.TYPE_WIFI == networkInfo.getType()) {
                            type = networkInfo.getType();
                        } else if (ConnectivityManager.TYPE_MOBILE == networkInfo.getType()) {
                            if (type != ConnectivityManager.TYPE_WIFI) {
                                type = networkInfo.getType();
                            }
                        } else if (ConnectivityManager.TYPE_ETHERNET == networkInfo.getType()) {
                            if ((type != ConnectivityManager.TYPE_WIFI) && (type != ConnectivityManager.TYPE_MOBILE)) {
                                type = networkInfo.getType();
                            }
                        } else {
                            type = networkInfo.getType();
                        }
                    } else {
                        if (ConnectivityManager.TYPE_WIFI == networkInfo.getType()) {
                            type = networkInfo.getType();
                        } else if (ConnectivityManager.TYPE_ETHERNET == networkInfo.getType()) {
                            if (type != ConnectivityManager.TYPE_WIFI) {
                                type = networkInfo.getType();
                            }
                        } else if (ConnectivityManager.TYPE_MOBILE == networkInfo.getType()) {
                            if ((type != ConnectivityManager.TYPE_WIFI) && (type != ConnectivityManager.TYPE_ETHERNET)) {
                                type = networkInfo.getType();
                            }
                        } else {
                            type = networkInfo.getType();
                        }
                    }
                }
            }
        } else {
            NetworkInfo network = cm.getActiveNetworkInfo();
            if (network != null && network.isAvailable() && network.isConnected()) {
                type = network.getType();
            }
        }

        return type;
    }

    public static boolean createFoldersAndExist(String filePath) {
        boolean bRet = false;

        if (TextUtils.isEmpty(filePath)) {
            return bRet;
        }
        try {

            File dir = new File(filePath);

            dir.mkdirs();

            if ((dir.exists()) && (dir.isDirectory())) {
                bRet = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bRet;
    }

    /**
     * 获取U盘或是sd卡的路径
     *
     * @return
     */
    public static String getExternalMountPath() {

        String dir = new String();
        try {
            File file = new File(PATH_USB);
            if (file.exists() && file.isDirectory()) {
                dir = PATH_USB;
                return dir;
            }

            file = new File("/mnt/storage");
            if (file.exists() && file.isDirectory()) {
                dir = "/mnt/storage";
                return dir;
            }

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
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (TextUtils.isEmpty(dir) || (dir.length() < 5)) {
                File mFile = Environment.getExternalStorageDirectory();
                if (mFile != null) {
                    dir = mFile.getAbsolutePath();
                }

            }

        } catch (Exception e) {

        }

        return dir;
    }

    public static String getFilePathAndName(String filePath, String fileName) {
        if ((TextUtils.isEmpty(filePath)) || (TextUtils.isEmpty(fileName))) {
            return null;
        }
        StringBuilder mStringBuilder = new StringBuilder();

        String mStrRootPath = getExternalStorageDirectory();
        if (!filePath.startsWith(mStrRootPath)) {
            mStringBuilder.append(mStrRootPath);
        }
        if ((!(mStringBuilder.toString()).endsWith("/")) && (!filePath.startsWith("/"))) {
            mStringBuilder.append("/");
        }
        mStringBuilder.append(filePath);

        if (!(mStringBuilder.toString()).endsWith("/")) {
            mStringBuilder.append("/");
        }
        mStringBuilder.append(fileName);

        return mStringBuilder.toString();
    }

    public static boolean saveData(boolean append, String filePath, String fileName, String data) {
        boolean bRet = false;
        if (TextUtils.isEmpty(filePath) || TextUtils.isEmpty(fileName)) {
            return bRet;
        }
        String mStrRootPath = getExternalStorageDirectory();

        if (!filePath.startsWith(mStrRootPath)) {
            filePath = mStrRootPath + "/" + filePath;
        }

        bRet = writeFileByLine(append, filePath, fileName, data);

        return bRet;
    }

    /**
     * 写入内容到txt文本中
     * data为内容
     */
    private static boolean writeFileByLine(boolean append, String filePath, String fileName, String data) {
        boolean bRet = false;
        if (!existsOrCreateFile(filePath, fileName)) {
            return bRet;
        }

        String mFilePathAndName = getFilePathAndName(filePath, fileName);

        data = data + "\n";

        FileWriter fw = null;

        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            fw = new FileWriter(mFilePathAndName, append);
            fw.write(data);

            bRet = true;

        } catch (Exception e) {

        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return bRet;
    }

    private static boolean existsOrCreateFile(String filePath, String fileName) {
        boolean bRet = false;
        if ((TextUtils.isEmpty(filePath)) || (TextUtils.isEmpty(fileName))) {
            return bRet;
        }
        String sdcard = XssUtility.getExternalStorageDirectory();
        String mDirPath = filePath;
        if (!filePath.startsWith(sdcard)) {
            mDirPath = sdcard + filePath;
        }
        try {
            File mDir = new File(mDirPath.trim());
            if ((!mDir.exists()) || (!mDir.isDirectory())) {
                mDir.mkdirs();
            }
            mDir = new File(mDirPath.trim());
            if ((mDir.exists()) && (mDir.isDirectory())) {
                String mFilePathAndName = getFilePathAndName(filePath, fileName);
                File mFile = new File(mFilePathAndName.trim());
                if ((!mFile.exists()) || (!mFile.isFile())) {
                    mFile.createNewFile();
                    mFile = new File(mFilePathAndName.trim());
                    if ((mFile.exists()) && (mFile.isFile())) {
                        bRet = true;
                    }
                } else if ((mFile.exists()) && (mFile.isFile())) {
                    bRet = true;
                } else {

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bRet;
    }

    /**
     * 写入内容到txt文本中
     * data为内容
     */
    public static boolean writeDataToFile(boolean append, String filePath, String fileName, String data) {
        Log.d("TcnUtility", "writeDataToFile: fileName  " + fileName + "  " + data);
        boolean bRet = false;
        if ((!existsOrCreateFile(filePath, fileName)) || (TextUtils.isEmpty(data))) {
            return bRet;
        }

        String mFilePathAndName = getFilePathAndName(filePath, fileName);

        if (TextUtils.isEmpty(mFilePathAndName)) {
            return bRet;
        }

        FileWriter fw = null;

        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            fw = new FileWriter(mFilePathAndName, append);
            fw.write(data);

            bRet = true;

        } catch (Exception e) {

        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return bRet;
    }

    public static boolean isFileSizeLessThanXM(int dataM, String fileName) {
        boolean bRet = false;
        File file = new File(fileName);
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                long available = fis.available();
                if ((0 == available) || (available > dataM * 1048576)) {
                    //
                } else {
                    bRet = true;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bRet;
    }

    public static boolean isFileSizeMoreThanXM(float dataM, String fileName) {
        boolean bRet = false;
        File file = new File(fileName);
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                long available = fis.available();
                if ((0 == available) || (available > ((long) (dataM * 1.0f * 1048576)))) {
                    bRet = true;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bRet;
    }

    public static void clearFile(String fileName) {
        BufferedWriter bw = null;
        try {
            File file = new File(fileName);
            FileWriter fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            bw.write("");
        } catch (Exception e) {
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getVersionName(Context context) {
        String strVerName = null;
        if (null == context) {
            return strVerName;
        }
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            if (pInfo != null) {
                strVerName = pInfo.versionName;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return strVerName;
    }

    public static int getVersionCode(Context context) {
        int iVerCode = -1;
        if (null == context) {
            return iVerCode;
        }
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            if (pInfo != null) {
                iVerCode = pInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return iVerCode;
    }

    public static String getPackageName(Context context) {
        String strPackageName = null;
        if (null == context) {
            return strPackageName;
        }
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            if (pInfo != null) {
                strPackageName = pInfo.packageName;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return strPackageName;
    }

    public static boolean isSpaceLack() {

        boolean bRet = false;

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
//			totalBlocks = statFs.getBlockCountLong();
            // 可以活动的块
            avaibleBlocks = statFs.getAvailableBlocksLong();
        } else {
            /*
             * 黑线说明这三个API已经过时了。但是为了兼容4.3一下的系统，我们需要写上
             */
            blockSize = statFs.getBlockSize();
//			totalBlocks = statFs.getBlockCount();
            avaibleBlocks = statFs.getAvailableBlocks();

        }

        long avaibleSpace = (avaibleBlocks * blockSize / 1024) / 1024;     //获取多少M

        if (avaibleSpace < 30) {    //小于40M就认为空间不足
            bRet = true;
        }

        return bRet;
    }

    public static int getInt(String s) {
        if (isDigital(s)) {
            try {
                return Integer.parseInt(s);
            } catch (Exception e) {

            }
        }
        return -99;

    }

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

    //返回多少M可以用的空间
    public static long getAvaibleSpace() {
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
//			totalBlocks = statFs.getBlockCountLong();
            // 可以活动的块
            avaibleBlocks = statFs.getAvailableBlocksLong();
        } else {
            /*
             * 黑线说明这三个API已经过时了。但是为了兼容4.3一下的系统，我们需要写上
             */
            blockSize = statFs.getBlockSize();
//			totalBlocks = statFs.getBlockCount();
            avaibleBlocks = statFs.getAvailableBlocks();

        }

        long avaibleSpace = (avaibleBlocks * blockSize / 1024) / 1024;     //获取多少M

        return avaibleSpace;
    }

    //返回多少M总共的空间
    public static long getTotalSpace() {
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

    public static boolean isNBitOne(int data, int shiftRight) {
        boolean bRet = false;

        int mData = (data >> shiftRight) & 0xff;

        if (1 == (mData % 2)) {
            bRet = true;
        }

        return bRet;
    }

    public static int getNBitTwo(int data, int shiftRight) {
        int iRet = -1;

        int mData = (data >> shiftRight) & 0xff;

        if (1 == (mData % 2)) {
            mData = (data >> (shiftRight + 1)) & 0xff;
            if (1 == (mData % 2)) {
                iRet = 3;
            } else {
                iRet = 1;
            }
        } else {
            mData = (data >> (shiftRight + 1)) & 0xff;
            if (1 == (mData % 2)) {
                iRet = 2;
            } else {
                iRet = 0;
            }
        }

        return iRet;
    }

    public static int getNBit(int data, int shiftRight) {

        int mData = (data >> shiftRight) & 0xff;

        return mData;
    }

    private static long lastClickTime;

    public static boolean isFastClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }


    public static boolean installSlientSu(Context context, String apkPath) {
        boolean bRet = false;
        if (TextUtils.isEmpty(apkPath)) {
            return bRet;
        }
        String cmd = "pm install -r " + apkPath;
        Process process = null;
        DataOutputStream os = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = null;
        StringBuilder errorMsg = null;
        try {

            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.write(cmd.getBytes());
            os.writeBytes("\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
            //获取返回结果
            successMsg = new StringBuilder();
            errorMsg = new StringBuilder();
            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;
            while ((s = successResult.readLine()) != null) {
                successMsg.append(s);
            }
            while ((s = errorResult.readLine()) != null) {
                errorMsg.append(s);
            }

            bRet = true;

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("YsUtility", "ys-- installSlientSu e: " + e);
            bRet = update(context, apkPath);
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (process != null) {
                    process.destroy();
                }
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return bRet;
    }

    public static boolean update(Context context, String apkPath) {
        boolean bRet = false;
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= 24) {
                //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
                Uri apkUri =
                        FileProvider.getUriForFile(context, "com.ys.service.fileprovider", new File(apkPath));
                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(
                        Uri.fromFile(new File(apkPath)),
                        "application/vnd.android.package-archive");
            }
            context.startActivity(intent);
            bRet = true;
        } catch (Exception e) {
            Log.e("YsUtility", "ys-- update e: " + e);
        }
        return bRet;
    }

    public static String getBigDecimalTwo(BigDecimal bigDecimal) {
        return bigDecimal.multiply(new BigDecimal(1.00f)).setScale(2, BigDecimal.ROUND_DOWN).toString();
    }
}
