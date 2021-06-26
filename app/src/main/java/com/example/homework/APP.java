package com.example.homework;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;

public class APP extends Application {

    public static DatabaseHelper mDatabaseHelper;
    public static SQLiteDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();

        //创建数据库
        if (mDatabaseHelper == null) mDatabaseHelper = new DatabaseHelper(this, "Sql.db", null, 1);
        database = mDatabaseHelper.getWritableDatabase();

        LogUtils.e("shi fou huo qu cunchu quanxian:" + Environment.isExternalStorageManager());

    }
}