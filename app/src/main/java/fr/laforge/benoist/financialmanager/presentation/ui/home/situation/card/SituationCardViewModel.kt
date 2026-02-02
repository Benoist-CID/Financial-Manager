package fr.laforge.benoist.financialmanager.presentation.ui.home.situation.card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.laforge.benoist.financialmanager.domain.repository.PreferencesRepository
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.CalculateSituationProportionsUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.GetMonthStartingBalanceUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.GetNonRecurringIncomeUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.GetRecurringExpensesUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.GetRecurringIncomeUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.GetRegularExpensesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime

/**
 * A ViewModel for the Situation Card.
 */
class SituationCardViewModel(
    preferencesRepository: PreferencesRepository,
    getRecurringExpensesUseCase: GetRecurringExpensesUseCase,
    getNonRecurringExpensesUseCase: GetRegularExpensesUseCase,
    getRecurringIncomeUseCase: GetRecurringIncomeUseCase,
    getNonRecurringIncomeUseCase: GetNonRecurringIncomeUseCase,
    getMonthStartingBalanceUseCase: GetMonthStartingBalanceUseCase,
    calculateSituationProportionsUseCase: CalculateSituationProportionsUseCase,
) : ViewModel() {
    private val now = LocalDateTime.now()
    private val currentMonth = java.time.YearMonth.from(now)

    // A flow for income
    private val incomeFlow = combine(
        getNonRecurringIncomeUseCase(),
        getRecurringIncomeUseCase()
    ) { nonRecurring, recurring ->
        nonRecurring + recurring
    }

    // A flow for expenses
    private val expensesFlow: Flow<ExpenseBreakdown> = combine(
        getNonRecurringExpensesUseCase(),
        getRecurringExpensesUseCase(),
    ) { regular, recurring ->
        ExpenseBreakdown(
            total = regular + recurring,
            regular = regular,
            recurring = recurring
        )
    }

    val uiState: StateFlow<SituationCardState> = combine(
        incomeFlow,
        expensesFlow,
        preferencesRepository.getSavingTarget(),
        getMonthStartingBalanceUseCase(now)
    ) { income, expenses, savings, startBalance ->

        val remaining = (income + startBalance) - expenses.total - savings

        // ... Daily Budget Logic ...
        val daysInMonth = currentMonth.lengthOfMonth()
        val remainingDays = (daysInMonth - now.dayOfMonth).coerceAtLeast(1)
        val dailyBudget = remaining / remainingDays

        SituationCardState(
            income = income,
            regularExpenses = expenses.regular,     // Retrieved from our Breakdown object
            recurringExpenses = expenses.recurring, // Retrieved from our Breakdown object
            savingsTarget = savings,
            remainingBalance = remaining,
            dailyBudget = dailyBudget,
            proportions = calculateSituationProportionsUseCase(
                income = income,
                recurringExpenses = expenses.recurring,
                regularExpenses = expenses.regular,
                savingsTarget = savings
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SituationCardState()
    )
}

private data class ExpenseBreakdown(
    val total: Float,
    val regular: Float,
    val recurring: Float
)
