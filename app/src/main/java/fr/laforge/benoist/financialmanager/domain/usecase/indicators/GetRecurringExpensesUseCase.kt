package fr.laforge.benoist.financialmanager.domain.usecase.indicators

import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionFilter
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Returns the sum of all recurring (fixed) expense transactions for the current month.
 *
 * Only periodic templates that match [TransactionFilter.monthlyRecurringExpenses] are
 * included. Variable / one-off expenses are excluded; use [GetRegularExpensesUseCase]
 * for those.
 *
 * @property financialRepository The [FinancialRepository] source of truth.
 */
class GetRecurringExpensesUseCase(private val financialRepository: FinancialRepository) {
    /**
     * @return A [Flow] emitting the total recurring expenses as a [Float],
     *   updated whenever the underlying data changes.
     */
    operator fun invoke(): Flow<Float> =
        financialRepository.getTransactions(filter = TransactionFilter.monthlyRecurringExpenses())
            .map { transactions ->
                // Sum all amounts in the list
                transactions.map { it.amount }.sum()
            }
}
