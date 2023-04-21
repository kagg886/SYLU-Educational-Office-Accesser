package com.qlstudio.lite_kagg886.fragment;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.alibaba.fastjson.JSON;
import com.kagg886.jxw_collector.exceptions.OfflineException;
import com.kagg886.jxw_collector.protocol.beans.ClassTable;
import com.kagg886.jxw_collector.protocol.beans.Schedule;
import com.kagg886.jxw_collector.protocol.beans.SchoolCalendar;
import com.qlstudio.lite_kagg886.GlobalApplication;
import com.qlstudio.lite_kagg886.R;
import com.qlstudio.lite_kagg886.adapter.ContentPagerAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    private Spinner spinner;

    private boolean isDrag; //判断是否为用户滑动

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.fragment_classinfo, null);
        pager2 = v.findViewById(R.id.fragment_classinfo_container);
        spinner = v.findViewById(R.id.fragment_classinfo_counter);
        adapter = new ContentPagerAdapter(getChildFragmentManager(), getLifecycle());
        pager2.setAdapter(adapter);

        pager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onPageSelected(int position) {
                spinner.setSelection(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    isDrag = true;
                }
            }
        });

        new Thread(() -> {
            Schedule schedule;
            SchoolCalendar calendar;
            ClassTable table;

            SharedPreferences preferences = GlobalApplication.getApplicationNoStatic().getPreferences();
            long life = Long.parseLong(preferences.getString("setting_cache", "180"));
            if (preferences.getLong("cache_deadline_class", 0) - System.currentTimeMillis() <= 0) {
                try {
                    schedule = GlobalApplication.getApplicationNoStatic().getSession().getSchedule();
                    table = schedule.queryClassByYearAndTerm(schedule.getDefaultYears(), schedule.getDefaultTeamVal());
                    calendar = GlobalApplication.getApplicationNoStatic().getSession().getSchoolCalendar();
                } catch (OfflineException e) {
                    GlobalApplication.getApplicationNoStatic().logout();
                    return;
                }
                preferences.edit()
                        .putLong("cache_deadline_class", System.currentTimeMillis() + life * 60000)
                        .putString("cache_schedule", JSON.toJSONString(schedule))
                        .putString("cache_calendar", JSON.toJSONString(calendar))
                        .putString("cache_table", JSON.toJSONString(table))
                        .apply();

            } else {
                schedule = JSON.parseObject(preferences.getString("cache_schedule", null), Schedule.class);
                schedule.setSession(GlobalApplication.getApplicationNoStatic().getSession());

                calendar = JSON.parseObject(preferences.getString("cache_calendar", null), SchoolCalendar.class);
                schedule.setSession(GlobalApplication.getApplicationNoStatic().getSession());

                table = JSON.parseObject(preferences.getString("cache_table", null), ClassTable.class);
            }

            int a = 0;
            ClassTable perWeek;
            LocalDate date = calendar.getStart();
            while ((perWeek = table.queryClassByWeek(a + 1)).size() != 0) {
                final ClassTable perWeek0 = perWeek;
                final LocalDate date0 = date;
                new Handler(Looper.getMainLooper()).post(() -> {
                    adapter.getData().add(new ClassPerWeekFragment(perWeek0, date0));
                });
                a++;
                date = date.plusDays(7);
//                break; //仅仅拿第一周做测试，
            }

            //设置到正确的周数
            final int len = a;
            new Handler(Looper.getMainLooper()).post(() -> {
                List<String> titles = new ArrayList<>();
                for (int i = 0; i < len; i++) {
                    titles.add("第" + (i + 1) + "周");
                }
                spinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, titles.toArray()));
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (!isDrag) { //如果是代码操作则切换pager，否则由recycler自行完成
                            pager2.setCurrentItem(position, false);
                        }
                        isDrag = false;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                pager2.setCurrentItem(calendar.getWeekFromStart() - 1, false); //防止一瞬间滑动n次造成的卡顿
            });
        }).start();
        return v;
    }

}
