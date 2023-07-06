package com.kagg886.jxw_collector.protocol.beans.relate;

import com.alibaba.fastjson.JSONObject;
import com.kagg886.jxw_collector.protocol.SyluSession;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * 代表了一个人的评价
 *
 * @author kagg886
 * @date 2023/7/6 11:59
 **/
public class RelateInfo {
    protected String className,
            jxb_id,
            kch_id,
            xsdm,
            jgh_id;

    protected int tjzt, //提交状态 -1为未评
            sfcjlrjs;

    protected String pyId;
    protected String xspfb_id;
    protected String table_id;
    private List<ChoiceQuestion> questions = new ArrayList<>();

    public List<ChoiceQuestion> getQuestions() {
        return questions;
    }

    public String getClassName() {
        return className;
    }

    public RelateInfo(JSONObject item) {
        className = item.getString("kcmc");
        jxb_id = item.getString("jxb_id");
        kch_id = item.getString("kch_id");
        xsdm = item.getString("xsdm");
        jgh_id = item.getString("jgh_id");
        tjzt = item.getIntValue("tjzt");
        sfcjlrjs = item.getIntValue("sfcjlrjs");
    }


    protected void lazyInit(SyluSession session) {
        Connection.Response resp = session.getClient().clearData().url("https://jxw.sylu.edu.cn/xspjgl/xspj_cxXspjDisplay.html?gnmkdm=N401605&su=" + session.getStuCode())
                .header("Origin", "https://jxw.sylu.edu.cn")
                .header("Referer", "https://jxw.sylu.edu.cn/xspjgl/xspj_cxXspjIndex.html?gnmkdm=N401605&layout=default&dltz=yes")
                .data("jxb_id", jxb_id)
                .data("kch_id", kch_id)
                .data("xsdm", xsdm)
                .data("jgh_id", jgh_id)
                .data("tjzt", String.valueOf(tjzt))
                .data("pjmbmcb_id", "")
                .data("sfcjlrjs", String.valueOf(sfcjlrjs))
                .post();

        Document doc = Jsoup.parse(resp.body());

        //选择题
        int index = 0;
        for (Element i : doc.getElementsByAttributeValue("class", "tr-xspj")) {
            questions.add(new ChoiceQuestion(index,i));
            index++;
        }

        //评语
        this.pyId = doc.getElementById("pyDiv").getElementsByTag("textarea").get(0).attr("id").split("_")[0];
        this.xspfb_id = doc.getElementsByAttributeValue("class","panel panel-default panel-pjdx").attr("data-xspfb_id");
        this.table_id = doc.getElementsByTag("table").get(0).attr("data-pjzbxm_id");
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RelateInfo{");
        sb.append("className='").append(className).append('\'');
        sb.append(", jxb_id='").append(jxb_id).append('\'');
        sb.append(", kch_id='").append(kch_id).append('\'');
        sb.append(", xsdm='").append(xsdm).append('\'');
        sb.append(", jgh_id='").append(jgh_id).append('\'');
        sb.append(", tjzt=").append(tjzt);
        sb.append(", sfcjlrjs=").append(sfcjlrjs);
        sb.append(", questions=").append(questions);
        sb.append('}');
        return sb.toString();
    }
}