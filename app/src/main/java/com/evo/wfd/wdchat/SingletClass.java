package com.evo.wfd.wdchat;

import android.content.Context;

public class SingletClass {
    private static SingletClass sSClass;
    private static ConnectivityFragment fragment;
    private static Connectivity conn;
    private static Device device;
    private static String deviceName;
    private static ChatFragment chatFragment;

    public static SingletClass get(Context context)
    {
        if(sSClass == null)
        {
            sSClass = new SingletClass(context);
        }
        return sSClass;
    }

    private SingletClass(Context context)
    {

    }

    public static  ConnectivityFragment getConnectivityFragment()
    {
        return fragment;
    }

    public static void setConnectivityFragment(ConnectivityFragment fm)
    {
        fragment = fm;
    }

    public static void setConnectivity(Connectivity con)
    {
        conn = con;
    }

    public static Connectivity getConnectivity()
    {
        return conn;
    }

    public static void setDevice(Device dev)
    {
        device = dev;
    }

    public static Device getDevice()
    {
        return device;
    }

    public static void setDeviceName(String name)
    {
        deviceName = name;
    }

    public static String getDeviceName()
    {
        return deviceName;
    }

    public static void setChatFragment(ChatFragment fr)
    {
        chatFragment = fr;
    }

    public static ChatFragment getChatFragment()
    {
        return chatFragment;
    }
}
