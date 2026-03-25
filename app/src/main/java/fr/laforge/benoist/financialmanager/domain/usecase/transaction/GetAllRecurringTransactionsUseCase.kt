package fr.laforge.benoist.financialmanager.domain.usecase.transaction

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionFilter
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case responsible for retrieving all recurring transaction templates stored
 * in the repository, regardless of their type (Expense or Income).
 *
 * A "recurring template" is a transaction that is the source definition of a
 * periodic series: [Transaction.isPeriodic] is `true` and [Transaction.parent] is `0`
 * (meaning it has no parent — it *is* the parent).
 *
 * This is intentionally broader than [GetRecurringExpenseTemplatesUseCase] or
 * [GetRecurringIncomeTransactionsUseCase], which are scoped to a single type.
 * Its primary use case is a full recurring-transactions export/backup, where
 * all fixed commitments (bills, subscriptions, salaries) must be included.
 *
 * @property repository The [FinancialRepository] source of truth for all transactions.
 */
class GetAllRecurringTransactionsUseCase(private val repository: FinancialRepository) {

    /**
     * Retrieves a stream of all recurring transaction templates.
     *
     * The filter targets rows where:
     * - `isPeriodic = true`  — only template (definition) rows
     * - `parentId = 0`       — excludes auto-generated child instances
     * - `type = null`        — includes both Expense and Income templates
     *
     * @return A [Flow] emitting the unordered list of recurring [Transaction] templates.
     *
     * @note No sorting is applied here because the consumer (export pipeline) does not
     * require a specific order. Keeping the use case free of presentation concerns
     * ensures it can be reused in any context without surprising side-effects.
     */
    operator fun invoke(): Flow<List<Transaction>> =
        repository.getTransactions(
            filter = TransactionFilter(
                isPeriodic = true,
                parentId = 0
            )
        )
}
