package fr.laforge.benoist.financialmanager.presentation.ui.settings.notificationformat

import fr.laforge.benoist.financialmanager.domain.model.notification.NotificationFormat

/**
 * Immutable UI state for the Notification Formats settings screen.
 *
 * @property formats The list of all user-defined [NotificationFormat] entries currently persisted.
 */
data class NotificationFormatUiState(
    val formats: List<NotificationFormat> = emptyList(),
)
