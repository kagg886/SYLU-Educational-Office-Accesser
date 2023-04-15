package com.qlstudio.lite_kagg886.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.qlstudio.lite_kagg886.adapter
 * @className: TextViewAdapter
 * @author: kagg886
 * @description: 网格布局，内存TextView
 * @date: 2023/4/15 10:06
 * @version: 1.0
 */
public class TextViewAdapter extends RecyclerView.Adapter<TextViewAdapter.TextViewHolder> {

    private int size;
    private List<String> strings = new ArrayList<>();


    public TextViewAdapter(int size) {
        this.size = size;
    }

    public List<String> getStrings() {
        return strings;
    }


    @NonNull
    @NotNull
    @Override
    public TextViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        TextView v = new TextView(parent.getContext());
        v.setTextSize(size);
        return new TextViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull TextViewHolder holder, int position) {
        holder.txt.setText(strings.get(position));
    }

    @Override
    public int getItemCount() {
        return strings.size();
    }


    public static class TextViewHolder extends RecyclerView.ViewHolder {

        private TextView txt;

        public TextViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.txt = (TextView) itemView;
        }
    }
}
