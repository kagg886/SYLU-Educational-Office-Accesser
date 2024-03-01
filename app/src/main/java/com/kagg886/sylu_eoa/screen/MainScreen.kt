package com.kagg886.sylu_eoa.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.graphics.drawable.toIcon
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kagg886.sylu_eoa.R
import com.kagg886.sylu_eoa.util.PageConfig
import com.kagg886.sylu_eoa.ui.theme.Typography


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val nav = rememberNavController()

    val reg by nav.currentBackStackEntryAsState()

    Scaffold(bottomBar = {
        NavigationBar {
            PageConfig.list.forEach { entry ->
                NavigationBarItem(
                    icon = {
                        Icon(painter = painterResource(entry.icon), "")
                    },
                    label = {
                        Text(entry.title)
                    },
                    selected = entry.router == (reg?.destination?.route ?: PageConfig.DEFAULT_ROUTER),
                    onClick = {
                        nav.navigate(entry.router)
                    },
                    alwaysShowLabel = false
                )
            }
        }
    }, topBar = {
        TopAppBar(title = {
            Column {
                Text(LocalContext.current.getString(R.string.app_name), style = Typography.titleLarge)
            }
        })
    }) {
        NavHost(
            navController = nav,
            startDestination = PageConfig.DEFAULT_ROUTER,
            modifier = Modifier.padding(it).fillMaxSize()
        ) {
            PageConfig.list.forEach { entry ->
                composable(entry.router) {
                    entry.widget()
                }
            }
        }
    }
}