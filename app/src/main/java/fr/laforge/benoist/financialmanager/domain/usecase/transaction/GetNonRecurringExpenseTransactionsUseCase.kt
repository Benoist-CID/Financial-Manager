package fr.laforge.benoist.financialmanager.domain.usecase.transaction

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionFilter
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import fr.laforge.benoist.financialmanager.domain.util.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

/**
 * Use case responsible for retrieving non-recurring (variable) expenses.
 *
 * This isolates "Variable Spending" (e.g., Groceries, Restaurants, Shopping) from
 * fixed monthly bills. It is used to analyze discretionary spending.
 *
 * @property repository The [FinancialRepository] source of truth.
 * @property logger Domain [Logger] for diagnostic output. Defaults to [Logger.NoOp].
 */
class GetNonRecurringExpenseTransactionsUseCase(
    private val repository: FinancialRepository,
    private val logger: Logger = Logger.NoOp
) {

    /**
     * Retrieves a stream of non-recurring expense transactions for the month of the given [date].
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
        logger.info("Fetching non-recurring expense transactions for the month of $date")

        return repository.getTransactions(
            filter = TransactionFilter.monthlyVariableExpenses(date)
        ).map { transactions ->
            transactions.sortedByDescending { it.amount }
        }
    }
}
