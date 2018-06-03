package com.ts.wfd.chat;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

public class ChatActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        //return new LoginFragment();
        //return new ConnectivityFragment();
        //SingletClass.setChat(this);
        return new ChatFragment();
    }
}
