package rakib.hasan.scheduleit.feature.app_list.viewmodel

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rakib.hasan.scheduleit.feature.app_list.model.AppInfo
import javax.inject.Inject

@HiltViewModel
class AppListViewModel @Inject constructor(
    private val packageManager: PackageManager,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    // State for installed apps
    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps: StateFlow<List<AppInfo>> = _installedApps.asStateFlow()

    init {
        loadUserAccessibleApps()
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

}