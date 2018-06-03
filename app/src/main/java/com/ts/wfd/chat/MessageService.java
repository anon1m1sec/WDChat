package com.ts.wfd.chat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.StrictMode;

public class MessageService extends Service {
    public MessageService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Boolean isAdmin = false;
        if(intent != null) {
            //intent.getBooleanExtra("isAdmin", isAdmin);
            isAdmin = intent.getBooleanExtra("isAdmin",false);
            Device device = new Device(intent.getStringExtra("name"), intent.getStringExtra("address"), isAdmin, this);
            SingletClass.setDevice(device);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
