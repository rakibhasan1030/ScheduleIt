package rakib.hasan.scheduleit.feature.home.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class AppLauncherAccessibilityService : AccessibilityService() {
    companion object {
        private var instance: AppLauncherAccessibilityService? = null
        private var pendingPackage: String? = null

        fun getInstance(): AppLauncherAccessibilityService? = instance

        fun setPackageToLaunch(packageName: String) {
            pendingPackage = packageName
            instance?.launchApp(packageName)
        }
    }

    override fun onServiceConnected() {
        instance = this
        pendingPackage?.let { launchApp(it) }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {}

    fun launchApp(packageName: String) {
        val intent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        if (intent != null) {
            startActivity(intent)
            Log.d("AppLauncherAccessibilityService", "Launched app: $packageName")
        } else {
            Log.e("AppLauncherAccessibilityService", "Launch intent is null for package: $packageName")
        }
    }
}