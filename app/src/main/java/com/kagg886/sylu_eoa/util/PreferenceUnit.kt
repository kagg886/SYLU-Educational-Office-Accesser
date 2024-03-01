package com.kagg886.sylu_eoa.util

import androidx.datastore.preferences.core.*

open class PreferenceUnit<T>(
    val key: Preferences.Key<T>,
    val default: T
)


object Announcement : PreferenceUnit<String>(stringPreferencesKey("announcement"), "")

object Account : PreferenceUnit<String>(stringPreferencesKey("account"), "")
object Password : PreferenceUnit<String>(stringPreferencesKey("password"), "")
object StorePassword : PreferenceUnit<Boolean>(booleanPreferencesKey("store-password"), false)
object SkipLogin : PreferenceUnit<Boolean>(booleanPreferencesKey("skip-login"), false)


object DayExpired : PreferenceUnit<Int>(intPreferencesKey("day-expired"), 7)


object ClassList : PreferenceUnit<String>(stringPreferencesKey("class-list"), "")
object ClassListExpire : PreferenceUnit<Long>(longPreferencesKey("class-list-expire"), -1)

object SchoolCalenderBean : PreferenceUnit<String>(stringPreferencesKey("school-calender-bean"), "")
object SchoolCalenderBeanExpire : PreferenceUnit<Long>(longPreferencesKey("school-calender-bean-expire"), -1)

object ExamBean : PreferenceUnit<String>(stringPreferencesKey("exam-bean"), "")
object ExamBeanExpire : PreferenceUnit<Long>(longPreferencesKey("exam-bean-expire"), -1)


object PickerBean : PreferenceUnit<String>(stringPreferencesKey("picker"), "")
object PickerBeanExpire : PreferenceUnit<Long>(longPreferencesKey("picker-expire"), -1)


object ProfileBean : PreferenceUnit<String>(stringPreferencesKey("profile"), "")
object ProfileBeanExpire : PreferenceUnit<Long>(longPreferencesKey("profile-expire"), -1)