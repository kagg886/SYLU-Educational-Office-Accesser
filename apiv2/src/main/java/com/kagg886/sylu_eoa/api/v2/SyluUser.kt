package com.kagg886.sylu_eoa.api.v2

import com.kagg886.sylu_eoa.api.v2.bean.*
import com.kagg886.sylu_eoa.api.v2.network.*
import com.kagg886.sylu_eoa.api.v2.util.RSA
import com.kagg886.utils.createLogger
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.FormBody
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.time.LocalDate
import javax.xml.crypto.Data


private val log = createLogger("SyluUser")

class SyluUser(
    val user: String,
    val serializer: CookieSerializer = InMemoryCookieSerializer,
    baseURL:String = "https://jxw.sylu.edu.cn",
) {
    internal val client: NetWorkClient = NetWorkClient(baseURL, serializer)

    private var password: String? = null

    //业务区
    suspend fun getUserProfile(): UserProfile {

        return kotlin.runCatching {
            val document =
                client.execute("/xsxxxggl/xsgrxxwh_cxXsgrxx.html?gnmkdm=N100801&layout=default&su=$user").asHTML()

            val avt = document.getElementsByTag("img")[0].attr("src")
            val a: ByteArray = client.execute(avt).body?.bytes()!!


            val ele: Elements = document.getElementsByClass("form-control-static")
            return@runCatching UserProfile(
                ele[1].text(),
                ele[14].text(),
                ele[15].text(),
                a,
                ele[26].text(),
                ele[27].text(),
                ele[7].text(),
                ele[10].text(),
                ele[24].text()
            )
        }.onFailure {
            serializer.clear()
            throw DataFetchedException("个人信息拉取失败!",it)
        }.getOrThrow()
    }

    suspend fun getSchoolCalender(): SchoolCalender {

        return kotlin.runCatching {
            val document = client.execute("/xtgl/index_cxAreaSix.html?localeKey=zh_CN&gnmkdm=index&su=$user") {
                method("POST", FormBody.Builder().build())
            }.asHTML().assertLogin()

            var source = document.getElementsByAttribute("colspan")[0].text()

            val l = source.indexOf("(")
            val r = source.indexOf(")")

            source = source.substring(l + 1, r)
            val se = source.split("至")

            var starts = se[0].split("-")
            val start =
                LocalDate.of(Integer.parseInt(starts[0]), Integer.parseInt(starts[1]), Integer.parseInt(starts[2]))

            starts = se[1].split("-")
            val end =
                LocalDate.of(Integer.parseInt(starts[0]), Integer.parseInt(starts[1]), Integer.parseInt(starts[2]))

            return@runCatching SchoolCalender(start, end)
        }.onFailure {
            serializer.clear()
            throw DataFetchedException("校历拉取失败!",it)
        }.getOrThrow()
    }

    suspend fun getAllAvailableTerms(): TermResult {

        return kotlin.runCatching {
            val termPickers = mutableListOf<TermPicker>()

            var defaultYearName: String? = null
            var defaultYearNameVal: String? = null
            var defaultYearCode: String? = null
            var defaultYearCodeVal: String? = null

            val document: Document =
                client.execute("/cjcx/cjcx_cxDgXscj.html?gnmkdm=N305005&layout=default&su=$user").asHTML().assertLogin()

            val tempYearNameMap = mutableMapOf<String, String>()
            val tempYearCodeMap = mutableMapOf<String, String>()

            for (e in document.getElementById("xnm")!!.getElementsByTag("option")) {
                if (e.attr("selected") == "selected") {
                    defaultYearName = e.text()
                    defaultYearNameVal = e.attr("value")
                }
                tempYearNameMap[e.text()] = e.attr("value")
            }

            for (e in document.getElementById("xqm")!!.getElementsByTag("option")) {
                if (e.attr("selected") != "") {
                    defaultYearCode = e.text()
                    defaultYearCodeVal = e.attr("value")
                }
                tempYearCodeMap[e.text()] = e.attr("value")
            }

            tempYearNameMap.forEach { name ->
                tempYearCodeMap.forEach { code ->
                    termPickers.add(TermPicker(Pair(name.key, name.value), Pair(code.key, code.value)))
                }
            }

            return@runCatching TermResult(
                termPickers,
                TermPicker(Pair(defaultYearName!!, defaultYearNameVal!!), Pair(defaultYearCode!!, defaultYearCodeVal!!))
            )
        }.onFailure {
            serializer.clear()
            throw DataFetchedException("学期信息拉取失败!",it)
        }.getOrThrow()
    }

    suspend fun getExamList(picker: TermPicker = TERM_ALL_PICKER): List<ExamItem> {

        return kotlin.runCatching {
            return@runCatching client.execute("/cjcx/cjcx_cxXsgrcj.html?doType=query&gnmkdm=N305005&su=$user") {
                post(
                    mapOf(
                        "xnm" to picker.asTerm().xnm,
                        "xqm" to picker.asTerm().xqm,
                        "_search" to "false",
                        "nd" to "${System.currentTimeMillis()}",
                        "queryModel.showCount" to "50",
                        "queryModel.currentPage" to "1",
                        "time" to "2"
                    ).asFormBody()
                )
            }.asJSONBean<List<ExamItem>> {
                this.jsonObject["items"]!!
            }
        }.onFailure {
            serializer.clear()
            throw DataFetchedException("考试信息拉取失败!",it)
        }.getOrThrow()
    }

    suspend fun getExamInfo(examItem: ExamItem): List<List<String>> {

        return kotlin.runCatching {
            val tr = client.execute("/cjcx/cjcx_cxCjxqGjh.html?gnmkdm=N305005&su=$user") {
                post(
                    mapOf(
                        "jxb_id" to examItem.detailsID,
                        "xnm" to examItem.year,
                        "xqm" to examItem.semester,
                        "kcmc" to examItem.name,
                    ).asFormBody()
                )
            }.asHTML().assertLogin().getElementsByTag("tr")

            val rtn = mutableListOf<List<String>>()
            for (j in 1..<tr.size) {
                val trs = mutableListOf<String>()
                tr[j].getElementsByTag("td").run {
                    trs.add(text())
                }
                rtn.add(trs)
            }
            return@runCatching rtn
        }.onFailure {
            serializer.clear()
            throw DataFetchedException("考试详情拉取失败!",it)
        }.getOrThrow()
    }

    suspend fun getClassTable(picker: TermPicker): List<ClassUnit> {

        return kotlin.runCatching {
            client.execute("/kbcx/xskbcx_cxXsgrkb.html?gnmkdm=ssss&su=$user") {
                post(
                    FormBody.Builder().add("xnm", picker.asTerm().xnm).add("xqm", picker.asTerm().xqm).add("kzlx", "ck")
                        .add("xsdm", "").build()
                )
            }.asJSONBean<List<ClassUnit>> {
                this.jsonObject["kbList"]!!
            }
        }.onFailure {
            serializer.clear()
            throw DataFetchedException("课程表拉取失败!",it)
        }.getOrThrow()
    }

    suspend fun getGPAScores(): Map<String, List<GPAScore>> {

        return kotlin.runCatching {
            val rtn = mutableMapOf<String, List<GPAScore>>()

            val document = client.execute("/xmfzgl/xshdfzcx_cxXshdfzcxIndex.html?doType=query&gnmkdm=N4780&su=$user") {
                post(
                    mapOf(
                        "_search" to "false",
                        "nd" to "${System.currentTimeMillis()}",
                        "queryModel.showCount" to "50",
                        "queryModel.currentPage" to "1",
                        "time" to "0"
                    ).asFormBody()
                )
            }.asJSONOrigin().jsonObject["items"]!!.jsonArray

            document.forEach {
                val i = it.jsonObject["xmlbmc"]!!.jsonPrimitive.content
                rtn[i] = getGPAInfo(i)
            }
            return@runCatching rtn
        }.onFailure {
            serializer.clear()
            throw DataFetchedException("绩点拉取失败!",it)
        }.getOrThrow()
    }

    private suspend fun getGPAInfo(name: String): List<GPAScore> {
        return client.execute("/xmfzgl/xshdfzcx_cxXmfzqr.html?gnmkdm=N4780&su=$user") {
            post(("xmlbmc" to name).asFormBody())
        }.asJSONBean<List<GPAScore>> {
            this.jsonObject["items"]!!
        }
    }

    fun getPassword(): String? {
        return this.password
    }

    suspend fun isLogin(): Boolean {
        try {
            client.execute("/xtgl/index_initMenu.html?jsdm=xs&_t=1708648305427").asHTML().assertLogin()
            return true
        } catch (e: LoginFailedException) {
            log.i("Cookie过期")
            serializer.clear()
            return false
        }
    }

    suspend fun login(pass: String, captchaHandler: (suspend (a: ByteArray) -> String)? = null) {
        client.execute("") //初始化Cookies
        login(pass, null, captchaHandler)
    }

    private suspend fun login(
        pass: String, captcha: String? = null, captchaHandler: (suspend (a: ByteArray) -> String)? = null
    ) {
        log.i("${user}开始获取RSA公钥")
        val param = kotlin.runCatching {
            client.execute("/xtgl/login_getPublicKey.html?time=${System.currentTimeMillis()}").asJSONBean<RSAParam>()
        }.onFailure {
            throw IllegalStateException("无法获取RSA公钥，请稍后再试")
        }.getOrThrow()
        log.i("${user}RSA公钥获取完成, 开始登录")
        kotlin.runCatching {
            client.execute("/xtgl/login_slogin.html") {
                post(LoginParam(user, RSA.encrypt(param, pass), captcha).asFormBody())
            }.asHTML().assertLogin()
        }.onFailure {
            if (it is LoginFailedException) {
                if (it.message == "验证码输入错误！") {
                    if (captchaHandler != null) {
                        //在此处抛出异常可以取消登录
                        val captchaGet = captchaHandler(
                            client.execute("/kaptcha").body?.bytes() ?: throw LoginFailedException("验证码获取失败")
                        )
                        login(pass, captchaGet, captchaHandler)
                        return
                    }
                }
                log.i("${user}登录失败: ${it.message}")
                throw it
            }
        }
        log.i("${user}登录成功")
        this.password = pass
    }

    suspend fun logout() {
        log.i("${user}退出登录")
        client.execute("/logout")
    }

    suspend fun reLogin() {
        login(password!!, null, null)
    }
}

@Serializable
data class RSAParam(val modulus: String, val exponent: String)

@Serializable
private data class LoginParam(val yhm: String, val mm: String, val yzm: String?)

internal fun Document.assertLogin(): Document {
    for (e in this.getElementsByTag("h5")) {
        if (e.text().equals("用户登录")) {
            val test: Element? = this.getElementById("tips")
            if (test != null) {
                throw LoginFailedException(test.text())
            }
            throw LoginFailedException("Cookie过期")
        }
    }
    return this
}