package fr.laforge.benoist.financialmanager.presentation.ui.transaction.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.laforge.benoist.financialmanager.domain.usecase.transaction.GetTransactionByIdUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel for the transaction detail screen.
 *
 * Loads a single transaction by its ID (taken from [SavedStateHandle]) and exposes
 * it as a [StateFlow] of [TransactionUiState] for the UI to observe.
 *
 * @property savedStateHandle Provides the [transactionId] navigation argument.
 * @property getTransactionByIdUseCase Domain use case that fetches the transaction reactively.
 *
 * @note [FinancialRepository] is intentionally absent from this constructor. All data
 * access must go through the use case layer to honour the Clean Architecture dependency rule.
 */
class TransactionDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val getTransactionByIdUseCase: GetTransactionByIdUseCase,
) : ViewModel() {

    private val transactionId = savedStateHandle.get<Int>("transactionId") ?: 0

    val uiState: StateFlow<TransactionUiState> = getTransactionByIdUseCase(transactionId)
        .filterNotNull()
        .map { TransactionUiState(transaction = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TransactionUiState()
        )
}
