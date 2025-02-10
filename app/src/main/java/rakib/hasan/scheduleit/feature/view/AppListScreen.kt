package rakib.hasan.scheduleit.feature.view

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import rakib.hasan.scheduleit.core.contents.AppListItem
import rakib.hasan.scheduleit.core.utils.ToastReceiver
import rakib.hasan.scheduleit.feature.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppListScreen(
    viewModel: AppViewModel,
) {
    val context = LocalContext.current
    val apps by viewModel.installedApps.collectAsState()

    LaunchedEffect(apps) {
        if (apps.isNotEmpty()) {
            apps.forEach {
                Log.v("AppListScreen", "${it.name} : ${it.packageName}")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App Scheduler") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            items(
                items = apps,
                key = { it.packageName }
            ) { app ->
                AppListItem(
                    app = app,
                    onScheduleClick = { it ->
                        val scheduleTime: Long? = it.scheduledTime
                        if (scheduleTime != null) {
                            scheduleToast(context, it.name, scheduleTime)
                        }
                    }
                )
            }
        }
    }
}

private fun scheduleToast(context: Context, appName: String, scheduleTime: Long) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // Check for Android 12+ before calling canScheduleExactAlarms()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // API 31+
        if (!alarmManager.canScheduleExactAlarms()) {
            requestExactAlarmPermission(context)
            return
        }
    }

    val intent = Intent(context, ToastReceiver::class.java).apply {
        putExtra("APP_NAME", appName)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        appName.hashCode(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Set exact alarm
    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        scheduleTime,
        pendingIntent
    )
}

fun requestExactAlarmPermission(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // API 31+
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
            data = android.net.Uri.parse("package:${context.packageName}")
        }
        context.startActivity(intent)
    }
}
