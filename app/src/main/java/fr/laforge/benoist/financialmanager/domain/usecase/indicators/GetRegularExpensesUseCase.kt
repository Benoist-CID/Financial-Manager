package fr.laforge.benoist.financialmanager.domain.usecase.indicators

import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionFilter
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.YearMonth

/**
 * A use case to retrieve regular expense transactions
 */
class GetRegularExpensesUseCase(
    private val financialRepository: FinancialRepository,
) {
    /**
     * Calculates the total expenses for a specific month.
     * @param currentMonth The month to calculate expenses for. Defaults to the current system month.
     */
    operator fun invoke(currentMonth: YearMonth = YearMonth.now()): Flow<Float> {

        // Strict boundaries: Start of 1st day (00:00:00) to End of Last day (23:59:59)
        val startDate = currentMonth.atDay(1).atStartOfDay()
        val endDate = currentMonth.atEndOfMonth().atTime(23, 59, 59)

        return financialRepository.getTransactions(
            TransactionFilter(
                type = TransactionType.Expense,
                startDate = startDate,
                endDate = endDate,
                descriptionQuery = "",
                isPeriodic = false,
                parentId = 0
            )
        ).map { transactions ->
            transactions
                .sumOf { it.amount.toDouble() }
                .toFloat()
        }
    }
}
