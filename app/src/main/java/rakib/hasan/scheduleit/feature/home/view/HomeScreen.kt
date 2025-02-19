package rakib.hasan.scheduleit.feature.home.view

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import rakib.hasan.scheduleit.core.contents.ItemScheduledApp
import rakib.hasan.scheduleit.feature.home.viewmodel.HomeViewModel
import rakib.hasan.scheduleit.feature.schedule.domain.model.ScheduledApp
import rakib.hasan.scheduleit.feature.schedule.presentation.viewmodel.ScheduleViewModel
import rakib.hasan.scheduleit.R


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

    LaunchedEffect(scheduledApps.value) {
        scheduledApps.value.forEach { app ->
            Log.v("Home", app.toString())
        }
    }

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
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                FloatingActionButton(
                    onClick = onAddClicked,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_add_alarm),
                        contentDescription = "Add"
                    )
                }
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
                        fontFamily = FontFamily(Font(R.font.cabin_medium)),
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
                        ItemScheduledApp(
                            context = context,
                            scheduledApp = scheduledApp,
                            onEditClicked = {
                                onEditClicked(scheduledApp)
                            },
                            onDeleteClicked = { viewModel.deleteScheduleByPackageName(scheduledApp.packageName) },
                            onScheduleClicked = {

                            }
                        )
                    }
                }
            }
        }
    }
}

