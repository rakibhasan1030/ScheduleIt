package rakib.hasan.scheduleit.feature.schedule.presentation.viewmodel

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rakib.hasan.scheduleit.feature.home.service.AlarmScheduler
import rakib.hasan.scheduleit.feature.schedule.domain.model.ScheduledApp
import rakib.hasan.scheduleit.feature.schedule.domain.usecase.GetScheduledAppByByPackageName
import rakib.hasan.scheduleit.feature.schedule.domain.usecase.GetScheduledAppById
import rakib.hasan.scheduleit.feature.schedule.domain.usecase.InsertScheduledApp
import rakib.hasan.scheduleit.feature.schedule.domain.usecase.UpdateScheduledApp
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val packageManager: PackageManager,
    private val insertScheduledApp: InsertScheduledApp,
    private val updateScheduledApp: UpdateScheduledApp,
    private val getScheduledAppById: GetScheduledAppById,
    private val scheduledAppByByPackageName: GetScheduledAppByByPackageName,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    // State for installed apps
    private val _installedApps = MutableStateFlow<List<ScheduledApp>>(emptyList())
    val installedApps: StateFlow<List<ScheduledApp>> = _installedApps.asStateFlow()

    init {
        loadUserAccessibleApps()
    }

    fun loadUserAccessibleApps() {
        viewModelScope.launch(Dispatchers.IO) {
            val apps = packageManager.queryIntentActivities(
                Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_LAUNCHER)
                },
                PackageManager.MATCH_ALL
            )
                .map { resolveInfo ->
                    val appInfo = resolveInfo.activityInfo.applicationInfo
                    ScheduledApp(
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

    fun saveSchedule(scheduledApp: ScheduledApp) {
        viewModelScope.launch(Dispatchers.IO) {
            insertScheduledApp(scheduledApp)
            alarmScheduler.scheduleAlarm(scheduledApp) // Schedule the alarm
        }
    }

    fun updateSchedule(scheduledApp: ScheduledApp) {
        viewModelScope.launch(Dispatchers.IO) {
            updateScheduledApp(scheduledApp)
            alarmScheduler.scheduleAlarm(scheduledApp) // Schedule the alarm
        }
    }

    suspend fun scheduledAppById(id: Long): ScheduledApp? {
        return getScheduledAppById(id)
    }

    suspend fun getScheduledAppByByPackageName(packageName: String): ScheduledApp? {
        return scheduledAppByByPackageName(packageName)
    }

    fun updateLastExecutionTime(packageName: String, lastExecutionTime: Long) {
        viewModelScope.launch {
            val scheduledApp = getScheduledAppByByPackageName(packageName)
            scheduledApp?.let {
                val updatedApp = it.copy(lastExecutionTime = lastExecutionTime)
                updateScheduledApp(updatedApp)
                loadUserAccessibleApps()
            }
        }
    }
}
