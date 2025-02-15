package rakib.hasan.scheduleit.feature.schedule.domain.usecase

import rakib.hasan.scheduleit.feature.schedule.domain.model.ScheduledApp
import rakib.hasan.scheduleit.feature.schedule.domain.repository.ScheduledAppRepository
import javax.inject.Inject

class DeleteScheduledApp @Inject constructor(
    private val repository: ScheduledAppRepository,
) {
    suspend operator fun invoke(scheduledApp: ScheduledApp) {
        repository.delete(scheduledApp)
    }
}
