package rakib.hasan.scheduleit.core.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import rakib.hasan.scheduleit.feature.home.service.AppLauncherService

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