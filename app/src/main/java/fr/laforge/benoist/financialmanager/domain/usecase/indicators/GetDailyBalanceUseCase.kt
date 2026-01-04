package fr.laforge.benoist.financialmanager.domain.usecase.indicators

import fr.laforge.benoist.financialmanager.domain.model.indicator.DailyPoint
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDateTime

class GetDailyBalanceUseCase(
    private val repository: FinancialRepository,
    private val getRecurringIncome: GetRecurringIncomeUseCase,
    private val getRecurringExpenses: GetRecurringExpensesUseCase,
    private val getMonthStartingBalanceUseCase: GetMonthStartingBalanceUseCase,
) {
    /**
     * Returns a flow of DailyPoint for a given date.
     *
     * @param date The date for which to retrieve the daily balance.
     *
     * @return A flow of DailyPoint representing the daily balance for each day of the month.
     */
    operator fun invoke(date: LocalDateTime = LocalDateTime.now()): Flow<List<DailyPoint>> = combine(
        getRecurringIncome(),
        getRecurringExpenses(),
        getMonthStartingBalanceUseCase(date),
        repository.getAllInDateRange(
            startDate = date.withDayOfMonth(1).toLocalDate().atStartOfDay(),
            endDate = date.plusMonths(1).withDayOfMonth(1).toLocalDate().atStartOfDay()
        )
    ) { income, recurringFixed, monthStartingBalance, transactions ->

        // 1. Starting Point (Fixed Budget)
        val startBalance = income - recurringFixed + monthStartingBalance

        // 2. Filter Regular Expenses ONLY (Same logic as your RegularExpensesUseCase)
        val regularExpenses = transactions.filter {
            it.type == TransactionType.Expense && !it.isPeriodic && it.parent == 0
        }

        // 3. Group by Day
        val expensesByDay = regularExpenses
            .groupBy { it.dateTime.dayOfMonth }
            .mapValues { entry -> entry.value.sumOf { it.amount.toDouble() }.toFloat() }

        // 4. Build the timeline
        val today = date.dayOfMonth
        val points = mutableListOf<DailyPoint>()

        var currentBalance = startBalance

        // Loop from Day 1 to Today (or end of month if you want to project flat line)
        for (day in 1..today) {
            val dailyExpense = expensesByDay[day] ?: 0f
            currentBalance -= dailyExpense
            points.add(DailyPoint(day, currentBalance))
        }

        points
    }
}
