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
        if (position >= 0 && position <= 7) {
            if (position == 0) {
                return;
            }
            holder.name.setText("星期" + position);
        }
        if (position % 8 == 0) {
            holder.name.setText("第" + (position / 8) + "节");
            holder.room.setText("10:00");
            return;
        }
        if (u == ClassTable.ClassUnit.EMPTY) {
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
