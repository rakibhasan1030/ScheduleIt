package rakib.hasan.scheduleit.feature.home.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import rakib.hasan.scheduleit.core.utils.AppBroadcastReceiver
import rakib.hasan.scheduleit.feature.schedule.domain.model.ScheduledApp

class AlarmScheduler(
    private val context: Context
) {

    fun scheduleAlarm(
        scheduledApp: ScheduledApp
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AppBroadcastReceiver::class.java).apply {
            putExtra("APP_NAME", scheduledApp.name)
            putExtra("APP_PACKAGE", scheduledApp.packageName)
            putExtra("REPEAT_INTERVAL", scheduledApp.repeatInterval)
            putExtra("REPEAT_VALUE", scheduledApp.repeatValue)
        }

        val requestCode = (scheduledApp.packageName + scheduledApp.scheduledTime.toString()).hashCode()
        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            pendingIntentFlags
        )

        try {
            if (scheduledApp.repeatInterval > 0) {
                // For repetitive tasks, calculate the interval in milliseconds
                val intervalMillis = when (scheduledApp.repeatInterval) {
                    1 -> scheduledApp.repeatValue * 60 * 1000L // Minutes
                    2 -> scheduledApp.repeatValue * 60 * 60 * 1000L // Hours
                    3 -> scheduledApp.repeatValue * 24 * 60 * 60 * 1000L // Days
                    4 -> scheduledApp.repeatValue * 30L * 24 * 60 * 60 * 1000L // Months (approximate)
                    else -> 0L
                }

                if (intervalMillis > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            scheduledApp.scheduledTime ?: System.currentTimeMillis(),
                            pendingIntent
                        )
                    } else {
                        alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            scheduledApp.scheduledTime ?: System.currentTimeMillis(),
                            pendingIntent
                        )
                    }
                    Log.d("AlarmScheduler", "Repeating alarm set for ${scheduledApp.packageName} with interval $intervalMillis")
                }
            } else {
                // For one-time tasks
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        scheduledApp.scheduledTime ?: System.currentTimeMillis(),
                        pendingIntent
                    )
                } else {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        scheduledApp.scheduledTime ?: System.currentTimeMillis(),
                        pendingIntent
                    )
                }
            }
        } catch (e: SecurityException) {
            Log.e("AlarmScheduler", "Permission required to schedule exact alarms!", e)
        }
    }
}