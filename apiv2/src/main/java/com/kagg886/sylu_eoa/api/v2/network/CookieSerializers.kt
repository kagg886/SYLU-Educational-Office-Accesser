package com.kagg886.sylu_eoa.api.v2.network

import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.charset.StandardCharsets

object InMemoryCookieSerializer : CookieSerializer {
    private val list = mutableMapOf<String, MutableMap<String, String>>()
    override fun save(host: String, param: MutableMap<String, String>) {
        list[host] = param
    }

    override fun load(host: String): MutableMap<String, String> {
        if (list[host] == null) {
            list[host] = mutableMapOf()
        }
        return list[host]!!
    }

    override fun clear() {
        list.clear()
    }

}

class InFileCookieSerializer(private val filePath: File) : CookieSerializer {

    private val list = mutableMapOf<String, MutableMap<String, String>>();
    private val serializer =
        MapSerializer(String.serializer(), MapSerializer(String.serializer(), String.serializer()))

    init {
        //初始化
        kotlin.runCatching {
            val json = filePath.readText(StandardCharsets.UTF_8)
            Json.decodeFromString(serializer, json).forEach {
                list[it.key] = it.value.toMutableMap()
            }
        }
    }


    override fun save(host: String, param: MutableMap<String, String>) {
        //保存
        list[host] = param
        filePath.writeText(Json.encodeToString(serializer, list))
    }

    override fun load(host: String): MutableMap<String, String> {
        if (list[host] == null) {
            list[host] = mutableMapOf()
        }
        return list[host]!!
    }

    override fun clear() {
        list.clear()
        filePath.delete()
    }
}