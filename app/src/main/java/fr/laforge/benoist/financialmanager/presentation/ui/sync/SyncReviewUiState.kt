package fr.laforge.benoist.financialmanager.presentation.ui.sync

import fr.laforge.benoist.financialmanager.domain.model.sync.TransactionMatchResult

/**
 * Immutable snapshot of the Sync Review screen state.
 *
 * @property isIdle     `true` before the user has initiated a sync run.
 * @property isLoading  `true` while a sync is in progress.
 * @property matches    Pending match candidates awaiting user review.
 * @property error      Non-null when the last sync attempt failed; contains a human-readable
 *                      message to display.
 */
data class SyncReviewUiState(
    val isIdle: Boolean = true,
    val isLoading: Boolean = false,
    val matches: List<TransactionMatchResult> = emptyList(),
    val error: String? = null,
)
