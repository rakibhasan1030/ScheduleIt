package rakib.hasan.scheduleit.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import rakib.hasan.scheduleit.feature.schedule.data.local.dao.ScheduledAppDao
import rakib.hasan.scheduleit.feature.schedule.data.local.entity.ScheduledAppEntity

@Database(entities = [ScheduledAppEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scheduledAppDao(): ScheduledAppDao
}