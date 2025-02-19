package rakib.hasan.scheduleit.core.utils

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

object Const {


    fun loadAppIcon(packageName: String, context: Context): Drawable? {
        return try {
            val packageManager = context.packageManager
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationIcon(applicationInfo)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }
}