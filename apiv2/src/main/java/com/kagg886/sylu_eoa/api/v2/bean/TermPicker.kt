package com.kagg886.sylu_eoa.api.v2.bean

import kotlinx.serialization.Serializable

@Serializable
data class TermPicker(
    private val yearName: Pair<String, String>,
    private val yearCode: Pair<String, String>
) {
    fun asTerm(): Term {
        return Term(yearName.second, yearCode.second)
    }

    override fun toString(): String {
        return when {
//            yearName.second.isEmpty() && yearCode.second.isEmpty() -> "全部"
//            yearName.second.isEmpty() -> "全部学年的${yearCode.first}学期"
//            yearCode.second.isEmpty() -> "${yearCode.first}学年的全部学期"
            else-> "${yearName.first}年度${yearCode.first}学期"
        }
    }

    override fun hashCode(): Int {
        var result = yearName.hashCode()
        result = 31 * result + yearCode.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TermPicker

        if (yearName != other.yearName) return false
        if (yearCode != other.yearCode) return false

        return true
    }
}

@Serializable
data class Term(val xnm: String, val xqm: String)


val TERM_ALL_PICKER = TermPicker("全部" to "", "全部" to "")
internal val TERM_ALL = TERM_ALL_PICKER.asTerm()

@Serializable
data class TermResult(val list: List<TermPicker>, val default: TermPicker)