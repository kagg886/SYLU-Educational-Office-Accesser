package com.kagg886.sylu_eoa.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseViewModel<T> : ViewModel() {
    private val _data = MutableStateFlow<T?>(null)
    private val _loading = MutableStateFlow<LoadingState>(LoadingState.NORMAL)
    private val _error = MutableStateFlow<Throwable?>(null)


    val data = _data.asStateFlow()
    val loading = _loading.asStateFlow()
    val error = _error.asStateFlow()

    fun loadData() {
        setDataLoading()
        viewModelScope.launch {
            try {
                setDataLoadSuccess(
                    withContext(Dispatchers.IO) {
                        onDataFetch()
                    }
                )
            } catch (t: Throwable) {
                setDataLoadError(t)
                return@launch
            }
        }
    }

    fun setDataLoading() {
        _loading.value = LoadingState.LOADING
        _data.value = null
        _error.value = null
    }

    open fun setDataLoadSuccess(new: T?) {
        _loading.value = LoadingState.SUCCESS
        _error.value = null
        _data.value = new
    }

    open fun setDataLoadError(t: Throwable) {
        _loading.value = LoadingState.FAILED
        _error.value = t
        _data.value = null
    }

    internal abstract suspend fun onDataFetch(): T?;

    fun clearLoading() {
        _loading.value = LoadingState.NORMAL
        _error.value = null
        _data.value = null
    }
}

interface LoadingState {
    object NORMAL : LoadingState
    object LOADING : LoadingState
    object SUCCESS : LoadingState
    object FAILED : LoadingState
}