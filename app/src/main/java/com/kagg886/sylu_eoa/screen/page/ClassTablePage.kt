package com.kagg886.sylu_eoa.screen.page

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kagg886.sylu_eoa.api.v2.bean.findClassByWeek
import com.kagg886.sylu_eoa.ui.componment.ClassPage
import com.kagg886.sylu_eoa.ui.componment.Loading
import com.kagg886.sylu_eoa.ui.model.LoadingState.*
import com.kagg886.sylu_eoa.ui.model.impl.ClassTableViewModel
import com.kagg886.sylu_eoa.ui.model.impl.SchoolCalenderViewModel
import com.kagg886.sylu_eoa.ui.model.impl.SyluUserViewModel
import com.kagg886.sylu_eoa.ui.theme.Typography
import com.kagg886.utils.createLogger

private val log = createLogger("MainPage")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Picker() {
    val calenderViewModel: SchoolCalenderViewModel =
        viewModel(viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner)
    val currentIndex by calenderViewModel.currentWeekIndex.collectAsState()
    val all by calenderViewModel.all.collectAsState()


    val init by remember {
        mutableIntStateOf(calenderViewModel.currentWeekIndex.value)
    }

    var choosePick by remember {
        mutableStateOf(false)
    }

    if (choosePick) {
        ModalBottomSheet(onDismissRequest = {
            choosePick = false
        }) {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items((1..all).toList()) {
                    ListItem(headlineContent = {
                        Text("第${it}周${if (it == init) "(当前周)" else ""}")
                    }, modifier = Modifier.clickable {
                        calenderViewModel.setCurrentSelectedWeek(it)
                        choosePick = false
                    })
                }
            }
        }
    }

    Text(
        "第${currentIndex}周，共${all}周",
        style = Typography.titleMedium,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().clickable {
            choosePick = true
        }.padding(top = 10.dp, bottom = 20.dp)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ClassTable() {
    val tableModel: ClassTableViewModel = viewModel(viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner)
    val calendarViewModel: SchoolCalenderViewModel =
        viewModel(viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner)

    val calender by calendarViewModel.data.collectAsState()

    val table by tableModel.data.collectAsState()
    val currentWeek by calendarViewModel.currentWeekIndex.collectAsState()
    val all by calendarViewModel.all.collectAsState()

    val init by remember {
        mutableIntStateOf(calendarViewModel.currentWeekIndex.value - 1)
    }

    val pagerState = rememberPagerState(
        initialPage = init,
        initialPageOffsetFraction = 0f,
    ) { all }

    LaunchedEffect(pagerState.currentPage) {
        calendarViewModel.setCurrentSelectedWeek(pagerState.currentPage + 1)
    }


    LaunchedEffect(currentWeek) {
        pagerState.animateScrollToPage(currentWeek - 1)
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { index -> //从0开始
        val week by remember(index) {
            mutableStateOf(calender!!.start.plusWeeks(index.toLong()))
        }
        val classDataByWeek by remember(week) {
            mutableStateOf(table!!.findClassByWeek(index + 1))
        }
        ClassPage(date = week, classDataByWeek)
    }
}

@Composable
fun ClassTablePage() {
    val tableModel: ClassTableViewModel = viewModel(viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner)
    val userModel: SyluUserViewModel = viewModel(viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner)
    val calenderViewModel: SchoolCalenderViewModel =
        viewModel(viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner)

    val user by userModel.data.collectAsState()
    val state by tableModel.loading.collectAsState()
    val state1 by calenderViewModel.loading.collectAsState()
    val err by tableModel.error.collectAsState()

    when (state) {
        NORMAL -> {
            LaunchedEffect(key1 = Unit) {
                calenderViewModel.loadData(user!!)
                tableModel.loadData()
            }
        }

        LOADING -> {
            Loading()
        }

        SUCCESS -> {
            if (state1 == SUCCESS) {
                Column {
                    Picker()
                    ClassTable()
                }
            } else {
                Loading()
            }
        }

        FAILED -> {
            if (err?.message == "need web") {
                LaunchedEffect(key1 = Unit) {
                    tableModel.loadDataByUser(user!!)
                }
                Loading()
                return
            }
            val err by tableModel.error.collectAsState()
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error!\n${err?.stackTraceToString()}")
            }
        }
    }
}