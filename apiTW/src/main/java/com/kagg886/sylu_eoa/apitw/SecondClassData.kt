package com.kagg886.sylu_eoa.apitw

import kotlinx.serialization.Serializable

@Serializable
data class SecondClassDataSummary(
    val id:String,
    val max:Double
)

operator fun MutableMap<SecondClassDataSummary,MutableList<SecondClassData>>.get(s:String):MutableList<SecondClassData> {
    return filter { it.key.id ==s }.toList()[0].second
}

fun MutableList<SecondClassData>.sum() {
    sumOf { it.score }
}
@Serializable
data class SecondClassData(
    val name:String,
    val sponsor:String,
    val time:String,
    val actor:String,
    val people:Int,
    val score:Double
)