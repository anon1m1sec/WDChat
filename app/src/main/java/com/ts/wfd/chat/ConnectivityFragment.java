package com.ts.wfd.chat;

import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ConnectivityFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //return super.onCreateView(inflater, container, savedInstanceState);
        SingletClass.setConnectivityFragment(this);
        Log.d("WDDebug","start");
        View view = inflater.inflate(R.layout.list_item,container,false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        return view;
    }

    private class ListHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView mTitleTextView;
        private TextView mStatusTextView;

        public ListHolder(LayoutInflater inflater, ViewGroup parent)
        {
            super(inflater.inflate(R.layout.connectivity_list_item,parent,false));
            mTitleTextView = (TextView)itemView.findViewById(R.id.device_name);
            mStatusTextView = (TextView) itemView.findViewById(R.id.device_status);
            itemView.setOnClickListener(this);
        }

        public void bind(String text,String status)
        {
            //text = "test";
            mTitleTextView.setText(text);
            mStatusTextView.setText(status);
        }

        @Override
        public void onClick(View v) {
            //Toast.makeText(getActivity(),mTitleTextView.getText() + " clicked!", Toast.LENGTH_SHORT).show();
            Connectivity conn = SingletClass.getConnectivity();
            conn.updateInfoState((String)mTitleTextView.getText(),(String)mStatusTextView.getText());
            Log.d("WDDebug","ConnectivityFragment clicked");
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
        public void onBindViewHolder(ListHolder holder, int position, List<Object> payloads) {
            //super.onBindViewHolder(holder, position, payloads);
            String text = mList.get(position);
            String status = "Unknown";
            Connectivity conn = SingletClass.getConnectivity();
            //WifiP2pDevice.
            int st = conn.getStatus(text);
            Log.d("WDDebug", "Returned status - " + String.valueOf(st));
            //WifiP2pDevice.
            if(st == WifiP2pDevice.AVAILABLE) status = "Available";
            if(st == WifiP2pDevice.CONNECTED) status = "Connected";
            if(st == WifiP2pDevice.INVITED) status = "Invited";
            if(st == WifiP2pDevice.FAILED) status = "Failed";
            if(st == WifiP2pDevice.UNAVAILABLE) status = "Unavailable";
            Log.d("WDDebug", status);
            //conn.get
            holder.bind(text,status);
        }

        @Override
        public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ListHolder(layoutInflater,parent);
        }

        @Override
        public void onBindViewHolder(ListHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    private void updateUI()
    {
        List<String> list = new ArrayList<String>();
        //for(int i = 0; i<100;i++) list.add(String.valueOf(i));
        mAdapter = new ListAdapter(list);
        mRecyclerView.setAdapter(mAdapter);
        //mAdapter.notifyDataSetChanged();
    }

    public void updateUI2(List<String> list)
    {
        mAdapter.mList.clear();
        for(int i=0;i<list.size();i++) mAdapter.mList.add(list.get(i));
        mAdapter.notifyDataSetChanged();
    }
}
