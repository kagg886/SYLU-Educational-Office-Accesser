package com.qlstudio.lite_kagg886.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kagg886.jxw_collector.protocol.beans.ExamResult;
import com.qlstudio.lite_kagg886.R;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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

    public List<ExamResult.ExamInfo> getResults() {
        return results;
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

    @NonNull
    @NotNull
    @Override
    public ExamInfoHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ExamInfoHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_exam, null));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ExamInfoHolder holder, int position) {
        ExamResult.ExamInfo info = results.get(position);

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

    @Override
    public int getItemCount() {
        return results.size();
    }

    public static class ExamInfoHolder extends RecyclerView.ViewHolder {

        private final ImageView status;
        private final TextView teacher, className, score, gradePoint, scTimeGr;

        public ExamInfoHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            status = itemView.findViewById(R.id.examitem_status);
            teacher = itemView.findViewById(R.id.examitem_teacher);
            className = itemView.findViewById(R.id.examitem_classname);
            score = itemView.findViewById(R.id.examitem_score);
            gradePoint = itemView.findViewById(R.id.examitem_gradepoint);
            scTimeGr = itemView.findViewById(R.id.examitem_gdtimesc);
        }
    }


}
