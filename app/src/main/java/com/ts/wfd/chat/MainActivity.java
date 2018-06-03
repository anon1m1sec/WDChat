package com.ts.wfd.chat;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends SingleFragmentActivity {

    private int countAccounts = 0;

    @Override
    protected Fragment createFragment() {

        checkCountAccounts();
        if(countAccounts == 0)
        return new LoginFragment();
        //else return new ChatFragment();
        else return new Connectivity();
        //return new ConnectivityFragment();
        //return new Connectivity();
    }

    private void checkCountAccounts()
    {
        Database dbHelper = new Database(this.getApplicationContext());
        SQLiteDatabase db = null;
        try
        {
            db = dbHelper.getReadableDatabase();
        }
        catch (SQLiteException ex)
        {
            ex.printStackTrace();
        }
        if(db != null) {
            String[] selectionArgs = new String[] {String.valueOf("NICKNAME")};
            Cursor cursor = db.query("ACCOUNTS", new String[]{"NICKNAME"}, null, null, null, null, null);
            //Cursor cursor = db.query("ACCOUNTS", null, null, selectionArgs, null, null, null);
            //int index = cursor.getColumnIndex("NICKNAME");
            //cursor.
            if(cursor.moveToFirst()) {
                //cursor.getString(cursor.getColumnIndexOrThrow("NICKNAME"));
                countAccounts = cursor.getCount();
            }
        }
        db.close();
        dbHelper.close();
    }

}
