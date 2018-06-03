package com.ts.wfd.chat;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class DialogFragment extends Fragment {

    private EditText mEditText;
    private Button mSendButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_activity,container,false);
        mEditText = (EditText) v.findViewById(R.id.text_message);
        mSendButton = (Button) v.findViewById(R.id.send_button);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mEditText.getText().toString();
                if(text.length() > 0)
                {
                    Device device = SingletClass.getDevice();
                    if(device != null)
                    {
                        device.SendMessage(SingletClass.getClickableChat(),text);
                        addMessageToDB(SingletClass.getClickableChat(),text);
                    }
                }
            }
        });
        return v;
    }

    private void addMessageToDB(String nick,String message)
    {
        Database dbHelper = new Database(getContext());
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLiteException ex) {
            ex.printStackTrace();
        }
        if (db != null) {
            ContentValues values = new ContentValues();
            values.put("NICKNAME", nick);
            values.put("MESSAGE", message);
            db.insert("MESSAGES", null, values);
        }
        db.close();
        dbHelper.close();
        SingletClass.getDialogMessageFragment().updateUI2(nick);
    }
}
