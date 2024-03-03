package com.kagg886.sylu_eoa.screen

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kagg886.sylu_eoa.R
import com.kagg886.sylu_eoa.ui.theme.Typography
import com.kagg886.sylu_eoa.util.PageConfig


val LocalNavController = compositionLocalOf<NavHostController> {
    error("NavController not provided")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {

    val nav = rememberNavController()

    val reg by nav.currentBackStackEntryAsState()

    Scaffold(bottomBar = {
        NavigationBar {
            PageConfig.nav.forEach { entry ->
                val select = entry.router == (reg?.destination?.route ?: PageConfig.DEFAULT_ROUTER)
                NavigationBarItem(
                    icon = {
                        Icon(painter = painterResource(entry.icon), "")
                    },
                    label = {
                        Text(entry.title)
                    },
                    selected = select,
                    onClick = {
                        if (!select) {
                            nav.navigate(entry.router)
                        }
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
        //enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) =
        //        { fadeIn(animationSpec = tween(700)) },
        //    exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) =
        //        { fadeOut(animationSpec = tween(700)) },
        //    popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) =
        //        enterTransition,
        //    popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) =
        //        exitTransition,
        CompositionLocalProvider(LocalNavController provides nav) {
            NavHost(
                navController = nav,
                startDestination = PageConfig.DEFAULT_ROUTER,
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize(),

                enterTransition = {
                    scaleIn(tween(300)) + fadeIn(animationSpec = tween(300))
//                fadeIn(animationSpec = tween(500))
                },
                exitTransition = {
                    scaleOut(tween(300)) + fadeOut(animationSpec = tween(300))
//                expandIn(tween(500), expandFrom = Alignment.Center)
                }
            ) {
                PageConfig.allPage.forEach { entry ->
                    composable(entry.router) {
                        entry.widget()
                    }
                }
            }
        }
    }

}