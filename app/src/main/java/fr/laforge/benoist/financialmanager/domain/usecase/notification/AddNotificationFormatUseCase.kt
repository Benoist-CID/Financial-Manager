package fr.laforge.benoist.financialmanager.domain.usecase.notification

import fr.laforge.benoist.financialmanager.domain.model.notification.NotificationFormat
import fr.laforge.benoist.financialmanager.domain.repository.NotificationFormatRepository

/**
 * Persists a new user-defined [NotificationFormat].
 *
 * Validates that the pattern contains at least the `{amount}` placeholder before saving,
 * since a format without an amount extraction point is functionally useless.
 *
 * @property repository Persistence port for [NotificationFormat] entries.
 */
class AddNotificationFormatUseCase(
    private val repository: NotificationFormatRepository,
) {
    /**
     * Saves [format] to the repository.
     *
     * @param format The format to persist.
     * @throws IllegalArgumentException if [NotificationFormat.pattern] does not contain
     *   the `{amount}` placeholder.
     */
    suspend operator fun invoke(format: NotificationFormat) {
        require(format.pattern.contains(AMOUNT_PLACEHOLDER)) {
            "Pattern must contain the {amount} placeholder"
        }
        repository.add(format)
    }

    companion object {
        const val AMOUNT_PLACEHOLDER = "{amount}"
    }
}
