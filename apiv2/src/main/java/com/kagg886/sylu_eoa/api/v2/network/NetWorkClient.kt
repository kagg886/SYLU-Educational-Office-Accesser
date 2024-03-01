package com.kagg886.sylu_eoa.api.v2.network

import com.kagg886.sylu_eoa.api.v2.util.MyHttpLoggingInterceptor
import com.kagg886.utils.createLogger
import kotlinx.coroutines.*
import kotlinx.serialization.json.*
import okhttp3.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

private val logger = createLogger("Network")

class NetWorkClient(
    private val baseURL: String,
    private val serializer: CookieSerializer = InMemoryCookieSerializer
) {
    private val scope = CoroutineScope(Dispatchers.IO) + CoroutineName("Network") + SupervisorJob()

    private val client: OkHttpClient = OkHttpClient.Builder()
        .addNetworkInterceptor(MyHttpLoggingInterceptor(logger).setLevel(MyHttpLoggingInterceptor.Level.BODY))
        .cookieJar(object : CookieJar {
            override fun loadForRequest(url: HttpUrl): List<Cookie> {
                return serializer.load(url.host).mapNotNull {
                    Cookie.parse(url, it.value)
                }
            }

            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                val cookie = serializer.load(url.host)
                cookies.forEach {
                    cookie[it.name] = it.toString()
                }
                serializer.save(url.host, cookie)
            }

        }).build()

    suspend fun execute(
        sub: String, builder: Request.Builder.() -> Request.Builder = { Request.Builder().url(baseURL + sub) }
    ): Response {
        return scope.async {
            client.newCall(
                builder(Request.Builder().url(baseURL + sub)).build()
            ).execute()
        }.await()
    }
}

interface CookieSerializer {
    fun save(host: String, param: MutableMap<String, String>)
    fun load(host: String): MutableMap<String, String>
    fun clear()
}

val json = Json { ignoreUnknownKeys = true } // 忽略未知键
inline fun <reified T> Response.asJSONBean(factory: JsonElement.() -> JsonElement = { this }): T {
    return json.decodeFromJsonElement<T>(
        factory(
            json.decodeFromString(this.body?.string() ?: throw IllegalStateException("string is empty"))
        )
    )
}

fun Response.asJSONOrigin(): JsonElement {
    return json.parseToJsonElement(this.body!!.string())
}

fun Response.asHTML(): Document {
    return Jsoup.parse(this.body?.string() ?: throw IllegalStateException("string is empty"))
}

fun <T> T.asFormBody(): RequestBody where T : Map<String, String> {
    val body = FormBody.Builder()
    this.forEach { (key, value) ->
        body.add(key, value)
    }
    return body.build()
}

fun Pair<String, String>.asFormBody(): RequestBody {
    return FormBody.Builder().add(first, second).build()
}

inline fun <reified T> T.asFormBody(): RequestBody {
    var body: FormBody.Builder = FormBody.Builder()
    Json.encodeToJsonElement(this).jsonObject.forEach {
        if (it.value.jsonPrimitive.contentOrNull != null) {
            body = body.add(it.key, it.value.jsonPrimitive.content)
        }
    }
    return body.build()
}
