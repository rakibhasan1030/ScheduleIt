package rakib.hasan.scheduleit.feature.schedule.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import rakib.hasan.scheduleit.core.database.AppDatabase
import rakib.hasan.scheduleit.feature.schedule.data.local.dao.ScheduledAppDao
import rakib.hasan.scheduleit.feature.schedule.data.repository_impl.ScheduledAppRepositoryImpl
import rakib.hasan.scheduleit.feature.schedule.domain.repository.ScheduledAppRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ScheduleModule {

    @Provides
    @Singleton
    fun provideScheduledAppDao(database: AppDatabase): ScheduledAppDao {
        return database.scheduledAppDao()
    }

    @Provides
    @Singleton
    fun provideScheduledAppRepository(dao: ScheduledAppDao): ScheduledAppRepository {
        return ScheduledAppRepositoryImpl(dao)
    }
}