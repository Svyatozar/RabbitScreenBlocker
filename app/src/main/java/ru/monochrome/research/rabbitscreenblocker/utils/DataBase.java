package ru.monochrome.research.rabbitscreenblocker.utils;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Провайдер услуг работы с Базой Данных
 * @author svyatozar
 *
 */
public class DataBase
{

    private static final String DB_NAME = "Base";
    private static final int DB_VERSION = 1;
    private static final String DB_TABLE = "apps";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_APPNAME = "name";
    public static final String COLUMN_APPPACKAGE = "package";

    private static final String DB_CREATE =
            "create table " + DB_TABLE + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_APPNAME + " text," +
                    COLUMN_APPPACKAGE + " text" +
                    ");";

    private final Context context;


    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    public DataBase(Context ctx)
    {
        context = ctx;
    }

    /**
     *  открыть подключение
     */
    public synchronized void open()
    {
        mDBHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    /**
     *  закрыть подключение
     */
    public synchronized void close()
    {
        if (mDBHelper!=null)
        {
            mDBHelper.close();
            mDBHelper = null;
        }
    }

    /**
     *  получить все данные из таблицы DB_TABLE
     * @return курсор с данными
     */
    public Cursor getAllData()
    {

        Cursor result = mDB.query(DB_TABLE, null, null, null, null, null, null);

        return result;
    }

    /**
     * Получить список всех приложений, доступных пользователю
     * @return
     */
    public ArrayList<String> getAllApps()
    {
        Cursor apps = getAllData();
        ArrayList<String> list = new ArrayList<String>();

        if (apps.moveToFirst())
        {
            // определяем номера столбцов по имени в выборке
            int nameIndex = apps.getColumnIndex(COLUMN_APPNAME);

            do
            {
                list.add(apps.getString(nameIndex));
            }
            while (apps.moveToNext());        	// переход на следующую строку
            // а если следующей нет (текущая - последняя), то false - выходим из цикла
        }

        return list;
    }

    /**
     * Получить пакет приложения
     * @param id
     */
    public String getPackage(long id)
    {

        Cursor app = mDB.query(DB_TABLE, null,  COLUMN_ID + " = " + id, null, null, null, null);

        app.moveToFirst();

        // определяем номера столбцов по id в выборке
        int packageIndex = app.getColumnIndex(COLUMN_APPPACKAGE);

        return app.getString(packageIndex);
    }

    /**
     *  добавить запись в DB_TABLE
     * @param app_name имя приложения
     * @param app_package пакет приложения
     */
    public void addRec(String app_name, String app_package)
    {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_APPNAME, app_name);
        cv.put(COLUMN_APPPACKAGE, app_package);


        mDB.insert(DB_TABLE, null, cv);

    }

    /**
     *  удалить запись из DB_TABLE
     * @param id записи
     */
    public void delRec(long id)
    {

        mDB.delete(DB_TABLE, COLUMN_ID + " = " + id, null);
    }

    /**
     *  удалить запись из DB_TABLE
     * @param app_package имя пакета
     */
    public void delRec(String app_package)
    {

        mDB.delete(DB_TABLE, COLUMN_APPPACKAGE + " = '" + app_package + "'", null);

    }

    /**
     *  класс по созданию и управлению БД
     * @author svyatozar
     *
     */
    private class DBHelper extends SQLiteOpenHelper
    {

        public DBHelper(Context context, String name, CursorFactory factory,int version)
        {
            super(context, name, factory, version);
        }

        /**
         *  создаем БД
         */
        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            // TO DO BE DO BE DO etc.
        }
    }
}