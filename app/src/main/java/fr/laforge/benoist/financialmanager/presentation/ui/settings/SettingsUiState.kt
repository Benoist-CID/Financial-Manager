package fr.laforge.benoist.financialmanager.presentation.ui.settings

/**
 * Immutable UI state for [SettingsScreen].
 *
 * @property versionName The application version name to display (e.g. `"1.0"`).
 */
data class SettingsUiState(
    val versionName: String = "",
)
