package fr.laforge.benoist.financialmanager.domain.usecase.transaction

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionFilter
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import java.time.YearMonth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Retrieves all concrete transactions for a given [YearMonth], optionally filtered
 * by a text search and/or a [TransactionType].
 *
 * Only non-periodic (real money movement) transactions are returned — periodic
 * templates are excluded. Both manual entries and generated recurring instances
 * are included ([TransactionFilter.parentId] == null).
 *
 * @property financialRepository The [FinancialRepository] source of truth.
 */
class GetMonthlyTransactionsUseCase(
    private val financialRepository: FinancialRepository
) {
    /**
     * @param month       The calendar month to query.
     * @param searchQuery Optional text filter applied against [Transaction.description]
     *   (case-insensitive substring match). Defaults to empty (no filter).
     * @param filterType  Optional [TransactionType] to restrict results to income or
     *   expense only. `null` returns both types.
     * @return A [Flow] emitting the list of matching transactions sorted by
     *   [Transaction.dateTime] descending (most recent first).
     */
    operator fun invoke(
        month: YearMonth,
        searchQuery: String = "",
        filterType: TransactionType? = null
    ): Flow<List<Transaction>> {

        // 1. Business Logic: Define the precise time window
        val startDate = month.atDay(1).atStartOfDay() // 00:00:00
        val endDate = month.atEndOfMonth().atTime(23, 59, 59) // 23:59:59

        // 2. Business Logic: Configure the Filter
        // We want actual transactions (isPeriodic = false), not Templates.
        // We want ALL transactions (parentId = null), both manual and recurring instances.
        val filter = TransactionFilter(
            type = filterType,
            startDate = startDate,
            endDate = endDate,
            descriptionQuery = searchQuery,
            isPeriodic = false,
            parentId = null
        )

        // 3. specific sorting or post-processing could happen here if not in SQL
        return financialRepository.getTransactions(filter).map { transactions ->
            // Sort in memory to guarantee order regardless of SQL implementation
            transactions.sortedByDescending { it.dateTime }
        }
    }
}
