package com.kagg886.utils

private val loggers = mutableListOf<LoggerReceiver>()

fun registryLogReceiver(i: LoggerReceiver) {
    loggers.add(i)
}

fun createLogger(tag: String): Logger {
    return object : Logger {
        override fun d(msg: String) {
            loggers.forEach {
                it.d("[$tag]: $msg")
            }
        }

        override fun i(msg: String) {
            loggers.forEach {
                it.i("[$tag]: $msg")
            }
        }

        override fun w(msg: String, t: Throwable?) {
            loggers.forEach {
                var stack = t?.stackTraceToString() ?: ""
                if (stack.isNotEmpty()) {
                    stack = "\n$stack"
                }
                it.w("[$tag]: ${msg}$stack")
            }
        }

        override fun e(msg: String, t: Throwable?) {
            loggers.forEach {
                var stack = t?.stackTraceToString() ?: ""
                if (stack.isNotEmpty()) {
                    stack = "\n$stack"
                }
                it.e("[$tag]: ${msg}$stack")
            }
        }

    }
}

interface LoggerReceiver {
    fun d(msg: String)
    fun i(msg: String)
    fun w(msg: String)
    fun e(msg: String)
}


interface Logger {
    fun d(msg: String)
    fun i(msg: String)
    fun w(msg: String, t: Throwable? = null)
    fun e(msg: String, t: Throwable? = null)
}