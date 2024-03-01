package com.kagg886.sylu_eoa.ui.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

object GlobalCrashViewModel {
    private val _crash = MutableStateFlow<Throwable?>(null)
    val crash = _crash.asStateFlow()


    private val _crashed = MutableStateFlow(false)
    val crashed = _crashed.asStateFlow()


    private val _file = MutableStateFlow("")
    val file = _file.asStateFlow()


    fun setCrashData(e: Throwable, f: File) {
        _crashed.value = true
        _crash.value = e
        _file.value = f.absolutePath
    }
}