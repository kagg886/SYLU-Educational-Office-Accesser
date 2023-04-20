package com.kagg886.jxw_collector.protocol.beans;

import java.util.ArrayList;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.kagg886.jxw_collector.protocol.beans
 * @className: BigInnovation
 * @author: kagg886
 * @description: 与大创学分有关
 * @date: 2023/4/20 21:50
 * @version: 1.0
 */
public class BigInnovation extends ArrayList<BigInnovation.Item> {
    //"sbxmmc":"2022年9月20日腾讯会议邢沛",
//"xmbmqk_id":"EB6E4AAEF20A4505E0530200050A0B9B",
//"xh_id":"2203050528",
//"xmnr":"互联网服务架构演进",
//"xmflmc":"报告",
//"qrfzsj":"2022-10-2410:04:04",
//"xmfzxssq_id":"EB6E4AAEF72C4505E0530200050A0B9B",
//"yxfz":"0.5",
//"xmlxmc":"报告",
//"xm":"赵洋",
//"sbfz":"0.5",
//"xmlbmc":"创新创业训练",
//"row_id":1,
//"totalresult":2
    public static class Item {
        private final String name;
        private final double score;

        public Item(String name, String score) {
            this.name = name;
            this.score = Double.parseDouble(score);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Item{");
            sb.append("name='").append(name).append('\'');
            sb.append(", score='").append(score).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
}
