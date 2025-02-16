package rakib.hasan.scheduleit.core.di

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import rakib.hasan.scheduleit.core.database.AppDatabase
import rakib.hasan.scheduleit.core.utils.AppBroadcastReceiver
import rakib.hasan.scheduleit.core.utils.PermissionManager
import rakib.hasan.scheduleit.feature.home.service.AlarmScheduler
import rakib.hasan.scheduleit.feature.schedule.presentation.viewmodel.ScheduleViewModel
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideApplicationContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun providePackageManager(@ApplicationContext context: Context): PackageManager {
        return context.packageManager
    }

    @Provides
    @Singleton
    fun providePermissionManager(@ApplicationContext context: Context): PermissionManager {
        return PermissionManager(context)
    }

/*
    @Provides
    fun provideAppBroadcastReceiver(viewModel: ScheduleViewModel, alarmScheduler: AlarmScheduler): AppBroadcastReceiver {
        return AppBroadcastReceiver(viewModel, alarmScheduler)
    }
*/

    @Provides
    @Singleton
    fun provideAlarmScheduler(@ApplicationContext context: Context): AlarmScheduler {
        return AlarmScheduler(context)
    }

}