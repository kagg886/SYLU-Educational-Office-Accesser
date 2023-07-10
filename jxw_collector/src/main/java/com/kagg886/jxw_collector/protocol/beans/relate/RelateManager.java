package com.kagg886.jxw_collector.protocol.beans.relate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kagg886.jxw_collector.internal.HttpClient;
import com.kagg886.jxw_collector.protocol.SyluSession;
import com.kagg886.jxw_collector.util.ParamUtil;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.*;

/**
 * 评价管理器，在web系统中不填写则进不去系统
 *
 * @author kagg886
 * @date 2023/7/6 10:16
 * //FIXME 此功能仍不可用，原因未知。
 **/
public class RelateManager {
    private SyluSession session;

    private List<RelateInfo> infos = new ArrayList<>() {
        @Override
        public RelateInfo get(int index) {
            RelateInfo info = super.get(index);
            info.lazyInit(session); //避免全部加载造成的卡顿
            return info;
        }
    };

    public List<RelateInfo> getInfos() {
        return infos;
    }

    public void submit(RelateInfo relateInfo) {
        if (relateInfo.tjzt == 1) {
            throw new IllegalStateException("已经提交，不可重复提交");
        }
        double score = 0;
        for (ChoiceQuestion question : relateInfo.getQuestions()) {
            score += question.getScore(); //计算总分
        }
        //bfzpf: 94.00
        //jxb_id: EF112BA8ADC45CDBE0530200050ADA52
        //jgh_id: 29c150dd93b7fdf9f2d748bf0a2ebde5dac6b7895812d224f923a781778eca462629e45c2d36186b2b3a4b8b092ca61d0879bc1d0c0e031cd959e0ebe131003c877076e5007afdb584edfe8e5485e04e46dc7ed9ebe91b106ea4f3a2c0b0d5e0b137ccfae58a5e0bf022e64ea8f2fa8194e64b1ae0b3491e4e34e27ea6056a5d
        @SuppressWarnings("DefaultLocale")
        Connection.Response response = session.getClient().clearData()
                .url("https://jxw.sylu.edu.cn/xspjgl/xspj_cxSftf.html?gnmkdm=N401605&su=" + session.getStuCode())
                .data("bfzpf","100.00")
                .data("jxb_id", relateInfo.jxb_id)
                .data("jgh_id", relateInfo.jgh_id).post();

        String body = response.body();
        if (!body.equals("0")) {
            throw new IllegalStateException(body);
        }

        //ztpjbl: 100
        //jszdpjbl: 0
        //xykzpjbl: 0
        //jxb_id: EF112BA8ADC45CDBE0530200050ADA52
        //kch_id: 219800006
        //jgh_id: 1a6deb8946e22905d7cc57e54a362b4e878f99f012665569e8ce054a19db8014812170f9ee467fbc9de8ea595567659ba8c00139105bf928a22b60eb6f534ca55c374e6fb555d9a4db55fc8c05793c76da2f37e6b3d98377fd49434a656475d3bad26bb85390c4a09ef59e4071d37785bd57fefaa3933c32294fa284e6de7681
        //xsdm: 01
        HttpClient client = session.getClient().clearData()
                .url("https://jxw.sylu.edu.cn/xspjgl/xspj_bcXspj.html?gnmkdm=N401605&su=" + session.getStuCode())
                .data("ztpjbl","100")
                .data("jszdpjbl","0")
                .data("xykzpjbl","0")
                .data("jxb_id", relateInfo.jxb_id)
                .data("kch_id", relateInfo.kch_id)
                .data("jgh_id", relateInfo.jgh_id)
                .data("xsdm", relateInfo.xsdm)


                //modelList[0].pjmbmcb_id: FFA403615426999DE0530100050A70ED
                //modelList[0].pjdxdm: 01
                //modelList[0].fxzgf:
                //modelList[0].py: %E6%9A%82%E6%97%A0
                //modelList[0].xspfb_id: FFC911D2BA3699A2E0530200050A1D97
                //modelList[0].xspjList[0].pjzbxm_id: FFA403615427999DE0530100050A70ED
                //modelList[0].pjzt: 1
                //tjzt: 0
                .data("modelList[0].pjmbmcb_id", relateInfo.pyId)
                .data("modelList[0].pjdxdm","01")
                .data("modelList[0].fxzgf","")
                .data("modelList[0].py","暂无")
                .data("modelList[0].xspfb_id", relateInfo.xspfb_id)
                .data("modelList[0].xspjList[0].pjzbxm_id", relateInfo.table_id)
                .data("modelList[0].pjzt","1")
                .data("tjzt", String.valueOf(relateInfo.tjzt));

        for (ChoiceQuestion question : relateInfo.getQuestions()) {
            client.data(question.generateKeyVal());
        }

        Map<String,String> a = client.data();
        for (Map.Entry<String, String> entry : a.entrySet()) {
            System.out.println(entry.getKey() + "=" +entry.getValue());
        }

        String msg = client.post().body();
        if (!msg.equals("评价保存成功！")) {
            throw new IllegalStateException(msg);
        }
    }

