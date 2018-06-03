package com.ts.wfd.chat;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginFragment extends Fragment {

    private EditText mLogin;
    private Button mLoginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onStop() {
    super.onStop();
    getActivity().finish();
  }


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.login_activity,container,false);
        getActivity().setTitle(R.string.login_title);
        mLogin = (EditText) v.findViewById(R.id.nickname);
        mLoginButton = (Button) v.findViewById(R.id.login_button);

        if(mLoginButton != null) {
            mLoginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mLogin.getText().length()>0) {
                        String str = String.valueOf(mLogin.getText());
                        writeNewAccount(str);
                        Intent intent = new Intent(getActivity(), ConnectivityActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(getActivity(),"Please choose your login!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        return v;
    }

    private void writeNewAccount(String nickname)
    {
        Database dbHelper = new Database(this.getContext());
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
            db.insert("ACCOUNTS",null,values);
        }
        db.close();
        dbHelper.close();
    }

}
