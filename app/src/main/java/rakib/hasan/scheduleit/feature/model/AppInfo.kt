package rakib.hasan.scheduleit.model

import android.graphics.drawable.Drawable
import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import android.content.pm.ApplicationInfo

data class AppInfo(
    val name: String,
    val packageName: String,
    val icon: Drawable,
    val isScheduled: Boolean = false,
    val scheduledTime: Long? = null,
    val isSystemApp: Boolean = false
) {
    fun getIconAsBitmap(): ImageBitmap {
        val bitmap = if (icon is Bitmap) {
            icon
        } else {
            val drawableBitmap = Bitmap.createBitmap(
                icon.intrinsicWidth,
                icon.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = android.graphics.Canvas(drawableBitmap)
            icon.setBounds(0, 0, canvas.width, canvas.height)
            icon.draw(canvas)
            drawableBitmap
        }
        return bitmap.asImageBitmap()
    }

    companion object {
        fun fromApplicationInfo(
            appInfo: ApplicationInfo,
            packageManager: android.content.pm.PackageManager
        ): AppInfo {
            return AppInfo(
                name = packageManager.getApplicationLabel(appInfo).toString(),
                packageName = appInfo.packageName,
                icon = packageManager.getApplicationIcon(appInfo),
                isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            )
        }
    }
}