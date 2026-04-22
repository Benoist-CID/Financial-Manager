package fr.laforge.benoist.financialmanager.presentation.ui.home.upcoming

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.laforge.benoist.financialmanager.domain.model.transaction.UpcomingExpense
import fr.laforge.benoist.financialmanager.domain.usecase.transaction.GetUpcomingExpensesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel for the Upcoming Expenses card on the Home screen.
 *
 * Derives both [upcomingExpenses] and [totalUpcomingAmount] from a single shared
 * [StateFlow] so that the underlying repository query is executed only once.
 *
 * @property getUpcomingExpensesUseCase Provides the reactive list of upcoming expenses.
 */
class UpcomingExpensesViewModel(
    getUpcomingExpensesUseCase: GetUpcomingExpensesUseCase,
) : ViewModel() {

    private val _expensesFlow: StateFlow<List<UpcomingExpense>> = getUpcomingExpensesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    /**
     * Emits the list of expenses between now and end of the current month,
     * sorted by date ascending. Empty list until data is loaded.
     */
    val upcomingExpenses: StateFlow<List<UpcomingExpense>> = _expensesFlow

    /**
     * Emits the sum of all upcoming expense amounts.
     * Derived from [_expensesFlow] — no additional repository query is issued.
     */
    val totalUpcomingAmount: StateFlow<Float> = _expensesFlow
        .map { expenses -> expenses.sumOf { it.amount.toDouble() }.toFloat() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0f,
        )
}
