package rakib.hasan.scheduleit.feature.schedule.domain.model

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import rakib.hasan.scheduleit.feature.schedule.data.local.entity.ScheduledAppEntity

data class ScheduledApp(
    val name: String,
    val packageName: String,
    val icon: Drawable? = null,
    val isScheduled: Boolean = false,
    val scheduledTime: Long? = null,
    val repeatInterval: Int = 0, // 0 = no repeat, 1 = min, 2 = hour, 3 = day, 4 = month
    val repeatValue: Int = 0, // e.g., 5 for "every 5 minutes"
    val lastExecutionTime: Long? = null, // Track the last execution time
    val isSystemApp: Boolean = false,
) {

    fun getIconAsBitmap(): ImageBitmap {
        icon?.let {
            val bitmap = if (it is Bitmap) {
                it
            } else {
                val drawableBitmap = Bitmap.createBitmap(
                    it.intrinsicWidth,
                    it.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(drawableBitmap)
                it.setBounds(0, 0, canvas.width, canvas.height)
                it.draw(canvas)
                drawableBitmap
            }
            return bitmap.asImageBitmap()
        }
        return ImageBitmap(1, 1)
    }

    // Convert ScheduledApp to Room Entity (if needed)
    fun toEntity(): ScheduledAppEntity {
        return ScheduledAppEntity(
            name = name,
            packageName = packageName,
            scheduledTime = scheduledTime ?: System.currentTimeMillis(),
            repeatInterval = repeatInterval,
            repeatValue = repeatValue,
            isSystemApp = isSystemApp
        )
    }
}