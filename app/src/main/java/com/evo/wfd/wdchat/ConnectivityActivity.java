package com.evo.wfd.wdchat;

import android.support.v4.app.Fragment;

public class ConnectivityActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
       //return new LoginFragment();
        //return new ConnectivityFragment();
        Connectivity act = new Connectivity();
        return act;
    }
}
