package com.kagg886.sylu_eoa.screen

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kagg886.sylu_eoa.api.v2.LoginFailedException
import com.kagg886.sylu_eoa.getApp
import com.kagg886.sylu_eoa.openURL
import com.kagg886.sylu_eoa.ui.model.impl.SyluUserViewModel
import com.kagg886.sylu_eoa.util.Account
import com.kagg886.sylu_eoa.util.Password
import com.kagg886.sylu_eoa.util.Promise
import kotlinx.coroutines.flow.first
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

@Composable
fun LoginScreen() {
    Scaffold { paddingValues ->
        LoginContent(modifier = Modifier.padding(paddingValues))
    }
}

@Composable
fun LoginContent(modifier: Modifier = Modifier) {
    val model: SyluUserViewModel = viewModel(LocalContext.current as ViewModelStoreOwner)
    val context = LocalContext.current

    val storePass by model.storePass.collectAsState()

    var user by remember(model.data) {
        mutableStateOf(model.data.value?.getUser() ?: "")
    }
    var pass by remember {
        mutableStateOf("")
    }

    var needCaptcha by remember {
        mutableStateOf(false)
    }

    var captcha by remember {
        mutableStateOf("")
    }
    var captchaImg by remember {
        mutableStateOf<ImageBitmap?>(null)
    }

    var logining by remember {
        mutableStateOf(false)
    }

    val promise by remember {
        mutableStateOf(Promise<ByteArray, String?> {
            needCaptcha = true
            captcha = ""
            captchaImg = BitmapFactory.decodeByteArray(it, 0, it!!.size).asImageBitmap()
        })
    }

    LaunchedEffect(key1 = Unit) {
        user = getApp().getConfig(Account).first()
        pass = getApp().getConfig(Password).first()

    }


    if (needCaptcha) {
        AlertDialog(
            onDismissRequest = {
                needCaptcha = false
                promise.resolve(null)
            },
            confirmButton = {
                TextButton(onClick = {
                    if (captcha.isEmpty()) {
                        return@TextButton
                    }
                    needCaptcha = false
                    promise.resolve(captcha)
                }) {
                    Text("确定")
                }
            }, text = {
                OutlinedTextField(onValueChange = {
                    captcha = it
                }, value = captcha, label = {
                    Text("输入验证码")
                }, trailingIcon = {
                    Image(
                        bitmap = captchaImg!!, contentDescription = ""
                    )
                })
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Login to SYLU",
            style = TextStyle(
                fontSize = 35.sp,
            )
        )

        OutlinedTextField(
            modifier = Modifier.padding(top = 40.dp),
            value = user,
            onValueChange = { user = it },
            label = {
                Text(text = "User Name")
            },
        )
        OutlinedTextField(
            modifier = Modifier.padding(top = 20.dp),
            value = pass,
            onValueChange = { pass = it },
            label = {
                Text(text = "Password")
            },
            visualTransformation = PasswordVisualTransformation()
        )

        Row(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth(0.6f)
        ) {
            Checkbox(
                checked = storePass,
                onCheckedChange = {
                    model.setStorePassword(it)
                },

                )
            Text("记住密码")
        }

        var errDialog by remember {
            mutableStateOf(false)
        }

        var errInfo by remember {
            mutableStateOf(Throwable())
        }

        if (errDialog) {
            AlertDialog(
                onDismissRequest = { errDialog = false }, confirmButton = {},
                title = {
                    Text(text = "登录失败")
                },
                text = {
                    when (errInfo) {
                        is TimeoutException -> Text(text = "网络连接超时，请检查网络质量后重试。")
                        is UnknownHostException -> Text(text = "未连接到网络。请连接到互联网后重试。")
                        else -> {
                            Text(text = errInfo.message ?: "未知错误")
                        }
                    }

                },
            )
        }

        Row(
            modifier = Modifier
                .padding(top = 60.dp)
                .fillMaxWidth(0.6f),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            if (!logining) {
                OutlinedButton(onClick = {
                    logining = true
                    model.login(user, pass, captchaHandler = {
                        promise.startForResult(it) ?: throw LoginFailedException("用户取消登录")
                    }, continueHandler = {
                        logining = false
                        if (it !== null) {
                            errDialog = true
                            errInfo = it
                        }
//                        if (it !== null) {
//                            context.toast(it.message ?: "未知错误")
//                        }
                    })
                }) {
                    Text(text = "Login!")
                }
            } else {
                CircularProgressIndicator()
            }
            TextButton(onClick = {
                context.openURL("https://jxw.sylu.edu.cn/pwdmgr/retake/index.zf")
            }) {
                Text(text = "Forget password?")
            }
        }
    }

}