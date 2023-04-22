package com.qlstudio.lite_kagg886.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.kagg886.jxw_collector.protocol.SyluSession;
import com.kagg886.jxw_collector.protocol.beans.BigInnovation;
import com.qlstudio.lite_kagg886.GlobalApplication;
import com.qlstudio.lite_kagg886.R;
import com.qlstudio.lite_kagg886.adapter.BigInnovationAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.qlstudio.lite_kagg886.fragment
 * @className: BigInnovationFragment
 * @author: kagg886
 * @description: 查大创学分
 * @date: 2023/4/21 17:28
 * @version: 1.0
 */
public class BigInnovationFragment extends Fragment {

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.fragment_biginnovation, null);
        TextView count = v.findViewById(R.id.fragment_bi_allCount);
        ExpandableListView contain = v.findViewById(R.id.fragment_bi_container);

        int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        contain.setIndicatorBounds(width - 40, width - 10);
        new Thread(() -> {
            SyluSession session = GlobalApplication.getApplicationNoStatic().getSession();
            HashMap<String, BigInnovation> map = session.getBigInnovations();
            double sum = map.entrySet()
                    .stream()
                    .flatMap(entry -> entry.getValue().stream())
                    .mapToDouble(BigInnovation.Item::getScore)
                    .sum();

            Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                contain.setAdapter(new BigInnovationAdapter(map));
                count.setText("总大创学分:" + sum);
                for (int i = 0; i < contain.getExpandableListAdapter().getGroupCount(); i++) {
                    contain.expandGroup(i); //默认展开所有项
                }
            });
        }).start();
        return v;
    }
}
