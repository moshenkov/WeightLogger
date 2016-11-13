package com.example.android.weightlogger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Dmitry on 22.08.2016.
 */
public class DB {

    private static final String DB_NAME = "weight_logger_db";
    private static final int DB_VERSION = 1;
    private static final String DB_TABLE = "weight_table";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_DELTA = "delta";
    public static final String COLUMN_ISPOSITIVE = "ispositive";

    public static final String COLUMN_MIN_WEIGHT = "min_weight";
    public static final String COLUMN_AVG_WEIGHT = "avg_weight";
    public static final String COLUMN_MAX_WEIGHT = "max_weight";

    private static final String DB_CREATE =
            "create table " + DB_TABLE + "(" +
                COLUMN_ID + " integer primary key autoincrement, " +
                COLUMN_DATE + " integer not null, " +
                COLUMN_WEIGHT + " real not null" +
            ");";
    private static final String DB_GET_ALL_DATA_QUERY =
            "SELECT " +
                "t1." + COLUMN_ID + " AS " + COLUMN_ID + ", " +
                "t1." + COLUMN_DATE + " AS " + COLUMN_DATE + ", " +
                "t1." + COLUMN_WEIGHT + " AS " + COLUMN_WEIGHT + ", " +
                "(t1." + COLUMN_WEIGHT + " - IFNULL(t2." + COLUMN_WEIGHT + ",t1." + COLUMN_WEIGHT +
                    ")) AS " + COLUMN_DELTA + ", " +
                "(CASE WHEN t1." + COLUMN_WEIGHT + " > IFNULL(t2." + COLUMN_WEIGHT +
                    ", t1." + COLUMN_WEIGHT + ") " + "THEN 1 ELSE 0 END) AS " + COLUMN_ISPOSITIVE +
                " FROM (" +
                    "SELECT " +
                    "t1." + COLUMN_ID + " AS " + COLUMN_ID + ", " +
                    "t1." + COLUMN_DATE + " AS " + COLUMN_DATE + ", " +
                    "t1." + COLUMN_WEIGHT + " AS " + COLUMN_WEIGHT + ", " +
                    "MAX(t2." + COLUMN_DATE + ") AS PREV_DATE " +
                    "FROM " + DB_TABLE + " AS t1 LEFT JOIN " + DB_TABLE + " AS t2 " +
                    "ON t1." + COLUMN_DATE + " > t2." + COLUMN_DATE + " " +
                    "GROUP BY t1." + COLUMN_ID + ", t1." + COLUMN_DATE + ", t1." + COLUMN_WEIGHT +
                    ") AS t1 LEFT JOIN " + DB_TABLE + " AS t2 " +
                    "ON t1.PREV_DATE = t2." + COLUMN_DATE + " ORDER BY " + COLUMN_DATE + " DESC";

    private static final String DB_GET_STATISTIC_QUERY =
            "SELECT " +
            "t." + COLUMN_DATE +       "  AS " + COLUMN_DATE + ", " +
            "min(t." + COLUMN_ID +     ") AS " + COLUMN_ID + ", " +
            "min(t." + COLUMN_WEIGHT + ") AS " + COLUMN_MIN_WEIGHT + ", " +
            "avg(t." + COLUMN_WEIGHT + ") AS " + COLUMN_AVG_WEIGHT + ", " +
            "max(t." + COLUMN_WEIGHT + ") AS " + COLUMN_MAX_WEIGHT + " " +
            "FROM (SELECT " + COLUMN_ID + " AS " + COLUMN_ID + ", " +
                    "round(" + COLUMN_DATE + " / 100) AS " + COLUMN_DATE + ", " +
                    COLUMN_WEIGHT + " AS " + COLUMN_WEIGHT + " FROM " + DB_TABLE + ") AS t " +
                    "GROUP BY t." + COLUMN_DATE + " ORDER BY t." + COLUMN_DATE + " DESC";

    private static final String DB_GET_GRAF_DATA_QUERY =
            "SELECT " + COLUMN_ID + ", " + COLUMN_DATE + ", " + COLUMN_WEIGHT +
            " FROM " + DB_TABLE + " ORDER BY " + COLUMN_DATE;

    private final Context mCtx;

    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    public DB(Context ctx){
        mCtx = ctx;
    }

    public static String getDatabaseName(){

        return DB_NAME;
    }

