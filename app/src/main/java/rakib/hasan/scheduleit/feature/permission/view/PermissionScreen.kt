package rakib.hasan.scheduleit.feature.permission.view

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import rakib.hasan.scheduleit.R
import rakib.hasan.scheduleit.core.contents.ItemPermissionRow
import rakib.hasan.scheduleit.feature.permission.viewmodel.PermissionViewModel

@Composable
fun PermissionScreen(
    viewModel: PermissionViewModel = hiltViewModel(),
    onAllPermissionGranted: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Observe permission states from the ViewModel
    val overlayPermissionGranted = viewModel.overlayPermissionGranted.collectAsState()
    val batteryOptimizationDisabled = viewModel.batteryOptimizationDisabled.collectAsState()
    val exactAlarmPermissionGranted = viewModel.exactAlarmPermissionGranted.collectAsState()

    // State to manage description visibility for each permission
    val (showOverlayDescription, setShowOverlayDescription) = remember { mutableStateOf(false) }
    val (showBatteryDescription, setShowBatteryDescription) = remember { mutableStateOf(false) }
    val (showExactAlarmDescription, setShowExactAlarmDescription) = remember { mutableStateOf(false) }

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
                    viewModel.checkPermissions()
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

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Permission Needed",
                    fontSize = 36.sp,
                    fontFamily = FontFamily(Font(R.font.cabin_bold)),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                )
                Spacer(Modifier.weight(1f))
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .border(
                            border = BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                            ),
                            shape = RoundedCornerShape(
                                size = 16.dp
                            )
                        )
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Overlay Permission Item
                    ItemPermissionRow(
                        permissionName = "Overlay Permission",
                        permissionDescription = "This app requires overlay permission to display system-alert windows. Please grant this permission to continue.",
                        icon = if (overlayPermissionGranted.value) Icons.Default.CheckCircle else Icons.Default.KeyboardArrowRight,
                        isDescriptionVisible = showOverlayDescription,
                        onPermissionNameClicked = {
                            setShowOverlayDescription(!showOverlayDescription)
                        },
                        onIconClicked = {
                            if (!overlayPermissionGranted.value) {
                                viewModel.requestOverlayPermission()
                            }
                        },
                        isPermissionGranted = overlayPermissionGranted.value // Pass permission state
                    )
                    Spacer(Modifier.height(8.dp))

                    // Battery Optimization Permission Item
                    ItemPermissionRow(
                        permissionName = "Battery Optimize Permission",
                        permissionDescription = "This app requires battery optimization to be disabled to ensure it can run in the background.",
                        icon = if (batteryOptimizationDisabled.value) Icons.Default.CheckCircle else Icons.Default.KeyboardArrowRight,
                        isDescriptionVisible = showBatteryDescription,
                        onPermissionNameClicked = {
                            setShowBatteryDescription(!showBatteryDescription)
                        },
                        onIconClicked = {
                            if (!batteryOptimizationDisabled.value) {
                                viewModel.requestDisableBatteryOptimization()
                            }
                        },
                        isPermissionGranted = batteryOptimizationDisabled.value // Pass permission state
                    )
                    Spacer(Modifier.height(8.dp))

                    // Exact Alarm Permission Item
                    ItemPermissionRow(
                        permissionName = "Exact Alarm Permission",
                        permissionDescription = "This app requires exact alarm permission to schedule alarms precisely. Please grant this permission to continue.",
                        icon = if (exactAlarmPermissionGranted.value) Icons.Default.CheckCircle else Icons.Default.KeyboardArrowRight,
                        isDescriptionVisible = showExactAlarmDescription,
                        onPermissionNameClicked = {
                            setShowExactAlarmDescription(!showExactAlarmDescription)
                        },
                        onIconClicked = {
                            if (!exactAlarmPermissionGranted.value) {
                                viewModel.requestExactAlarmPermission()
                            }
                        },
                        isPermissionGranted = exactAlarmPermissionGranted.value // Pass permission state
                    )
                }

                Spacer(Modifier.weight(1f))

                Button(
                    onClick = {
                        // Navigate back or proceed to the next screen
                        onAllPermissionGranted()
                    },
                    enabled = viewModel.areAllPermissionsGranted(),
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = "Proceed",
                        fontFamily = FontFamily(Font(R.font.cabin_bold)),
                        fontSize = 20.sp,
                        color = if (viewModel.areAllPermissionsGranted())
                            MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.background.copy(
                            alpha = 0.7f
                        ),
                        modifier = Modifier
                            .padding(vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PermissionScreenPreview() {
    PermissionScreen(
        onAllPermissionGranted = {

        },
        onNavigateBack = {

        },
    )
}

























