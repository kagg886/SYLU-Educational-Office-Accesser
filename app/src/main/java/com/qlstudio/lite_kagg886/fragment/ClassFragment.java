package com.qlstudio.lite_kagg886.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.kagg886.jxw_collector.protocol.beans.ClassTable;
import com.kagg886.jxw_collector.protocol.beans.Schedule;
import com.kagg886.jxw_collector.protocol.beans.SchoolCalendar;
import com.qlstudio.lite_kagg886.GlobalApplication;
import com.qlstudio.lite_kagg886.R;
import com.qlstudio.lite_kagg886.adapter.ContentPagerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.qlstudio.lite_kagg886.fragment
 * @className: ClassFragment
 * @author: kagg886
 * @description: 课程表的布局
 * @date: 2023/4/16 14:20
 * @version: 1.0
 */
public class ClassFragment extends Fragment {

    private ViewPager2 pager2; //每周的课程表Fragment用此实现
    private ContentPagerAdapter adapter;

    private TextView textView;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.fragment_classinfo, null);
        pager2 = v.findViewById(R.id.fragment_classinfo_container);
        textView = v.findViewById(R.id.fragment_classinfo_counter);
        adapter = new ContentPagerAdapter(getActivity());
        pager2.setAdapter(adapter);
        pager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onPageSelected(int position) {
                textView.setText("第" + (position + 1) + "周");
            }
        });

        new Thread(() -> {
            Schedule schedule = GlobalApplication.getApplicationNoStatic().getSession().getSchedule();
            ClassTable table = schedule.queryClassByYearAndTerm(schedule.getDefaultYears(), schedule.getDefaultTeamVal());
            int a = 0;
            ClassTable perWeek;
            while ((perWeek = table.queryClassByWeek(a + 1)).size() != 0) {
                final ClassTable perWeek0 = perWeek;
                new Handler(Looper.getMainLooper()).post(() ->
                        adapter.getData().add(new ClassPerWeekFragment(perWeek0))
                );
                a++;
//                break; //仅仅拿第一周做测试，
            }
            //设置到正确的周数
            SchoolCalendar calendar = GlobalApplication.getApplicationNoStatic().getSession().getSchoolCalendar();


            new Handler(Looper.getMainLooper()).post(() -> {
                pager2.setCurrentItem(calendar.getWeekFromStart() - 1, false); //防止一瞬间滑动n次造成的卡顿
            });
        }).start();
        return v;
    }
}
