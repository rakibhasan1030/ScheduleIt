package rakib.hasan.scheduleit.feature.permission.viewmodel

import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import rakib.hasan.scheduleit.core.utils.PermissionManager
import javax.inject.Inject

@HiltViewModel
class PermissionViewModel @Inject constructor(
    private val packageManager: PackageManager,
    @ApplicationContext private val context: Context,
    private val permissionManager: PermissionManager,
) : ViewModel() {

    // State for overlay permission
    private val _overlayPermissionGranted = MutableStateFlow(false)
    val overlayPermissionGranted: StateFlow<Boolean> = _overlayPermissionGranted.asStateFlow()

    // State for battery optimization
    private val _batteryOptimizationDisabled = MutableStateFlow(false)
    val batteryOptimizationDisabled: StateFlow<Boolean> = _batteryOptimizationDisabled.asStateFlow()

    // State for exact alarm permission
    private val _exactAlarmPermissionGranted = MutableStateFlow(false)
    val exactAlarmPermissionGranted: StateFlow<Boolean> = _exactAlarmPermissionGranted.asStateFlow()

    init {
        checkPermissions()
    }

    // Check permissions and update states
    fun checkPermissions() {
        _overlayPermissionGranted.value = permissionManager.isOverlayPermissionGranted()
        _batteryOptimizationDisabled.value = permissionManager.isBatteryOptimizationDisabled()
        _exactAlarmPermissionGranted.value = permissionManager.isExactAlarmPermissionGranted()
    }

    // Request overlay permission
    fun requestOverlayPermission() {
        permissionManager.requestOverlayPermission()
    }

    // Request to disable battery optimization
    fun requestDisableBatteryOptimization() {
        permissionManager.requestDisableBatteryOptimization()
    }

    // Request exact alarm permission
    fun requestExactAlarmPermission() {
        permissionManager.requestExactAlarmPermission()
    }

    fun areAllPermissionsGranted(): Boolean = overlayPermissionGranted.value && batteryOptimizationDisabled.value && exactAlarmPermissionGranted.value

}