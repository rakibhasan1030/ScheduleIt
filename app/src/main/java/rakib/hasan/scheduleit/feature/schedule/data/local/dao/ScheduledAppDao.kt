package rakib.hasan.scheduleit.feature.schedule.data.local.dao

import androidx.room.*
import rakib.hasan.scheduleit.feature.schedule.data.local.entity.ScheduledAppEntity

@Dao
interface ScheduledAppDao {
    @Insert
    suspend fun insert(scheduledApp: ScheduledAppEntity): Long

    @Update
    suspend fun update(scheduledApp: ScheduledAppEntity)

    @Delete
    suspend fun delete(scheduledApp: ScheduledAppEntity)

    @Query("DELETE FROM scheduled_apps WHERE packageName = :packageName")
    suspend fun deleteByPackageName(packageName: String)

    @Query("SELECT * FROM scheduled_apps WHERE id = :id")
    suspend fun getById(id: Long): ScheduledAppEntity?

    @Query("SELECT * FROM scheduled_apps WHERE packageName = :packageName")
    suspend fun getByPackageName(packageName: String): ScheduledAppEntity?

    @Query("SELECT * FROM scheduled_apps")
    suspend fun getAll(): List<ScheduledAppEntity>
}