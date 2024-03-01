package com.kagg886.sylu_eoa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alorma.compose.settings.storage.base.rememberBooleanSettingState
import com.alorma.compose.settings.storage.base.rememberIntSettingState
import com.alorma.compose.settings.ui.SettingsGroup
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.alorma.compose.settings.ui.SettingsSlider
import com.alorma.compose.settings.ui.SettingsSwitch
import com.kagg886.sylu_eoa.ui.theme.SYLU_EOATheme
import com.kagg886.sylu_eoa.ui.theme.Typography
import com.kagg886.sylu_eoa.util.*

class SettingActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = getApp()
        setContent {
            val _storePass by app.getConfig(StorePassword).collectAsState(false)
            val _skipLogin by app.getConfig(SkipLogin).collectAsState(false)
            val _expire by app.getConfig(DayExpired).collectAsState(7)


            val storePass = rememberBooleanSettingState()
            val skipLogin = rememberBooleanSettingState()
            val expire = rememberIntSettingState()

            LaunchedEffect(key1 = _storePass) {
                storePass.value = _storePass
            }
            LaunchedEffect(key1 = _skipLogin) {
                skipLogin.value = _skipLogin
            }
            LaunchedEffect(key1 = _expire) {
                expire.value = _expire
            }


            SYLU_EOATheme {
                Scaffold(
                    topBar = {
                        TopAppBar(title = {
                            Text("设置")
                        })
                    }
                ) { paddingValues ->
                    Column(modifier = Modifier.padding(paddingValues).verticalScroll(rememberScrollState())) {
                        SettingsGroup(title = {
                            Text("登录")
                        }) {
                            Column(modifier = Modifier.height(75.dp)) {
                                SettingsSwitch(title = {
                                    Text("记住密码")
                                }, subtitle = {
                                    Text("登录检验失败时自动使用密码登录")
                                }, state = storePass) {
                                    app.updateConfig(StorePassword, it)
                                }
                            }

                            SettingsSwitch(title = {
                                Text("离线模式")
                            }, subtitle = {
                                Text("跳过登录检验。当登录凭证过期时有闪退风险。")
                            }, state = skipLogin,modifier = Modifier.height(85.dp)) {
                                app.updateConfig(SkipLogin, it)
                            }
                        }

                        SettingsGroup(title = {
                            Text("数据")
                        }) {
                            SettingsSlider(modifier = Modifier.height(100.dp), state = expire, title = {
                                Column {
                                    Text("数据过期时间：${_expire}天")
                                    if (expire.value == 0) {
                                        Text("该设置设置为0意味着每次进入都将刷新最新数据。若非开发者，请最好不要怎么做", color = Color.Red, style = Typography.labelMedium)
                                    }
                                }
                            }, onValueChange = {
                                app.updateConfig(DayExpired, it)
                            }, valueRange = 0f..30f, steps = 30)
                        }

                        var cacheClearDialog by remember {
                            mutableStateOf(false)
                        }

                        if (cacheClearDialog) {
                            AlertDialog(onDismissRequest = {
                                cacheClearDialog = false
                            }, confirmButton = {
                                TextButton(onClick = {
                                    app.updateConfig(ProfileBeanExpire)
                                    app.updateConfig(ClassListExpire)
                                    app.updateConfig(ExamBeanExpire)
                                    app.updateConfig(SchoolCalenderBeanExpire)
                                    app.updateConfig(PickerBeanExpire)
                                    app.toast("清空成功!重启生效")
                                    finishAffinity()
                                }) {
                                    Text("确定")
                                }
                            }, dismissButton = {
                                TextButton(onClick = {
                                    cacheClearDialog = false
                                }) {
                                    Text("取消")
                                }
                            }, title = {
                                Text("清空缓存")
                            }, text = {
                                Text("这么做会重新拉取数据。\n注意：如果开启离线登录很有可能会因为Cookie过期而导致进不去软件")
                            })
                        }

                        SettingsMenuLink(title = {
                            Text("立即清空缓存")
                        }, subtitle = {
                            Text("及时拉取最新缓存。若配合'离线登录'有闪退风险")
                        }) {
                            cacheClearDialog = true
                        }

                        var restoreSettingDialog by remember {
                            mutableStateOf(false)
                        }

                        if (restoreSettingDialog) {
                            AlertDialog(onDismissRequest = {
                                restoreSettingDialog = false
                            }, confirmButton = {
                                TextButton(onClick = {
                                    app.updateConfig(SkipLogin)
                                    app.updateConfig(DayExpired)
                                    app.toast("清空成功!重启生效")
                                    finishAffinity()
                                }) {
                                    Text("确定")
                                }
                            }, dismissButton = {
                                TextButton(onClick = {
                                    restoreSettingDialog = false
                                }) {
                                    Text("取消")
                                }
                            }, title = {
                                Text("恢复默认设置")
                            }, text = {
                                Text("这么做会恢复默认设置，确定要这么做吗？")
                            })
                        }
                        SettingsMenuLink(title = {
                            Text("恢复默认设置")
                        }, subtitle = {
                            Text("如果你不记得默认的设置是什么样，可以点击此条目以恢复")
                        }) {
                            restoreSettingDialog = true
                        }
                    }
                }
            }
        }
    }
}