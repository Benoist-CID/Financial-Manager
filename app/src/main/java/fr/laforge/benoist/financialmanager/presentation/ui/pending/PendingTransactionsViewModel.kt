package fr.laforge.benoist.financialmanager.presentation.ui.pending

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.laforge.benoist.financialmanager.domain.model.notification.PendingTransaction
import fr.laforge.benoist.financialmanager.domain.repository.PendingTransactionRepository
import fr.laforge.benoist.financialmanager.domain.usecase.notification.ConfirmPendingTransactionUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.notification.DismissPendingTransactionUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for the Pending Transactions screen.
 *
 * Exposes the current list of pending transactions as a [StateFlow] and provides
 * actions to confirm or dismiss individual entries.
 *
 * @property repository Source of truth for pending transactions.
 * @property confirmPendingTransactionUseCase Promotes a pending entry to the transaction history.
 * @property dismissPendingTransactionUseCase Marks a pending entry as a false positive.
 */
class PendingTransactionsViewModel(
    private val repository: PendingTransactionRepository,
    private val confirmPendingTransactionUseCase: ConfirmPendingTransactionUseCase,
    private val dismissPendingTransactionUseCase: DismissPendingTransactionUseCase,
) : ViewModel() {

    /**
     * Current UI state, updated reactively whenever the pending-transaction table changes.
     */
    val uiState: StateFlow<PendingTransactionsUiState> = repository
        .getAllPending()
        .map { PendingTransactionsUiState(transactions = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PendingTransactionsUiState(),
        )

    /**
     * Confirms [pendingTransaction], promoting it to the main transaction history.
     */
    fun confirm(pendingTransaction: PendingTransaction) {
        viewModelScope.launch {
            confirmPendingTransactionUseCase(pendingTransaction)
        }
    }

    /**
     * Dismisses [pendingTransaction] as a false positive, removing it from the pending queue.
     */
    fun dismiss(pendingTransaction: PendingTransaction) {
        viewModelScope.launch {
            dismissPendingTransactionUseCase(pendingTransaction)
        }
    }
}
