package com.ts.wfd.chat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper{

    //public static String ACCOUNT_TABLE = "ACCOUNT";
    //public static String NAME_COLUMN = "NICKNAME";

    public Database(Context context)
    {
        super(context,"WDChatDB",null,21);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE ACCOUNTS (ID INTEGER PRIMARY KEY AUTOINCREMENT, NICKNAME);");
        db.execSQL("CREATE TABLE CHAT (ID INTEGER PRIMARY KEY AUTOINCREMENT, NICKNAME, DEVICE_NAME);");
        db.execSQL("CREATE TABLE MESSAGES (ID INTEGER PRIMARY KEY AUTOINCREMENT, NICKNAME, MESSAGE);");
        //db.execSQL("DELETE FROM MESSAGES WHERE ID>0");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("");
        db.execSQL("DELETE FROM MESSAGES WHERE ID>0");
        db.execSQL("DELETE FROM CHAT WHERE ID>0");
    }
}
