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
import androidx.recyclerview.widget.RecyclerView;
import com.kagg886.jxw_collector.protocol.SyluSession;
import com.kagg886.jxw_collector.protocol.beans.ExamResult;
import com.qlstudio.lite_kagg886.GlobalApplication;
import com.qlstudio.lite_kagg886.R;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

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

    private RecyclerView view;

    private Handler dialogController = new Handler(Looper.getMainLooper()) {
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
            }
        }
    };
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
        view = v.findViewById(R.id.fragment_exam_container);
        new Thread(() -> {
            SyluSession session = GlobalApplication.getApplicationNoStatic().getSession();

            //设置默认UI
            result = session.getExamResult();
            new Handler(Looper.getMainLooper()).post(() -> {

                //选定选择器
                String[] yearArr = result.getYears().keySet().toArray(new String[0]);
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
            List<ExamResult.ExamInfo> info = result.queryResultByYearAndTerm(
                    choose_year.getSelectedItem().toString(),
                    choose_term.getSelectedItem().toString()
            );
            updateUI(info);
            dialogController.sendEmptyMessage(1);
            isOnSelecting = false;
        }).start();
    }

    private void updateUI(List<ExamResult.ExamInfo> info) {

        new Handler(Looper.getMainLooper()).post(() -> {
            //TODO 在此处更新RecyclerView中的内容逻辑
            Toast.makeText(getActivity(), "共找到:" + info.size() + "条信息", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
