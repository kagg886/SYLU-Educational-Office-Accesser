package com.kagg886.sylu_eoa.ui.componment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kagg886.sylu_eoa.ui.theme.Typography

@Composable
fun ErrorPage(modifier: Modifier = Modifier, ex: Throwable?, onReloadBtnClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center, modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row {
                Icon(imageVector = Icons.Outlined.Lock, contentDescription = "Error")
                Text("出错了！", style = Typography.titleLarge)
            }
            var dialog by remember {
                mutableStateOf(false)
            }
            if (dialog) {
                AlertDialog(onDismissRequest = { dialog = false },
                    confirmButton = { },
                    title = { Text(text = "详细报错信息") },
                    text = {
                        Text(
                            text = ex?.stackTraceToString() ?: "未知报错", modifier = Modifier.verticalScroll(
                                rememberScrollState()
                            )
                        )
                    })
            }
            Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { dialog = true }) {
                    Text(text = "详细信息")
                }
                Button(onClick = onReloadBtnClick) {
                    Text("重新加载")
                }
            }
        }
    }
}