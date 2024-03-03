package com.kagg886.sylu_eoa.screen.page

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kagg886.sylu_eoa.R
import com.kagg886.sylu_eoa.api.v2.bean.ClassUnit
import com.kagg886.sylu_eoa.api.v2.bean.findClassByWeek
import com.kagg886.sylu_eoa.ui.componment.ClassDialog
import com.kagg886.sylu_eoa.ui.componment.ErrorPage
import com.kagg886.sylu_eoa.ui.componment.Loading
import com.kagg886.sylu_eoa.ui.model.LoadingState
import com.kagg886.sylu_eoa.ui.model.impl.ClassTableViewModel
import com.kagg886.sylu_eoa.ui.model.impl.SchoolCalenderViewModel
import com.kagg886.sylu_eoa.ui.model.impl.SyluUserViewModel
import com.kagg886.sylu_eoa.ui.theme.Typography
import com.pushpal.jetlime.*
import java.time.LocalDate
import java.time.LocalTime

fun getTips(): Pair<String, String> {
    val now = LocalTime.now()

    return when {
        now < LocalTime.of(3, 0) -> "半夜了！" to "该睡觉啦！我的奶奶会给我念windows激活码助眠，你的奶奶会吗？" //0:00-3:00
        now < LocalTime.of(6, 0) -> "凌晨！" to "大学生不可能起的这么早！你究竟是谁？" //3:00-6:00
        now < LocalTime.of(11, 0) -> "早上好" to "愿世上没有早八！" //6:00-11:00
        now < LocalTime.of(13, 0) -> "中午好" to "干饭人万岁！" //11:00-13:00
        now < LocalTime.of(17, 0) -> "下午好" to "午睡有助于恢复精力！有课的除外（逃）" //13:00-17:00
        now < LocalTime.of(21, 0) -> "晚上好" to "今天要通宵玩游戏吗？"//17:00-21:00
        else -> "半夜了！" to "该睡觉啦！我的奶奶会给我念windows激活码助眠，你的奶奶会吗？" //21:00-0:00
    }
}

@Composable
fun MainPage() {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .weight(0.2f, fill = true)
            .padding(start = 25.dp)) {
            val (a, b) = getTips()
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Outlined.Email, contentDescription = "")
                Text(a, style = Typography.titleLarge)
            }
            Text(b, modifier = Modifier.padding(top = 20.dp))
        }
        Box(modifier = Modifier.weight(0.8f, fill = true), contentAlignment = Alignment.TopCenter) {
            Card(modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight()) {
                ClassSummary()
            }
        }
    }
}

@Composable
fun ClassSummary() {
    val tableModel: ClassTableViewModel = viewModel(viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner)
    val userModel: SyluUserViewModel = viewModel(viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner)
    val calenderViewModel: SchoolCalenderViewModel =
        viewModel(viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner)

    val data by tableModel.data.collectAsState()
    val user by userModel.data.collectAsState()
    val state by tableModel.loading.collectAsState()
    val state1 by calenderViewModel.loading.collectAsState()
    val err by tableModel.error.collectAsState()

    when (state) {
        LoadingState.NORMAL -> {
            LaunchedEffect(key1 = Unit) {
                calenderViewModel.loadData(user!!)
                tableModel.loadData()
            }
        }

        LoadingState.LOADING -> {
            Loading()
        }

        LoadingState.SUCCESS -> {
            if (state1 == LoadingState.SUCCESS) {
                TimeLineTable(data!!)
            } else {
                Loading()
            }
        }

        LoadingState.FAILED -> {
            if (err?.message == "need web") {
                LaunchedEffect(key1 = Unit) {
                    tableModel.loadDataByUser(user!!)
                }
                Loading()
                return
            }
            val err by tableModel.error.collectAsState()
            ErrorPage(ex = err) {
                tableModel.clearLoading()
            }
        }
    }
}

@Composable
fun TimeLineTable(data: List<ClassUnit>) {
    val calenderViewModel: SchoolCalenderViewModel =
        viewModel(viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner)

    val calender by calenderViewModel.data.collectAsState()

    val iconColor = if (isSystemInDarkTheme()) {
        Color.White
    } else {
        Color.Gray
    }

    val dayOfWeek by remember {
        mutableStateOf(LocalDate.now().dayOfWeek!!)
    }

    JetLimeColumn(
        modifier = Modifier.padding(16.dp),
        itemsList = ItemsList(data.findClassByWeek(calender!!.currentWeek())
            .filter { it.dayInWeek.toInt() == dayOfWeek.value }),
        key = { _, item -> item.hashCode() },
        style = JetLimeDefaults.columnStyle(
            contentDistance = 32.dp,
            itemSpacing = 16.dp,
            lineThickness = 2.dp,
            lineBrush = JetLimeDefaults.lineSolidBrush(color = iconColor),
        ),
    ) { _, unit, position ->
        val type = getTypeInClass(unit)
        JetLimeEvent(
            style = JetLimeEventDefaults.eventStyle(
                position = position,
                pointColor = Color(0xFF2889D6),
                pointFillColor = Color(0xFFD5F2FF),
                pointRadius = 14.dp,
                pointAnimation = if (type == ClassType.PROCESS) JetLimeEventDefaults.pointAnimation() else null,
                pointType = when (type) {
                    ClassType.SUCCESS -> EventPointType.custom(painterResource(id = R.drawable.ic_check))

                    else -> EventPointType.filled(fillPercent = 0.8f)
                },
                pointStrokeColor = MaterialTheme.colorScheme.onBackground,
            ),
        ) {
            var dialog by remember {
                mutableStateOf(false)
            }
            ClassDialog(onDismiss = { dialog = false }, unit = unit, dialog = dialog)

            Card(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(height = 90.dp)
                    .padding(3.dp)
                    .clickable {
                        dialog = true
                    },
                colors = CardDefaults.cardColors(
                    containerColor = Color(unit.name.hashCode())
                )
            ) {
                Column {
                    Text(unit.name, style = Typography.bodyLarge, maxLines = 3, overflow = TextOverflow.Ellipsis)
                    Text(unit.room, style = Typography.bodyMedium)
                    Text(getTime(unit).toString(), style = Typography.bodySmall)
                }
            }
        }
    }
}

private fun getTime(u: ClassUnit): Pair<LocalTime, LocalTime> {
    val dt = (u.rangeEveryDay[0] + 1) / 2 //1-2 3-4 5-6 7-8 9-10 11-12
    return when (dt) {
        1 -> LocalTime.of(8, 0) to LocalTime.of(9, 40)
        2 -> LocalTime.of(10, 0) to LocalTime.of(11, 40)
        3 -> LocalTime.of(13, 0) to LocalTime.of(14, 40)
        4 -> LocalTime.of(15, 0) to LocalTime.of(16, 40)
        5 -> LocalTime.of(17, 0) to LocalTime.of(18, 40)
        6 -> LocalTime.of(19, 0) to LocalTime.of(20, 40)
        else -> throw IllegalStateException("no this class")
    }
}

private fun getTypeInClass(u: ClassUnit, now: LocalTime = LocalTime.now()): ClassType {
    val (start, end) = getTime(u)
    return when {
        now < start -> ClassType.WAIT
        now > end -> ClassType.SUCCESS
        else -> ClassType.PROCESS
    }
}

enum class ClassType {
    WAIT, PROCESS, SUCCESS
}