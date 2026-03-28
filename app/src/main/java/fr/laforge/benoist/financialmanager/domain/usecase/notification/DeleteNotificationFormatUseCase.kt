package fr.laforge.benoist.financialmanager.domain.usecase.notification

import fr.laforge.benoist.financialmanager.domain.repository.NotificationFormatRepository
import java.util.UUID

/**
 * Removes a user-defined notification format from the repository.
 *
 * @property repository Persistence port for notification formats.
 */
class DeleteNotificationFormatUseCase(
    private val repository: NotificationFormatRepository,
) {
    /**
     * Deletes the format identified by [id].
     *
     * Silently succeeds if the format no longer exists.
     *
     * @param id The [UUID] of the format to remove.
     */
    suspend operator fun invoke(id: UUID) {
        repository.delete(id)
    }
}
