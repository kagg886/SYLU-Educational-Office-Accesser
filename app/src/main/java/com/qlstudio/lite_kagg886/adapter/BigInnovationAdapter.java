package com.qlstudio.lite_kagg886.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import com.kagg886.jxw_collector.protocol.beans.BigInnovation;
import com.qlstudio.lite_kagg886.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.qlstudio.lite_kagg886.adapter
 * @className: BigInnovationAdapter
 * @author: kagg886
 * @description: TODO
 * @date: 2023/4/21 17:48
 * @version: 1.0
 */
public class BigInnovationAdapter extends BaseExpandableListAdapter {

    public HashMap<String, BigInnovation> map;

    public BigInnovationAdapter(HashMap<String, BigInnovation> map) {
        this.map = map;
    }

    @Override
    public int getGroupCount() {
        return map.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return ((BigInnovation) getGroup(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return map.get(new ArrayList<>(map.keySet()).get(groupPosition));
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return ((BigInnovation) getGroup(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return getGroup(groupPosition).hashCode();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return getChild(groupPosition, childPosition).hashCode();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        TextView view = new TextView(parent.getContext());
        view.setTextColor(Color.BLACK);
        view.setTextSize(20);
        view.setText(new ArrayList<>(map.keySet()).get(groupPosition));
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_bi_score, null);


        BigInnovation.Item item = (BigInnovation.Item) getChild(groupPosition, childPosition);
        ((TextView) view.findViewById(R.id.adapter_bi_name)).setText(item.getName());
        ((TextView) view.findViewById(R.id.adapter_bi_score)).setText(String.format("%.1f", item.getScore()));
        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
