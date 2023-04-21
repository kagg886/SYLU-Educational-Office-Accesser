package com.kagg886.jxw_collector.protocol.beans;

import com.kagg886.jxw_collector.protocol.SyluSession;
import com.kagg886.jxw_collector.protocol.beans.abs.YearSemesterSelectable;

import java.util.HashMap;
import java.util.Objects;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.kagg886.jxw_collector.protocol.beans
 * @className: Schedule
 * @author: kagg886
 * @description: 课程表实例
 * @date: 2023/4/13 15:07
 * @version: 1.0
 */
public class Schedule extends YearSemesterSelectable {

    public Schedule(SyluSession session) {
        super(session, session.compile("/kbcx/xskbcx_cxXskbcxIndex.html?gnmkdm=ssss&layout=default&su=", session.getStuCode()));
    }

    public Schedule() {
        super();
    }

    public Schedule(HashMap<String, String> teamVal, HashMap<String, String> years, String defaultTeamVal, String defaultYears) {
        super(teamVal, years, defaultTeamVal, defaultYears);
    }

    public ClassTable queryClassByYearAndTerm(String years, String teams) {
        String xnm = Objects.requireNonNull(getYears().get(years));
        String xqm = Objects.requireNonNull(getTeamVal().get(teams));
        return new ClassTable(xnm, xqm, getSession());
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
