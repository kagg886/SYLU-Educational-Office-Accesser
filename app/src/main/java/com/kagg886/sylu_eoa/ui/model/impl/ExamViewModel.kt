package com.kagg886.sylu_eoa.ui.model.impl

import androidx.lifecycle.viewModelScope
import com.kagg886.sylu_eoa.api.v2.SyluUser
import com.kagg886.sylu_eoa.api.v2.bean.ExamItem
import com.kagg886.sylu_eoa.api.v2.bean.TermPicker
import com.kagg886.sylu_eoa.getApp
import com.kagg886.sylu_eoa.ui.model.BaseViewModel
import com.kagg886.sylu_eoa.util.DayExpired
import com.kagg886.sylu_eoa.util.ExamBean
import com.kagg886.sylu_eoa.util.ExamBeanExpire
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val json = Json {
    ignoreUnknownKeys = true
}

class ExamViewModel : BaseViewModel<List<ExamItem>>() {
    private val context by lazy {
        getApp()
    }

    override suspend fun onDataFetch(): List<ExamItem> {
        val list = context.getConfig(ExamBean).first()
        val expire = context.getConfig(ExamBeanExpire).first()

        if ((expire == -1L) && (list.isEmpty() || System.currentTimeMillis() > expire)) {
            throw IllegalStateException("need web")
        }
        val list1 = json.decodeFromString<List<ExamItem>>(list)
        return list1
    }

    fun loadDataByUser(user: SyluUser) {
        setDataLoading()
        viewModelScope.launch {
            try {
                val day = context.getConfig(DayExpired).first()
                val list = user.getExamList()

                setDataLoadSuccess(list)
                context.updateConfig(ExamBean, Json.encodeToString(list))
                context.updateConfig(ExamBeanExpire, System.currentTimeMillis() + day * 864_000_00)
            } catch (e:Exception) {
                setDataLoadError(e)
            }
        }
    }
}