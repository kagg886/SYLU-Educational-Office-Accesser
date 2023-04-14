package com.kagg886.jxw_collector.protocol.beans;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kagg886.jxw_collector.internal.HttpClient;
import com.kagg886.jxw_collector.protocol.SyluSession;
import com.kagg886.jxw_collector.protocol.beans.abs.YearSemesterSelectable;
import org.jsoup.Connection;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.kagg886.jxw_collector.protocol.beans.abs
 * @className: ExamResult
 * @author: kagg886
 * @description: 查询考试结果
 * @date: 2023/4/14 18:55
 * @version: 1.0
 */
public class ExamResult extends YearSemesterSelectable {

    public ExamResult(SyluSession session) {
        super(session, session.compile("/cjcx/cjcx_cxDgXscj.html?gnmkdm=N305005&layout=default&su=", session.getStuCode()));
    }

    public List<ExamInfo> queryResultByYearAndTerm(String year, String term) {
        SyluSession session = getSession();
        session.assertLogin();
        HttpClient client = session.getClient().url(session.compile("/cjcx/cjcx_cxXsgrcj.html?doType=query&gnmkdm=N305005&su=" + session.getStuCode()))
                .data("xnm:", getYears().get(year))
                .data("xqm", getYears().get(term))
                .data("_search", "false")
                .data("nd", String.valueOf(new Date().getTime()))
                .data("queryModel.showCount", " 15")
                .data("queryModel.currentPage", " 1")
                .data("queryModel.sortName:", "")
                .data("queryModel.sortOrder:", "asc")
                .data("time", "2");
        Connection.Response resp = client.post();
        JSONArray array = JSON.parseObject(resp.body()).getJSONArray("items");

        List<ExamInfo> infos = new ArrayList<>();
        array.forEach((s) -> {
            JSONObject o = (JSONObject) s;
            String credit = o.getString("xf");
            String gradePoint = o.getString("jd");
            String crTimesGp = o.getString("xfjd");
            String detailsID = o.getString("jxb_id");
            String absoluteScore = o.getString("bfzcj");
            String relateScore = o.getString("cj");
            infos.add(new ExamInfo(gradePoint, credit, crTimesGp, detailsID, relateScore, absoluteScore));
        });
        return infos;
    }

    public static class ExamInfo {

        private final String gradePoint; //绩点
        private final String credit; //学分

        private final String gpTimesCr; //学分*绩点

        private final String detailsID; //详细信息所需要的ID

        private final String relate; //评价分数

        private final String absoluteScore; //绝对分数，低于60为挂科

        public ExamInfo(String gradePoint, String credit, String gpTimesCr, String detailsID, String relate, String absoluteScore) {
            this.gradePoint = gradePoint;
            this.credit = credit;
            this.gpTimesCr = gpTimesCr;
            this.detailsID = detailsID;
            this.relate = relate;
            this.absoluteScore = absoluteScore;
        }

        public boolean isFuckTeacher() {
            return Double.parseDouble(absoluteScore) < 60;
        }

        public String getGradePoint() {
            return gradePoint;
        }

        public String getCredit() {
            return credit;
        }

        public String getGpTimesCr() {
            return gpTimesCr;
        }

        public String getDetailsID() {
            return detailsID;
        }

        public String getRelate() {
            return relate;
        }

        public String getAbsoluteScore() {
            return absoluteScore;
        }
    }
}
