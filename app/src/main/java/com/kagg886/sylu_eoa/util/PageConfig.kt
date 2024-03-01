package com.kagg886.sylu_eoa.util

import android.graphics.Bitmap
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.kagg886.sylu_eoa.R
import com.kagg886.sylu_eoa.screen.page.ClassTablePage
import com.kagg886.sylu_eoa.screen.page.ExamPage
import com.kagg886.sylu_eoa.screen.page.MainPage
import com.kagg886.sylu_eoa.screen.page.MePage

object PageConfig {
    val list = listOf(
        PageItem("首页", R.drawable.outline_apps_24, "MainPage") @Composable { MainPage() },
        PageItem("课程表", R.drawable.baseline_calendar_month_24, "ClassTablePage") @Composable { ClassTablePage() },
        PageItem("考试", R.drawable.baseline_check_24, "ExamPage") @Composable { ExamPage() },
        PageItem("我", R.drawable.baseline_cyclone_24, "MePage") @Composable { MePage() },
    )

    const val DEFAULT_ROUTER = "MainPage"

}

data class PageItem(val title: String, val icon: Int, val router: String, val widget: @Composable () -> Unit)