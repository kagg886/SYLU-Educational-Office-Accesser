package com.kagg886.jxw_collector.protocol.beans;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kagg886.jxw_collector.internal.HttpClient;
import com.kagg886.jxw_collector.protocol.SyluSession;
import com.kagg886.jxw_collector.util.ExceptionUtil;
import org.jsoup.Connection;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.kagg886.jxw_collector.protocol.beans
 * @className: ClassTable
 * @author: kagg886
 * @description: 课程表总信息
 * @date: 2023/4/13 15:58
 * @version: 1.0
 */
public class ClassTable extends ArrayList<ClassTable.ClassUnit> {

    public ClassTable() {

    }

    public ClassTable(ClassTable table) {
        addAll(table);
    }

    public ClassTable(String xnm, String xqm, SyluSession session) {
        JSONArray array = ExceptionUtil.executeUntilNoException(() -> {
            session.assertLogin();
            HttpClient client = session.getClient();
            client.url(session.compile("/kbcx/xskbcx_cxXsgrkb.html?gnmkdm=ssss&su=" + session.getStuCode()))
                    .data("xnm", xnm).data("xqm", xqm)
                    .data("kzlx", "ck").data("xsdm", "");
            Connection.Response resp = client.post();

            JSONArray object = JSON.parseObject(resp.body()).getJSONArray("kbList");
            if (object.size() == 0) {
                throw new ExceptionUtil.Ignored(new IllegalStateException("该学年学期的课表尚未开放!"));
            }
            return object;
        }, 30000);
        array.forEach((obj) -> {
            JSONObject object = ((JSONObject) obj);
            String room = object.getString("cdmc");
            String name = object.getString("kcmc");
            String timeEachLesson = object.getString("jcs");
            String teacher = object.getString("xm");
            String week = object.getString("zcd");
            String xqj = object.getString("xqj");
            add(new ClassUnit(name, teacher, room, week, timeEachLesson, xqj));
        });
    }

    public ClassTable queryClassByWeek(int week) {
        ClassTable rtn = new ClassTable();
        rtn.addAll(this.stream().filter(classUnit -> {
            for (Range range : classUnit.getWeekAsMinMax()) {
                if (week >= range.getStart() && week <= range.getEnd()) {
                    switch (range.getType()) {
                        case ALL -> {
                            return true;
                        }
                        case SINGULAR -> {
                            return week % 2 == 1;
                        }
                        case EVEN -> {
                            return week % 2 == 0;
                        }
                    }
                }
            }
            return false;
        }).collect(Collectors.toList()));
        return rtn;
    }

    public ClassTable queryClassByLesson(String lesson) {
        ClassTable rtn = new ClassTable();
        rtn.addAll(this.stream().filter(classUnit -> classUnit.lesson.equals(lesson)).collect(Collectors.toList()));
        return rtn;
    }

    public ClassTable queryClassByDay(int day) {
        ClassTable rtn = new ClassTable();
        rtn.addAll(this.stream().filter(classUnit -> classUnit.getDayInWeek() == day).collect(Collectors.toList()));
        return rtn;
    }

    public enum FilterType {
        ALL, //单双周
        SINGULAR,//单周
        EVEN;//双周
    }

    public static class ClassUnit {

        public static ClassUnit EMPTY = new ClassUnit(null, null, null, null, null, "0");

        //课程:大学物理A1
        //老师:迟宝倩
        //教室:A-306
        //周数:1-16周
        //节数:1-2
        private String name;
        private String teacher;
        private String room;

        private String lesson;
        private int dayInWeek;

        private List<Range> weekAsMinMax;

        private String weekEachLesson;


        public ClassUnit() {

        }

        private ClassUnit(String name, String teacher, String room, String weekEachLesson, String lesson, String dayInWeek) {
            if (name == null) {
                return;
            }
            this.name = name;
            this.teacher = teacher;
            this.room = room;
            this.lesson = lesson;
            this.dayInWeek = Integer.parseInt(dayInWeek);
            this.weekEachLesson = weekEachLesson;

            List<Range> rtn = new ArrayList<>();
            for (String a : weekEachLesson.split(",")) {
                a = a.substring(0, a.length() - 1);
                if (a.contains("-")) {
                    String[] k = a.split("-");
                    int l;
                    FilterType type = FilterType.ALL;
                    try {
                        l = Integer.parseInt(k[1]);
                    } catch (NumberFormatException e) {
                        l = Integer.parseInt(k[1].split("周")[0]);
                        switch (k[1].split("\\(")[1]) {
                            case "单" -> type = FilterType.SINGULAR;
                            case "双" -> type = FilterType.EVEN;
                        }
                    }
                    rtn.add(new Range(Integer.parseInt(k[0]), l, type));
                    continue;
                }
                rtn.add(new Range(Integer.parseInt(a), Integer.parseInt(a), FilterType.ALL));
            }
            this.weekAsMinMax = rtn;
        }

        public String getWeekEachLesson() {
            return weekEachLesson;
        }

        public void setWeekEachLesson(String weekEachLesson) {
            this.weekEachLesson = weekEachLesson;
        }

        public int getDayInWeek() {
            return dayInWeek;
        }

        public String getName() {
            return name;
        }

        public String getTeacher() {
            return teacher;
        }

        public String getRoom() {
            return room;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setTeacher(String teacher) {
            this.teacher = teacher;
        }

        public void setRoom(String room) {
            this.room = room;
        }

        public void setLesson(String lesson) {
            this.lesson = lesson;
        }

        public void setDayInWeek(int dayInWeek) {
            this.dayInWeek = dayInWeek;
        }

        public List<Range> getWeekAsMinMax() { //列表中每个数组代表起止
            return weekAsMinMax;
        }

        public void setWeekAsMinMax(List<Range> weekAsMinMax) {
            this.weekAsMinMax = weekAsMinMax;
        }

        public String getLesson() {
            return lesson;
        }


        @Override
        public String toString() {
            return new StringJoiner(", ", ClassUnit.class.getSimpleName() + "[", "]")
                    .add("name='" + name + "'")
                    .add("teacher='" + teacher + "'")
                    .add("room='" + room + "'")
                    .add("lesson='" + lesson + "'")
                    .add("dayInWeek=" + dayInWeek)
                    .add("weekAsMinMax=" + weekAsMinMax)
                    .add("weekEachLesson='" + weekEachLesson + "'")
                    .toString();
        }
    }

    public static class Range {
        private int start;
        private int end;
        private FilterType type;

        public Range(int start, int end, FilterType type) {
            this.start = start;
            this.end = end;
            this.type = type;
        }

        public Range() {

        }

        public void setStart(int start) {
            this.start = start;
        }

        public void setEnd(int end) {
            this.end = end;
        }

        public void setType(FilterType type) {
            this.type = type;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }

        public FilterType getType() {
            return type;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", Range.class.getSimpleName() + "[", "]")
                    .add("start=" + start)
                    .add("end=" + end)
                    .add("type=" + type)
                    .toString();
        }
    }
}