package rakib.hasan.scheduleit.feature.schedule.domain.usecase

import rakib.hasan.scheduleit.feature.schedule.domain.model.ScheduledApp
import rakib.hasan.scheduleit.feature.schedule.domain.repository.ScheduledAppRepository
import javax.inject.Inject

class UpdateScheduledApp @Inject constructor(
    private val repository: ScheduledAppRepository,
) {
    suspend operator fun invoke(scheduledApp: ScheduledApp) {
        repository.update(scheduledApp)
    }
}
