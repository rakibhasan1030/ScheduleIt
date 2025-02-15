package rakib.hasan.scheduleit.feature.schedule.domain.usecase

import rakib.hasan.scheduleit.feature.schedule.domain.model.ScheduledApp
import rakib.hasan.scheduleit.feature.schedule.domain.repository.ScheduledAppRepository
import javax.inject.Inject

class DeleteScheduledAppByPackageName @Inject constructor(
    private val repository: ScheduledAppRepository,
) {
    suspend operator fun invoke(packageName: String) {
        repository.deleteByPackageName(packageName)
    }
}
