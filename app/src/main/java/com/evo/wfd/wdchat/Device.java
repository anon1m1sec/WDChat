package com.evo.wfd.wdchat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Device {

    private String mMainDeviceIP;
    private String mDeviceName;
    private String mNickName;
    private List<String> mDeviceList;
    private List<String> mNickNames = new ArrayList<>();
    private List<String> mIPs = new ArrayList<>();
    //private List<ServerSocket> mServerSockets;
    private List<Socket> mSockets = new ArrayList<>();
    private ServerSocket ss;
    private Socket ClientSocket;
    private Boolean isAdmin;
    private Context cont;
    //private boolean nickname_not_send = false;
    //private ChatFragment chatFragment;
    //private Socket mSocket;

    public Device(String deviceName,String deviceIP, Boolean isadmin, Context context)
    {
        isAdmin = isadmin;
        mMainDeviceIP = deviceIP;
        mDeviceName = deviceName;
        mNickName = getMainAccount(context);
        cont = context;
        try
        {
            if(!isAdmin)
            {
                send(mMainDeviceIP);
            }
            ss = new ServerSocket(8000);
            runServer();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.d("WDDebug",e.getMessage());
        }

    }

    private void send(final String ip)
    {
        try
        {
            final Thread task = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ClientSocket = new Socket(ip, 8000);
                        sendNickName(ClientSocket);
                        sendDeviceName(ClientSocket);
                        Log.d("WDDebug", "nickname message sended");
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        Log.d("WDDebug",e.getMessage());
                        try {
                            Thread.sleep(5000);
                            send(ip);
                        }
                        catch (Exception e1)
                        {
                            e1.printStackTrace();
                            Log.d("WDDebug",e1.getMessage());
                        }
                    }
                }
            });
            task.start();
            Log.d("WDDebug",task.getState().name());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.d("WDDebug",e.getMessage());
        }
    }

    public String getDeviceName()
    {
        return mDeviceName;
    }

    public String getNickName()
    {
        return mNickName;
    }

    public List<String> getListNickNames()
    {
        Log.d("WDDebug","The list of the nicknames");
        for(int i=0;i<mNickNames.size();i++)
        {
            Log.d("WDDebug",mNickNames.get(i));
        }
        return mNickNames;
    }

    private void sendNickName(Socket s)
    {
        sendMessage("nickName," + mNickName,s);
    }

    private void sendDeviceName(Socket s)
    {
        sendMessage("deviceName," + mDeviceName,s);
    }

    private void sendMessage(String message, Socket s)
    {
        try {
            OutputStream sout = s.getOutputStream();
            sout.write(message.getBytes());
            Log.d("WDDebug","send message running...");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.d("WDDebug",e.getMessage());
        }
    }

    private String getMainAccount(Context context)
    {
        String result = "";
        Database dbHelper = new Database(context);
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
            Cursor cursor = db.query("ACCOUNTS", new String[]{"NICKNAME"}, null, null, null, null, null);
            //result = cursor.getString(0);

            if(cursor.moveToFirst()) {
                result = cursor.getString(cursor.getColumnIndexOrThrow("NICKNAME"));
            }
        }
        db.close();
        dbHelper.close();
        return result;
    }

    public void readMessage(String message)
    {
        String[] mass = message.split(",");
        if(mass[0].equals("nickName"))
        {
            //boolean is_exist
            mNickNames.add(mass[1]);
            Log.d("WDDebug","new nickname added");
        }
        else if(mass[0].equals("deviceName"))
        {
            mDeviceList.add(mass[1]);
            int id = mDeviceList.size()-1;
            String nm = mNickNames.get(id);
            String dn = mNickNames.get(id);
            if(!checkAccount(nm,dn))
            {
                writeNewAccount(nm,dn);
                updateUI();
            }
        }
        else
        {

        }
    }

    private void updateUI()
    {
        ChatFragment fr = SingletClass.getChatFragment();
        if(fr != null) fr.updateUI2(getAccounts());
    }

    private List<String> getAccounts()
    {
        List<String> list = new ArrayList<>();
        Database dbHelper = new Database(cont);
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
            Cursor cursor = db.query("CHAT", new String[]{"NICKNAME","DEVICE_NAME"}, null, null, null, null, null);
            //result = cursor.getString(0);

            if(cursor.moveToFirst()) {
                while (cursor.moveToNext()) {
                    String nick = cursor.getString(cursor.getColumnIndexOrThrow("NICKNAME"));
                    String device_name = cursor.getString(cursor.getColumnIndexOrThrow("DEVICE_NAME"));
                    list.add(nick);
                }
            }
        }
        db.close();
        dbHelper.close();
        return list;
    }

    private void writeNewAccount(String nickname, String devicename)
    {
        Database dbHelper = new Database(cont);
        SQLiteDatabase db = null;
        try
        {
            db = dbHelper.getWritableDatabase();
        }
        catch (SQLiteException ex)
        {
            ex.printStackTrace();
        }
        if(db != null) {
            ContentValues values = new ContentValues();
            values.put("NICKNAME",nickname);
            values.put("DEVICE_NAME",devicename);
            db.insert("CHAT",null,values);
        }
        db.close();
        dbHelper.close();
    }


    private Boolean checkAccount(String nickname, String devicename)
    {
        boolean check = false;
        Database dbHelper = new Database(cont);
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
            Cursor cursor = db.query("CHAT", new String[]{"NICKNAME","DEVICE_NAME"}, null, null, null, null, null);
            //result = cursor.getString(0);

            if(cursor.moveToFirst()) {
                while (cursor.moveToNext()) {
                    String nick = cursor.getString(cursor.getColumnIndexOrThrow("NICKNAME"));
                    String device_name = cursor.getString(cursor.getColumnIndexOrThrow("DEVICE_NAME"));
                    if(nickname.equals(nick) && device_name.equals(device_name)) return check;
                }
            }
        }
        db.close();
        dbHelper.close();
        return check;
    }


    private void runServer()
    {
        try {
            Thread task = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            Socket s = ss.accept();
                            if(isAdmin) send(s.getInetAddress().getHostAddress());
                            mSockets.add(s);
                            mIPs.add(s.getInetAddress().getHostAddress());
                            InputStream sin = s.getInputStream();
                            byte buf[] = new byte[64 * 1024];
                            int r = sin.read(buf);
                            // создаём строку, содержащую полученую от клиента информацию
                            String request = new String(buf, 0, r);
                            Log.d("WDDebug", "message received");
                            readMessage(request);
                        }
                        //runServer();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        Log.d("WDDebug",e.getMessage());
                    }
                }
            });
            task.start();
            Log.d("WDDebug","Server is running");
            //ServerSocket ss = new ServerSocket(8000);
            //mServerSockets.add(ss);
            //mSocket = ss.accept();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.d("WDDebug",e.getMessage());
        }
    }
}
