package fr.laforge.benoist.financialmanager.ui.transaction.update

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.laforge.benoist.financialmanager.interactors.TransactionInteractor
import fr.laforge.benoist.financialmanager.ui.transaction.detail.TransactionUiState
import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.model.TransactionCategory
import fr.laforge.benoist.model.TransactionType
import fr.laforge.benoist.repository.FinancialRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

class UpdateTransactionViewModel(
    savedStateHandle: SavedStateHandle,
    private val financialRepository: FinancialRepository,
    private val transactionInteractor: TransactionInteractor,
) : ViewModel() {
    private val transactionId = savedStateHandle["transactionId"] ?: 0
    var uiState: TransactionUiState by mutableStateOf(TransactionUiState())
    private val _updateTransactionResult = MutableStateFlow<Result<Boolean>>(Result.success(false))
    val updateTransactionResult: StateFlow<Result<Boolean>> = _updateTransactionResult.asStateFlow()

    init {
        viewModelScope.launch {
            uiState = TransactionUiState(
                financialRepository.get(uid = transactionId)
                    .filterNotNull()
                    .first()
            )
        }
    }

    fun isPeriodicTransaction(transaction: Transaction) =
        transactionInteractor.isPeriodicTransaction(transaction)

    /**
     * Updates a transaction.
     *
     * @param transaction The transaction to update.
     * @param shouldUpdateParent Whether to update the parent transaction if it exists.
     */
    fun updateTransaction(
        transaction: Transaction,
        shouldUpdateParent: Boolean = false
    ) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                _updateTransactionResult.value =
                    transactionInteractor.updateTransaction(transaction, shouldUpdateParent)
            }
        } catch (e: Exception) {
            _updateTransactionResult.value = Result.failure(e)
        }
    }

    fun updateInputType(transactionType: TransactionType) {
        uiState = uiState.copy(transaction = uiState.transaction.copy(type = transactionType))
    }

    fun updateTransactionCategory(transactionCategory: TransactionCategory) {
        uiState =
            uiState.copy(transaction = uiState.transaction.copy(category = transactionCategory))
    }

    fun updateAmount(amount: String) {
        try {
            uiState =
                uiState.copy(transaction = uiState.transaction.copy(amount = amount.toFloat()))
        } catch (e: NumberFormatException) {
            Timber.w(e)
        }
    }

    fun updateDescription(newDescription: String) {
        uiState = uiState.copy(transaction = uiState.transaction.copy(description = newDescription))
    }

    /**
     * Updates the transaction
     */
    fun updateTransaction(dispatcher: CoroutineDispatcher = Dispatchers.IO) {
        viewModelScope.launch(dispatcher) {
            financialRepository.updateTransaction(uiState.transaction)
        }
    }
}
