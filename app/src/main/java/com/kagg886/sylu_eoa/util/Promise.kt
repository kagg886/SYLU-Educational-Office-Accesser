package com.kagg886.sylu_eoa.util

import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class Promise<SEND, RESULT>(val onSend: Scope<SEND, RESULT>.(SEND?) -> Unit) {
    private var continuation: Continuation<RESULT>? = null

    interface Scope<SEND, RESULT> {
        fun resolve(result: RESULT)
    }

    private val scope = object : Scope<SEND, RESULT> {
        override fun resolve(result: RESULT) {
            continuation?.resume(result)
            continuation = null
        }
    }

    suspend fun startForResult(intent: SEND? = null): RESULT {
        if (continuation != null) {
            throw IllegalStateException("Task was already started...")
        }
        return suspendCoroutine { cont ->
            continuation = cont
            onSend(scope, intent)
        }
    }

    fun resolve(result: RESULT) {
        scope.resolve(result)
    }
}