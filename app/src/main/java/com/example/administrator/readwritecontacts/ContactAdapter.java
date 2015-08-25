package com.example.administrator.readwritecontacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/8/25.
 */
public class ContactAdapter extends ArrayAdapter<Contact> {

    private int resourceId;

    public ContactAdapter(Context context, int resource, List<Contact> objects) {
        super(context, resource, objects);
        resourceId=resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        Contact contact=getItem(position);
        ViewHolder viewHolder=null;
        if(convertView==null){
            view= LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewHolder=new ViewHolder();
            viewHolder.name= (TextView) view.findViewById(R.id.text_name);
            viewHolder.number= (TextView) view.findViewById(R.id.text_number);
            viewHolder.checkBox= (CheckBox) view.findViewById(R.id.checkbox);
            view.setTag(viewHolder);
        }else{
            view=convertView;
            viewHolder= (ViewHolder) view.getTag();
        }
        if(contact.isChecked)viewHolder.checkBox.setChecked(true);
        else viewHolder.checkBox.setChecked(false);
        viewHolder.name.setText(contact.getName());
        viewHolder.number.setText(contact.getNumber());
        return view;
    }

    class ViewHolder{
        TextView name;
        TextView number;
        CheckBox checkBox;
    }
}
