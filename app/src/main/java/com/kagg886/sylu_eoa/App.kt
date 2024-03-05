package com.kagg886.sylu_eoa

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.kagg886.sylu_eoa.api.v2.util.RSA
import com.kagg886.sylu_eoa.util.PreferenceUnit
import com.kagg886.sylu_eoa.util.newEncryptedPreferenceDataStore
import com.kagg886.utils.LoggerReceiver
import com.kagg886.utils.createLogger
import com.kagg886.utils.registryLogReceiver
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.time.LocalDateTime
import kotlin.system.exitProcess


private val scope = CoroutineScope(Dispatchers.IO)
private val log = createLogger("Application")

class App : Application(), Thread.UncaughtExceptionHandler {
    private lateinit var data: DataStore<Preferences>;

    override fun onCreate() {
        super.onCreate()

        data = newEncryptedPreferenceDataStore("storage", this)

        registryLogReceiver(object : LoggerReceiver {
            override fun d(msg: String) {
                Log.d("SYLU_EOA", msg)
            }

            override fun i(msg: String) {
                Log.i("SYLU_EOA", msg)
            }

            override fun w(msg: String) {
                Log.w("SYLU_EOA", msg)
            }

            override fun e(msg: String) {
                Log.e("SYLU_EOA", msg)
            }

        })
        RSA.TAG = "RSA/ECB/PKCS1Padding"

        val job = scope.launch {
            val process = Runtime.getRuntime().exec(arrayOf("logcat", "-v", "threadtime", "TAG:*"))
            File(externalCacheDir!!, "app-run.log").apply {
                if (exists()) {
                    delete()
                    log.i("日志文件已删除，准备重新创建")
                }
                createNewFile()
                log.i("日志文件创建完毕")
            }.outputStream().use { to ->
                process.inputStream.use { come ->
                    val buf = ByteArray(8192)
                    var len: Int
                    log.i("开始收集日志文件")
                    if (!isActive) {
                        return@launch
                    }
                    while (come.read(buf).also { len = it } != -1) {
                        to.write(buf, 0, len)
                    }
                }
            }
            log.w("协程退出，日志文件收集结束")
        }

        Thread.setDefaultUncaughtExceptionHandler(this);
        Handler(Looper.getMainLooper()).post {
            try {
                while (true) {
                    Looper.loop()
                }
            } catch (e: Throwable) {
                job.cancel()
                uncaughtException(Thread.currentThread(), e)
            }
        }
    }


    override fun uncaughtException(t: Thread, e: Throwable) {
        log.e("App崩溃", e)
        val input = File(externalCacheDir!!, "app-run.log").inputStream()
        val file = File(getExternalFilesDir("crash-logs")!!.apply {
            if (exists()) {
                mkdirs()
            }
        }, "Crash-${LocalDateTime.now()}.log").apply {
            createNewFile()
        }


        file.outputStream().use {
            val buf = ByteArray(8192)
            var len: Int
            while (input.read(buf).also { len = it } != -1) {
                it.write(buf, 0, len)
            }
        }
        //    if (crash) {
        //        AlertDialog(onDismissRequest = {
        //            exitProcess(1)
        //        }, confirmButton = {
        //            Button(onClick = {
        //                exitProcess(1)
        //            }) {
        //                Text("退出程序")
        //            }
        //        }, title = {
        //            Text("App遇到了崩溃错误!")
        //        }, text = {
        //            Column(
        //                modifier = Modifier
        //                    .fillMaxHeight(0.7f)
        //                    .verticalScroll(rememberScrollState())
        //            ) {
        //                Text("详细信息请长按复制以下路径，然后前往文件管理器查看。")
        //                SelectionContainer {
        //                    Text(f, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        //                }
        //
        //                var showCrash by remember {
        //                    mutableStateOf(false)
        //                }
        //                TextButton(onClick = { showCrash = !showCrash }) {
        //                    Text("点我显示高级调试信息(反馈问题时务必使用长截图或录屏)", fontSize = 9.sp)
        //                }
        //                if (showCrash) {
        //                    Text(crashData!!.stackTraceToString())
        //                }
        //
        //            }
        //        })
        //    }
        scope.launch {
            val ctx = currentActivity()
            withContext(Dispatchers.Main) {
                AlertDialog.Builder(ctx)
                    .setOnCancelListener {
                        exitProcess(1)
                    }
                    .setTitle("app即将崩溃(ｷ｀ﾟДﾟ´)!!")
                    .setView(LinearLayout(ctx).apply {
                        orientation = LinearLayout.VERTICAL
                        addView(TextView(ctx).apply {
                            text = "请复制下面的路径，转到文件管理器查看错误信息"
                        })
                        addView(TextView(ctx).apply {
                            setTextIsSelectable(true)
                            text = file.absolutePath
                        })

                        val txt = ScrollView(ctx).apply {
                            addView(TextView(ctx).apply {
                                text = e.stackTraceToString()
                            })
                        }
                        var isOpen = false
                        val handleOpen = {
                            addView(txt)
                        }

                        val handleClose = {
                            removeView(txt)
                        }
                        addView(Button(ctx).apply {
                            text = "点我显示高级调试信息(反馈问题时务必使用长截图或录屏)"
                            setOnClickListener {
                                isOpen = !isOpen
                                if (isOpen) handleOpen() else handleClose()
                            }
                        })
                    })
                    .show()
            }
        }
    }


