package com.kagg886.sylu_eoa.api.v2

import com.kagg886.utils.LoggerReceiver
import com.kagg886.utils.createLogger
import com.kagg886.utils.registryLogReceiver
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class SyluUserLoginTest {

    @Test
    fun testUserLogin() = runBlocking {
        val user = SyluUser("2203050528")
        user.login("123456") {
            "qwq"
        }
    }

    @Test
    fun testNeedCaptcha() = runBlocking {
        val user = SyluUser("2203050528")
        for (i in 1..5) {
            kotlin.runCatching {
                user.login("123456") {
                    println("需要验证码")
                    "qwq"
                }
            }.onFailure {
                if (it.message == "用户名或密码不正确，请重新输入！")
                    return@runBlocking
                it.printStackTrace()
            }
        }
    }

    @Test
    fun testCanceledCaptcha() = runBlocking {
        val user = SyluUser("2203050528")
        for (i in 1..5) {
            kotlin.runCatching {
                user.login("123456") {
                    println("需要验证码")
                    throw LoginFailedException("取消登录")
                }
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun reg(): Unit {
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
}