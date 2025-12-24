package fr.laforge.benoist.financialmanager.presentation.ui.transaction.non.recurring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.usecase.DeleteTransactionUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.transaction.GetNonRecurringExpenseTransactionsUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.transaction.GetNonRecurringIncomeTransactionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NonRecurringManagementViewModel(
    getNonRecurringExpenseTransactionsUseCase: GetNonRecurringExpenseTransactionsUseCase,
    getNonRecurringIncomeTransactionsUseCase: GetNonRecurringIncomeTransactionsUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
) : ViewModel() {

    // 1. Raw Data
    private val _rawExpenses = getNonRecurringExpenseTransactionsUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
    private val _rawIncomes = getNonRecurringIncomeTransactionsUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // 2. Search State
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    // 3. Filtered List (Displayed in UI)
    val nonRecurringExpensesItems: StateFlow<List<Transaction>> = combine(_rawExpenses, _query) { items, query ->
        if (query.isBlank()) {
            items
        } else {
            items.filter { it.description.contains(query, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val nonRecurringIncomesItems: StateFlow<List<Transaction>> = combine(_rawIncomes, _query) { items, query ->
        if (query.isBlank()) {
            items
        } else {
            items.filter { it.description.contains(query, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 4. Total Calculation (Based on filtered list)
    val totalExpensesMonthly: StateFlow<Float> = nonRecurringExpensesItems.map { list ->
        list.sumOf { it.amount.toDouble() }.toFloat()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    val totalIncomesMonthly: StateFlow<Float> = nonRecurringIncomesItems.map { list ->
        list.sumOf { it.amount.toDouble() }.toFloat()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    fun updateSearch(newQuery: String) {
        _query.value = newQuery
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            // Logic to delete the template
            deleteTransactionUseCase(transaction)
        }
    }
}
