package rakib.hasan.scheduleit.feature.schedule.presentation.view

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import rakib.hasan.scheduleit.core.contents.AppListDialog
import rakib.hasan.scheduleit.core.contents.CustomInputBox
import rakib.hasan.scheduleit.core.contents.ScheduleDialog
import rakib.hasan.scheduleit.core.contents.ThoughtInputField
import rakib.hasan.scheduleit.core.utils.AppBroadcastReceiver
import rakib.hasan.scheduleit.feature.schedule.domain.model.ScheduledApp
import rakib.hasan.scheduleit.feature.schedule.presentation.viewmodel.ScheduleViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


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

    // State for selected app
    var selectedApp by remember { mutableStateOf<ScheduledApp?>(null) }

    // State for showing app list dialog
    val shouldShowAppListDialog = remember { mutableStateOf(false) }

    // State for selected time and date
    val selectedTimeAndDate = remember { mutableLongStateOf(System.currentTimeMillis()) }

    // State for showing schedule dialog
    val shouldShowScheduleDialog = remember { mutableStateOf(false) }

    // State for repeat options
    var isRepeatEnabled by remember { mutableStateOf(false) } // Checkbox state
    var repeatValue by remember { mutableStateOf("") } // User input for repeat value
    var repeatInterval by remember { mutableStateOf("Minutes") } // Dropdown selection

    // State for thought input
    var thought by remember { mutableStateOf("") }

    // Load scheduled app details if in edit mode
    LaunchedEffect(Unit) {
        Log.v("PACKAGE_NAME", "get package name at screen(Unit): $packageName")
        if (packageName.isNotEmpty()) {
            val app = viewModel.getScheduledAppByByPackageName(packageName = packageName)
            app?.let {
                selectedApp = it
                selectedTimeAndDate.longValue = it.scheduledTime ?: System.currentTimeMillis()
                isRepeatEnabled = it.repeatInterval > 0
                repeatValue = if (it.repeatInterval > 0) it.repeatInterval.toString() else ""
                repeatInterval = when (it.repeatInterval) {
                    1 -> "Minutes"
                    2 -> "Hours"
                    3 -> "Days"
                    4 -> "Months"
                    5 -> "Years"
                    else -> "Minutes"
                }
            }
        }
    }

    // Function to show toast
    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
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
                        .clickable {
                            if (selectedApp == null) {
                                showToast("Select an application first")
                            } else {
                                shouldShowScheduleDialog.value = true
                            }
                        },
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    //enabled = selectedApp != null
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Want to Repeat?"
                    )
                    Checkbox(
                        checked = isRepeatEnabled,
                        onCheckedChange = { isRepeatEnabled = it }
                    )
                }

                if (isRepeatEnabled) {
                    Spacer(Modifier.height(8.dp))
                    Card(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {

                            CustomInputBox(
                                value = repeatValue,
                                onValueChange = { repeatValue = it },
                                label = repeatInterval,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(Modifier.width(16.dp))
                            RepeatIntervalDropdown(
                                selectedInterval = repeatInterval,
                                onIntervalSelected = { repeatInterval = it }
                            )
                        }
                    }
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
                            val repeatIntervalValue = when (repeatInterval) {
                                "Minutes" -> 1
                                "Hours" -> 2
                                "Days" -> 3
                                "Months" -> 4
                                "Years" -> 5
                                else -> 0
                            }
                            val scheduledApp = app.copy(
                                scheduledTime = selectedTimeAndDate.longValue,
                                repeatInterval = if (isRepeatEnabled) repeatIntervalValue else 0
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
                    ),
                    enabled = selectedApp != null && selectedTimeAndDate.longValue != System.currentTimeMillis()
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

@Composable
fun RepeatIntervalDropdown(
    selectedInterval: String,
    onIntervalSelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val intervals = listOf("Minutes", "Hours", "Days", "Months", "Years")

    Box(
        modifier = Modifier
            .wrapContentSize(Alignment.TopStart)
    ) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.width(120.dp)
        ) {
            Text(text = selectedInterval)
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Dropdown"
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            intervals.forEach { interval ->
                DropdownMenuItem(
                    text = { Text(interval) },
                    onClick = {
                        onIntervalSelected(interval)
                        expanded = false
                    }
                )
            }
        }
    }
}




























