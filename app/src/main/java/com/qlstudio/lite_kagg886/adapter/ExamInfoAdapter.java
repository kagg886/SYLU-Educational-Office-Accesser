package com.qlstudio.lite_kagg886.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.kagg886.jxw_collector.exceptions.OfflineException;
import com.kagg886.jxw_collector.protocol.beans.ExamResult;
import com.qlstudio.lite_kagg886.GlobalApplication;
import com.qlstudio.lite_kagg886.R;
import com.qlstudio.lite_kagg886.widget.GridItemDecoration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.qlstudio.lite_kagg886.adapter
 * @className: ExamResultAdapter
 * @author: kagg886
 * @description: 存储考试信息的适配器
 * @date: 2023/4/15 7:58
 * @version: 1.0
 */
public class ExamInfoAdapter extends RecyclerView.Adapter<ExamInfoAdapter.ExamInfoHolder> {

    private final ExamResult result;

    public ExamInfoAdapter(ExamResult result) {
        this.result = result;
    }

    public List<ExamResult.ExamInfo> getResults() {
        return results;
    }

    private void showDetailDialog(Context context, String name, List<List<String>> data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("课程: '" + name + "' 详细信息");

        RecyclerView view = new RecyclerView(context);
        GridLayoutManager layoutManager = new GridLayoutManager(context, 3);
        view.setLayoutManager(layoutManager);
        view.addItemDecoration(new GridItemDecoration(GridLayoutManager.VERTICAL));

        TextViewAdapter adapter = new TextViewAdapter(17);

        adapter.getStrings().addAll(Arrays.asList("成绩分项", "成绩分项比例", "成绩"));
        data.forEach((line) -> {
            line.forEach((col) -> {
                adapter.getStrings().add(col);
            });
        });
        view.setAdapter(adapter);

        builder.setView(view);
        builder.create().show();
    }    private final List<ExamResult.ExamInfo> results = new ArrayList<ExamResult.ExamInfo>() {
        @Override
        public boolean add(ExamResult.ExamInfo examInfo) {
            boolean a = super.add(examInfo);
            notifyItemInserted(results.size());
            return a;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void clear() {
            super.clear();
            notifyDataSetChanged();
        }
    };



    @Override
    public void onBindViewHolder(@NonNull @NotNull ExamInfoHolder holder, int position) {
        ExamResult.ExamInfo info = results.get(position);
        holder.root.setOnClickListener((v -> {
            new Thread(() -> {
                try {
                    List<List<String>> data = result.queryDetailsByExamInfo(info);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        showDetailDialog(holder.root.getContext(), info.getName(), data);
                    });
                } catch (OfflineException ignored) {
                    Toast.makeText(v.getContext(), "登录状态已过期，请重新登录", Toast.LENGTH_LONG).show();
                    GlobalApplication.getApplicationNoStatic().logout();
                }
            }).start();
        }));
        holder.className.setText(info.getName());
        holder.teacher.setText(info.getTeacher());
        holder.score.setText(info.getCredit());
        holder.gradePoint.setText(info.getGradePoint());
        holder.scTimeGr.setText(info.getGpTimesCr());

        switch (info.getStatus()) {
            case SUCCESS:
                holder.status.setImageResource(R.drawable.ic_examstatus_success);
                break;
            case SUCCESS_RE:
                holder.status.setImageResource(R.drawable.ic_examstatus_success_re);
                break;
            case FUCK_TEACHER:
                holder.status.setImageResource(R.drawable.ic_examstatus_fuckteacher);
                break;
        }
    }

    @NonNull
    @NotNull
    @Override
    public ExamInfoHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ExamInfoHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_exam, null));
    }

    public static class ExamInfoHolder extends RecyclerView.ViewHolder {

        private final View root;
        private final ImageView status;
        private final TextView teacher, className, score, gradePoint, scTimeGr;

        public ExamInfoHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.root = itemView;
            status = itemView.findViewById(R.id.examitem_status);
            teacher = itemView.findViewById(R.id.examitem_teacher);
            className = itemView.findViewById(R.id.examitem_classname);
            score = itemView.findViewById(R.id.examitem_score);
            gradePoint = itemView.findViewById(R.id.examitem_gradepoint);
            scTimeGr = itemView.findViewById(R.id.examitem_gdtimesc);
        }

        public View getRoot() {
            return root;
        }
    }

    @Override
    public int getItemCount() {
        return results.size();
    }


}