    public void open(){
        mDBHelper = new DBHelper(mCtx, DB_NAME,  null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    public void close(){
        if (mDBHelper != null) mDBHelper.close();
    }

    public Cursor getAllData(){
        //return mDB.query(DB_TABLE, null, null, null, null, null, COLUMN_DATE);
        return mDB.rawQuery(DB_GET_ALL_DATA_QUERY, new String[] {});
    }

    public Cursor getStatistic() {
        return mDB.rawQuery(DB_GET_STATISTIC_QUERY, new String[] {});
    }

    public Cursor getGrafData(){
        return mDB.rawQuery(DB_GET_GRAF_DATA_QUERY, new String[] {});
    }

    public void addRec(ListItem listItem){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_DATE, listItem.getDateInt());
        cv.put(COLUMN_WEIGHT, listItem.getWeight());
        mDB.insert(DB_TABLE, null, cv);
    }

    public void updateRec(ListItem listItem){

        long idForUpdate = listItem.getId();

        //ищем запись с такой же датой
        ListItem oldListItem = getListItemByDate(listItem.getDateInt());

        //если запись нашлась, и это не наша запись
        if(oldListItem != null && oldListItem.getId() != idForUpdate) {
            //если наша запись новая, то обновим старую, иначе удалим старую
            if (listItem.isNew()) idForUpdate = oldListItem.getId();
            else delRec(oldListItem);
        }

        //если id для изменения пустой
        if (idForUpdate == ListItem.EMPTY_ID) {
            //добавим запись
            addRec(listItem);
        }
        else {
            //иначе обновляем существующую
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_DATE, listItem.getDateInt());
            cv.put(COLUMN_WEIGHT, listItem.getWeight());
            mDB.update(DB_TABLE, cv, COLUMN_ID + " = ?",
                    new String[] { String.valueOf(idForUpdate) });
        }

    }

    public void delRec(ListItem listItem){
        //mDB.delete(DB_TABLE, COLUMN_DATE + " = " + String.valueOf(listItem.getDateInt()), null);
        mDB.delete(DB_TABLE, COLUMN_ID + " = " + String.valueOf(listItem.getId()), null);
    }

    public void deleteById(long id) {
        mDB.delete(DB_TABLE, COLUMN_ID + " = " + String.valueOf(id), null);
    }


    public boolean isExistingRec(ListItem listItem){
        Cursor cur = mDB.query(DB_TABLE, null, COLUMN_DATE + " = ?",
                new String[] {String.valueOf(listItem.getDateInt())}, null, null, null);
        return cur.getCount() != 0;
    }

//    public ListItem getLastDayInfo(){
//        Cursor cur = getAllData();
//        if (cur.getCount() == 0) return new ListItem();
//        else {
//            cur.moveToLast();
//            return new ListItem(
//                    cur.getLong(cur.getColumnIndex(COLUMN_ID)),
//                    cur.getInt(cur.getColumnIndex(COLUMN_DATE)),
//                    cur.getFloat(cur.getColumnIndex(COLUMN_WEIGHT)));
//        }
//    }

    public ListItem getListItemByID(long id) {
        Cursor cur = mDB.query(DB_TABLE, null, COLUMN_ID + " = ?",
                new String[] {String.valueOf(id)}, null, null, null);
        if (cur.getCount() != 0) {
            cur.moveToFirst();
            return new ListItem(
                    cur.getLong(cur.getColumnIndex(COLUMN_ID)),
                    cur.getInt(cur.getColumnIndex(COLUMN_DATE)),
                    cur.getFloat(cur.getColumnIndex(COLUMN_WEIGHT)));
        }
        else return null;
    }

    public ListItem getListItemByDate(long date) {
        Cursor cur = mDB.query(DB_TABLE, null, COLUMN_DATE + " = ?",
                new String[] {String.valueOf(date)}, null, null, null);
        if (cur.getCount() != 0) {
            cur.moveToFirst();
            return new ListItem(
                    cur.getLong(cur.getColumnIndex(COLUMN_ID)),
                    cur.getInt(cur.getColumnIndex(COLUMN_DATE)),
                    cur.getFloat(cur.getColumnIndex(COLUMN_WEIGHT)));
        }
        else return null;
    }



    private class DBHelper extends SQLiteOpenHelper{

        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                        int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }
}
