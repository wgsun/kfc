package com.hua.back.kfc.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.common.base.XssTrands;

import java.util.function.ToDoubleBiFunction;

/**
 * 描述：
 * 作者：Jiancheng,Song on 2016/6/1 08:48
 * 邮箱：m68013@qq.com
 */
public class KfcSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = "TcnSQLiteOpenHelper";


    private volatile boolean m_bHasSQUpdae = false;

    private volatile boolean m_bHasSQErr = false;

    public KfcSQLiteOpenHelper(Context context, String name,
                               int version) {
        super(context, name, null, version);
    }

    public KfcSQLiteOpenHelper(Context context) {
        super(context, KfcDBUtils.DATABASE_NAME, null, KfcDBUtils.DATABASE_VERSION);
        logx("XssSQLiteOpenHelper DATABASE_NAME   " + KfcDBUtils.DATABASE_NAME + "   " + KfcDBUtils.DATABASE_VERSION);

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String slotInfo = getCreatData(KfcDBUtils.TABLE_ERROR_INFO, 6,
                KfcDBUtils.DATE,   KfcDBUtils.HOUR,   KfcDBUtils.CODE,   KfcDBUtils.STATUS,KfcDBUtils.COUNT,KfcDBUtils.INT_OTHER,
                KfcDBUtils.TIME_START, KfcDBUtils.TIME_LOG, KfcDBUtils.TIME_END, KfcDBUtils.OHTERDATA1, KfcDBUtils.OHTERDATA2);
        logx("onCreate  slotInfo:  " + slotInfo);
        db.execSQL(slotInfo);

    }

//    GOODS_INFO(ID integer primary key autoincrement,PROCODE text,PRICE text,NAME text,OVERTIME text,PROCODE text,PRICE text,NAME text,SN text)
//create table if not exists SLOT_INFO(ID integer primary key autoincrement,SLOT integer,NUM integer,SLOTLIST text,SN text,LOCATION_DETAIL text,OVERTIME text)
//create table if not exists GOODS_INFO(ID integer primary key autoincrement,PROCODE text,PRICE text,NAME text,OVERTIME text,PROCODE text,PRICE text,NAME text,SN text)

    /**
     * 0-3参数为整数，其他为
     *
     * @param
     * @return
     * @author hua
     * @time 2021/11/11 17:05
     */
    public String getCreatData(String tableName, int intNum, String... parm) {
        String SOLT_INFO_CREATE = "create table if not exists " +
                tableName +
                "(" + KfcDBUtils.LOCALKEY + " integer primary key autoincrement";
        if (intNum > 0) {
            for (int x = 0; x < intNum; x++) {
                SOLT_INFO_CREATE += "," + parm[x] + " integer";
            }
        }
        for (int x = intNum; x < parm.length; x++) {
            SOLT_INFO_CREATE += "," + parm[x] + " text";
        }
        SOLT_INFO_CREATE += ")";
        return SOLT_INFO_CREATE;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        logx("onUpgrade  oldVersion: " + oldVersion + " newVersion: " + newVersion);
        m_bHasSQUpdae = true;
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        logx("onDowngrade  oldVersion: " + oldVersion + " newVersion: " + newVersion);
        m_bHasSQUpdae = true;
    }

    public boolean insertData(String tablename, ContentValues values) {
        long insert = -1;
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            insert = database.insert(tablename, null, values);
        } catch (Exception e) {
            m_bHasSQErr = true;
        }

        if (insert != -1) {
            return true;
        } else {
            return false;
        }
    }

    public int deleteErrorCode(String tablename, int date) {
        SQLiteDatabase database = this.getWritableDatabase();
        return database.delete(tablename, KfcDBUtils.DATE + "=? "  , new String[]{String.valueOf(date)});
    }


    public Cursor queryData(String tablename) {
        SQLiteDatabase database = this.getReadableDatabase();
        return database.query(tablename, null, null, null, null, null, null);
    }


    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        SQLiteDatabase database = this.getReadableDatabase();
        return database.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }


    public boolean updateDataSlot(ContentValues values, int id) {
        if (null == values) {
            return false;
        }
        SQLiteDatabase database = this.getWritableDatabase();
        int i = -1;
        try {
            i = database.update(KfcDBUtils.TABLE_ERROR_INFO, values, KfcDBUtils.DATE + "=?", new String[]{String.valueOf(id)});
        } catch (Exception e) {
            m_bHasSQErr = true;

        }

        if (i == -1) {
            return false;
        } else {
            return true;
        }
    }

/**
 *  数据库更新
 * @param 
 * @return 
 * @author hua
 * @time 2021/11/12 13:57
 */
    public boolean updateDbData(String table,ContentValues values, String  seleKey,String... seleVl) {
        if (null == values) {
            return false;
        }
        SQLiteDatabase database = this.getWritableDatabase();
        int i = -1;
        try {
            i = database.update(table, values, seleKey + "=?",seleVl);
        } catch (Exception e) {
            m_bHasSQErr = true;

        }

        if (i == -1) {
            return false;
        } else {
            return true;
        }
    }




    // 更新

    private void logx(String msg) {
        XssTrands.getInstanll().logd(this.getClass().getSimpleName(), msg);

    }

}
