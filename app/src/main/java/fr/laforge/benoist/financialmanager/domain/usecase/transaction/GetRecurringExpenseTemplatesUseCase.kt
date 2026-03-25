package fr.laforge.benoist.financialmanager.domain.usecase.transaction

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionFilter
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Use case responsible for retrieving the definitions (templates) of all recurring expenses.
 *
 * This use case is primarily used for the "Recurring Expenses" management screen, allowing
 * the user to see and edit their fixed costs (e.g., Rent, Netflix, Loan payments).
 *
 * @property repository The [FinancialRepository] source of truth for all transactions.
 */
class GetRecurringExpenseTemplatesUseCase(private val repository: FinancialRepository) {
    /**
     * Retrieves a stream of recurring expense templates.
     *
     * The resulting list is filtered to include only:
     * 1. Transactions marked as [Transaction.isPeriodic] (Templates).
     * 2. Transactions where [Transaction.type] is [TransactionType.Expense].
     *
     * The list is **sorted by amount in descending order**, ensuring that the
     * largest fixed expenses (like Rent or Loans) appear at the top of the UI.
     *
     * @return A [Flow] emitting the filtered and sorted list of [Transaction] objects.
     */
    operator fun invoke(): Flow<List<Transaction>> = repository.getTransactions(
        filter = TransactionFilter.monthlyRecurringExpenses()
    ).map { transactions ->
        transactions
            .filter { it.isPeriodic && it.type == TransactionType.Expense }
            .sortedByDescending { it.amount }
    }
}
