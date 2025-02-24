package rakib.hasan.scheduleit.feature.schedule.data.repository_impl

import android.util.Log
import android.widget.Toast
import rakib.hasan.scheduleit.feature.schedule.data.local.dao.ScheduledAppDao
import rakib.hasan.scheduleit.feature.schedule.data.local.entity.ScheduledAppEntity
import rakib.hasan.scheduleit.feature.schedule.domain.model.ScheduledApp
import rakib.hasan.scheduleit.feature.schedule.domain.repository.ScheduledAppRepository
import javax.inject.Inject


class ScheduledAppRepositoryImpl @Inject constructor(
    private val scheduledAppDao: ScheduledAppDao
) : ScheduledAppRepository {

    override suspend fun insert(scheduledApp: ScheduledApp): Long {
        Log.v("ScheduledAppRepositoryImpl", "ScheduledApp Info: $scheduledApp")
        return scheduledAppDao.insert(scheduledApp.toEntity())
    }

    override suspend fun update(scheduledApp: ScheduledApp) {
        Log.v("ScheduledAppRepositoryImpl", "ScheduledApp Info: $scheduledApp")
        val app = scheduledAppDao.getByPackageName(scheduledApp.packageName)
        Log.v("ScheduledAppRepositoryImpl", "Entity Info: $app")
        if (app != null) {
            scheduledAppDao.update(scheduledApp.toEntity().copy(id = app.id))
        } else Log.v("ScheduledAppRepositoryImpl", "Could not find the application")
    }

    override suspend fun delete(scheduledApp: ScheduledApp) {
        scheduledAppDao.delete(scheduledApp.toEntity())
    }

    override suspend fun deleteByPackageName(packageName: String) {
        scheduledAppDao.deleteByPackageName(packageName)
    }

    override suspend fun getById(id: Long): ScheduledApp? {
        return scheduledAppDao.getById(id)?.toDomain()
    }

    override suspend fun getByPackageName(packageName: String): ScheduledApp? {
        return scheduledAppDao.getByPackageName(packageName)?.toDomain()
    }

    override suspend fun getAll(): List<ScheduledApp> {
        return scheduledAppDao.getAll().map { it.toDomain() }
    }

    private fun ScheduledAppEntity.toDomain(): ScheduledApp {
        return ScheduledApp(
            name = name,
            packageName = packageName,
            scheduledTime = scheduledTime,
            repeatInterval = repeatInterval,
            repeatValue = repeatValue,
            lastExecutionTime = lastExecutionTime,
            isSystemApp = isSystemApp
        )
    }
}