package fr.laforge.benoist.financialmanager.domain.usecase.transaction

import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionFilter
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import fr.laforge.benoist.financialmanager.domain.model.transaction.UpcomingExpense
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.YearMonth

/**
 * Returns all non-template expenses scheduled between [now] and the last moment
 * of [currentMonth], sorted by date ascending.
 *
 * This covers two kinds of upcoming expenses:
 * - One-off future-dated entries ([Transaction.parent] == 0).
 * - Generated instances of recurring templates ([Transaction.parent] != 0).
 *
 * Periodic **templates** ([Transaction.isPeriodic] == true) are excluded: they
 * represent configuration, not actual scheduled payments.
 *
 * @property financialRepository Source of truth for transactions.
 */
class GetUpcomingExpensesUseCase(
    private val financialRepository: FinancialRepository,
) {
    /**
     * @param now          Lower bound (inclusive). Defaults to [LocalDateTime.now].
     * @param currentMonth Determines the upper bound (last second of its last day).
     *                     Defaults to [YearMonth.now].
     * @return [Flow] emitting an updated list of [UpcomingExpense] sorted by date ascending.
     */
    operator fun invoke(
        now: LocalDateTime = LocalDateTime.now(),
        currentMonth: YearMonth = YearMonth.now(),
    ): Flow<List<UpcomingExpense>> {
        val endOfMonth = currentMonth.atEndOfMonth().atTime(23, 59, 59)

        return financialRepository.getTransactions(
            TransactionFilter(
                type = TransactionType.Expense,
                startDate = now,
                endDate = endOfMonth,
                isPeriodic = false,
                // null skips the parent filter — includes both standalone (parent=0)
                // and recurring-child (parent!=0) expenses.
                parentId = null,
            )
        ).map { transactions ->
            transactions
                .sortedBy { it.dateTime }
                .map { t ->
                    UpcomingExpense(
                        uid = t.uid,
                        date = t.dateTime,
                        amount = t.amount,
                        description = t.description,
                        category = t.category,
                        isRecurring = t.parent != 0,
                    )
                }
        }
    }
}
