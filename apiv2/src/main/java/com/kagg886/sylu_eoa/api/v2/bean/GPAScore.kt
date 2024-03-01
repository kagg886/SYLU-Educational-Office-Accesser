package com.kagg886.sylu_eoa.api.v2.bean

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GPAScore(
    @SerialName("xmnr")
    val name:String,
    @SerialName("yxfz")
    val score:String
)