package fr.laforge.benoist.financialmanager.presentation.ui.transaction.update

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.laforge.benoist.financialmanager.domain.usecase.TransactionInteractor
import fr.laforge.benoist.financialmanager.domain.usecase.transaction.GetTransactionByIdUseCase
import fr.laforge.benoist.financialmanager.presentation.ui.transaction.detail.TransactionUiState
import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionCategory
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * ViewModel for the update-transaction screen.
 *
 * Loads the target transaction on initialisation, exposes editable state to the UI,
 * and delegates all mutations to [TransactionInteractor] so that business rules
 * (e.g. cascading parent updates) are enforced in the domain layer.
 *
 * @property savedStateHandle Provides the [transactionId] navigation argument.
 * @property getTransactionByIdUseCase Domain use case for the initial data load.
 * @property transactionInteractor Orchestrates update and periodic-check operations.
 *
 * @note [FinancialRepository] is intentionally absent from this constructor. The second
 * [updateTransaction] overload previously called the repository directly; it now routes
 * through [TransactionInteractor] with [shouldUpdateParent] = false, keeping the
 * presentation layer free of data-layer dependencies.
 */
class UpdateTransactionViewModel(
    savedStateHandle: SavedStateHandle,
    private val getTransactionByIdUseCase: GetTransactionByIdUseCase,
    private val transactionInteractor: TransactionInteractor,
) : ViewModel() {

    private val transactionId = savedStateHandle["transactionId"] ?: 0
    var uiState: TransactionUiState by mutableStateOf(TransactionUiState())
    private val _updateTransactionResult = MutableStateFlow<Result<Boolean>>(Result.success(false))
    val updateTransactionResult: StateFlow<Result<Boolean>> = _updateTransactionResult.asStateFlow()

    init {
        viewModelScope.launch {
            uiState = TransactionUiState(
                getTransactionByIdUseCase(transactionId)
                    .filterNotNull()
                    .first()
            )
        }
    }

    /**
     * Returns whether [transaction] is a periodic (recurring) transaction.
     *
     * @param transaction The transaction to inspect.
     * @return `true` if the transaction is periodic, `false` otherwise.
     */
    fun isPeriodicTransaction(transaction: Transaction) =
        transactionInteractor.isPeriodicTransaction(transaction)

    /**
     * Updates [transaction] via the interactor, optionally cascading to its parent.
     *
     * @param transaction The transaction to persist.
     * @param shouldUpdateParent Whether to propagate changes to the parent recurring template.
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

    /**
     * Persists the current [uiState] transaction without cascading to the parent.
     *
     * @param dispatcher Coroutine dispatcher to use for the IO operation (injectable for testing).
     */
    fun updateTransaction(dispatcher: CoroutineDispatcher = Dispatchers.IO) {
        viewModelScope.launch(dispatcher) {
            transactionInteractor.updateTransaction(uiState.transaction, shouldUpdateParent = false)
        }
    }

    /**
     * Updates the transaction type in the local UI state.
     *
     * @param transactionType The new [TransactionType] to apply.
     */
    fun updateInputType(transactionType: TransactionType) {
        uiState = uiState.copy(transaction = uiState.transaction.copy(type = transactionType))
    }

    /**
     * Updates the transaction category in the local UI state.
     *
     * @param transactionCategory The new [TransactionCategory] to apply.
     */
    fun updateTransactionCategory(transactionCategory: TransactionCategory) {
        uiState =
            uiState.copy(transaction = uiState.transaction.copy(category = transactionCategory))
    }

    /**
     * Parses [amount] and updates the amount in the local UI state.
     * Logs a warning and discards the value if [amount] is not a valid number.
     *
     * @param amount Raw string amount from the UI input field.
     */
    fun updateAmount(amount: String) {
        try {
            uiState =
                uiState.copy(transaction = uiState.transaction.copy(amount = amount.toFloat()))
        } catch (e: NumberFormatException) {
            Timber.w(e)
        }
    }

    /**
     * Updates the description in the local UI state.
     *
     * @param newDescription The new description string.
     */
    fun updateDescription(newDescription: String) {
        uiState = uiState.copy(transaction = uiState.transaction.copy(description = newDescription))
    }
}
