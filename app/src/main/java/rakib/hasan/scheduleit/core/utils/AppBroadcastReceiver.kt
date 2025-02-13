package rakib.hasan.scheduleit.core.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import rakib.hasan.scheduleit.feature.service.AppLauncherService

/*class ToastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val appName = intent.getStringExtra("APP_NAME") ?: "Scheduled App"
        showNotification(context, "Time to open $appName!")
    }

    private fun showNotification(context: Context, message: String) {
        val channelId = "scheduler_channel"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Scheduler Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Scheduled Reminder")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.ic_media_play)
            .build()

        notificationManager.notify(1001, notification)
    }
}*/

/*
class ToastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val packageName = intent.getStringExtra("APP_PACKAGE")
        val appName = intent.getStringExtra("APP_NAME") ?: "App"

        // Acquire wake lock to ensure device is awake
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "ScheduleIt:LaunchAppWakeLock"
        )

        try {
            wakeLock.acquire(10*1000L) // 10 seconds

            if (packageName != null) {
                try {
                    val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)?.apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                    }

                    if (launchIntent != null) {
                        context.startActivity(launchIntent)
                        Log.d("ToastReceiver", "Successfully launched app: $packageName")
                    } else {
                        Log.e("ToastReceiver", "No launch intent available for: $packageName")
                        Toast.makeText(context, "Cannot launch $appName", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Log.e("ToastReceiver", "Error launching app: ${e.message}", e)
                    Toast.makeText(context, "Failed to open $appName: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(context, "App package not found!", Toast.LENGTH_LONG).show()
            }
        } finally {
            if (wakeLock.isHeld) {
                wakeLock.release()
            }
        }
    }
}*/

class AppBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val packageName = intent.getStringExtra("APP_PACKAGE")
        val appName = intent.getStringExtra("APP_NAME")

        if (packageName != null) {
            val serviceIntent = Intent(context, AppLauncherService::class.java).apply {
                putExtra("APP_PACKAGE", packageName)
                putExtra("APP_NAME", appName)
            }

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
                Log.d("AppBroadcastReceiver", "Started AppLauncherService for $packageName")
            } catch (e: Exception) {
                Log.e("AppBroadcastReceiver", "Error starting service: ${e.message}", e)
                Toast.makeText(
                    context,
                    "Failed to start app launcher: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            Toast.makeText(context, "App package not found!", Toast.LENGTH_LONG).show()
        }
    }
}