package com.kagg886.jxw_collector.protocol.beans.relate;

import org.jsoup.nodes.Element;

/**
 * 选择的对象
 *
 * @author kagg886
 * @date 2023/7/6 12:33
 **/
public class ChoiceInfo {
    protected int score;
    protected String id1, id2, id3, id4, choiceString;


    public ChoiceInfo(int score, String id1, String id2, String id3, String id4, String choiceString) {
        this.score = score;
        this.id1 = id1;
        this.id2 = id2;
        this.id3 = id3;
        this.id4 = id4;
        this.choiceString = choiceString;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ChoiceInfo{");
        sb.append("score=").append(score);
        sb.append(", id1='").append(id1).append('\'');
        sb.append(", id2='").append(id2).append('\'');
        sb.append(", id3='").append(id3).append('\'');
        sb.append(", id4='").append(id4).append('\'');
        sb.append(", choiceString='").append(choiceString).append('\'');
        sb.append('}');
        return sb.toString();
    }
}