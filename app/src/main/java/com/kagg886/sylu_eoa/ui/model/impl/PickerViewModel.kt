package com.kagg886.sylu_eoa.ui.model.impl

import androidx.lifecycle.viewModelScope
import com.kagg886.sylu_eoa.api.v2.SyluUser
import com.kagg886.sylu_eoa.api.v2.bean.TERM_ALL_PICKER
import com.kagg886.sylu_eoa.api.v2.bean.TermPicker
import com.kagg886.sylu_eoa.api.v2.bean.TermResult
import com.kagg886.sylu_eoa.getApp
import com.kagg886.sylu_eoa.ui.model.BaseViewModel
import com.kagg886.sylu_eoa.util.DayExpired
import com.kagg886.sylu_eoa.util.PickerBean
import com.kagg886.sylu_eoa.util.PickerBeanExpire
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val json = Json {
    ignoreUnknownKeys = true
}

class PickerViewModel : BaseViewModel<TermResult>() {
    private val context by lazy {
        getApp()
    }

    private val _currentTermPicker = MutableStateFlow(TERM_ALL_PICKER)

    val currentTermPicker = _currentTermPicker.asStateFlow()

    fun setCurrentTermPicker(l: TermPicker) {
        _currentTermPicker.value = l
    }

    override suspend fun onDataFetch(): TermResult {
        val list = context.getConfig(PickerBean).first()
        val expire = context.getConfig(PickerBeanExpire).first()

        if ((expire == -1L) && (list.isEmpty() || System.currentTimeMillis() > expire)) {
            throw IllegalStateException("need web")
        }

        val cd = json.decodeFromString<TermResult>(list)
        _currentTermPicker.value = cd.default
        return cd
    }

    fun loadDataByUser(user: SyluUser) {
        setDataLoading()
        viewModelScope.launch {
            try {
                val day = context.getConfig(DayExpired).first()
                val list = user.getAllAvailableTerms()

                _currentTermPicker.value = list.default

                setDataLoadSuccess(list)
                context.updateConfig(PickerBean, Json.encodeToString(list))
                context.updateConfig(PickerBeanExpire, System.currentTimeMillis() + day * 864_000_00)
            } catch (e:Exception) {
                setDataLoadError(e)
            }
        }
    }
}