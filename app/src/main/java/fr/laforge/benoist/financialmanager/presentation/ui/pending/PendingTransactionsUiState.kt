package fr.laforge.benoist.financialmanager.presentation.ui.pending

import fr.laforge.benoist.financialmanager.domain.model.notification.PendingTransaction

/**
 * UI state for the Pending Transactions screen.
 *
 * @property transactions The current list of unconfirmed pending transactions.
 */
data class PendingTransactionsUiState(
    val transactions: List<PendingTransaction> = emptyList(),
)
