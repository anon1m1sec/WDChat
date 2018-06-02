package com.evo.wfd.wdchat;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class DialogMessageFragment extends Fragment{
    private RecyclerView mRecyclerView;
    //private ListAdapter mAdapter;

    private class ListHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        //private TextView mTitleTextView;
        //private TextView mMessageTextView;


        public ListHolder(LayoutInflater inflater, ViewGroup parent)
        {
            super(inflater.inflate(R.layout.dialog_list_item,parent,false));
            //mTitleTextView = (TextView) itemView.findViewById(R.id.chat_nickname);
            //mMessageTextView = (TextView) itemView.findViewById(R.id.chat_message);
            itemView.setOnClickListener(this);
        }

        public void bind(String text,String status)
        {
            //text = "test";
            //mTitleTextView.setText(text);
            //mMessageTextView.setText(status);
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
            holder.bind(text,"message");
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }
}
