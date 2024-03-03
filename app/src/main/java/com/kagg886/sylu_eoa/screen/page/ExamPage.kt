package com.kagg886.sylu_eoa.screen.page

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kagg886.sylu_eoa.api.v2.bean.ExamStatus.*
import com.kagg886.sylu_eoa.api.v2.bean.TERM_ALL_PICKER
import com.kagg886.sylu_eoa.api.v2.bean.findListByTerm
import com.kagg886.sylu_eoa.ui.componment.Details
import com.kagg886.sylu_eoa.ui.componment.ErrorPage
import com.kagg886.sylu_eoa.ui.componment.Loading
import com.kagg886.sylu_eoa.ui.model.LoadingState
import com.kagg886.sylu_eoa.ui.model.impl.ExamDetailsViewModel
import com.kagg886.sylu_eoa.ui.model.impl.ExamViewModel
import com.kagg886.sylu_eoa.ui.model.impl.PickerViewModel
import com.kagg886.sylu_eoa.ui.model.impl.SyluUserViewModel
import com.kagg886.sylu_eoa.ui.theme.Typography

@Composable
fun ExamPage() {
    Column(modifier = Modifier.fillMaxSize()) {
        PickerContainer()
        ExamContainer()
    }
}

@Composable
fun ExamContainer() {
    val userModel: SyluUserViewModel = viewModel(viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner)
    val examViewModel: ExamViewModel = viewModel(viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner)
    val pickerViewModel: PickerViewModel = viewModel(viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner)

    val picker by pickerViewModel.currentTermPicker.collectAsState()
    val state by examViewModel.loading.collectAsState()
    val data by examViewModel.data.collectAsState()
    val err by examViewModel.error.collectAsState()
    val user by userModel.data.collectAsState()

    when (state) {
        LoadingState.NORMAL -> {
            LaunchedEffect(key1 = Unit) {
                examViewModel.loadData()
            }
        }

        LoadingState.LOADING -> {
            Loading()
        }

        LoadingState.SUCCESS -> {
            val s by remember(picker) {
                mutableStateOf(data!!.findListByTerm(picker!!))
            }
            if (s.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("该学期无考试结果！")
                }
                return
            }
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(s) {
                    var dialog by remember {
                        mutableStateOf(false)
                    }

                    val model: ExamDetailsViewModel = viewModel()
                    if (dialog) {
                        AlertDialog(onDismissRequest = {
                            dialog = false
                            model.clearLoading()
                        }, confirmButton = {}, text = {
                            val state by model.loading.collectAsState()
                            val data by model.data.collectAsState()
                            when (state) {
                                LoadingState.NORMAL -> {
                                    LaunchedEffect(key1 = Unit) {
                                        model.loadDataByUser(user!!,it)
                                    }
                                }

                                LoadingState.LOADING -> {
                                    Loading(fullScreen = false)
                                }

                                LoadingState.SUCCESS -> {
                                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                        items(data?: listOf()) {
                                            ListItem(headlineContent = {
                                                Text(it[0])
                                            })
                                        }
                                    }
                                }

                                LoadingState.FAILED -> {
                                    val err by model.error.collectAsState()
                                    ErrorPage(ex = err, modifier = Modifier.height(108.dp)) {
                                        model.clearLoading()
                                    }
//                                    Text("检测到Cookie过期！\n这大概率是你开启了离线模式。\n请前往设置关闭 '离线模式' 后重新启动。\n详细报错信息：\n${err?.stackTraceToString() ?: "未知错误"}", modifier = Modifier.verticalScroll(rememberScrollState()))
                                }
                            }
                        }, title = {
                            Text("考试: ${it.name} 成绩详情")
                        })
                    }
                    ListItem(headlineContent = {
                        Text(
                            "${it.name} ${it.relateScore}(${it.absoluteScore})",
                            color = if (it.degreeProgram) Color.Red else Color.Unspecified
                        )
                    }, trailingContent = {
                        Text("${it.credit} ${it.gradePoint} ${it.crTimesGp}")
                    }, overlineContent = {
                        Text(it.teacher)
                    }, leadingContent = {
                        when (it.examStatus) {
                            SUCCESS -> Text("过")
                            FAILED -> Text("挂")
                            RE_SUCCESS -> Text("补")
                        }
                    }, modifier = Modifier.clickable {
                        dialog = true
                    })
                }
            }
        }

        LoadingState.FAILED -> {
            if (err?.message == "need web") {
                LaunchedEffect(key1 = Unit) {
                    examViewModel.loadDataByUser(user!!)
                }
                Loading()
                return
            }
            ErrorPage(ex = err) {
                examViewModel.clearLoading()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickerContainer() {
    val userModel: SyluUserViewModel = viewModel(viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner)
    val pickerViewModel: PickerViewModel = viewModel(viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner)


    val data by pickerViewModel.data.collectAsState()
    val current by pickerViewModel.currentTermPicker.collectAsState()
    val state by pickerViewModel.loading.collectAsState()
    val err by pickerViewModel.error.collectAsState()
    val user by userModel.data.collectAsState()

    var show by remember { mutableStateOf(false) }
    when (state) {
        LoadingState.NORMAL -> {
            LaunchedEffect(key1 = Unit) {
                pickerViewModel.loadData()
            }
        }

        LoadingState.LOADING -> {
            Loading()
        }

        LoadingState.SUCCESS -> {
            if (show) {
                ModalBottomSheet(onDismissRequest = {
                    show = false
                }) {
                    LazyColumn {
                        items(
                            data!!.list.filter { it.asTerm().xnm.isEmpty().not() }.toMutableList()
                                .also { it.add(0, TERM_ALL_PICKER) }) {
                            ListItem(
                                headlineContent = {
                                    Text(it.toString())
                                },
                                modifier = Modifier.clickable {
                                    show = false
                                    pickerViewModel.setCurrentTermPicker(it)
                                },
                                colors = if (it == current!!) ListItemDefaults.colors(containerColor = Color.Cyan) else ListItemDefaults.colors()
                            )
                        }
                    }
                }
            }

            Text(
                current.toString(),
                style = Typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        show = true
                    }
                    .padding(top = 10.dp, bottom = 20.dp)
            )
        }

        LoadingState.FAILED -> {
            if (err?.message == "need web") {
                LaunchedEffect(key1 = Unit) {
                    pickerViewModel.loadDataByUser(user!!)
                }
                Loading()
                return
            }
            ErrorPage(ex = err) {
                pickerViewModel.clearLoading()
            }
        }
    }
}