package com.kagg886.sylu_eoa.ui.model.impl

import androidx.lifecycle.viewModelScope
import com.kagg886.sylu_eoa.api.v2.SyluUser
import com.kagg886.sylu_eoa.api.v2.bean.UserProfile
import com.kagg886.sylu_eoa.getApp
import com.kagg886.sylu_eoa.ui.model.BaseViewModel
import com.kagg886.sylu_eoa.util.DayExpired
import com.kagg886.sylu_eoa.util.ProfileBean
import com.kagg886.sylu_eoa.util.ProfileBeanExpire
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val json = Json {
    ignoreUnknownKeys = true
}

class ProfileViewModel : BaseViewModel<UserProfile>() {
    private val context by lazy {
        getApp()
    }
    override suspend fun onDataFetch(): UserProfile {
        val list = context.getConfig(ProfileBean).first()
        val expire = context.getConfig(ProfileBeanExpire).first()

        if ((expire == -1L) && (list.isEmpty() || System.currentTimeMillis() > expire)) {
            throw IllegalStateException("need web")
        }

        val cd = json.decodeFromString<UserProfile>(list)
        return cd
    }

    fun loadDataByUser(user: SyluUser) {
        setDataLoading()
        viewModelScope.launch {
            try {
                val day = context.getConfig(DayExpired).first()
                val list = user.getUserProfile()
                setDataLoadSuccess(list)
                context.updateConfig(ProfileBean, Json.encodeToString(list))
                context.updateConfig(ProfileBeanExpire, System.currentTimeMillis() + day * 864_000_00)
            } catch (e:Exception) {
                setDataLoadError(e)
            }
        }
    }
}