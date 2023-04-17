package com.qlstudio.lite_kagg886.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kagg886.jxw_collector.protocol.beans.ClassTable;
import com.qlstudio.lite_kagg886.R;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.qlstudio.lite_kagg886.adapter
 * @className: ClassTableAdapter
 * @author: kagg886
 * @description: 装载课程表条目的适配器，横向装配
 * @date: 2023/4/16 14:49
 * @version: 1.0
 */
public class ClassTableAdapter extends RecyclerView.Adapter<ClassTableAdapter.TableUnit> {

    private final List<ClassTable.ClassUnit> list = new ArrayList<ClassTable.ClassUnit>() {
        @Override
        public boolean add(ClassTable.ClassUnit classUnit) {
            boolean a = super.add(classUnit);
            notifyItemInserted(size());
            return a;
        }
    };

    public List<ClassTable.ClassUnit> getList() {
        return list;
    }

    @NonNull
    @NotNull
    @Override
    public TableUnit onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new TableUnit(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_classunit, null));
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull TableUnit holder, int position) {
        //0 1 2  3  4  5  6  7
        //8 9 10 11 12 13 14 15
        //a mod 8 = 0的是头元素，要丢入时间View
        ClassTable.ClassUnit u = list.get(position);
        if (position >= 1 && position <= 7) {
            holder.name.setText("星期" + position);
            return;
        }
        if (position % 8 == 0) {
            int k = (position / 8);
            holder.name.setText("第" + k + "节");
            switch (k) {
                case 1: //1-2
                    holder.room.setText("8:00-8:45\n\n8:55-9:40");
                    break;
                case 2://3-4
                    holder.room.setText("10:00-10:45\n\n10:55-11:40");
                    break;
                case 3://5-6
                    holder.room.setText("13:00-13:45\n\n13:55-14:40");
                    break;
                case 4://7-8
                    holder.room.setText("14:50-15:35\n\n15:45-16:30");
                    break;
                case 5://9-10
                    holder.room.setText("16:40-17:25\n\n17:35-18:20");
                    break;
                case 6://11-12
                    holder.room.setText("19:00-19:45\n\n19:55-20:30");
                    break;
            }
            return;
        }
        if (u == ClassTable.ClassUnit.EMPTY) {
            holder.name.setText(""); //得加个占位，不然有课程表错位bug
            holder.room.setText("");
            return;
        }
        holder.name.setText(u.getName());
        holder.room.setText(u.getRoom());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class TableUnit extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView room;

        public TableUnit(@NonNull @NotNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.adapter_classunit_class);
            room = itemView.findViewById(R.id.adapter_classunit_room);
        }
    }
}
