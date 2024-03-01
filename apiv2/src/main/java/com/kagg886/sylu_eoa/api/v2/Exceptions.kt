package com.kagg886.sylu_eoa.api.v2

data class LoginFailedException(override val message: String): IllegalStateException(message)


data class DataFetchedException(override val message: String, override val cause: Throwable?): IllegalStateException(message,cause)