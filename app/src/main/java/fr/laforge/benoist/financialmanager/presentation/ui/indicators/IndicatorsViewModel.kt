package fr.laforge.benoist.financialmanager.presentation.ui.indicators

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.laforge.benoist.financialmanager.domain.model.indicator.DailyPoint
import fr.laforge.benoist.financialmanager.domain.model.indicator.LifestyleState
import fr.laforge.benoist.financialmanager.domain.model.indicator.LifestyleStatus
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.GetDailyBalanceUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.GetMonthStartingBalanceUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.GetNonRecurringIncomeUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.GetRecurringExpensesUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.GetRecurringIncomeUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.GetRegularExpensesUseCase
import fr.laforge.benoist.financialmanager.domain.util.getNumberOfRemainingDaysInMonth
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime

class IndicatorsViewModel(
    getRecurringIncomeUseCase: GetRecurringIncomeUseCase,
    getRecurringExpensesUseCase: GetRecurringExpensesUseCase,
    getRegularExpensesUseCase: GetRegularExpensesUseCase,
    getDailyBalanceUseCase: GetDailyBalanceUseCase,
    getNonRecurringIncomeUseCase: GetNonRecurringIncomeUseCase,
    getMonthStartingBalanceUseCase: GetMonthStartingBalanceUseCase,
) : ViewModel() {
    /**
     * Exposes the recurring income as a hot state flow.
     * * - started = WhileSubscribed(5000): Stops the upstream flow 5 seconds
     * after the UI disappears (saves resources), but keeps it alive during
     * rotations.
     */
    val recurringIncome: StateFlow<Float> = getRecurringIncomeUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0f
        )

    /**
     * Exposes the non-recurring income as a hot state flow.
     */
    val nonRecurringIncome = getNonRecurringIncomeUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0f)

    /**
     * Exposes the recurring expenses as a hot state flow.
     * * - started = WhileSubscribed(5000): Stops the upstream flow 5 seconds
     * after the UI disappears (saves resources), but keeps it alive during
     * rotations.
     */
    val recurringExpenses: StateFlow<Float> = getRecurringExpensesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0f
        )

    /**
     * Exposes the regular expenses as a hot state flow.
     * * - started = WhileSubscribed(5000): Stops the upstream flow 5 seconds
     * after the UI disappears (saves resources), but keeps it alive during
     * rotations.
     */
    val regularExpenses: StateFlow<Float> = getRegularExpensesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0f
        )

    /**
     * Exposes the start of month balance.
     * * - started = WhileSubscribed(5000): Stops the upstream flow 5 seconds
     * after the UI disappears (saves resources), but keeps it alive during
     * rotations.
     */
    val startBalanceFlow: StateFlow<Float> = getMonthStartingBalanceUseCase(LocalDateTime.now()).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = 0f
    )


    val projectedBalance: StateFlow<Float> = combine(
        recurringIncome,
        nonRecurringIncome, // <--- Added
        recurringExpenses,
        regularExpenses,
        startBalanceFlow,
    ) { rIncome, nrIncome, rExpenses, varExpenses, startBalance ->

        val totalIncome = rIncome + nrIncome
        val currentBalance = totalIncome - rExpenses - varExpenses + startBalance

        // Calculate Average Spend Rate
        val now = LocalDateTime.now()
        val daysPassed = if (now.dayOfMonth == 0) 1 else now.dayOfMonth
        val remainingDays = now.getNumberOfRemainingDaysInMonth()

        val dailyAverage = varExpenses / daysPassed
        val projectedFutureSpend = dailyAverage * remainingDays

        // Result: Where will we land?
        currentBalance - projectedFutureSpend

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0f)

    val lifestyleRatio: StateFlow<LifestyleState> = combine(
        recurringIncome,
        recurringExpenses
    ) { income, expenses ->
        if (income == 0f) return@combine LifestyleState(0f, LifestyleStatus.Danger)

        val ratio = expenses / income

        // Logic is now delegated to the Enum
        val status = LifestyleStatus.from(ratio)

        LifestyleState(ratio, status)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LifestyleState())

    /**
     * Exposes the daily balance graph as a hot state flow.
     */
    val dailyBalanceGraph: StateFlow<List<DailyPoint>> = getDailyBalanceUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
}
