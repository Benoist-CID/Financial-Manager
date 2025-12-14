package fr.laforge.benoist.financialmanager.presentation.ui.indicators

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.GetRecurringExpensesUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.GetRecurringIncomeUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class IndicatorsViewModel(
    getRecurringIncomeUseCase: GetRecurringIncomeUseCase,
    getRecurringExpensesUseCase: GetRecurringExpensesUseCase,
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
}
