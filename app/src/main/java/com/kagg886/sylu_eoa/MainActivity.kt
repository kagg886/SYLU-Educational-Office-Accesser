package com.kagg886.sylu_eoa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kagg886.sylu_eoa.screen.LoginScreen
import com.kagg886.sylu_eoa.screen.MainScreen
import com.kagg886.sylu_eoa.ui.componment.Loading
import com.kagg886.sylu_eoa.ui.componment.MaskAnimModel
import com.kagg886.sylu_eoa.ui.componment.MaskBox
import com.kagg886.sylu_eoa.ui.model.LoadingState
import com.kagg886.sylu_eoa.ui.model.impl.AppOnlineConfigViewModel
import com.kagg886.sylu_eoa.ui.model.impl.SyluUserViewModel
import com.kagg886.sylu_eoa.ui.theme.SYLU_EOATheme
import com.kagg886.sylu_eoa.util.NightMode
import com.kagg886.utils.createLogger
import okio.IOException

private val log = createLogger("MainActivity")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //0跟随系统，1为强制日间，2为强制夜间
            val nightMode by getApp().getConfig(NightMode).collectAsState(0)
            val isNightSystem = isSystemInDarkTheme()


            var isDark by remember { //延迟变换
                mutableStateOf(false)
            }

            LaunchedEffect(key1 = nightMode) {
//                delay(1)
                isDark = when (nightMode) {
                    2 -> true
                    1 -> false
                    0 -> isNightSystem
                    else -> throw IllegalStateException("bad theme code")
                }
            }

            SYLU_EOATheme(
                darkTheme = isDark
            ) {
                MaskBox(
                    animTime = 1000L,
                    maskComplete = {
                    },
                    animFinish = {},
                ) { emit ->
                    //非延迟变换
                    LaunchedEffect(key1 = nightMode) {
                        emit(MaskAnimModel.EXPEND, 0F, 0F)
                    }
                    CheckUpdate()
                    Main()
                }

            }
        }
    }
}

@Composable
fun CheckUpdate() {
    val updateModel: AppOnlineConfigViewModel = viewModel()

    val state by updateModel.loading.collectAsState()
    val data by updateModel.data.collectAsState()
    val err by updateModel.error.collectAsState()
    val broadcast by updateModel.broadcast.collectAsState()
    val oldCode by remember {
        mutableStateOf(getApp().packageManager.getPackageInfo(getApp().packageName, 0).versionName)
    }

    when (state) {
        LoadingState.NORMAL -> {
            updateModel.loadData()
        }

        LoadingState.SUCCESS -> {

            var open1 by remember {
                mutableStateOf(true)
            }
            if (broadcast.isNotEmpty() && open1) {
                AlertDialog(
                    onDismissRequest = {},
                    confirmButton = {
                        TextButton(onClick = {
                            open1 = false
                        }) {
                            Text("确定")
                        }
                    }, title = {
                        Text("有新公告！")
                    }, text = {
                        Text(broadcast)
                    }
                )
            }


            var open by remember {
                mutableStateOf(true)
            }
            if (data!!.name != oldCode && open) {
                AlertDialog(
                    onDismissRequest = {},
                    confirmButton = {
                        TextButton(onClick = {
                            getApp().openURL(data!!.assets.filter {
                                it.name == "app-release.apk"
                            }[0].browser_download_url)
                        }) {
                            Text("下载")
                        }
                    }, dismissButton = {
                        TextButton(onClick = {
                            open = false
                        }) {
                            Text("不更新")
                        }
                    }, title = {
                        Text("有更新！${data!!.name}")
                    }, text = {
                        Text(data!!.body)
                    }
                )
            }
        }

        LoadingState.FAILED -> {
            LaunchedEffect(key1 = Unit) {
                log.w("更新检查失败", err)
                getApp().toast("更新检查失败...")
            }
        }
    }
}

@Composable
fun Main() {
    val syluUserViewModel: SyluUserViewModel = viewModel(LocalContext.current as ViewModelStoreOwner)

    val loading by syluUserViewModel.loading.collectAsState()
    val err by syluUserViewModel.error.collectAsState()

    when (loading) {
        //开始加载
        LoadingState.NORMAL -> {
            LaunchedEffect(key1 = Unit) {
                syluUserViewModel.loadData()
            }
        }

        //加载中
        LoadingState.LOADING -> {
            Loading()
        }

        //成功登录
        LoadingState.SUCCESS -> {
            MainScreen()
        }
        //登录失败或从未登录
        LoadingState.FAILED -> {
            if (err?.message != "未登录") {
                var dialog by remember {
                    mutableStateOf(true)
                }

                if (err is IOException) {
                    syluUserViewModel.setSkipCheckLogin(true)
                    syluUserViewModel.clearLoading()
                    LocalContext.current.toast("网络连接失败，自动开启离线模式!")
                    return
                }

                if (dialog) {
                    AlertDialog(onDismissRequest = {
                        dialog = false
                    }, confirmButton = {}, title = {
                        Text("登录失败")
                    }, text = {
                        Text(err?.stackTraceToString() ?: "未知异常")
                    })
                }
            }
            log.w("登录检查失败", err)
            LoginScreen()
        }
    }
}