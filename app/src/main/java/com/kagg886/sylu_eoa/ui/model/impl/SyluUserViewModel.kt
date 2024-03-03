package com.kagg886.sylu_eoa.ui.model.impl

import androidx.lifecycle.viewModelScope
import com.kagg886.sylu_eoa.api.v2.LoginFailedException
import com.kagg886.sylu_eoa.api.v2.SyluUser
import com.kagg886.sylu_eoa.api.v2.network.CookieSerializer
import com.kagg886.sylu_eoa.api.v2.network.InFileCookieSerializer
import com.kagg886.sylu_eoa.getApp
import com.kagg886.sylu_eoa.ui.model.BaseViewModel
import com.kagg886.sylu_eoa.util.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.charset.StandardCharsets

class SyluUserViewModel : BaseViewModel<SyluUser>() {
    private val context by lazy {
        getApp()
    }

    private var _storePass = MutableStateFlow(false)
    val storePass = _storePass.asStateFlow()


    private var _skipCheckLogin = MutableStateFlow(false)
    val skipCheckLogin = _skipCheckLogin.asStateFlow()

    fun clearLogin() {
        context.updateConfig(Account)
        context.updateConfig(Password)
        setStorePassword(false)
        setSkipCheckLogin(false)
        viewModelScope.launch {
            delay(1000)
            clearLoading()
        }
    }

    fun login(
        user0: String,
        pass: String,
        captchaHandler: (suspend (a: ByteArray) -> String),
        continueHandler: (i: Throwable?) -> Unit,
    ) {
        viewModelScope.launch {
            kotlin.runCatching {
                if (user0.isEmpty()) {
                    throw IllegalArgumentException("请输入账号！")
                }
                if (pass.isEmpty()) {

                    throw IllegalArgumentException("请输入密码！")
                }
                val user = newSyluUser(user0)
                user.login(pass) {
                    captchaHandler(it)
                }

                //若登录不一样直接销毁数据
                val oldLogin = context.getConfig(Account).first()
                if (oldLogin != user0) {
                    context.updateConfig(ClassListExpire, -1)
                    context.updateConfig(SchoolCalenderBeanExpire, -1)
                    context.updateConfig(ExamBeanExpire, -1)
                    context.updateConfig(PickerBeanExpire, -1)
                }

                context.updateConfig(Password, if (_storePass.value) user.getPassword()!! else "")
                setDataLoadSuccess(user)
                continueHandler(null)
            }.onFailure {
                continueHandler(it)
            }
        }
    }

    fun setStorePassword(new: Boolean) {
        _storePass.value = new;
        context.updateConfig(StorePassword, new)
    }

    fun setSkipCheckLogin(new: Boolean) {
        _skipCheckLogin.value = new
        context.updateConfig(SkipLogin, new)
    }

    override fun setDataLoadSuccess(new: SyluUser?) {
        super.setDataLoadSuccess(new)
        context.updateConfig(Account, new!!.getUser())
    }

    override suspend fun onDataFetch(): SyluUser {
        val account0 = context.getConfig(Account).first()
        val password0 = context.getConfig(Password).first()
        _storePass.value = context.getConfig(StorePassword).first()
        _skipCheckLogin.value = context.getConfig(SkipLogin).first()

        //未填写账户，返回null
        if (account0.isEmpty()) {
            throw LoginFailedException("未登录")
        }
        val user = newSyluUser(account0)

        //跳过登录检查
        if (skipCheckLogin.first()) {
            return user
        }

        //尝试检查在线，若在线返回User
        if (user.isLogin()) {
            return user
        }
//
//        kotlin.runCatching {
//            //尝试检查在线，若在线返回User
//            if (user.isLogin()) {
//                return user
//            }
//        }.onFailure {
//            //网络波动错误，自动开启离线模式
//            _skipCheckLogin.value = true
//
//            getApp().toast("网络错误，强制开启离线模式")
//            return user
//        }
        //不在线尝试登录。若报错则抛异常
        user.login(password0)
        return user
    }
}

fun newSyluUser(name: String): SyluUser {
    return SyluUser(name, EncryptedInFileCookieSerializer(File(getApp().filesDir, "config.json")))
}

class EncryptedInFileCookieSerializer(private val filePath: File) : CookieSerializer {

    private val list = mutableMapOf<String, MutableMap<String, String>>();
    private val serializer =
        MapSerializer(String.serializer(), MapSerializer(String.serializer(), String.serializer()))

    private val des = DESCrypt(getDeviceId())

    init {
        //初始化
        kotlin.runCatching {
            val json = des.decrypt(filePath.readText(StandardCharsets.UTF_8))
            Json.decodeFromString(serializer, json).forEach {
                list[it.key] = it.value.toMutableMap()
            }
        }
    }


    override fun save(host: String, param: MutableMap<String, String>) {
        //保存
        list[host] = param
        filePath.writeText(des.encrypt(Json.encodeToString(serializer, list)))
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