    fun <T> getConfig(config: PreferenceUnit<T>): Flow<T> {
        return data.data.map {
            it[config.key] ?: config.default
        }
    }

    fun <T> updateConfig(config: PreferenceUnit<T>, newVal: T = config.default) {
        scope.launch {
            data.edit {
                it[config.key] = newVal
            }
        }
    }
}


@SuppressLint("DiscouragedPrivateApi", "PrivateApi")
fun getApp(): App {
    var application: Application? = null
    try {
        val atClass = Class.forName("android.app.ActivityThread")
        val currentApplicationMethod = atClass.getDeclaredMethod("currentApplication")
        currentApplicationMethod.isAccessible = true
        application = currentApplicationMethod.invoke(null) as Application
    } catch (ignored: Exception) {
    }
    if (application != null) return application as App
    try {
        val atClass = Class.forName("android.app.AppGlobals")
        val currentApplicationMethod = atClass.getDeclaredMethod("getInitialApplication")
        currentApplicationMethod.isAccessible = true
        application = currentApplicationMethod.invoke(null) as Application
    } catch (ignored: Exception) {
    }
    return application as App
}

@SuppressLint("DiscouragedPrivateApi", "PrivateApi")
fun currentActivity(): ComponentActivity {
    var current: ComponentActivity? = null
    try {
        val activityThreadClass = Class.forName("android.app.ActivityThread")
        val activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(
            null
        )
        val activitiesField: Field = activityThreadClass.getDeclaredField("mActivities")
        activitiesField.isAccessible = true
        for (activityRecord in (activitiesField.get(activityThread) as Map<*, *>).values) {
            val activityRecordClass: Class<*> = activityRecord!!.javaClass
            val pausedField: Field = activityRecordClass.getDeclaredField("paused")
            pausedField.isAccessible = true
            if (!pausedField.getBoolean(activityRecord)) {
                val activityField: Field = activityRecordClass.getDeclaredField("activity")
                activityField.isAccessible = true
                current = activityField.get(activityRecord) as ComponentActivity?
            }
        }
    } catch (e: ClassNotFoundException) {
        e.printStackTrace()
    } catch (e: InvocationTargetException) {
        e.printStackTrace()
    } catch (e: NoSuchMethodException) {
        e.printStackTrace()
    } catch (e: NoSuchFieldException) {
        e.printStackTrace()
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
    }
    return current ?: throw IllegalStateException("Background!")
}

fun Context.openURL(link: String) {
    val uri = Uri.parse(link)
    startActivity(Intent(Intent.ACTION_VIEW, uri).apply {
        setFlags(FLAG_ACTIVITY_NEW_TASK)
    })
}

fun Context.toast(s: String) {
    scope.launch {
        withContext(Dispatchers.Main) {
            Toast.makeText(this@toast, s, Toast.LENGTH_LONG).show()
        }
    }
}