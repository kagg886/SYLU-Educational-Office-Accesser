package com.kagg886.sylu_eoa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kagg886.sylu_eoa.screen.LoginScreen
import com.kagg886.sylu_eoa.screen.MainScreen
import com.kagg886.sylu_eoa.ui.componment.Loading
import com.kagg886.sylu_eoa.ui.model.GlobalCrashViewModel
import com.kagg886.sylu_eoa.ui.model.LoadingState
import com.kagg886.sylu_eoa.ui.model.impl.AppOnlineConfigViewModel
import com.kagg886.sylu_eoa.ui.model.impl.SyluUserViewModel
import com.kagg886.sylu_eoa.ui.theme.SYLU_EOATheme
import com.kagg886.utils.createLogger
import okio.IOException
import kotlin.system.exitProcess

private val log = createLogger("MainActivity")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SYLU_EOATheme(darkTheme = isSystemInDarkTheme()) {
                CheckUpdate()
                Main()
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

    val crashData by GlobalCrashViewModel.crash.collectAsState()
    val crash by GlobalCrashViewModel.crashed.collectAsState()
    val f by GlobalCrashViewModel.file.collectAsState()

    if (crash) {
        AlertDialog(onDismissRequest = {
            exitProcess(1)
        }, confirmButton = {
            Button(onClick = {
                exitProcess(1)
            }) {
                Text("退出程序")
            }
        }, title = {
            Text("App遇到了崩溃错误!")
        }, text = {
            Column(modifier = Modifier
                .fillMaxHeight(0.7f)
                .verticalScroll(rememberScrollState())) {
                Text("详细信息请长按复制以下路径，然后前往文件管理器查看。")
                SelectionContainer {
                    Text(f, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }

                var showCrash by remember {
                    mutableStateOf(false)
                }
                TextButton(onClick = { showCrash = !showCrash }) {
                    Text("点我显示高级调试信息(反馈问题时务必使用长截图或录屏)", fontSize = 9.sp)
                }
                if (showCrash) {
                    Text(crashData!!.stackTraceToString())
                }

            }
        })
    }

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