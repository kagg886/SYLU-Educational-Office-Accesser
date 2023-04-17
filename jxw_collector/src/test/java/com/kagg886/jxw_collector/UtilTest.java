package com.kagg886.jxw_collector;

import com.alibaba.fastjson.JSON;
import com.kagg886.jxw_collector.protocol.beans.ClassTable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.kagg886.jxw_collector
 * @className: UtilTest
 * @author: kagg886
 * @description: 其他测试
 * @date: 2023/4/17 13:51
 * @version: 1.0
 */
public class UtilTest {
    @Test
    public void testDeSerializableJavaBean() {
        String p = "[{\"dayInWeek\":1,\"lesson\":\"1-2\",\"name\":\"军事理论\",\"room\":\"A-321\",\"teacher\":\"王薛兵\",\"weekAsMinMax\":[{\"end\":10,\"start\":1,\"type\":\"ALL\"},{\"end\":13,\"start\":12,\"type\":\"ALL\"}]},{\"dayInWeek\":1,\"lesson\":\"3-4\",\"name\":\"中国近现代史纲要\",\"room\":\"A-414\",\"teacher\":\"朱秀芬,姜义军\",\"weekAsMinMax\":[{\"end\":2,\"start\":1,\"type\":\"ALL\"},{\"end\":10,\"start\":5,\"type\":\"ALL\"},{\"end\":15,\"start\":12,\"type\":\"ALL\"}]},{\"dayInWeek\":1,\"lesson\":\"5-6\",\"name\":\"高等数学A2\",\"room\":\"A-322\",\"teacher\":\"唐颖\",\"weekAsMinMax\":[{\"end\":10,\"start\":1,\"type\":\"ALL\"},{\"end\":15,\"start\":12,\"type\":\"ALL\"}]},{\"dayInWeek\":1,\"lesson\":\"7-8\",\"name\":\"Web前端开发技术\",\"room\":\"XX-228\",\"teacher\":\"冯壮,马玉峰\",\"weekAsMinMax\":[{\"end\":8,\"start\":1,\"type\":\"ALL\"}]},{\"dayInWeek\":2,\"lesson\":\"3-4\",\"name\":\"大学物理A1\",\"room\":\"A-306\",\"teacher\":\"迟宝倩\",\"weekAsMinMax\":[{\"end\":10,\"start\":1,\"type\":\"ALL\"},{\"end\":17,\"start\":12,\"type\":\"ALL\"}]},{\"dayInWeek\":2,\"lesson\":\"5-6\",\"name\":\"大学外语2\",\"room\":\"A-217\",\"teacher\":\"陈颖\",\"weekAsMinMax\":[{\"end\":10,\"start\":1,\"type\":\"ALL\"},{\"end\":13,\"start\":12,\"type\":\"ALL\"}]},{\"dayInWeek\":2,\"lesson\":\"7-8\",\"name\":\"面向对象程序设计\",\"room\":\"XX-427\",\"teacher\":\"宁佳绪\",\"weekAsMinMax\":[{\"end\":10,\"start\":1,\"type\":\"ALL\"},{\"end\":13,\"start\":12,\"type\":\"ALL\"}]},{\"dayInWeek\":2,\"lesson\":\"9-10\",\"name\":\"音乐欣赏\",\"room\":\"A-120\",\"teacher\":\"赵辉\",\"weekAsMinMax\":[{\"end\":10,\"start\":1,\"type\":\"ALL\"},{\"end\":13,\"start\":12,\"type\":\"ALL\"}]},{\"dayInWeek\":3,\"lesson\":\"3-4\",\"name\":\"高等数学A2\",\"room\":\"A-322\",\"teacher\":\"唐颖\",\"weekAsMinMax\":[{\"end\":6,\"start\":1,\"type\":\"ALL\"},{\"end\":10,\"start\":8,\"type\":\"ALL\"},{\"end\":15,\"start\":12,\"type\":\"ALL\"}]},{\"dayInWeek\":3,\"lesson\":\"5-6\",\"name\":\"线性代数A\",\"room\":\"A-411\",\"teacher\":\"李霞\",\"weekAsMinMax\":[{\"end\":6,\"start\":1,\"type\":\"ALL\"},{\"end\":10,\"start\":8,\"type\":\"ALL\"},{\"end\":12,\"start\":12,\"type\":\"ALL\"}]},{\"dayInWeek\":3,\"lesson\":\"7-8\",\"name\":\"大学生职业生涯与发展规划\",\"room\":\"A-119\",\"teacher\":\"袁妍\",\"weekAsMinMax\":[{\"end\":6,\"start\":1,\"type\":\"ALL\"},{\"end\":9,\"start\":8,\"type\":\"ALL\"}]},{\"dayInWeek\":4,\"lesson\":\"1-2\",\"name\":\"大学物理A1\",\"room\":\"A-306\",\"teacher\":\"迟宝倩\",\"weekAsMinMax\":[{\"end\":16,\"start\":1,\"type\":\"ALL\"}]},{\"dayInWeek\":4,\"lesson\":\"3-4\",\"name\":\"高等数学A2\",\"room\":\"A-322\",\"teacher\":\"唐颖\",\"weekAsMinMax\":[{\"end\":13,\"start\":1,\"type\":\"ALL\"}]},{\"dayInWeek\":4,\"lesson\":\"5-6\",\"name\":\"Web前端开发技术\",\"room\":\"XX-228\",\"teacher\":\"冯壮,马玉峰\",\"weekAsMinMax\":[{\"end\":8,\"start\":1,\"type\":\"ALL\"}]},{\"dayInWeek\":4,\"lesson\":\"7-8\",\"name\":\"面向对象程序设计\",\"room\":\"XX-427\",\"teacher\":\"宁佳绪\",\"weekAsMinMax\":[{\"end\":12,\"start\":1,\"type\":\"ALL\"}]},{\"dayInWeek\":4,\"lesson\":\"9-10\",\"name\":\"戏剧鉴赏\",\"room\":\"A-421\",\"teacher\":\"邹锴锋\",\"weekAsMinMax\":[{\"end\":12,\"start\":1,\"type\":\"ALL\"}]},{\"dayInWeek\":5,\"lesson\":\"3-4\",\"name\":\"大学外语2\",\"room\":\"A-217\",\"teacher\":\"陈颖\",\"weekAsMinMax\":[{\"end\":12,\"start\":1,\"type\":\"ALL\"}]},{\"dayInWeek\":5,\"lesson\":\"5-6\",\"name\":\"中国近现代史纲要\",\"room\":\"A-414\",\"teacher\":\"朱秀芬,姜义军\",\"weekAsMinMax\":[{\"end\":2,\"start\":1,\"type\":\"ALL\"},{\"end\":14,\"start\":5,\"type\":\"ALL\"}]},{\"dayInWeek\":5,\"lesson\":\"7-8\",\"name\":\"线性代数A\",\"room\":\"A-411\",\"teacher\":\"李霞\",\"weekAsMinMax\":[{\"end\":10,\"start\":1,\"type\":\"ALL\"}]}]";
        ClassTable t = JSON.parseObject(p, ClassTable.class);
        for (ClassTable.ClassUnit a : t) {
            Assertions.assertFalse(a.toString().contains("null"));
        }
    }
}
