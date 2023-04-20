package com.kagg886.jxw_collector.protocol.beans;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kagg886.jxw_collector.internal.HttpClient;
import com.kagg886.jxw_collector.protocol.SyluSession;
import com.kagg886.jxw_collector.protocol.beans.abs.YearSemesterSelectable;
import com.kagg886.jxw_collector.util.ExceptionUtil;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

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

    public List<List<String>> queryDetailsByExamInfo(ExamInfo i) {
        SyluSession session = getSession();
        session.assertLogin();
        //jxb_id: F75019F3F5DA44D8E0530100050A0316
        //xnm: 2022
        //xqm: 12
        //kcmc: 大学外语1

        return ExceptionUtil.executeUntilNoException(() -> {
            Connection.Response resp;
            resp = session.getClient()
                    .clearData() //防止变量污染
                    .url(session.compile("/cjcx/cjcx_cxCjxqGjh.html?time=", new Date().getTime(), "&gnmkdm=N305005&su=", session.getStuCode()))
                    .data("jxb_id", i.getDetailsID())
                    .data("xnm", i.getYear())
                    .data("xqm", i.getTerm())
                    .data("kcmc", i.getName())
                    .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                    .header("Host", "jxw.sylu.edu.cn")
                    .header("Origin", "https://jxw.sylu.edu.cn")
                    .header("Referer", session.compile("/cjcx/cjcx_cxDgXscj.html?gnmkdm=N305005&layout=default&su=", session.getStuCode()))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36 Edg/112.0.1722.48")
                    .header("X-Requested-With", "XMLHttpRequest")
                    .post();
            List<List<String>> rtn = new ArrayList<>();
            Elements tr = Jsoup.parse(resp.body()).getElementsByTag("tr");
            for (int j = 1; j < tr.size(); j++) {
                List<String> trs = new ArrayList<>();
                for (Element td : tr.get(j).getElementsByTag("td")) {
                    trs.add(td.text());
                }
                rtn.add(trs);
            }
            return rtn;
        }, 30000);
    }

    public List<ExamInfo> queryResultByYearAndTerm(String year, String term) {
        SyluSession session = getSession();
        session.assertLogin();
        String xnm = Objects.requireNonNull(getYears().getOrDefault(year, null));
        String xqm = Objects.requireNonNull(getTeamVal().getOrDefault(term, null));
        HttpClient client = session.getClient().clearData().url(session.compile("/cjcx/cjcx_cxXsgrcj.html?doType=query&gnmkdm=sssss&su=" + session.getStuCode()))
                .data("xnm", xnm)
                .data("xqm", xqm)
                .data("_search", "false")
                .data("nd", String.valueOf(new Date().getTime()))
                .data("queryModel.showCount", " 15")
                .data("queryModel.currentPage", " 1")
                .data("queryModel.sortName", "")
                .data("queryModel.sortOrder", "asc")
                .data("time", "2");
        Connection.Response resp = client.post();
        JSONArray array = JSON.parseObject(resp.body()).getJSONArray("items");

        List<ExamInfo> infos = new ArrayList<>();
        array.forEach((s) -> {
            JSONObject o = (JSONObject) s;
            String name = o.getString("kcmc");
            String teacher = o.getString("tjrxm");
            String credit = o.getString("xf");
            String gradePoint = o.getString("jd");
            String crTimesGp = o.getString("xfjd");
            String detailsID = o.getString("jxb_id");
            String absoluteScore = o.getString("bfzcj");
            String relateScore = o.getString("cj");

            int ksxzdm = Integer.parseInt(o.getString("ksxzdm"));
            infos.add(new ExamInfo(
                    o.getString("xnm"),
                    o.getString("xqm"),
                    name,
                    teacher,
                    gradePoint,
                    credit,
                    crTimesGp,
                    detailsID,
                    relateScore,
                    absoluteScore,
                    ksxzdm));
        });
        return infos;
    }

    public enum Status {
        SUCCESS, //考试一遍过
        FUCK_TEACHER, //老师不捞我，呜呜呜
        SUCCESS_RE //重修或补考成功
    }

    public static class ExamInfo {

        private final String year; //学年代号
        private final String term; //学期代号

        private final String name; //课程名


        private final String teacher; //老师

        private final String gradePoint; //绩点
        private final String credit; //学分

        private final String gpTimesCr; //学分*绩点

        private final String detailsID; //详细信息所需要的ID

        private final String relate; //评价分数

        private final Double absoluteScore; //绝对分数，低于60为挂科

        private final int ksxzdm; //判断重修或补考需要的凭证


        public ExamInfo(String xnm, String xqm, String name, String teacher, String gradePoint, String credit, String gpTimesCr, String detailsID, String relate, String absoluteScore, int ksxzdm) {
            this.year = xnm;
            this.term = xqm;

            this.name = name;
            this.teacher = teacher;
            this.gradePoint = gradePoint;
            this.credit = credit;
            this.gpTimesCr = gpTimesCr;
            this.detailsID = detailsID;
            this.relate = relate;
            this.absoluteScore = Double.parseDouble(absoluteScore);
            this.ksxzdm = ksxzdm;
        }

        /**
         * @param :
         * @return Status
         * @author kagg886
         * @description 获取本次考试的状态
         * @date 2023/04/15 08:58
         * @see <a href="https://jxw.sylu.edu.cn/js/comp/jwglxt/cjgl/cjcx/cxDgXscj.js?ver=27989871">186行</a>
         */

        //if (rowData.bfzcj >= 60 && (rowData.ksxzdm == "11" || rowData.ksxzdm == "16" || rowData.ksxzdm == "17")) {
        //     $("#tabGrid tr[id='" + ids[ii] + "']").attr("style", "color:blue");
        //}
        public Status getStatus() {
            if (Double.compare(absoluteScore, 60) == -1) {
                return Status.FUCK_TEACHER;
            } else {
                if (ksxzdm == 11 || ksxzdm == 16 || ksxzdm == 17) {
                    return Status.SUCCESS_RE;
                }
            }
            return Status.SUCCESS;
        }

        public String getTerm() {
            return term;
        }

        public String getYear() {
            return year;
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

        public Double getAbsoluteScore() {
            return absoluteScore;
        }

        public String getTeacher() {
            return teacher;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", ExamInfo.class.getSimpleName() + "[", "]")
                    .add("year='" + year + "'")
                    .add("term='" + term + "'")
                    .add("name='" + name + "'")
                    .add("teacher='" + teacher + "'")
                    .add("gradePoint='" + gradePoint + "'")
                    .add("credit='" + credit + "'")
                    .add("gpTimesCr='" + gpTimesCr + "'")
                    .add("detailsID='" + detailsID + "'")
                    .add("relate='" + relate + "'")
                    .add("absoluteScore=" + absoluteScore)
                    .add("ksxzdm=" + ksxzdm)
                    .toString();
        }
    }
}
