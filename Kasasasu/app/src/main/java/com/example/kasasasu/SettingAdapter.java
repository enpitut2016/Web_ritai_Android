package com.example.kasasasu;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by 真史 on 2016/08/27.
 */
public class SettingAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<Setting> settings;

    public SettingAdapter(Context context, ArrayList<Setting> settings){
        this.context = context;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.settings = settings;
    }

    public void add(Setting setting) {
        boolean isContained = false;
        for (Setting s: settings)
            if (s.getName().equals(setting.getName())) isContained = true;

        if (! isContained) settings.add(setting);
    }

    public void delete(String itemName) {
        for (Setting s: settings)
            if (s.getName().equals(itemName)) settings.remove(s);
    }

    @Override
    public int getCount(){
        return settings.size();
    }

    @Override
    public Object getItem(int position){
        return settings.get(position);
    }

    @Override
    public long getItemId(int position){
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        convertView = layoutInflater.inflate(R.layout.setting_listview, parent, false);
        TextView nameView = (TextView)convertView.findViewById(R.id.itemNameTextView);
        nameView.setText(settings.get(position).getName());

        TextView hobbyView = (TextView)convertView.findViewById(R.id.itemContentTextView);
        hobbyView.setText(settings.get(position).getContent());

        return convertView;
    }
}