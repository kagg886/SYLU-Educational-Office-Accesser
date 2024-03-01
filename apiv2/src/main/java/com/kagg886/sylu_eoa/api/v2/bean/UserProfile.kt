package com.kagg886.sylu_eoa.api.v2.bean

import kotlinx.serialization.Serializable


/**
 * 用户的基本信息
 *
 * @author kagg886
 * @date 2023/9/4 10:42
 */
//    private String name; //姓名
//    private String collegeName; //学院
//    private String studyName; //专业
//    private byte[] avatar; //头像
//
//    private String email; //邮箱
//    private String phone; //手机
//    private String id; //身份证
//    private String policy; //政治面貌
//    private String language; //外语语种
@Serializable
data class UserProfile(
    val name: String,
    val collegeName: String,
    val studyName: String,
    val avatar: ByteArray,

    val email: String,
    val phone: String,
    val id: String,
    val policy: String,
    val language: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserProfile

        if (name != other.name) return false
        if (collegeName != other.collegeName) return false
        if (studyName != other.studyName) return false
        if (!avatar.contentEquals(other.avatar)) return false
        if (email != other.email) return false
        if (phone != other.phone) return false
        if (id != other.id) return false
        if (policy != other.policy) return false
        if (language != other.language) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + collegeName.hashCode()
        result = 31 * result + studyName.hashCode()
        result = 31 * result + avatar.contentHashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + phone.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + policy.hashCode()
        result = 31 * result + language.hashCode()
        return result
    }

}
