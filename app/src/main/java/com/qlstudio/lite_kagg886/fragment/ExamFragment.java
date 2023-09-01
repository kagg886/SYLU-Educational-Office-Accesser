package com.qlstudio.lite_kagg886.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.kagg886.jxw_collector.exceptions.OfflineException;
import com.kagg886.jxw_collector.protocol.SyluSession;
import com.kagg886.jxw_collector.protocol.beans.ExamResult;
import com.qlstudio.lite_kagg886.GlobalApplication;
import com.qlstudio.lite_kagg886.R;
import com.qlstudio.lite_kagg886.adapter.ExamInfoAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.qlstudio.lite_kagg886.fragment
 * @className: ExamFragment
 * @author: kagg886
 * @description: 考试信息
 * @date: 2023/4/14 19:53
 * @version: 1.0
 */
public class ExamFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private AlertDialog dialog;

    private ExamResult result;

    private Spinner choose_year, choose_term;

    private final Handler dialogController = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0:
                    dialog.show();
                    break;
                case 1:
                    dialog.cancel();
                    dialog.dismiss();
                    break;
                case 2:
                    dialog.cancel();
                    dialog.dismiss();
                    Toast.makeText(getActivity(), "登录状态已过期，请重新登录", Toast.LENGTH_LONG).show();
                    GlobalApplication.getApplicationNoStatic().logout();
                    break;
            }
        }
    };
    private RecyclerView container;
    private ExamInfoAdapter adapter;
    private boolean isOnSelecting = false;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        initDialog();
        dialogController.sendEmptyMessage(0);
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_exam, null);
        choose_year = v.findViewById(R.id.fragment_exam_chooseYear);
        choose_term = v.findViewById(R.id.fragment_exam_chooseTerm);

        this.container = v.findViewById(R.id.fragment_exam_container);
        new Thread(() -> {
            SyluSession session = GlobalApplication.getApplicationNoStatic().getSession();

            //设置默认UI
            try {
                result = session.getExamResult();
            } catch (OfflineException e) {
                dialogController.sendEmptyMessage(2);
                return;
            }
            new Handler(Looper.getMainLooper()).post(() -> {
                adapter = new ExamInfoAdapter(result);
                this.container.setAdapter(adapter);
                this.container.setLayoutManager(new LinearLayoutManager(getContext()));
                this.container.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), DividerItemDecoration.VERTICAL));

                //选定选择器
                String[] yearArr = result.getYears().keySet().stream().sorted(Comparator.comparingInt(k -> {
                    try {
                        return -Integer.parseInt(k.split("-")[0]);
                    } catch (Exception e) {
                        return -114514;
                    }
                })).toArray(String[]::new);
                choose_year.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, yearArr));
                for (int i = 0; i < yearArr.length; i++) {
                    if (yearArr[i].equals(result.getDefaultYears())) {
                        choose_year.setSelection(i);
                    }
                }

                String[] teamArr = result.getTeamVal().keySet().toArray(new String[0]);
                choose_term.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, teamArr));

                for (int i = 0; i < teamArr.length; i++) {
                    if (teamArr[i].equals(result.getDefaultTeamVal())) {
                        choose_term.setSelection(i);
                    }
                }
            });
        }).start();
        choose_year.setOnItemSelectedListener(this);
        choose_term.setOnItemSelectedListener(this);
        return v;
    }

    private void initDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setTitle("加载中...");
        builder.setView(new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleHorizontal));
        builder.setCancelable(false);
        dialog = builder.create();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (isOnSelecting) {
            return;
        }
        isOnSelecting = true;
        dialogController.sendEmptyMessage(0);
        new Thread(() -> {
            List<ExamResult.ExamInfo> info;
            try {
                info = result.queryResultByYearAndTerm(
                        choose_year.getSelectedItem().toString(),
                        choose_term.getSelectedItem().toString()
                );
                if (GlobalApplication.getApplicationNoStatic().getPreferences().getBoolean("setting_nullfail", false)) {
                    info = info.stream().filter((v) -> v.getStatus() != ExamResult.Status.FUCK_TEACHER).collect(Collectors.toList());
                }
            } catch (OfflineException e) {
                dialogController.sendEmptyMessage(2);
                return;
            }
            updateUI(info);
            dialogController.sendEmptyMessage(1);
            isOnSelecting = false;
        }).start();
    }

    private void updateUI(List<ExamResult.ExamInfo> info) {
        new Handler(Looper.getMainLooper()).post(() -> {
            adapter.getResults().clear();
            info.forEach((a) -> {
                adapter.getResults().add(a);
            });
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
