package com.kagg886.sylu_eoa.ui.model.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kagg886.sylu_eoa.api.v2.SyluUser
import com.kagg886.sylu_eoa.api.v2.bean.ExamItem
import com.kagg886.sylu_eoa.api.v2.bean.GPAScore
import com.kagg886.sylu_eoa.getApp
import com.kagg886.sylu_eoa.ui.model.BaseViewModel
import com.kagg886.sylu_eoa.util.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val json = Json {
    ignoreUnknownKeys = true
}

private val context by lazy {
    getApp()
}

class GPAViewModel:BaseViewModel<Map<String, List<GPAScore>>>() {
    override suspend fun onDataFetch(): Map<String, List<GPAScore>> {
        val list = context.getConfig(GPABean).first()
        val expire = context.getConfig(GPABeanExpire).first()

        if ((expire == -1L) && (list.isEmpty() || System.currentTimeMillis() > expire)) {
            throw IllegalStateException("need web")
        }
        val list1 = json.decodeFromString<Map<String, List<GPAScore>>>(list)
        return list1
    }

    fun fetchUser(user:SyluUser) {
        setDataLoading()
        viewModelScope.launch {
            try {
                val day = context.getConfig(DayExpired).first()
                val list = user.getGPAScores()

                setDataLoadSuccess(list)
                context.updateConfig(GPABean, Json.encodeToString(list))
                context.updateConfig(GPABeanExpire, System.currentTimeMillis() + day * 864_000_00)
            } catch (e:Exception) {
                setDataLoadError(e)
            }
        }
    }
}