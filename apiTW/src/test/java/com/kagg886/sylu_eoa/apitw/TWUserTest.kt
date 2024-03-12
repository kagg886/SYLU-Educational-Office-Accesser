package com.kagg886.sylu_eoa.apitw

import com.kagg886.sylu_eoa.api.v2.SyluUser
import com.kagg886.utils.LoggerReceiver
import com.kagg886.utils.createLogger
import com.kagg886.utils.registryLogReceiver
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.descriptors.mapSerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.File

class TWUserTest {

    companion object {
        @JvmStatic
        @BeforeAll
        fun reg() {
            registryLogReceiver(object : LoggerReceiver {
                override fun d(msg: String) {
                    println(msg)
                }

                override fun i(msg: String) {
                    println(msg)
                }

                override fun w(msg: String) {
                    println(msg)
                }

                override fun e(msg: String) {
                    println(msg)
                }

            })
        }
    }

    @Test
    fun loginError() = runBlocking {
        val user = SyluUser("2203050528").getTWUser()

        user.login("123456")
    }

    @Test
    fun loginSuccess() = runBlocking {
        createLogger("File").i("Pass:${File("").absolutePath}")
        val user = SyluUser("2203050528").getTWUser()

        user.login(File("password.txt").readText())
    }

    @Test
    fun getTWInfo() = runBlocking {
        val user = SyluUser("2203050528").getTWUser()
        user.login(File("password.txt").readText())
        if (user.isLogin) {
            val data = user.getData()
            data.forEach { (t, u) ->
                createLogger("TW").run {
                    i("Class:$t")
                    i("Info:")
                    u.forEach {
                        i(it.toString())
                    }
                }
            }

            val json = Json { allowStructuredMapKeys = true }
            File("secData.json").apply {
                createNewFile()
                writeText(json.encodeToString(data))
            }
//            println(json.encodeToString(data))
//            println(json.decodeFromString<Map<SecondClassDataSummary, List<SecondClassData>>>(Json.encodeToString(data)).toString())
        }
    }

    @Test
    fun testRestore() {
        val json = Json {
            allowStructuredMapKeys = true
        }
        println(json.decodeFromString(
            deserializer = MapSerializer(SecondClassDataSummary.serializer(), ListSerializer(SecondClassData.serializer())),
            string = File("secData.json").readText()).toString(),
        )
    }
}