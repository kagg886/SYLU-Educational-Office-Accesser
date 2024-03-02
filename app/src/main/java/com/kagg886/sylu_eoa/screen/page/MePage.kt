package com.kagg886.sylu_eoa.screen.page

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kagg886.sylu_eoa.AboutActivity
import com.kagg886.sylu_eoa.SettingActivity
import com.kagg886.sylu_eoa.getApp
import com.kagg886.sylu_eoa.toast
import com.kagg886.sylu_eoa.ui.componment.Details
import com.kagg886.sylu_eoa.ui.componment.ErrorPage
import com.kagg886.sylu_eoa.ui.componment.Loading
import com.kagg886.sylu_eoa.ui.model.LoadingState
import com.kagg886.sylu_eoa.ui.model.impl.ProfileViewModel
import com.kagg886.sylu_eoa.ui.model.impl.SyluUserViewModel

@Composable
fun MePage() {

    val avt = LocalContext.current
    val userModel: SyluUserViewModel = viewModel(viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner)
    val profileModel: ProfileViewModel = viewModel(viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner)

    val user by userModel.data.collectAsState()
    val profile by profileModel.data.collectAsState()

    val state by profileModel.loading.collectAsState()
    val err by profileModel.error.collectAsState()

    when (state) {
        LoadingState.NORMAL -> {
            LaunchedEffect(key1 = Unit) {
                profileModel.loadData()
            }
        }

        LoadingState.LOADING -> {
            Loading()
        }

        LoadingState.SUCCESS -> {
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                Card(modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .padding(top = 30.dp, bottom = 30.dp)) {
                    val profile = profile!!
                    val byte = profile.avatar
                    Row(modifier = Modifier.height(120.dp)) {
                        Image(
                            bitmap = BitmapFactory.decodeByteArray(byte, 0, byte.size).asImageBitmap(),
                            contentDescription = "头像",
                        )
                        Column(modifier = Modifier.fillMaxHeight()) {
                            var dialog by remember {
                                mutableStateOf(false)
                            }

                            if (dialog) {
                                AlertDialog(onDismissRequest = { dialog = false }, confirmButton = {}, title = {
                                    Text("个人信息")
                                }, text = {
                                    Column {
                                        Details("姓名", profile.name)
                                        Details("学号", user!!.getUser())
                                        Details("学院", profile.collegeName)
                                        Details("专业", profile.studyName)
                                        Details("政治面貌", profile.policy)
                                        Details("电话", profile.phone)
                                        Details("邮箱", profile.email)
                                        Details("外语语种", profile.language)
                                    }
                                })
                            }
                            Text(buildAnnotatedString {
                                withStyle(ParagraphStyle(textIndent = TextIndent(10.sp))) {
                                    withStyle(SpanStyle(fontSize = 25.sp, fontWeight = FontWeight.Bold)) {
                                        append(profile.name)
                                    }
                                    append("\n")
                                    append(profile.collegeName)
                                    append("\n")
                                    append(profile.studyName)
                                }
                            })
                            TextButton(onClick = { dialog = true }) {
                                Text("查看详情")
                            }
                        }
                    }
                }

                var exitDialog by remember {
                    mutableStateOf(false)
                }

                if (exitDialog) {
                    AlertDialog(onDismissRequest = {
                        exitDialog = false
                    }, confirmButton = {
                        TextButton(onClick = {
                            userModel.clearLogin()
                            getApp().toast("退出登录成功!")
                        }) {
                            Text("确定")
                        }
                    }, dismissButton = {
                        TextButton(onClick = {
                            exitDialog = false
                        }) {
                            Text("取消")
                        }
                    }, title = {
                        Text("退出登录")
                    }, text = {
                        Text("这么做会清空登录信息并重新登录，确定要这么做吗？")
                    })
                }
                Column(modifier = Modifier.fillMaxWidth(0.9f)) {
                    ListItem(headlineContent = {
                        Text("退出登录")
                    }, leadingContent = {
                        Icon(imageVector = Icons.AutoMirrored.Outlined.ExitToApp, contentDescription = "")
                    }, modifier = Modifier.clickable {
                        exitDialog = true
                    })
                    ListItem(headlineContent = {
                        Text("设置")
                    }, leadingContent = {
                        Icon(imageVector = Icons.Outlined.Settings, contentDescription = "")
                    }, modifier = Modifier.clickable {
                        avt.startActivity(Intent(avt, SettingActivity::class.java))
                    })
                    ListItem(headlineContent = {
                        Text("关于")
                    }, leadingContent = {
                        Icon(imageVector = Icons.Outlined.Star, contentDescription = "")
                    }, modifier = Modifier.clickable {
                        avt.startActivity(Intent(avt,AboutActivity::class.java))
                    })
                }
            }
        }

        LoadingState.FAILED -> {
            if (err?.message == "need web") {
                LaunchedEffect(key1 = Unit) {
                    profileModel.loadDataByUser(user!!)
                }
                Loading()
                return
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                ErrorPage(ex = err, modifier = Modifier.weight(0.2f)) {
                    profileModel.clearLoading()
                }

                HorizontalDivider()

                Column(modifier = Modifier.fillMaxSize(0.9f).weight(0.8f), horizontalAlignment = Alignment.CenterHorizontally) {
                    var exitDialog by remember {
                        mutableStateOf(false)
                    }

                    if (exitDialog) {
                        AlertDialog(onDismissRequest = {
                            exitDialog = false
                        }, confirmButton = {
                            TextButton(onClick = {
                                userModel.clearLogin()
                                getApp().toast("退出登录成功!")
                            }) {
                                Text("确定")
                            }
                        }, dismissButton = {
                            TextButton(onClick = {
                                exitDialog = false
                            }) {
                                Text("取消")
                            }
                        }, title = {
                            Text("退出登录")
                        }, text = {
                            Text("这么做会清空登录信息并重新登录，确定要这么做吗？")
                        })
                    }


                    ListItem(headlineContent = {
                        Text("退出登录")
                    }, leadingContent = {
                        Icon(imageVector = Icons.AutoMirrored.Outlined.ExitToApp, contentDescription = "")
                    }, modifier = Modifier.clickable {
                        exitDialog = true
                    })
                    ListItem(headlineContent = {
                        Text("设置")
                    }, leadingContent = {
                        Icon(imageVector = Icons.Outlined.Settings, contentDescription = "")
                    }, modifier = Modifier.clickable {
                        avt.startActivity(Intent(avt, SettingActivity::class.java))
                    })
                    ListItem(headlineContent = {
                        Text("关于")
                    }, leadingContent = {
                        Icon(imageVector = Icons.Outlined.Star, contentDescription = "")
                    }, modifier = Modifier.clickable {
                        avt.startActivity(Intent(avt,AboutActivity::class.java))
                    })
                }
            }

        }
    }
}