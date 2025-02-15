package rakib.hasan.scheduleit.feature.schedule.domain.repository

import rakib.hasan.scheduleit.feature.schedule.domain.model.ScheduledApp

interface ScheduledAppRepository {
    suspend fun insert(scheduledApp: ScheduledApp): Long
    suspend fun update(scheduledApp: ScheduledApp)
    suspend fun delete(scheduledApp: ScheduledApp)
    suspend fun deleteByPackageName(packageName: String)
    suspend fun getById(id: Long): ScheduledApp?
    suspend fun getByPackageName(packageName: String): ScheduledApp?
    suspend fun getAll(): List<ScheduledApp>
}