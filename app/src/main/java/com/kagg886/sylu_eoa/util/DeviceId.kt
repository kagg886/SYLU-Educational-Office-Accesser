package com.kagg886.sylu_eoa.util

import android.annotation.SuppressLint
import android.content.Context
import com.kagg886.sylu_eoa.getApp
import java.util.*

@SuppressLint("HardwareIds")
fun getDeviceId(app:Context = getApp()): String {
    val info = app.packageManager.getPackageInfo(app.packageName, 0)

    val byte = ByteArray(64)
    Random(info.firstInstallTime).nextBytes(byte)
    return Base64.getEncoder().encodeToString(byte);
}