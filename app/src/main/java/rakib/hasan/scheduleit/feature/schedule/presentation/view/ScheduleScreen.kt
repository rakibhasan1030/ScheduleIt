package rakib.hasan.scheduleit.feature.schedule.presentation.view

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import rakib.hasan.scheduleit.core.contents.AppListDialog
import rakib.hasan.scheduleit.core.contents.ScheduleDialog
import rakib.hasan.scheduleit.core.contents.ThoughtInputField
import rakib.hasan.scheduleit.core.utils.AppBroadcastReceiver
import rakib.hasan.scheduleit.feature.schedule.domain.model.ScheduledApp
import rakib.hasan.scheduleit.feature.schedule.domain.usecase.GetScheduledAppByByPackageName
import rakib.hasan.scheduleit.feature.schedule.presentation.viewmodel.ScheduleViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun schedule(context: Context, appName: String, packageName: String, triggerTimeMillis: Long) {
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

fun formatTime(millis: Long?): String {
    val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
    return if (millis != null) sdf.format(Date(millis)) else ""
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    packageName: String,
    viewModel: ScheduleViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Collect installed apps from ViewModel
    val apps by viewModel.installedApps.collectAsState()
//    val scheduledAppPackageName by viewModel.packageName.collectAsState()

    // State for selected app
    var selectedApp by remember { mutableStateOf<ScheduledApp?>(null) }

    // State for showing app list dialog
    val shouldShowAppListDialog = remember { mutableStateOf(false) }

    // State for selected time and date
    val selectedTimeAndDate = remember { mutableLongStateOf(System.currentTimeMillis()) }

    // State for showing schedule dialog
    val shouldShowScheduleDialog = remember { mutableStateOf(false) }

    // State for repeat interval
    var repeatInterval by remember { mutableIntStateOf(0) } // 0 = no repeat, 1 = min, 2 = hour, 3 = day, 4 = month

    // State for thought input
    var thought by remember { mutableStateOf("") }

    // Load scheduled app details if in edit mode

    LaunchedEffect(Unit) {
        Log.v("PACKAGE_NAME", "get package name at screen(Unit): $packageName")
        if (packageName.isNotEmpty()) {
            val app =
                viewModel.getScheduledAppByByPackageName(packageName = packageName)
            app?.let {
                selectedApp = it
                selectedTimeAndDate.longValue = it.scheduledTime ?: System.currentTimeMillis()
                repeatInterval = it.repeatInterval
            }
        }
    }

    // App List Dialog
    if (shouldShowAppListDialog.value) {
        AppListDialog(
            title = "Applications",
            items = apps,
            selectedItem = selectedApp,
            onDismiss = { shouldShowAppListDialog.value = false },
            onSelect = { item ->
                selectedApp = item
                shouldShowAppListDialog.value = false
            }
        )
    }

    // Schedule Dialog
    if (shouldShowScheduleDialog.value) {
        ScheduleDialog(
            app = selectedApp,
            onDismiss = { shouldShowScheduleDialog.value = false },
            onSchedule = { time ->
                selectedTimeAndDate.value = time
                shouldShowScheduleDialog.value = false
            }
        )
    }

    // Scaffold for the screen
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Schedule Your App") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures {
                        keyboardController?.hide()
                    }
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp)
            ) {
                // Applications Section
                Text(text = "Applications")
                Spacer(Modifier.height(8.dp))
                Card(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { shouldShowAppListDialog.value = true },
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    selectedApp?.let { app ->
                        Row(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                bitmap = app.getIconAsBitmap(),
                                contentDescription = app.name,
                                modifier = Modifier
                                    .size(50.dp)
                                    .padding(end = 8.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = app.name,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = app.packageName,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    } ?: Text(
                        "Select an application",
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Time and Date Section
                Text(text = "Time and Date")
                Spacer(Modifier.height(8.dp))
                Card(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { shouldShowScheduleDialog.value = true },
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = formatTime(selectedTimeAndDate.longValue),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp)
                        )
                        Box(
                            modifier = Modifier
                                .padding(all = 8.dp)
                                .clip(CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.DateRange,
                                contentDescription = "Check",
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Repeat Section
                Text(text = "Repeat")
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    RepeatOption("Min", repeatInterval == 1) { repeatInterval = 1 }
                    RepeatOption("Hour", repeatInterval == 2) { repeatInterval = 2 }
                    RepeatOption("Day", repeatInterval == 3) { repeatInterval = 3 }
                    RepeatOption("Month", repeatInterval == 4) { repeatInterval = 4 }
                }

                Spacer(Modifier.height(16.dp))

                // Thought Input Field
                ThoughtInputField(
                    thought = thought,
                    onThoughtChange = { thought = it }
                )

                Spacer(Modifier.height(16.dp))

                // Save/Update Button
                Spacer(Modifier.weight(1f))
                Button(
                    onClick = {
                        selectedApp?.let { app ->
                            val scheduledApp = app.copy(
                                scheduledTime = selectedTimeAndDate.longValue,
                                repeatInterval = repeatInterval
                            )
                            if (packageName.isEmpty()) {
                                viewModel.saveSchedule(scheduledApp)
                            } else {
                                viewModel.updateSchedule(scheduledApp)
                            }
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = if (packageName.isEmpty()) "Save Schedule" else "Update Schedule",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

// Repeat Option Composable
@Composable
fun RepeatOption(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor =
        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val contentColor =
        if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = contentColor
        )
    }
}



































