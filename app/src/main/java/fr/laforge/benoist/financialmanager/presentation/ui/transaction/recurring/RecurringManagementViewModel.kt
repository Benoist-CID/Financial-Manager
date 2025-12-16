package fr.laforge.benoist.financialmanager.presentation.ui.transaction.recurring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.usecase.DeleteTransactionUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.transaction.GetRecurringExpenseTemplatesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RecurringManagementViewModel(
    private val getRecurringExpenseTemplatesUseCase: GetRecurringExpenseTemplatesUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
) : ViewModel() {

    // 1. Raw Data
    private val _rawItems = getRecurringExpenseTemplatesUseCase().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    // 2. Search State
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    // 3. Filtered List (Displayed in UI)
    val recurringItems: StateFlow<List<Transaction>> = combine(_rawItems, _query) { items, query ->
        if (query.isBlank()) {
            items
        } else {
            items.filter { it.description.contains(query, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 4. Total Calculation (Based on filtered list)
    val totalMonthly: StateFlow<Float> = recurringItems.map { list ->
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
