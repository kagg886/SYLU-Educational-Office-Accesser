package com.kagg886.jxw_collector.protocol.beans;

import com.kagg886.jxw_collector.exceptions.OfflineException;
import com.kagg886.jxw_collector.protocol.SyluSession;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.kagg886.jxw_collector.protocol.beans
 * @className: TeacherRelate
 * @author: kagg886
 * @description: 教师评价
 * @date: 2023/4/20 20:27
 * @version: 1.0
 */
public class TeacherRelate {

    //TODO 此类未经任何测试，待下学期测试。另外 需要数据以供测试
    private final SyluSession session;

    private final String jxb_id;
    private final String kch_id;
    private final String xsdm;
    private final String jgh_id;
    private final String pjmbmcb_id;
    private final String sfcjlrjs;

    private final String className;

    private final String teacherName;

    private final State state;

    public TeacherRelate(SyluSession session, String jxb_id, String kch_id, String xsdm, String jgh_id, String pjmbmcb_id, String sfcjlrjs, String className, String teacherName, int pjzt, int tjzt) {
        this.session = session;
        this.jxb_id = jxb_id;
        this.kch_id = kch_id;
        this.xsdm = xsdm;
        this.jgh_id = jgh_id;
        this.pjmbmcb_id = pjmbmcb_id;
        this.sfcjlrjs = sfcjlrjs;
        this.className = className;
        this.teacherName = teacherName;

        if (-1 == tjzt) {
            this.state = State.UN_SUBMIT;
        } else if (0 == tjzt) {
            if (1 == pjzt) {
                this.state = State.SAVED;
            } else {
                this.state = State.UN_SUBMIT;
            }
        } else if (1 == tjzt) {
            this.state = State.SUBMIT;
        } else {
            throw new OfflineException("tjzt和pjzt代码值错误!");
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TeacherRelate{");
        sb.append("session=").append(session);
        sb.append(", jxb_id='").append(jxb_id).append('\'');
        sb.append(", kch_id='").append(kch_id).append('\'');
        sb.append(", xsdm='").append(xsdm).append('\'');
        sb.append(", jgh_id='").append(jgh_id).append('\'');
        sb.append(", pjmbmcb_id='").append(pjmbmcb_id).append('\'');
        sb.append(", sfcjlrjs='").append(sfcjlrjs).append('\'');
        sb.append(", className='").append(className).append('\'');
        sb.append(", teacherName='").append(teacherName).append('\'');
        sb.append(", state=").append(state);
        sb.append('}');
        return sb.toString();
    }

    public enum State {

        /*      @see https://jxw.sylu.edu.cn/js/comp/jwglxt/jxpjgl/xspj/xspj_index.js?ver=27989872
                tips:什么**能写出这种代码
                function addCellAttr(rowId, val, rowObject, cm, rdata) {
                    var tjzt = rowObject.tjzt;
                    var pjzt = rowObject.pjzt;
                    return '-1' == tjzt ? "style='color:red'" : '0' == tjzt ? ('1' == pjzt ?"style='color:blue'" :"style='color:red'") : '1' == tjzt ? "style='color:green'" : "";
                }
                拆解一下:'-1' == tjzt ? "style='color:red'" : '0' == tjzt ? X : '1' == tjzt ? "style='color:green'" : "";
                X = ('1' == pjzt ?"style='color:blue'" :"style='color:red'")


                if ('-1' === tjzt) {
                    red
                } else if ('0' === tjzt) {
                    if ('1' === pjzt) {
                        blue
                    } else {
                        red
                    }
                } else if ('1' === tjzt) {
                    green //绿色为提交
                } else {
                    ????
                }

                tjzt为-1时红 为1时绿
                tjzt为0时，若pjzt为1则为蓝，否则为红

                也就是说 tjzt可取-1 0 1
                pjzt为1 0
         */
        SUBMIT, //已提交
        SAVED, //已保存

        UN_SUBMIT //未提交
    }
}
