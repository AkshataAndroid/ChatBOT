package com.unfyd.unfydChatBot.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.unfyd.unfydChatBot.Activity.MainFragment;
import com.unfyd.unfydChatBot.Classes.Message;
import com.unfyd.unfydChatBot.R;

import java.util.List;

public class CustomAdapter extends BaseAdapter   {
    Context context;
    List<String> mListOptions;
    List<Message> mMessageLsit;
    LayoutInflater inflter;
    MainFragment mainFragment;
    public CustomAdapter(Context applicationContext, List<String> mList, List<Message> messageList,MainFragment mFragment) {
        this.context = applicationContext;
        this.mListOptions=mList;
        mainFragment=mFragment;
        mMessageLsit=messageList;
        inflter = (LayoutInflater.from(applicationContext));
    }
    @Override
    public int getCount() {
        return mListOptions.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = inflter.inflate(R.layout.item_options, null);
        Button btnOPtion=(Button) convertView.findViewById(R.id.txtViewOptionName);
        btnOPtion.setText(mListOptions.get(position).toString());
        btnOPtion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Log.d("BUTTONS","CLICKED"+mListOptions.get(position).toString());
                mainFragment.addMessage(mMessageLsit.get(position).getUsername(),mListOptions.get(position).toString(),"message",null,null,null,null,null,null,null,null,null);

            }
        });
        return  convertView;
    }


}
