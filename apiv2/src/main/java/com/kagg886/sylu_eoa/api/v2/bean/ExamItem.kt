package com.kagg886.sylu_eoa.api.v2.bean

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.lang.Double
import kotlin.OptIn
import kotlin.String
import kotlin.getValue
import kotlin.lazy

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class ExamItem(
    @SerialName("xnm")
    internal val year: String,
    @SerialName("xqm")
    internal val semester: String,

    @SerialName("jxb_id")
    internal val detailsID: String,

//================下面是有用的信息================//
    @SerialName("kcmc")
    val name: String, //课程名称

    @SerialName("tjrxm")
    val teacher: String, //老师名字

    @SerialName("xf")
    val credit: String, //学分

    @SerialName("jd")
    val gradePoint: String, //绩点

    @SerialName("xfjd")
    val crTimesGp: String, //学分*绩点

    @SerialName("bfzcj")
    val absoluteScore: String, //考试绝对分数

    @SerialName("cj")
    val relateScore: String, //评级

    @SerialName("ksxzdm")
    val completionCode: String, //挂科标识

    @SerialName("sfxwkc")
    private val _degreeProgram:String //是否是学位课
) {
    val degreeProgram = _degreeProgram == "是"

    val examStatus by lazy {
        if (Double.parseDouble(absoluteScore).compareTo(60) == -1) {
            return@lazy ExamStatus.FAILED;
        } else {
            val ksxzdm = Integer.parseInt(completionCode);
            if (ksxzdm == 11 || ksxzdm == 16 || ksxzdm == 17) {
                return@lazy ExamStatus.RE_SUCCESS
            }
        }
        ExamStatus.SUCCESS
    }
}

enum class ExamStatus {
    SUCCESS, FAILED, RE_SUCCESS
}

fun List<ExamItem>.findListByTerm(picker: TermPicker): List<ExamItem> {
    return filter {
        val xnm = picker.asTerm().xnm
        val xqm = picker.asTerm().xqm

        if (xqm.isEmpty() && xnm.isEmpty()) {
            return@filter true
        }

        if (xnm.isEmpty()) {
            return@filter it.semester == xqm;
        }

        if (xqm.isEmpty()) {
            return@filter it.year == xnm
        }

        return@filter it.year == xnm && it.semester == xqm

    }
}