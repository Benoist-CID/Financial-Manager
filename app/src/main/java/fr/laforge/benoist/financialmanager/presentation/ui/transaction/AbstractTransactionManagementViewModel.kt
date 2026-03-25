package fr.laforge.benoist.financialmanager.presentation.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.usecase.DeleteTransactionUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Abstract base [ViewModel] for screens that manage lists of expense and income transactions.
 *
 * Provides shared logic for:
 * 1. Searching/filtering transactions by description.
 * 2. Calculating monthly totals for both expenses and incomes.
 * 3. Deleting transactions.
 *
 * @param expensesFlow A [Flow] providing the stream of expense transactions.
 * @param incomesFlow A [Flow] providing the stream of income transactions.
 * @property deleteTransactionUseCase Use case for transaction deletion.
 */
abstract class AbstractTransactionManagementViewModel(
    expensesFlow: Flow<List<Transaction>>,
    incomesFlow: Flow<List<Transaction>>,
    private val deleteTransactionUseCase: DeleteTransactionUseCase
) : ViewModel() {

    // 1. Raw Data State
    private val _rawExpenses = expensesFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
    private val _rawIncomes = incomesFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // 2. Search State
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    // 3. Filtered Lists (Displayed in UI)
    val expenseItems: StateFlow<List<Transaction>> = combine(_rawExpenses, _query) { items, query ->
        if (query.isBlank()) {
            items
        } else {
            items.filter { it.description.contains(query, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val incomeItems: StateFlow<List<Transaction>> = combine(_rawIncomes, _query) { items, query ->
        if (query.isBlank()) {
            items
        } else {
            items.filter { it.description.contains(query, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 4. Total Calculations (Based on filtered lists)
    val totalExpensesMonthly: StateFlow<Float> = expenseItems.map { list ->
        list.sumOf { it.amount.toDouble() }.toFloat()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    val totalIncomesMonthly: StateFlow<Float> = incomeItems.map { list ->
        list.sumOf { it.amount.toDouble() }.toFloat()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    /**
     * Updates the current search query.
     *
     * @param newQuery The new search string.
     */
    fun updateSearch(newQuery: String) {
        _query.value = newQuery
    }

    /**
     * Deletes the specified transaction.
     *
     * @param transaction The transaction to delete.
     */
    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            deleteTransactionUseCase(transaction)
        }
    }
}
