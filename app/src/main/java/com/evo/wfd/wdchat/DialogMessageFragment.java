package com.evo.wfd.wdchat;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DialogMessageFragment extends Fragment{
    private RecyclerView mRecyclerView;
    private ListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        SingletClass.setDialogMessageFragment(this);
        View view = inflater.inflate(R.layout.list_item,container,false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //view.getLayoutParams();
        updateUI();
        return view;
    }

    private class ListHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView mMessageTextView;
        //private TextView mMessageTextView;


        public ListHolder(LayoutInflater inflater, ViewGroup parent)
        {
            super(inflater.inflate(R.layout.dialog_list_item,parent,false));
            mMessageTextView = (TextView) itemView.findViewById(R.id.text_message);
            /*Random rnd = new Random(System.currentTimeMillis());
            // Получаем случайное число в диапазоне от min до max (включительно)
            int min = -100;
            int max = 100;
            int number = min- + rnd.nextInt(max - min + 1);
            Log.d("WDDebug", "RANDOM: " + number);
            if(number<-200)
            mMessageTextView.setGravity(Gravity.RIGHT);
            else mMessageTextView.setGravity(Gravity.LEFT);*/
            /*LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200);
            param.gravity = Gravity.RIGHT;
            mMessageTextView.setLayoutParams(param);*/
            //mMessageTextView = (TextView) itemView.findViewById(R.id.chat_message);
            itemView.setOnClickListener(this);
        }

        public void bind(String text)
        {
            //text = "test";
            //mTitleTextView.setText(text);
            mMessageTextView.setText(text);
        }

        @Override
        public void onClick(View v) {
            //Toast.makeText(getActivity(),mTitleTextView.getText() + " clicked!", Toast.LENGTH_SHORT).show();
            //ChatActivity chat = SingletClass.getChat();
            //chat.updateUI((String)mTitleTextView.getText());
            //updateUI2((String)mTitleTextView.getText());
        }
    }

    private class ListAdapter extends RecyclerView.Adapter<ListHolder>
    {
        private List<String> mList;

        public ListAdapter(List<String> list)
        {
            mList = list;
        }

        @Override
        public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ListHolder(layoutInflater,parent);
        }

        @Override
        public void onBindViewHolder(ListHolder holder, int position) {
            String text = mList.get(position);
            holder.bind(text);
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    private void updateUI()
    {
        /*List<String> list = new ArrayList<String>();
        //for(int i = 0; i<100;i++) list.add(String.valueOf(i));
        mAdapter = new ListAdapter(list);
        mRecyclerView.setAdapter(mAdapter);
        if(mRecyclerView.getAdapter().getItemCount() > 0) mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount()-1);
        mAdapter.notifyDataSetChanged();*/
        updateUI2(SingletClass.getClickableChat());
    }

    public void updateUI2(String nick)
    {
        if(mAdapter != null) mAdapter.mList.clear();
        ArrayList<String> list = new ArrayList<>();
        Database dbHelper = new Database(getContext());
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
            Cursor cursor = db.query("MESSAGES", new String[]{"NICKNAME","MESSAGE"}, "NICKNAME = \"" + nick + "\"", null, null, null, null);
            //result = cursor.getString(0);
            if(cursor.moveToFirst()) {
                do {
                    String message = cursor.getString(cursor.getColumnIndexOrThrow("MESSAGE"));
                    list.add(message);
                } while (cursor.moveToNext());
            }
        }
        db.close();
        dbHelper.close();
        mAdapter = new ListAdapter(list);
        mRecyclerView.setAdapter(mAdapter);
        if(mRecyclerView.getAdapter().getItemCount() > 0) mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount()-1);
        mAdapter.notifyDataSetChanged();
    }

}
