package com.kagg886.sylu_eoa.util

import androidx.compose.runtime.Composable
import com.kagg886.sylu_eoa.R
import com.kagg886.sylu_eoa.screen.page.*

object PageConfig {
    val nav = listOf(
        PageItem("首页", R.drawable.outline_apps_24, "MainPage") @Composable { MainPage() },
        PageItem("课程表", R.drawable.baseline_calendar_month_24, "ClassTablePage") @Composable { ClassTablePage() },
        PageItem("考试", R.drawable.baseline_check_24, "ExamPage") @Composable { ExamPage() },
        PageItem("我", R.drawable.baseline_cyclone_24, "MePage") @Composable { MePage() },
    )

    val allPage: List<PageItem> = mutableListOf<PageItem>().apply {
        addAll(nav)
        add(PageItem("设置", 0, "SettingPage") @Composable { SettingPage() })
        add(PageItem("关于", 0, "AboutPage") @Composable { AboutPage() })
    }

    const val DEFAULT_ROUTER = "MainPage"

}

data class PageItem(val title: String, val icon: Int = 0, val router: String, val widget: @Composable () -> Unit)