package com.qlstudio.lite_kagg886.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.qlstudio.lite_kagg886.adapter
 * @className: ContentPagerAdapter
 * @author: kagg886
 * @description: ViewPager2的适配器
 * @date: 2023/4/16 14:22
 * @version: 1.0
 */
public class ContentPagerAdapter extends FragmentStateAdapter {
    private final List<Fragment> data = new ArrayList<Fragment>() {
        @Override
        public boolean add(Fragment fragment) {
            boolean a = super.add(fragment);
            notifyItemInserted(size());
            return a;
        }
    };

    public ContentPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public List<Fragment> getData() {
        return data;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return data.get(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}