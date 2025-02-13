package rakib.hasan.scheduleit.feature.service

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityManager
import android.widget.Toast
import androidx.core.app.NotificationCompat


class AppLauncherService : Service() {

    private val NOTIFICATION_ID = 1
    private val CHANNEL_ID = "app_launcher_channel"

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val packageName = intent?.getStringExtra("APP_PACKAGE")
        val appName = intent?.getStringExtra("APP_NAME") ?: "App"

        createNotificationChannel()

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Launching App")
            .setContentText("Preparing to launch $appName...")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(NOTIFICATION_ID, notification)

        if (packageName != null) {
            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            if (launchIntent != null) {
                try {
                    startActivity(launchIntent)
                    Log.d("AppLauncherService", "Launched app via intent: $packageName")
                } catch (e: Exception) {
                    if (isAccessibilityServiceEnabled()) {
                        AppLauncherAccessibilityService.setPackageToLaunch(packageName)
                        Log.d("AppLauncherService", "Requested launch via accessibility service: $packageName")
                    } else {
                        Log.e("AppLauncherService", "Accessibility service not enabled")
                        promptEnableAccessibilityService()
                    }
                }
            } else {
                Log.e("AppLauncherService", "Launch intent is null for package: $packageName")
            }
        } else {
            Log.e("AppLauncherService", "Package name is null")
        }

        Handler(Looper.getMainLooper()).postDelayed({
            stopSelf()
        }, 3000)

        return START_NOT_STICKY
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val accessibilityManager = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        return enabledServices.any { it.id.contains("$packageName/.AppLauncherAccessibilityService") }
    }

    private fun promptEnableAccessibilityService() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

        Toast.makeText(
            this,
            "Please enable the App Launcher Accessibility Service",
            Toast.LENGTH_LONG
        ).show()

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "App Launcher Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Used to launch scheduled apps"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

}