package com.hua.back.kfc.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.common.base.XssTrands;

import com.common.base.utils.XssData;
import com.common.base.utils.XssUtility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class KfcSqlControl {
    static KfcSqlControl sqlControl;
    KfcSQLiteOpenHelper m_helper;
    int max = XssData.SLotMax_csj;

    public static KfcSqlControl getInstall() {
        if (sqlControl == null) {
            synchronized (KfcSqlControl.class) {
                if (sqlControl == null) {
                    sqlControl = new KfcSqlControl();
                }
            }
        }
        return sqlControl;
    }

    public void init(Context mContent) {
        logx("init");
        m_helper = new KfcSQLiteOpenHelper(mContent);
        // 步骤2：真正创建 / 打开数据库
        m_helper.getWritableDatabase(); // 创建 or 打开 可读/写的数据库
        m_helper.getReadableDatabase();
    }

    /**
     * 添加货道信息
     *
     * @param
     * @return
     * @author hua
     * @time 2021/11/12 13:02
     */
    public void addErrorCode(int code) {
        ContentValues values = new ContentValues();
        String time = XssUtility.getTime14B();
        int date = Integer.parseInt(time.substring(6, 8));
        int hour = Integer.parseInt(time.substring(8, 10));
        String timelog = XssUtility.getTimeShow(System.currentTimeMillis());
        values.put(KfcDBUtils.DATE, date);
        values.put(KfcDBUtils.HOUR, hour);
        values.put(KfcDBUtils.CODE, code);
        values.put(KfcDBUtils.STATUS, 0);
        values.put(KfcDBUtils.COUNT, 0);
        values.put(KfcDBUtils.TIME_START, "" + System.currentTimeMillis());
        values.put(KfcDBUtils.TIME_LOG, timelog);
        boolean result = m_helper.insertData(KfcDBUtils.TABLE_ERROR_INFO, values);
        logx("addErrorCode:  code=" + code + "  date=" + date + "  hour=" + hour + "  timelog=" + timelog + "  result=" + result);
    }


    // TODO: 2022/8/18  每日删除多余数据 
    public void deleteErrorCode(int date) {
        int result = m_helper.deleteErrorCode(KfcDBUtils.TABLE_ERROR_INFO, date);
        logx("deleteErrorCode: date=" + date + "   result=" + result);

    }

    /**
     * 查询所有故障信息
     *
     * @param
     * @return
     * @author hua
     * @time 2022/8/18 9:21
     */
    public List<KfcErrorInfo> queryErrorAllInfo() {
        List<KfcErrorInfo> coil_list = new ArrayList<>();
        try {
            SQLiteDatabase database = m_helper.getReadableDatabase();
            Cursor c = null;
            c = database.query(KfcDBUtils.TABLE_ERROR_INFO,
                    null, null,
                    null, null, null, null);
            cursorSlotInfo(c, coil_list);
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
            logx("querySlotInfo: " + e.toString());
        }
        return coil_list;
    }

    public List<KfcErrorInfo> queryErrorInfo(int date, int hour) {
        logx("queryErrorInfo date="+date+"   hour="+hour);
        List<KfcErrorInfo> errorInfos = new ArrayList<>();
        try {
            SQLiteDatabase database = m_helper.getReadableDatabase();
            Cursor c = null;
            if (hour == -1) {
                c = database.query(KfcDBUtils.TABLE_ERROR_INFO,
                        null, KfcDBUtils.DATE + "=?" , new String[]{date + ""},
                        null, null, null, null);
            } else {
                c = database.query(KfcDBUtils.TABLE_ERROR_INFO,
                        null, KfcDBUtils.DATE + "=? AND " + KfcDBUtils.HOUR + "=? ", new String[]{date + "", String.valueOf(hour)},
                        null, null, null, null);
            }

            cursorSlotInfo(c, errorInfos);
            c.close();
            logx("querySlotInfo  errorInfos: " +errorInfos.size());

        } catch (Exception e) {
            e.printStackTrace();
            logx("querySlotInfo: " + e.toString());
        }
        return errorInfos;
    }

    public void cursorSlotInfo(Cursor c, List<KfcErrorInfo> errorInfos) {
        if (c == null) {
            logx("cursorSlotInfo: c == null");
            return;
        }
        while (c.moveToNext()) {
            KfcErrorInfo info = new KfcErrorInfo();
            info.setDate(c.getInt(c.getColumnIndex(KfcDBUtils.DATE)));
            info.setHour(c.getInt(c.getColumnIndex(KfcDBUtils.HOUR)));
            info.setCode(c.getInt(c.getColumnIndex(KfcDBUtils.CODE)));
            info.setCount(c.getInt(c.getColumnIndex(KfcDBUtils.COUNT)));
            info.setStatus(c.getInt(c.getColumnIndex(KfcDBUtils.STATUS)));
            info.setEnd(c.getString(c.getColumnIndex(KfcDBUtils.TIME_END)));
            info.setStart(c.getString(c.getColumnIndex(KfcDBUtils.TIME_START)));
            info.setTime(c.getString(c.getColumnIndex(KfcDBUtils.TIME_LOG)));
            info.setKey(c.getInt(c.getColumnIndex(KfcDBUtils.LOCALKEY)));
            errorInfos.add(info);
        }
    }

    public String getSlotSortString(List<Long> sorts) {
        if (sorts != null && sorts.size() > 0) {
            return new Gson().toJson(sorts);
        }
        return "";
    }

    public List<Long> getSlotSortList(String sort) {
        List<Long> JsonList = new Gson().fromJson(sort, new TypeToken<ArrayList<Long>>() {
        }.getType());
        return JsonList;
    }

    public void logx(String msg) {
        String name ="KfcSqlControl";
        XssTrands.getInstanll().LoggerDebug(name, msg);
    }
}
