package com.kagg886.sylu_eoa.screen.page

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.kagg886.sylu_eoa.getApp
import com.kagg886.sylu_eoa.openURL
import com.kagg886.sylu_eoa.ui.theme.Typography
import java.io.InputStream


@OptIn(ExperimentalFoundationApi::class)
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
                        modifier = Modifier
                            .padding(30.dp)
                            .size(80.dp, 80.dp)
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

            var dialog by remember {
                mutableStateOf(false)
            }
            if (dialog) {
                AlertDialog(onDismissRequest = { dialog = false }, confirmButton = {}, title = {
                    Column {
                        Text(text = "截图保存二维码以捐赠")
                        Text(text = "可以左右滑动喔!", style = Typography.titleSmall)
                    }
                }, text = {
                    val state = rememberPagerState(pageCount = {3})
                    HorizontalPager(state = state) {
                        Image(bitmap = loadImageFromAssets(assetFileName = "${it+1}.jpg"), contentDescription = "")
                    }
                })
            }
            AboutItem(
                title = "赞助我",
                icon = ImageVector.vectorResource(com.kagg886.sylu_eoa.R.drawable.gift_outline)
            ) {
                dialog = true
            }

            AboutItem(title = "加入QQ群", icon = ImageVector.vectorResource(id = com.kagg886.sylu_eoa.R.drawable.chat_plus_outline)) {
                val intent = Intent()
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.setAction("android.intent.action.VIEW")
                intent.setData(Uri.parse("mqqapi://card/show_pslcard?src_type=internal&version=1&uin=798201505&card_type=group&source=qrcode"))
                getApp().startActivity(intent)
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

@Composable
fun loadImageFromAssets(assetFileName: String): ImageBitmap {
    val context = LocalContext.current
    var inputStream: InputStream? = null
    try {
        inputStream = context.assets.open(assetFileName)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        return bitmap.asImageBitmap()
    } finally {
        inputStream?.close()
    }
}