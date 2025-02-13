package rakib.hasan.scheduleit.feature.view

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import rakib.hasan.scheduleit.core.contents.AppListItem
import rakib.hasan.scheduleit.core.contents.CustomDialog
import rakib.hasan.scheduleit.core.utils.AppBroadcastReceiver
import rakib.hasan.scheduleit.feature.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/*@OptIn(ExperimentalMaterial3Api::class)
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
                    onScheduleClick = { app ->
                        val scheduleTimeMillis = System.currentTimeMillis() + 60_000 // 1 min later

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                            !(context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).canScheduleExactAlarms()
                        ) {
                            requestExactAlarmPermission(context)
                        } else {
                            scheduleToast(context, app.name, app.packageName, scheduleTimeMillis)
                            Toast.makeText(
                                context,
                                "App will open at ${formatTime(scheduleTimeMillis)}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                )
            }
        }
    }
}
*/


fun scheduleToast(context: Context, appName: String, packageName: String, triggerTimeMillis: Long) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val intent = Intent(context, AppBroadcastReceiver::class.java).apply {
        putExtra("APP_NAME", appName)
        putExtra("APP_PACKAGE", packageName)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTimeMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTimeMillis, pendingIntent)
        }
    } catch (e: SecurityException) {
        Toast.makeText(context, "Permission required to schedule exact alarms!", Toast.LENGTH_LONG)
            .show()
    }
}

fun requestExactAlarmPermission(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
            data = android.net.Uri.parse("package:${context.packageName}")
        }
        context.startActivity(intent)
        Toast.makeText(context, "Grant exact alarm permission in settings.", Toast.LENGTH_LONG)
            .show()
    }
}

fun formatTime(millis: Long): String {
    val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
    return sdf.format(Date(millis))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppListScreen(
    viewModel: AppViewModel
) {
    val context = LocalContext.current
    val apps by viewModel.installedApps.collectAsState()

    // Observe permission states from the ViewModel
    val overlayPermissionGranted by viewModel.overlayPermissionGranted.collectAsState()
    val batteryOptimizationDisabled by viewModel.batteryOptimizationDisabled.collectAsState()

    // State to manage dialog visibility
    var showOverlayPermissionDialog = remember { mutableStateOf(true) }
    var showBatteryOptimizationDialog = remember { mutableStateOf(false) }

    // Observe lifecycle events to re-check permissions when the activity resumes
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // Re-check permissions when the activity resumes
                viewModel.checkPermissions()

                // Show battery optimization dialog if overlay permission is granted but battery optimization is not disabled
                if (overlayPermissionGranted && !batteryOptimizationDisabled) {
                    showBatteryOptimizationDialog.value = true
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Show the overlay permission dialog first
    if (showOverlayPermissionDialog.value && !overlayPermissionGranted) {
        CustomDialog(
            title = "Overlay Permission Required",
            message = "This app requires overlay permission to display system-alert windows. " +
                    "This is necessary to ensure the app can function properly. " +
                    "Please grant this permission to continue.",
            confirmButtonText = "Grant Permission",
            dismissButtonText = "Decline",
            onConfirm = {
                // Redirect to overlay permission settings
                viewModel.requestOverlayPermission()
                showOverlayPermissionDialog.value = false
            },
            onDismiss = {
                // If the user declines, exit the app
                Toast.makeText(context, "You cannot access the app without granting overlay permission.", Toast.LENGTH_LONG).show()
                (context as Activity).finish()
            }
        )
    }

    // Show the battery optimization dialog after overlay permission is granted
    if (overlayPermissionGranted && showBatteryOptimizationDialog.value && !batteryOptimizationDisabled) {
        CustomDialog(
            title = "Disable Battery Optimization",
            message = "This app requires battery optimization to be disabled to ensure it can run in the background. " +
                    "This is necessary for the app to function properly. " +
                    "Please disable battery optimization to continue.",
            confirmButtonText = "Disable Optimization",
            dismissButtonText = "Decline",
            onConfirm = {
                // Redirect to battery optimization settings
                viewModel.requestDisableBatteryOptimization()
                showBatteryOptimizationDialog.value = false
            },
            onDismiss = {
                // If the user declines, exit the app
                Toast.makeText(context, "You cannot access the app without disabling battery optimization.", Toast.LENGTH_LONG).show()
                (context as Activity).finish()
            }
        )
    }

    // Display the app list only if both permissions are granted
    if (overlayPermissionGranted && batteryOptimizationDisabled) {
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
                        onScheduleClick = { app ->
                            val scheduleTimeMillis = System.currentTimeMillis() + 60_000 // 1 min later

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                                !(context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).canScheduleExactAlarms()
                            ) {
                                requestExactAlarmPermission(context)
                            } else {
                                scheduleToast(context, app.name, app.packageName, scheduleTimeMillis)
                                Toast.makeText(
                                    context,
                                    "App will open at ${formatTime(scheduleTimeMillis)}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    )
                }
            }
        }
    }
}

// Function to request overlay permission
private fun requestOverlayPermission(context: Context, onResult: (Boolean) -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (!Settings.canDrawOverlays(context)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            )
            (context as Activity).startActivityForResult(intent, REQUEST_CODE_OVERLAY_PERMISSION)
        } else {
            onResult(true) // Permission already granted
        }
    } else {
        onResult(true) // Permission not required below Android M
    }
}

// Function to request disabling battery optimization
private fun requestDisableBatteryOptimization(context: Context, onResult: (Boolean) -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (powerManager.isIgnoringBatteryOptimizations(context.packageName)) {
            onResult(true) // Battery optimization already disabled
        } else {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:${context.packageName}")
            }
            (context as Activity).startActivityForResult(intent, REQUEST_CODE_BATTERY_OPTIMIZATION)
        }
    } else {
        onResult(true) // Battery optimization not available below Android M
    }
}

// Constants for request codes
private const val REQUEST_CODE_OVERLAY_PERMISSION = 1001
private const val REQUEST_CODE_BATTERY_OPTIMIZATION = 1002