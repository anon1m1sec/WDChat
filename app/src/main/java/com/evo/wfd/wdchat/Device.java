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
    private List<String> mDeviceList = new ArrayList<>();
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
        Log.d("WDDebug","DEVICE_IP: " + deviceIP);
        Log.d("WDDebug","IS_ADMIN: " + isAdmin);
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
                        Thread.sleep(1000);
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

    /*private void sendInfoToClient()
    {
        sendNickName(mSockets.get(mSockets.size()-1));
        try {
            Thread.sleep(1000);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        sendDeviceName(mSockets.get(mSockets.size()-1));
    }*/

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
        Log.d("WDDebug", "nickname sended");
        sendMessage("nickName," + mNickName,s);
    }

    private void sendDeviceName(Socket s)
    {
        Log.d("WDDebug", "devicename sended");
        sendMessage("deviceName," + SingletClass.getDeviceName(),s);
    }

    /*private void sendMainDeviceName(Socket s)
    {
        Log.d("WDDebug", "devicename sended");
        sendMessage("deviceName," + getMainAccount(cont),s);
    }*/

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

            //if(cursor.moveToFirst()) {
                cursor.moveToFirst();
                result = cursor.getString(cursor.getColumnIndexOrThrow("NICKNAME"));
            //}
        }
        db.close();
        dbHelper.close();
        return result;
    }

    public void readMessage(String message)
    {
        Log.d("WDDebug","message: " + message);
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
            String dn = mDeviceList.get(id);
            Log.d("WDChat","Message received");
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
        if(fr != null) fr.updateUI2(getChatList());
    }

    private List<String> getChatList()
    {
        ArrayList<String> list = new ArrayList<>();
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
            Cursor cursor = db.query("MESSAGES", new String[]{"NICKNAME","MESSAGE"}, null, null, null, null, null);
            //result = cursor.getString(0);
            if(cursor.moveToFirst()) {
                do {
                    String nick = cursor.getString(cursor.getColumnIndexOrThrow("NICKNAME"));
                    String message = cursor.getString(cursor.getColumnIndexOrThrow("MESSAGE"));
                    list.add(nick);
                } while (cursor.moveToNext());
            }
            /*if(cursor.moveToFirst()) {
                while (cursor.moveToNext()) {
                    String nick = cursor.getString(cursor.getColumnIndexOrThrow("NICKNAME"));
                    String message = cursor.getString(cursor.getColumnIndexOrThrow("MESSAGE"));
                    list.add(nick);
                    //list.add(nick);
                }
            }*/
        }
        db.close();
        dbHelper.close();
        return list;
    }

    public void addChat(String nick)
    {
        if(!checkChat(nick)) {
            Database dbHelper = new Database(cont);
            SQLiteDatabase db = null;
            try {
                db = dbHelper.getWritableDatabase();
            } catch (SQLiteException ex) {
                ex.printStackTrace();
            }
            if (db != null) {
                ContentValues values = new ContentValues();
                values.put("NICKNAME", nick);
                values.put("MESSAGE", "");
                db.insert("MESSAGES", null, values);
            }
            db.close();
            dbHelper.close();
        }
        updateUI();
    }

    public String getNick(String deviceName)
    {
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
            Log.d("WDChat","DB count: " + cursor.getCount());
            //if(cursor.moveToFirst()) {
            if(cursor.moveToFirst()) {
                do {
                    String nick = cursor.getString(cursor.getColumnIndexOrThrow("NICKNAME"));
                    String device_name = cursor.getString(cursor.getColumnIndexOrThrow("DEVICE_NAME"));
                    Log.d("WDChat", "device: " + nick + " " + device_name);
                    if (device_name.equals(deviceName)) return nick;

                } while (cursor.moveToNext());
            }
        }
        db.close();
        dbHelper.close();
        return null;
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
                do {
                    String nick = cursor.getString(cursor.getColumnIndexOrThrow("NICKNAME"));
                    String device_name = cursor.getString(cursor.getColumnIndexOrThrow("DEVICE_NAME"));
                    list.add(nick);
                } while (cursor.moveToNext());
            }
        }
        db.close();
        dbHelper.close();
        return list;
    }

    private void writeNewAccount(String nickname, String devicename)
    {
        Log.d("WDChat","Account added: " + nickname + " - " + devicename);
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
            Log.d("WDChat","Account added! ");
        }
        db.close();
        dbHelper.close();
    }

    public Boolean checkChat(String nickname)
    {
        Boolean check = false;
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
            Cursor cursor = db.query("MESSAGES", new String[]{"NICKNAME"}, null, null, null, null, null);
            //result = cursor.getString(0);

            if(cursor.moveToFirst()) {
                do {
                    String nick = cursor.getString(cursor.getColumnIndexOrThrow("NICKNAME"));
                    //String device_name = cursor.getString(cursor.getColumnIndexOrThrow("DEVICE_NAME"));
                    if (nickname.equals(nick)) {
                        check = true;
                        return check;
                    }
                } while (cursor.moveToNext());
            }
            /*if(cursor.moveToFirst()) {
                while (cursor.moveToNext()) {
                    String nick = cursor.getString(cursor.getColumnIndexOrThrow("NICKNAME"));
                    //String device_name = cursor.getString(cursor.getColumnIndexOrThrow("DEVICE_NAME"));
                    if(nickname.equals(nick))
                    {
                        check = true;
                        return check;
                    }
                }
            }*/
        }
        db.close();
        dbHelper.close();
        return check;
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
                do {
                    String nick = cursor.getString(cursor.getColumnIndexOrThrow("NICKNAME"));
                    String device_name = cursor.getString(cursor.getColumnIndexOrThrow("DEVICE_NAME"));
                    if (nickname.equals(nick) && device_name.equals(devicename)) {
                        check = true;
                        return check;
                    }
                } while (cursor.moveToNext());
            }
            /*if(cursor.moveToFirst()) {
                while (cursor.moveToNext()) {

                }
            }*/
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
                        Socket s = ss.accept();
                        Thread task1 = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                runServer();
                            }
                        });
                        task1.start();
                        while (true) {
                            //if(isAdmin) send(s.getRemoteSocketAddress().toString());
                            //mSockets.add(s);
                            //if(isAdmin) sendInfoToClient();
                            //mIPs.add(s.getInetAddress().getHostAddress());
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
            //runServer();
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
