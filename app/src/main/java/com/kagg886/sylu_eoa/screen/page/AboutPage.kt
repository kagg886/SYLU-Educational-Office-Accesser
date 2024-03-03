package com.kagg886.sylu_eoa.screen.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.kagg886.sylu_eoa.getApp
import com.kagg886.sylu_eoa.openURL
import com.kagg886.sylu_eoa.ui.theme.Typography

@Composable
fun AboutPage() {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var egg by remember {
                mutableIntStateOf(0)
            }
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp)
                .clickable {
                    egg++
                }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(com.kagg886.sylu_eoa.R.drawable.ic_launcher),
                        contentDescription = "",
                        modifier = Modifier.padding(30.dp).size(80.dp,80.dp)
                    )
                    Text(if (egg < 100) "有你所在的日子，便是奇迹。" else "真的是奇迹欸！你已经点了${egg}下，加油！", modifier = Modifier.padding(top = 10.dp))
                }
            }
            AboutItem(
                title = "软件版本",
                content = LocalContext.current.packageManager.getPackageInfo(LocalContext.current.packageName, 0).versionName,
                icon = Icons.Outlined.Star
            )
            AboutItem(
                title = "查看源代码",
                icon = ImageVector.vectorResource(com.kagg886.sylu_eoa.R.drawable.ic_github)
            ) {
                getApp().openURL("https://gitee.com/kagg886/sylu-educational-office-accesser")
            }
        }
    }
}

@Composable
fun AboutItem(
    modifier: Modifier = Modifier,
    title: String,
    content: String? = null,
    icon: ImageVector,
    onClick: (() -> Unit)? = null,
) {
    ListItem(headlineContent = {
        Text(title, style = Typography.titleMedium)
    }, supportingContent = {
        if (content != null) {
            Text(content)
        }
    }, leadingContent = {
        Icon(imageVector = icon, contentDescription = "")
    }, trailingContent = {
        if (onClick != null) {
            Icon(imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight, contentDescription = "")
        }
    }, modifier = modifier.clickable { onClick?.invoke() })
}