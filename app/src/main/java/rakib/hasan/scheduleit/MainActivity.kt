package rakib.hasan.scheduleit

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import rakib.hasan.scheduleit.feature.view.AppListScreen
import rakib.hasan.scheduleit.feature.viewmodel.AppViewModel
import rakib.hasan.scheduleit.ui.theme.ScheduleItTheme
import kotlin.getValue

/*
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScheduleItTheme {
                AppListScreen(viewModel = viewModel)
            }
        }

        // Request overlay permission
        requestOverlayPermission()

        // Request to disable battery optimization
        requestDisableBatteryOptimization(this)
    }

    private fun requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivityForResult(intent, REQUEST_CODE_OVERLAY_PERMISSION)
            }
        }
    }

    private fun requestDisableBatteryOptimization(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:${context.packageName}")
            }
            context.startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_OVERLAY_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "Overlay permission is required for this app to function properly.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_OVERLAY_PERMISSION = 1001
    }

}*/


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScheduleItTheme {
                AppListScreen(viewModel = viewModel)
            }
        }
    }
}