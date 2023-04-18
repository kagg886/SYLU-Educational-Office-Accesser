package com.qlstudio.lite_kagg886.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.fastjson.JSON;
import com.kagg886.jxw_collector.protocol.beans.ClassTable;
import com.qlstudio.lite_kagg886.GlobalApplication;
import com.qlstudio.lite_kagg886.R;
import com.qlstudio.lite_kagg886.adapter.ClassTableAdapter;
import com.qlstudio.lite_kagg886.widget.GridItemDecoration;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.qlstudio.lite_kagg886.fragment
 * @className: ClassPerWeekFragment
 * @author: kagg886
 * @description: 每周课表显示页面
 * @date: 2023/4/16 14:28
 * @version: 1.0
 */
public class ClassPerWeekFragment extends Fragment {

    private ClassTable perWeek; //每周的课表对象
    private RecyclerView contain;
    private ClassTableAdapter adapter;

    private LocalDate startTime;

    public ClassPerWeekFragment() {
        //必须提供此构造函数，否则旋转屏幕时Activity重载会导致Fragment不能被正确初始化
    }

    public ClassPerWeekFragment(ClassTable t, LocalDate startTime) {
        this.perWeek = t;
        this.startTime = startTime;
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("list", JSON.toJSONString(perWeek));
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            this.perWeek = JSON.parseObject(savedInstanceState.getString("list"), ClassTable.class);
        }
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.fragment_classperweek, null);
        contain = v.findViewById(R.id.fragment_classperweek_container);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 8); //一周七天，外加一个显示第几节课的View，所以是8

        contain.setLayoutManager(manager);
        if (GlobalApplication.getApplicationNoStatic().getPreferences().getBoolean("setting_show_line", false)) {
            contain.addItemDecoration(new GridItemDecoration(GridLayoutManager.VERTICAL));
        }

        adapter = new ClassTableAdapter();
        adapter.setDate(startTime);
        for (int i = 0; i <= 7; i++) {
            adapter.getList().add(ClassTable.ClassUnit.EMPTY); //显示星期几用的
        }
        for (int i = 1; i <= 11; i += 2) { //1-2 3-4 5-6 7-8 9-10 11-13
            @SuppressLint("DefaultLocale")
            ClassTable a = perWeek.queryClassByLesson(String.format("%d-%d", i, i + 1));
            adapter.getList().add(ClassTable.ClassUnit.EMPTY); //占位，保证一排有八个
            for (int j = 1; j <= 7; j++) { //礼拜一到礼拜七
                ClassTable b = a.queryClassByDay(j);
                if (b.size() == 0) { //这节没课
                    adapter.getList().add(ClassTable.ClassUnit.EMPTY);
                    continue;
                }
                adapter.getList().add(b.get(0));
            }
        }
        contain.setAdapter(adapter);
        return v;
    }
}
