package rakib.hasan.scheduleit.core.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import rakib.hasan.scheduleit.feature.schedule.data.local.dao.ScheduledAppDao
import rakib.hasan.scheduleit.feature.schedule.data.local.entity.ScheduledAppEntity

@Database(entities = [ScheduledAppEntity::class], version = 2) // Incremented version
abstract class AppDatabase : RoomDatabase() {
    abstract fun scheduledAppDao(): ScheduledAppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration() // Enable destructive migration
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}