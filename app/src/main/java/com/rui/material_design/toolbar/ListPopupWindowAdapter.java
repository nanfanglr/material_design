package com.rui.material_design.toolbar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rui.material_design.R;

import java.util.ArrayList;
import java.util.List;

public class ListPopupWindowAdapter extends BaseAdapter {
    private List<String> mArrayList;
    private Context mContext;
    public ListPopupWindowAdapter(List<String> list, Context context) {
        super();
        this.mArrayList = list;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        if (mArrayList == null) {
            return 0;
        } else {
            return this.mArrayList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (mArrayList == null) {
            return null;
        } else {
            return this.mArrayList.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.spinner_item_mutil, null, false);
            holder.itemTextView = (TextView) convertView.findViewById(R.id.textView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (this.mArrayList != null) {
            final String itemName = this.mArrayList.get(position);
            if (holder.itemTextView != null) {
                holder.itemTextView.setText(itemName);
            }
        }

        return convertView;

    }

    private class ViewHolder {
        TextView itemTextView;
    }

}

