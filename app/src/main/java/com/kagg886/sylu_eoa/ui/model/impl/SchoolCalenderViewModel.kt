package com.kagg886.sylu_eoa.ui.model.impl

import androidx.lifecycle.viewModelScope
import com.kagg886.sylu_eoa.api.v2.SyluUser
import com.kagg886.sylu_eoa.api.v2.bean.SchoolCalender
import com.kagg886.sylu_eoa.getApp
import com.kagg886.sylu_eoa.ui.model.BaseViewModel
import com.kagg886.sylu_eoa.util.DayExpired
import com.kagg886.sylu_eoa.util.SchoolCalenderBean
import com.kagg886.sylu_eoa.util.SchoolCalenderBeanExpire
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.temporal.ChronoUnit

private val json = Json {
    ignoreUnknownKeys = true
}

class SchoolCalenderViewModel : BaseViewModel<SchoolCalender>() {
    private val context by lazy {
        getApp()
    }

    private val _currentWeekIndex = MutableStateFlow(0)
    private val _all = MutableStateFlow(0)

    val currentWeekIndex = _currentWeekIndex.asStateFlow()
    val all = _all.asStateFlow()

    override suspend fun onDataFetch(): SchoolCalender? {
        return null;
    }

    fun setCurrentSelectedWeek(w:Int) {
        _currentWeekIndex.value = w
    }

    fun loadData(user: SyluUser) {
        setDataLoading()
        viewModelScope.launch {
            runCatching {
                val list = context.getConfig(SchoolCalenderBean).first()
                val expire = context.getConfig(SchoolCalenderBeanExpire).first()
                if ((expire == -1L) && (list.isEmpty() || System.currentTimeMillis() > expire)) {
                    val day = context.getConfig(DayExpired).first()
                    val list = user.getSchoolCalender()
                    setDataLoadSuccess(list)
                    context.updateConfig(SchoolCalenderBean, Json.encodeToString(list))
                    context.updateConfig(SchoolCalenderBeanExpire, System.currentTimeMillis() + day * 864_000_00)
                    return@launch
                }
                setDataLoadSuccess(json.decodeFromString<SchoolCalender>(list))
            }.onFailure {
                setDataLoadError(it)
            }
        }
    }

    override fun setDataLoadSuccess(new: SchoolCalender?) {
        super.setDataLoadSuccess(new)
        _currentWeekIndex.value = new!!.currentWeek()
        _all.value = new.start.until(new.end, ChronoUnit.WEEKS).toInt() + 1
    }
}