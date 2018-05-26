package com.evo.wfd.wdchat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ListAdapter mAdapter;
    private Device device;
    //private ConnectivityFragment.ListAdapter mAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SingletClass.setChatFragment(this);
        View view = inflater.inflate(R.layout.list_item,container,false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //Toast.makeText(getActivity()," clicked!", Toast.LENGTH_SHORT).show();
        device = SingletClass.getDevice();
        String title = device.getNickName() + "(" + device.getDeviceName() + ")";
        updateUI();
        getActivity().setTitle(title);
        return view;
    }

    public void updateUI2(List<String> list)
    {
        mAdapter.mList.clear();
        for(int i=0;i<list.size();i++) mAdapter.mList.add(list.get(i));
        mAdapter.notifyDataSetChanged();
    }


    private void updateUI()
    {
        List<String> list = device.getListNickNames();
        //for(int i = 0; i<100;i++) list.add(String.valueOf(i));
        mAdapter = new ListAdapter(list);
        mRecyclerView.setAdapter(mAdapter);
        //mAdapter.notifyDataSetChanged();
    }

    private class ListHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView mTitleTextView;
        private TextView mMessageTextView;


        public ListHolder(LayoutInflater inflater, ViewGroup parent)
        {
            super(inflater.inflate(R.layout.chat_list_item,parent,false));
            mTitleTextView = (TextView) itemView.findViewById(R.id.chat_nickname);
            mMessageTextView = (TextView) itemView.findViewById(R.id.chat_message);
            itemView.setOnClickListener(this);
        }

        public void bind(String text,String status)
        {
            //text = "test";
            mTitleTextView.setText(text);
            mMessageTextView.setText(status);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(getActivity(),mTitleTextView.getText() + " clicked!", Toast.LENGTH_SHORT).show();
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
            holder.bind(text,"message");
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }
}
