package cn.yanweijia.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by weijia on 2016/8/19.
 * 数据库工具类
 */
public class DBHelper extends SQLiteOpenHelper {

    static private final String CREARE_TABLE_AD = "CREATE TABLE AD(isRemovedAD integer)";
    static private final String SQL_INSERT_AD = "INSERT INTO AD(isRemovedAD)VALUES(0)";
    static private final String SQL_GET_AD_VALUE = "SELECT isRemovedAD FROM AD";
    static private final String SQL_REMOVE_AD = "UPDATE AD SET isRemovedAD=1 WHERE isRemovedAD=0";

    /**
     * 判断广告是否已经移除
     * @return
     */
    public boolean isRemovedAD(){
//        DBHelper dbhelper = new DBHelper(context,"ad.db",null,1);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL_GET_AD_VALUE, null);
        if(cursor.getCount() == 0)
            return false;
        cursor.moveToFirst();   //将当前指针移动到第一个位置上
        int result = cursor.getInt(cursor.getColumnIndex("isRemovedAD"));
        //关闭数据库连接
        cursor.close();
        db.close();

        if(result == 1)
            return true;
        else
            return false;
    }

    /**
     * 移除广告
     */
    public void removeAD(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(SQL_REMOVE_AD);
        //关闭数据库连接
        db.close();
    }



    /**
     * 构造方法
     * @param context context
     * @param name 数据库名称
     * @param factory 默认null
     * @param version 版本,填写1吧
     */
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(SQL_INSERT_AD);
        db.close();
    }

    /**
     * 首次使用数据库CREATE数据表语句
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREARE_TABLE_AD);

    }

    /**
     * 版本升级
     * @param db
     * @param arg1
     * @param arg2
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {}
}
