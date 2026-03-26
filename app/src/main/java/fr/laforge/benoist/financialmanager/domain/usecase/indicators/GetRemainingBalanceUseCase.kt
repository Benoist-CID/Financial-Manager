package fr.laforge.benoist.financialmanager.domain.usecase.indicators

import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

/**
 * Calculates the remaining budget balance for the month that contains [date].
 *
 * Formula: `(Recurring Income + Extra Income) − (Fixed Expenses + Variable Expenses)`
 *
 * Periodic templates ([Transaction.isPeriodic] == true) are excluded — only real
 * money movements count.
 *
 * @property repository The [FinancialRepository] source of truth.
 */
class GetRemainingBalanceUseCase(private val repository: FinancialRepository) {
    /**
     * @param date Reference date used to determine the month window. Defaults to now.
     * @return A [Flow] emitting the running net balance as a [Float]. Negative values
     *   indicate that spending exceeds income for the month.
     */
    operator fun invoke(date: LocalDateTime = LocalDateTime.now()): Flow<Float> {
        val startOfMonth = date.withDayOfMonth(1).toLocalDate().atStartOfDay()
        val endOfMonth = date.plusMonths(1).withDayOfMonth(1).toLocalDate().atStartOfDay()

        return repository.getAllInDateRange(startOfMonth, endOfMonth)
            .map { transactions ->
                transactions
                    // Filter out Templates (Definitions)
                    // We only want real money movements (Manual inputs OR Generated instances)
                    .filter { !it.isPeriodic }
                    .fold(0f) { acc, transaction ->
                        when (transaction.type) {
                            TransactionType.Income -> acc + transaction.amount
                            TransactionType.Expense -> acc - transaction.amount
                        }
                    }
            }
    }
}
