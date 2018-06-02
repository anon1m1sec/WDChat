package com.evo.wfd.wdchat;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

public class DialogActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        //return new LoginFragment();
        //return new ConnectivityFragment();
        //SingletClass.setChat(this);
        return new DialogFragment();
    }
}
