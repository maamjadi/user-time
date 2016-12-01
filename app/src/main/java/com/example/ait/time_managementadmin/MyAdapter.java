package com.example.ait.time_managementadmin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class MyAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private LayoutInflater inflater;
    private ArrayList<String> events = new ArrayList<>();

    //String header;
    //String event;
   /* "date|event"

    String[] x = events.get(position).split("|");
    x[0]
    x[1]*/

    public MyAdapter(Context context, ArrayList<String> events) {
        inflater = LayoutInflater.from(context);
        this.events = events;
        cleanEvents();
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_layout, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.event);
            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        String event = events.get(position).split("_")[0].split("-")[1];
        holder.text.setText(event);


        return convertView;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = inflater.inflate(R.layout.header, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.header);
            convertView.setTag(holder);

        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        //set header text as first char in name
        // for (position = 0; position < events.size(); ++position) {

        String header = events.get(position).split("_")[0].split("-")[0];
        holder.text.setText(header);

        // }
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        //return the first character of the country as ID because this is what headers are based upon
        return events.get(position).subSequence(0, 1).charAt(0);

    }

    class HeaderViewHolder {
        TextView text;
    }

    class ViewHolder {
        TextView text;
    }

    private void cleanEvents() {
        ArrayList<String> tempEvents = new ArrayList<String>();
        for (int i = 0, j = 1; i < events.size(); ++i, ++j) {

            if (Integer.parseInt(events.get(j).split("-")[0]) < Integer.parseInt(events.get(i).split("-")[0])) {
                String temp = events.get(i);
                events.add(i, events.get(j));
                events.add(j, temp);
            }
        }


    }

}

