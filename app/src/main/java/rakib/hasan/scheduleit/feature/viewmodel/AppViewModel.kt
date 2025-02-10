package rakib.hasan.scheduleit.feature.viewmodel

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rakib.hasan.scheduleit.feature.model.AppInfo
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val packageManager: PackageManager
) : ViewModel() {

    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps: StateFlow<List<AppInfo>> = _installedApps.asStateFlow()

    init {
        loadUserAccessibleApps()
    }

    private fun loadUserAccessibleApps() {
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

}