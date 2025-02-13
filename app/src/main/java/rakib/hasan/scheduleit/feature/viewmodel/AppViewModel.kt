package rakib.hasan.scheduleit.feature.viewmodel

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rakib.hasan.scheduleit.feature.model.AppInfo
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val packageManager: PackageManager,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    // State for installed apps
    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps: StateFlow<List<AppInfo>> = _installedApps.asStateFlow()

    // State for overlay permission
    private val _overlayPermissionGranted = MutableStateFlow(false)
    val overlayPermissionGranted: StateFlow<Boolean> = _overlayPermissionGranted.asStateFlow()

    // State for battery optimization
    private val _batteryOptimizationDisabled = MutableStateFlow(false)
    val batteryOptimizationDisabled: StateFlow<Boolean> = _batteryOptimizationDisabled.asStateFlow()

    init {
        loadUserAccessibleApps()
        checkPermissions()
    }

    fun loadUserAccessibleApps() {
        viewModelScope.launch {
            val apps = packageManager.queryIntentActivities(
                Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_LAUNCHER)
                },
                PackageManager.MATCH_ALL
            )
                .map { resolveInfo ->
                    val appInfo = resolveInfo.activityInfo.applicationInfo
                    AppInfo(
                        name = packageManager.getApplicationLabel(appInfo).toString(),
                        packageName = appInfo.packageName,
                        icon = packageManager.getApplicationIcon(appInfo)
                    )
                }
                .distinctBy { it.packageName } // Remove duplicates
                .sortedBy { it.name } // Sort alphabetically
            _installedApps.value = apps
        }
    }

    // Check permissions and update states
    fun checkPermissions() {
        _overlayPermissionGranted.value = isOverlayPermissionGranted()
        _batteryOptimizationDisabled.value = isBatteryOptimizationDisabled()
    }

    // Check if overlay permission is granted
    private fun isOverlayPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true // Permission not required below Android M
        }
    }

    // Check if battery optimization is disabled
    private fun isBatteryOptimizationDisabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            powerManager.isIgnoringBatteryOptimizations(context.packageName)
        } else {
            true // Battery optimization not available below Android M
        }
    }

    // Request overlay permission
    fun requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    // Request to disable battery optimization
    fun requestDisableBatteryOptimization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent().apply {
                action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }
}