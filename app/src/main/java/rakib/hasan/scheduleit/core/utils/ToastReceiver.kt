package rakib.hasan.scheduleit.core.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class ToastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val appName = intent.getStringExtra("APP_NAME") ?: "Scheduled App"
        Toast.makeText(context, "Time to open $appName!", Toast.LENGTH_LONG).show()
    }
}