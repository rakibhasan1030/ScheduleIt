package rakib.hasan.scheduleit.feature.schedule.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scheduled_apps")
data class ScheduledAppEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val packageName: String,
    val scheduledTime: Long,
    val repeatInterval: Int = 0,
    val repeatValue: Int = 0,
    val lastExecutionTime: Long? = null,
    val isSystemApp: Boolean = false
)