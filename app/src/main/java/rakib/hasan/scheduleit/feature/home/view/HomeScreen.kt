package rakib.hasan.scheduleit.feature.home.view

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import rakib.hasan.scheduleit.feature.home.viewmodel.HomeViewModel
import rakib.hasan.scheduleit.feature.schedule.domain.model.ScheduledApp
import rakib.hasan.scheduleit.feature.schedule.presentation.view.formatTime
import rakib.hasan.scheduleit.feature.schedule.presentation.viewmodel.ScheduleViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    scheduledViewModel: ScheduleViewModel = hiltViewModel(),
    onAddClicked: () -> Unit,
    onEditClicked: (ScheduledApp) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scheduledApps = viewModel.scheduledApps.collectAsState()

    // Re-check permissions when the screen is composed or resumed
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    Log.d("PermissionScreen", "ON_CREATE: Screen created")
                    // Perform any setup if needed
                }

                Lifecycle.Event.ON_START -> {
                    Log.d("PermissionScreen", "ON_START: Screen started")
                    // Perform any actions when the screen becomes visible
                }

                Lifecycle.Event.ON_RESUME -> {
                    Log.d("PermissionScreen", "ON_RESUME: Screen resumed")
                    // Re-check permissions when the screen is resumed
                    viewModel.loadScheduledApps()
                }

                Lifecycle.Event.ON_PAUSE -> {
                    Log.d("PermissionScreen", "ON_PAUSE: Screen paused")
                    // Perform any cleanup when the screen is no longer interactive
                }

                Lifecycle.Event.ON_STOP -> {
                    Log.d("PermissionScreen", "ON_STOP: Screen stopped")
                    // Perform any cleanup when the screen is no longer visible
                }

                Lifecycle.Event.ON_DESTROY -> {
                    Log.d("PermissionScreen", "ON_DESTROY: Screen destroyed")
                    // Perform any final cleanup
                }

                else -> {
                    Log.d("PermissionScreen", "Unknown lifecycle event: $event")
                }
            }
        }

        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)

        // Remove the observer when the composable is disposed
        onDispose {
            Log.d("PermissionScreen", "DisposableEffect: Observer removed")
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home") },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClicked
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add"
                )
            }
        },
        floatingActionButtonPosition = FabPosition.EndOverlay
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (scheduledApps.value.isEmpty()) {
                // Show placeholder if no scheduled apps are available
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No scheduled apps",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            } else {
                // Display list of scheduled apps
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    items(scheduledApps.value) { scheduledApp ->
                        ScheduledAppItem(
                            context = context,
                            scheduledApp = scheduledApp,
                            onEditClicked = {
                                onEditClicked(scheduledApp)
                            },
                            onDeleteClicked = { viewModel.deleteScheduleByPackageName(scheduledApp.packageName) },
                        )
                    }
                }
            }
        }
    }
}

// Composable for displaying a single scheduled app item
@Composable
fun ScheduledAppItem(
    context: Context,
    scheduledApp: ScheduledApp,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
) {

    val status = if (scheduledApp.lastExecutionTime != null && scheduledApp.lastExecutionTime >= scheduledApp.scheduledTime!!) {
        "Completed"
    } else {
        "Pending"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val iconBitmap = loadAppIcon(scheduledApp.packageName, context)
            if (iconBitmap != null) {
                Image(
                    bitmap = iconBitmap.toBitmap().asImageBitmap(),
                    contentDescription = scheduledApp.name,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(end = 12.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = "App Icon",
                    modifier = Modifier
                        .size(50.dp)
                        .padding(end = 8.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = scheduledApp.name,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = scheduledApp.packageName,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Scheduled at: ${formatTime(scheduledApp.scheduledTime)}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Repeat: ${getRepeatText(scheduledApp.repeatInterval)}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(text = "Status: $status", style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onEditClicked) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }
            IconButton(onClick = onDeleteClicked) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

// Helper function to get repeat interval text
fun getRepeatText(repeatInterval: Int): String {
    return when (repeatInterval) {
        1 -> "Every Minute"
        2 -> "Every Hour"
        3 -> "Every Day"
        4 -> "Every Month"
        else -> "No Repeat"
    }
}

fun loadAppIcon(packageName: String, context: Context): Drawable? {
    return try {
        val packageManager = context.packageManager
        val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
        packageManager.getApplicationIcon(applicationInfo)
    } catch (e: PackageManager.NameNotFoundException) {
        null
    }
}
