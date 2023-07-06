package com.kagg886.jxw_collector.protocol.beans.relate;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 刁难你的问题
 *
 * @author kagg886
 * @date 2023/7/6 12:01
 **/
public class ChoiceQuestion {
    private String question;
    private List<ChoiceInfo> choices;

    private int index;

    private int choice = 0;

    public void setChoice(int choice) {
        this.choice = choice;
    }

    public int getScore() {
        return choices.get(choice).score;
    }

    public ChoiceQuestion(int index, Element i) {
        this.index = index;
        this.question = i.getElementsByAttributeValue("style", "width: 400px;").text(); //问题
        this.choices = new ArrayList<>();
        for (Element ele9 : i.getElementsByClass("input-xspj")) {
            //        map.put(prefix + ".pjzbxm_id",info.id1);
            //        map.put(prefix + ".pfdjdmb_id",info.id2);
            //        map.put(prefix + ".pfdjdmxmb_id",info.id3);
            //        map.put(prefix + ".zsmbmcb_id",info.id4);
            String[] datas = ele9.getElementsByTag("input").get(0).attr("name").split("_");
            String zsmsmcb_id = datas[0];
            String pjzbxm_id = datas[1];
            String pfdjdmb_id = datas[2];
            String pfdjdmxmb_id = ele9.getElementsByTag("input").get(0).attr("data-pfdjdmxmb_id");
            int score = Integer.parseInt(ele9.getElementsByTag("input").get(0).attr("data-dyf"));
            choices.add(new ChoiceInfo(score,pjzbxm_id,pfdjdmb_id,pfdjdmxmb_id,zsmsmcb_id,ele9.text()));
        }
    }

    public Map<String,String> generateKeyVal() {
        ChoiceInfo info = choices.get(choice);
        HashMap<String,String> map = new HashMap<>();
        String prefix = "modelList[0].xspjList[0].childXspjList[" + index + "]";


        map.put(prefix + ".pjzbxm_id",info.id1);
        map.put(prefix + ".pfdjdmb_id",info.id2);
        map.put(prefix + ".pfdjdmxmb_id",info.id3);
        map.put(prefix + ".zsmbmcb_id",info.id4);

        return map;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ChoiceQuestion{");
        sb.append("question='").append(question).append('\'');
        sb.append(", choices=").append(choices);
        sb.append(", index=").append(index);
        sb.append(", choice=").append(choice);
        sb.append('}');
        return sb.toString();
    }
}
