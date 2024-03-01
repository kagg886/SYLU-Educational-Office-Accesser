package com.kagg886.sylu_eoa.ui.model.impl

import androidx.lifecycle.viewModelScope
import com.kagg886.sylu_eoa.api.v2.SyluUser
import com.kagg886.sylu_eoa.api.v2.bean.ClassUnit
import com.kagg886.sylu_eoa.getApp
import com.kagg886.sylu_eoa.ui.model.BaseViewModel
import com.kagg886.sylu_eoa.util.ClassList
import com.kagg886.sylu_eoa.util.ClassListExpire
import com.kagg886.sylu_eoa.util.DayExpired
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val json = Json {
    ignoreUnknownKeys = true
}

class ClassTableViewModel : BaseViewModel<List<ClassUnit>>() {
    private val context by lazy {
        getApp()
    }

    override suspend fun onDataFetch(): List<ClassUnit> {
        val list = context.getConfig(ClassList).first()
        val expire = context.getConfig(ClassListExpire).first()
        //list.isEmpty()  System.currentTimeMillis() > expire          refreshForWeb
        //   false                      false                              false
        //   false                      true                               true
        //   true                      false                               true
        //   true                      true                                true
        if ((expire == -1L) && (list.isEmpty() || System.currentTimeMillis() > expire)) {
            throw IllegalStateException("need web")
        }
        return json.decodeFromString<List<ClassUnit>>(list)
    }

    fun loadDataByUser(user: SyluUser) {
        setDataLoading()
        viewModelScope.launch {
            try {
                val day = context.getConfig(DayExpired).first()
                val list = user.getClassTable(user.getAllAvailableTerms().default)
                setDataLoadSuccess(list)
                context.updateConfig(ClassList, Json.encodeToString(list))
                context.updateConfig(ClassListExpire, System.currentTimeMillis() + day * 864_000_00)
            } catch (e: Exception) {
                setDataLoadError(e)
            }
        }
    }
}