package fr.laforge.benoist.financialmanager.domain.usecase.notification

import fr.laforge.benoist.financialmanager.domain.model.notification.NotificationFormat
import fr.laforge.benoist.financialmanager.domain.repository.NotificationFormatRepository
import kotlinx.coroutines.flow.Flow

/**
 * Returns a reactive stream of all user-defined [NotificationFormat] entries.
 *
 * @property repository Persistence port for notification formats.
 */
class GetAllNotificationFormatsUseCase(
    private val repository: NotificationFormatRepository,
) {
    /**
     * @return A [Flow] that emits the current list of formats and re-emits on every change.
     */
    operator fun invoke(): Flow<List<NotificationFormat>> = repository.getAll()
}
