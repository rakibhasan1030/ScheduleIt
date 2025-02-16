package rakib.hasan.scheduleit.feature.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import rakib.hasan.scheduleit.feature.schedule.domain.model.ScheduledApp
import rakib.hasan.scheduleit.feature.schedule.domain.usecase.DeleteScheduledApp
import rakib.hasan.scheduleit.feature.schedule.domain.usecase.DeleteScheduledAppByPackageName
import rakib.hasan.scheduleit.feature.schedule.domain.usecase.GetScheduledApps
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getScheduledApps: GetScheduledApps,
    private val deleteScheduledApp: DeleteScheduledApp,
    private val deleteScheduledAppByPackageName: DeleteScheduledAppByPackageName,
) : ViewModel() {

    private val _scheduledApps = MutableStateFlow<List<ScheduledApp>>(emptyList())
    val scheduledApps: StateFlow<List<ScheduledApp>> = _scheduledApps

    init {
        loadScheduledApps()
    }

    fun loadScheduledApps() {
        viewModelScope.launch {
            _scheduledApps.value = getScheduledApps()
        }
    }

    fun deleteSchedule(scheduledApp: ScheduledApp) {
        viewModelScope.launch {
            Log.d("DELETE_APP", "Deleting app(HomeViewModel): ${scheduledApp.name}")
            deleteScheduledApp(scheduledApp)
            loadScheduledApps()
        }
    }

    fun deleteScheduleByPackageName(packageName: String) {
        viewModelScope.launch {
            Log.d("DELETE_APP", "Deleting app(HomeViewModel): ${packageName}")
            deleteScheduledAppByPackageName(packageName)
            loadScheduledApps()
        }
    }

}