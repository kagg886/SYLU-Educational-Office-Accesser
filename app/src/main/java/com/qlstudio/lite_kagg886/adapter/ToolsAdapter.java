package com.qlstudio.lite_kagg886.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.qlstudio.lite_kagg886.GlobalApplication;
import com.qlstudio.lite_kagg886.R;
import com.qlstudio.lite_kagg886.fragment.tools.AbstractDialogFragments;
import com.qlstudio.lite_kagg886.fragment.tools.impl.PhotoDeSigner;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.qlstudio.lite_kagg886.adapter
 * @className: ClassTableAdapter
 * @author: kagg886
 * @description: 装载课程表条目的适配器，横向装配
 * @date: 2023/5/15 10:43
 * @version: 1.0
 */
public class ToolsAdapter extends RecyclerView.Adapter<ToolsAdapter.TableUnit> {

    private static final List<AbstractDialogFragments> list;


    static {
        list = new ArrayList<>();
        list.add(new PhotoDeSigner(GlobalApplication.getCurrentActivity()));
    }

    @NonNull
    @NotNull
    @Override
    public TableUnit onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new TableUnit(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_tools, null));
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull TableUnit holder, int position) {
        AbstractDialogFragments u = list.get(position);
        holder.img.setImageResource(u.getId());
        holder.txt.setText(u.getText());
        holder.itemView.setOnClickListener(u.getListener());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class TableUnit extends RecyclerView.ViewHolder {
        public final ImageView img;
        public final TextView txt;


        public TableUnit(@NonNull @NotNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.adapter_tools_img);
            txt = itemView.findViewById(R.id.adapter_tools_details);
        }
    }
}

