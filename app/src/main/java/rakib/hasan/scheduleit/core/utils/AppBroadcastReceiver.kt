package rakib.hasan.scheduleit.core.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import rakib.hasan.scheduleit.feature.home.service.AlarmScheduler
import rakib.hasan.scheduleit.feature.schedule.domain.model.ScheduledApp
import rakib.hasan.scheduleit.feature.schedule.presentation.viewmodel.ScheduleViewModel
import javax.inject.Inject

class AppBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var viewModel: ScheduleViewModel

    override fun onReceive(context: Context, intent: Intent) {

        val alarmScheduler = AlarmScheduler(context)
        val appName = intent.getStringExtra("APP_NAME")
        val packageName = intent.getStringExtra("APP_PACKAGE")
        val repeatInterval = intent.getIntExtra("REPEAT_INTERVAL", 0)
        val repeatValue = intent.getIntExtra("REPEAT_VALUE", 0)

        Log.d("AppBroadcastReceiver", "App: $appName, Package: $packageName, Repeat: $repeatInterval, Value: $repeatValue")

        // Launch the app
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName!!)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (launchIntent != null) {
            context.startActivity(launchIntent)
        } else {
            Toast.makeText(
                context,
                "Could not find launch intent for $packageName",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Update the last execution time in the database
        viewModel.updateLastExecutionTime(packageName, System.currentTimeMillis())

        // Reschedule the task if it is repetitive
        if (repeatInterval > 0) {
            val nextTriggerTime = calculateNextTriggerTime(repeatInterval, repeatValue)
            Toast.makeText(context, "Next trigger time: $nextTriggerTime", Toast.LENGTH_SHORT)
                .show()
            val scheduledApp = ScheduledApp(
                name = appName!!,
                packageName = packageName,
                scheduledTime = nextTriggerTime,
                repeatInterval = repeatInterval,
                repeatValue = repeatValue
            )
            alarmScheduler.scheduleAlarm(scheduledApp)
        }
    }

    private fun calculateNextTriggerTime(repeatInterval: Int, repeatValue: Int): Long {
        return when (repeatInterval) {
            1 -> System.currentTimeMillis() + repeatValue * 60 * 1000L // Minutes
            2 -> System.currentTimeMillis() + repeatValue * 60 * 60 * 1000L // Hours
            3 -> System.currentTimeMillis() + repeatValue * 24 * 60 * 60 * 1000L // Days
            4 -> System.currentTimeMillis() + repeatValue * 30L * 24 * 60 * 60 * 1000L // Months (approximate)
            else -> 0L
        }
    }

}