    public RelateManager(SyluSession session) {
        this.session = session;
        JSONObject details = JSON.parseObject(
                ParamUtil.addQueryListParam(
                        session.getClient().clearData().url("https://jxw.sylu.edu.cn/xspjgl/xspj_cxXspjIndex.html?doType=query&gnmkdm=N401605&su=" + session.getStuCode())
                ).post().body()
        );
        details.getJSONArray("items")
                .stream().map((v) -> (JSONObject) v)
                .map(RelateInfo::new).forEach(infos::add);

        //{
        //    "date": "二○二三年七月六日",
        //    "dateDigit": "2023年7月6日",
        //    "dateDigitSeparator": "2023-7-6",
        //    "day": "6",
        //    "jgh_id": "16fb2a367ed87de8231d136f19575263d2dc506d0f875e558d8b8f0602ad4b1defdbebf57a14c72830b6cfc4471fee809b540ff25ab076f17baf41554a73dfd47952d0043bc01043b4e7355a967bebe49e5b69e70d5b98f34a31c393295f6ade8506fbd7c86c77716f6804fe44cbb216f14ead7309295f605de59491cc8b7165",
        //    "jgpxzd": "1",
        //    "jxb_id": "EF112BA8ADC45CDBE0530200050ADA52",
        //    "jxbmc": "(2022-2023-2)-219800006-18",
        //    "jxdd": "A-414;A-414",
        //    "jzgmc": "苗青",
        //    "kch_id": "219800006",
        //    "kcmc": "形势与政策2",
        //    "listnav": "false",
        //    "localeKey": "zh_CN",
        //    "month": "7",
        //    "pageTotal": 0,
        //    "pageable": true,
        //    "pjzt": "0",
        //    "queryModel": {
        //        "currentPage": 1,
        //        "currentResult": 0,
        //        "entityOrField": false,
        //        "limit": 15,
        //        "offset": 0,
        //        "pageNo": 0,
        //        "pageSize": 15,
        //        "showCount": 10,
        //        "sorts": [],
        //        "totalCount": 0,
        //        "totalPage": 0,
        //        "totalResult": 0
        //    },
        //    "rangeable": true,
        //    "row_id": "1",
        //    "sfcjlrjs": "1",
        //    "sksj": "星期一第3-4节{3-4周};星期五第5-6节{3-4周}",
        //    "tjzt": "-1",
        //    "tjztmc": "未评",
        //    "totalResult": "22",
        //    "userModel": {
        //        "monitor": false,
        //        "roleCount": 0,
        //        "roleKeys": "",
        //        "roleValues": "",
        //        "status": 0,
        //        "usable": false
        //    },
        //    "xnm": "2022",
        //    "xqm": "12",
        //    "xsdm": "01",
        //    "xsmc": "讲课学时",
        //    "year": "2023"
        //},
    }
}
