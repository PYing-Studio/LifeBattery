package io.github.zhaomy6.lifebattery;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class MyDB extends SQLiteOpenHelper {
    private static final String DB_Name = "PlanStores3";
    private static final String Table_Name = "Plans4";

    //  新建一个表存储已完成事件
    //  为了解决无法重复添加同名事件的BUG
    private static final String Table_Finish_Task = "FinishPlans";
    private static final int DB_Version = 1;

    public MyDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public MyDB(Context context) {
        super(context, DB_Name, null, DB_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //  type == true: 短期计划
        //  type == false: 长远计划
        String Create_Table = "CREATE TABLE if not exists "
                + Table_Name
                + " (_id INTEGER PRIMARY KEY, title TEXT, DDL TEXT, type TEXT, detail TEXT, finished TEXT)";
        db.execSQL(Create_Table);

        //  新建一个表存储已完成事件
        String Create_Finish_Table = "CREATE TABLE if not exists "
                + Table_Finish_Task
                + " (_id INTEGER PRIMARY KEY, title TEXT, DDL TEXT, type TEXT, detail TEXT, finished TEXT)";
        db.execSQL(Create_Finish_Table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void insert2DB(String title, String DDL, String type, String detail, String finished) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("DDL", DDL);
        cv.put("type", type);
        cv.put("detail", detail);
        cv.put("finished", finished);
        db.insert(Table_Name, null, cv);
        db.close();
    }

    //  插入到存储已完成任务的表中
    public void insert2FinishTable(String title, String DDL, String type, String detail, String finished) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("DDL", DDL);
        cv.put("type", type);
        cv.put("detail", detail);
        cv.put("finished", finished);
        db.insert(Table_Finish_Task, null, cv);
        db.close();
    }

    public void updateDB(String title, String DDL, String type, String detail) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("DDL", DDL);
        cv.put("type", type);
        cv.put("detail", detail);
        String whereClause = "title=?";
        String[] whereArgs = {title};
        db.update(Table_Name, cv, whereClause, whereArgs);
        db.close();
    }

    private void updateDBItemById(int id, String newTitle, String newDDL, String newType, String newDetail) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", newTitle);
        cv.put("DDL", newDDL);
        cv.put("type", newType);
        cv.put("detail", newDetail);
        String whereClause = "_id=?";
        String[] whereArgs = {"" + id};
        db.update(Table_Name, cv, whereClause, whereArgs);
        db.close();
    }

    //  完成任务时，需要从原表中删除，并添加到新表中
    public void updateFinished(String title, String finished) {
        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = getWithTitle(title);
        cursor.moveToFirst();
        final String DDL = cursor.getString(cursor.getColumnIndex("DDL"));
        final String detailText = cursor.getString(cursor.getColumnIndex("detail"));
        final String typeText = cursor.getString(cursor.getColumnIndex("type"));
        insert2FinishTable(title, DDL, typeText, detailText, finished);
        deleteDB(title);
        db.close();
    }

    public void updateTimeout(String currentTime) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("finished", "超时");
        String whereClause = "type = ? AND DDL <= ?";
        String[] whereArgs = {"false", currentTime};
        db.update(Table_Name, cv, whereClause, whereArgs);
        db.close();
    }

    public void deleteDB(String title) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = "title=?";
        String[] whereArgs = {title};
        db.delete(Table_Name, whereClause, whereArgs);
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
        String[] colums = {"_id", "title", "DDL", "type", "detail", "finished"};
        String whereClause = "title=?";
        String[] whereArgs = {title};
        Cursor cursor = db.query(Table_Name, colums, whereClause, whereArgs, null, null, null);
        if (cursor.getCount() > 0) {
            Log.i(TAG, "getWithTitle: count");
        }
        return cursor;
    }

//    public Cursor queryDB(String title) {
//        SQLiteDatabase db = getWritableDatabase();
//        Cursor cursor = db.query(Table_Name, new String[]{"title"}, null, null, null, null, null);
//        db.close();
//        return cursor;
//    }

    public Cursor getLatestPlan() {
        SQLiteDatabase db = getWritableDatabase();
        String query_sql = "SELECT title, MIN(DDL) AS DDL FROM " + Table_Name + " WHERE type = 'false' AND finished = '未完成'";
        Cursor cursor = db.rawQuery(query_sql, null);
        return cursor;
    }

    public Cursor getAll() {
        SQLiteDatabase db = getWritableDatabase();
        String[] tableColumns = {"_id", "title", "DDL", "type", "detail", "finished"};
        return db.query(Table_Name, tableColumns,
                null, null, null, null, null);
    }

    // 获取表中部分列
    public Cursor getPart() {
        SQLiteDatabase db = getWritableDatabase();
        String query_sql = "SELECT _id, title, DDL FROM " + Table_Name + " WHERE type = 'false' AND finished = '未完成'";
        Cursor cursor = db.rawQuery(query_sql, null);
        return cursor;
    }

    //  返回所有超时的任务数，并将这些任务的title以及内容置空
    public int getOvertimeTaskNum() {
        SQLiteDatabase db = getWritableDatabase();
        String query_sql = "SELECT _id, title, DDL FROM " + Table_Name + " WHERE type = 'false' AND finished = '超时'";
        Cursor cursor = db.rawQuery(query_sql, null);
        int num = cursor.getCount();
        while (cursor.moveToNext()) {
            int id = Integer.parseInt(cursor.getString(0));
            String title = cursor.getString(1);
            String DDL = cursor.getString(2);
//            Log.d("test db", title + " " + DDL);
            //  | title | ddl | type | detail |
            updateDBItemById(id, "", DDL, "false", "");
        }
        cursor.close();
        return num;
    }

    public int getUnfinishedTaskNum() {
        SQLiteDatabase db = getWritableDatabase();
        String query_sql = "SELECT _id FROM " + Table_Name;
        Cursor cursor = db.rawQuery(query_sql, null);
        int num = cursor.getCount();
        cursor.close();
        return num;
    }
    public int getFinisedTaskNum() {
        SQLiteDatabase db = getWritableDatabase();
        String query_sql = "SELECT _id FROM " + Table_Finish_Task;
        Cursor cursor = db.rawQuery(query_sql, null);
        int num = cursor.getCount();
        cursor.close();
        return num;
    }

    // 根据关键词对任务的每一列进行匹配
    public Cursor queryWithKeyword(String keyword) {
        SQLiteDatabase db = getWritableDatabase();
        String query_sql = "SELECT * FROM " + Table_Name + " WHERE title LIKE '%" + keyword
                + "%' OR DDL LIKE '%" + keyword + "%' OR type LIKE '%" + keyword + "%' OR detail LIKE '%"
                + keyword + "%' OR finished LIKE '%" + keyword + "%'";
        Cursor cursor = db.rawQuery(query_sql, null);
        return cursor;
    }

    public Cursor sortWithTime() {
        SQLiteDatabase db = getWritableDatabase();
        String query_sql = "SELECT * FROM " + Table_Name + " WHERE type = 'false' AND finished = '未完成' ORDER BY DDL";
        Cursor cursor = db.rawQuery(query_sql, null);
        return cursor;
    }
}
