package io.github.zhaomy6.lifebattery;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by zhangsht on 2016/12/10.
 */

public class MyDB extends SQLiteOpenHelper {
    private static final String DB_Name = "PlanStores2";
    private static final String Table_Name = "Plans3";
    private static final int DB_Version = 1;

    public MyDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public MyDB(Context context) {
        super(context, DB_Name, null, DB_Version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String Create_Table = "CREATE TABLE if not exists "
                + Table_Name
                + " (_id INTEGER PRIMARY KEY, title TEXT, DDL TEXT, type TEXT, progress TEXT, detail TEXT)";
        db.execSQL(Create_Table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void insert2DB(String title, String DDL, String type, String progress, String detail) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("DDL", DDL);
        cv.put("type", type);
        cv.put("progress", progress);
        cv.put("detail", detail);
        long res = db.insert(Table_Name, null, cv);
        db.close();
    }

    public void updateDB(String title, String DDL, String type, String progress, String detail) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("DDL", DDL);
        cv.put("type", type);
        cv.put("progress", progress);
        cv.put("detail", detail);
        String whereClause = "title=?";
        String[] whereArgs = {title};
        long res = db.update(Table_Name, cv, whereClause, whereArgs);
        db.close();
    }

    public void deleteDB(String title) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = "title=?";
        String[] whereArgs = {title};
        long res = db.delete(Table_Name, whereClause, whereArgs);
        db.close();
    }

    public boolean isExists(String title) {
        SQLiteDatabase db = getWritableDatabase();
        String[] columns = {"title"};
        String whereClause = "title=?";
        String[] whereArgs = {title};
        Cursor cursor = db.query(Table_Name, columns, whereClause, whereArgs, null, null, null);
        boolean ans = cursor.getCount() != 0;
        cursor.close();
        db.close();
        return ans;
    }

    public Cursor getWithTitle(String title) {
        SQLiteDatabase db = getWritableDatabase();
        String[] colums = {"_id", "title", "DDL", "type", "progress", "detail"};
        String whereClause = "title=?";
        String[] whereArgs = {title};
        Cursor cursor = db.query(Table_Name, colums, whereClause, whereArgs, null, null, null);
        if (cursor.getCount() > 0) {
            Log.i(TAG, "getWithTitle: count");
        }
        return cursor;
    }

    public Cursor queryDB(String title) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(Table_Name, new String[]{"title"}, null, null, null, null, null);
        db.close();
        return cursor;
    }

    public Cursor getAll() {
        SQLiteDatabase db = getWritableDatabase();
        String[] tableColumns = {"_id", "title", "DDL", "type", "progress", "detail"};
        return db.query(Table_Name, tableColumns,
                null, null, null, null, null);
    }

    public Cursor getPart() {
        SQLiteDatabase db = getWritableDatabase();
        String[] tableColumns = {"_id", "title", "DDL"};
        return db.query(Table_Name, tableColumns,
                null, null, null, null, null);
    }

    public Cursor queryWithKeyword(String keyword) {
        SQLiteDatabase db = getWritableDatabase();
//        String[] colums = {"title", "DDL", "type", "progress", "detail"};
////        String whereClause = "title like %?% or DDL like %?% or type like %?% or progress like %?% or" +
////                "detail like %?%";
////        String[] whereArgs = {keyword, keyword, keyword, keyword, keyword};
//        Cursor cursor = db.query(Table_Name, colums, whereClause, whereArgs, null, null, null);
//        if (cursor.getCount() > 0) {
//            Log.i(TAG, "getWithTitle: count");
//        }

        String query_sql = "SELECT * FROM " + Table_Name + " WHERE title LIKE '%" + keyword
                + "%' OR DDL LIKE '%" + keyword + "%' OR type LIKE '%" + keyword + "%' OR progress LIKE '%"
                + keyword + "%' OR detail LIKE '%" + keyword + "%'";
        Cursor cursor = db.rawQuery(query_sql, null);
        return cursor;
    }
}
