package fr.laforge.benoist.financialmanager.domain.usecase.transaction

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

/**
 * Use case responsible for retrieving non-recurring (variable) income.
 *
 * @property repository The [FinancialRepository] source of truth.
 */
class GetNonRecurringIncomeTransactionsUseCase(private val repository: FinancialRepository) {

    /**
     * Retrieves a stream of non-recurring income transactions for the month of the given [date].
     *
     * The logic performs the following operations:
     * 1. **Time Range**: Fetches transactions only from the 1st of the month to the 1st of the next month.
     * 2. **Filter Type**: Must be [TransactionType.Expense].
     * 3. **Filter Periodic**: Must NOT be a template ([Transaction.isPeriodic] == false).
     * 4. **Filter Source**: Must be a manual entry ([Transaction.parent] == 0).
     * (Items with a parent ID are generated instances of a recurring bill, so we exclude them).
     * 5. **Sort**: Orders the list by [Transaction.amount] in descending order.
     *
     * @param date The reference date to determine the month (defaults to now).
     * @return A [Flow] emitting the filtered and sorted list of [Transaction] objects.
     */
    operator fun invoke(date: LocalDateTime = LocalDateTime.now()): Flow<List<Transaction>> {
        val startDate = date.withDayOfMonth(1).toLocalDate().atStartOfDay()
        val endDate = date.plusMonths(1).withDayOfMonth(1).toLocalDate().atStartOfDay()

        return repository.getAllInDateRange(startDate, endDate).map { transactions ->
            transactions
                .filter {
                    it.type == TransactionType.Income &&
                            !it.isPeriodic &&
                            it.parent == 0
                }
                .sortedByDescending { it.amount }
        }
    }
}
