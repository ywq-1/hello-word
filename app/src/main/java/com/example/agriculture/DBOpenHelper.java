package com.example.agriculture;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

public class DBOpenHelper extends SQLiteOpenHelper {

    final String CAEATE_TABLE_SQL =
            "create table record (_id integer primary key autoincrement,temperature VARCHAR2(100),time VARCHAR2(100))";

    public DBOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //创建单词信息表
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CAEATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("---版本更新-----" + oldVersion + "--->" + newVersion);
    }
}