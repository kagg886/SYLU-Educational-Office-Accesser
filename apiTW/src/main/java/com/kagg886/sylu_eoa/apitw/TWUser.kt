package com.kagg886.sylu_eoa.apitw

import com.kagg886.sylu_eoa.api.v2.SyluUser
import com.kagg886.sylu_eoa.api.v2.network.NetWorkClient
import com.kagg886.sylu_eoa.api.v2.network.asFormBody
import com.kagg886.sylu_eoa.api.v2.network.asHTML
import com.kagg886.sylu_eoa.api.v2.util.RSA
import org.jsoup.nodes.Element
import org.jsoup.select.Elements


fun SyluUser.getTWUser(): TWUser {
    return TWUser(user, NetWorkClient("http://xg.sylu.edu.cn/SyluTW/Sys", serializer))
}

class TWUser(val user: String, val net: NetWorkClient) {

    var isLogin = false

    private val keys = arrayOf(
        "思想成长",
        "实践实习",
        "创新创业",
        "志愿公益",
        "文体活动"
    )

    private val pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC3hzrH91c0OKgtaSB7GWGfDuUJ" +
            "sMrtiYThDXtJdrCr7exKt2fmIZngoFk71Dv/BPVQCHSuohNNvEV9VVDFSBhsP9xK" +
            "EDAM4/2Lv+wlzN9CuZtLpV3Elo8VacjwMHcjTRmTchRBmijQzZRFrA2LM+qsH3U5" +
            "tRM1uJFbfRMkBq24AwIDAQAB";

    suspend fun login(pass: String) {
        var dom = net.execute("/UserLogin.aspx").asHTML()
        //.data("UserName", stuID)
        //                .data("__VIEWSTATE", dom.getElementById("__VIEWSTATE").attr("value"))
        //                .data("__VIEWSTATEGENERATOR", dom.getElementById("__VIEWSTATEGENERATOR").attr("value"))
        //                .data("__EVENTVALIDATION", dom.getElementById("__EVENTVALIDATION").attr("value"))
        //                .data("Password", pass)
        //                .data("pwd", RSA.getInstance().encrypt(pass, pubKey))
        //                .data("pubKey", pubKey)
        //                .data("codeInput", "KHG6")
        //                .data("queryBtn", "%B5%C7++++++++++%C2%BC")
        dom = net.execute("/UserLogin.aspx") {
            this.post(
                mapOf(
                    "UserName" to user,
                    "__VIEWSTATE" to dom.getElementById("__VIEWSTATE")!!.attr("value"),
                    "__VIEWSTATEGENERATOR" to dom.getElementById("__VIEWSTATEGENERATOR")!!.attr("value"),
                    "__EVENTVALIDATION" to dom.getElementById("__EVENTVALIDATION")!!.attr("value"),
                    "Password" to pass,
                    "pwd" to RSA.encrypt(pass, pubKey),
                    "pubKey" to pubKey,
                    "codeInput" to "KHG6",
                    "queryBtn" to "%B5%C7++++++++++%C2%BC"
                ).asFormBody()
            )
        }.asHTML()

        dom.getElementsByTag("script").forEach { v ->
            if (v.html().startsWith("layer.alert('")) {
                val l = v.html().indexOf("'") + 1;
                val r = v.html().indexOf("'", l);
                throw IllegalStateException(v.html().substring(l, r));
            }

            if (v.html().equals("window.location.href='SystemForm/main.htm';")) {
                isLogin = true
            }
        }
    }

    suspend fun getData(): Map<SecondClassDataSummary, List<SecondClassData>> {
        var map = mutableMapOf<SecondClassDataSummary, MutableList<SecondClassData>>()

        var dom = net.execute("/SystemForm/FinishExam/StuFinishStudentScore.aspx").asHTML()

        var id = 'A'
        while (id <= 'E') {
            val e: String = dom.getElementById("Count" + id + "1").text()
            val now = (e.ifEmpty { "0.00" }).toDouble()
            map[SecondClassDataSummary(keys[id - 'A'], now)] = mutableListOf()
            id++
        }

        val e: String = dom.getElementById("SunCount1").text()
        val sum1 = (e.ifEmpty { "0.00" }).toDouble()
        map[SecondClassDataSummary("All", sum1)] = mutableListOf()

        dom = net.execute("/SystemForm/StuAction/StuActionSearch.aspx").asHTML()

        val data: Elements = dom.getElementsByTag("tr")

        for (i in 2 until data.size) {
            val info: Element = data[i]

            val elements: Elements = info.getElementsByTag("td")
            map[elements[3].text()].add(SecondClassData(
                elements[0].text(),
                elements[1].text(),  //申请单位
                elements[2].text(),  //时间
                elements[4].text(),  //身份
                elements[5].text().toInt(),  //参与人数
                elements[7].text().toDouble()
            ))
        }

        return map
    }

}