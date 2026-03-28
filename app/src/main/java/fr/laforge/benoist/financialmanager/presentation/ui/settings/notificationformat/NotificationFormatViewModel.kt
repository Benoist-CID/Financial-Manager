package fr.laforge.benoist.financialmanager.presentation.ui.settings.notificationformat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.laforge.benoist.financialmanager.domain.model.notification.NotificationFormat
import fr.laforge.benoist.financialmanager.domain.usecase.notification.AddNotificationFormatUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.notification.DeleteNotificationFormatUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.notification.GetAllNotificationFormatsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * ViewModel for the Notification Formats settings screen.
 *
 * Exposes the live list of user-defined [NotificationFormat] entries and provides
 * actions to add or delete formats. All mutations are delegated to dedicated use cases,
 * keeping business rules out of the presentation layer.
 *
 * @property getAllNotificationFormatsUseCase Provides a live [kotlinx.coroutines.flow.Flow]
 *   of persisted formats.
 * @property addNotificationFormatUseCase Validates and persists a new format.
 * @property deleteNotificationFormatUseCase Removes a format by its [UUID].
 */
class NotificationFormatViewModel(
    private val getAllNotificationFormatsUseCase: GetAllNotificationFormatsUseCase,
    private val addNotificationFormatUseCase: AddNotificationFormatUseCase,
    private val deleteNotificationFormatUseCase: DeleteNotificationFormatUseCase,
) : ViewModel() {

    /**
     * Observable UI state reflecting the current list of notification formats.
     * Starts with an empty list; updated whenever the underlying repository changes.
     */
    val uiState: StateFlow<NotificationFormatUiState> = getAllNotificationFormatsUseCase()
        .map { NotificationFormatUiState(formats = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NotificationFormatUiState(),
        )

    /**
     * Persists a new notification format with the given [description] and [pattern].
     *
     * Delegates validation (e.g. presence of `{amount}`) to [AddNotificationFormatUseCase].
     * If validation fails the exception is silently swallowed here; the UI should
     * pre-validate before calling this method.
     *
     * @param description Human-readable label for the format.
     * @param pattern      Placeholder-based template string, e.g. `"Spent {amount} at {description}"`.
     */
    fun addFormat(description: String, pattern: String) {
        viewModelScope.launch {
            runCatching {
                addNotificationFormatUseCase(
                    NotificationFormat(description = description, pattern = pattern),
                )
            }
        }
    }

    /**
     * Deletes the notification format identified by [id].
     *
     * @param id UUID of the format to remove.
     */
    fun deleteFormat(id: UUID) {
        viewModelScope.launch {
            deleteNotificationFormatUseCase(id)
        }
    }
}
