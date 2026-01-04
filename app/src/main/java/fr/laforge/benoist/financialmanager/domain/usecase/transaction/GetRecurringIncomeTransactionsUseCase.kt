package fr.laforge.benoist.financialmanager.domain.usecase.transaction

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionFilter
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Use case responsible for retrieving the definitions (templates) of all recurring income sources.
 *
 * This is used to manage fixed revenue streams such as salaries, rental income, or
 * monthly stipends. It filters the raw transaction list to isolate items that represent
 * income templates.
 *
 * @property repository The [FinancialRepository] source of truth.
 */
class GetRecurringIncomeTransactionsUseCase(private val repository: FinancialRepository) {
    /**
     * Retrieves a stream of recurring income transactions.
     *
     * @return A [Flow] emitting the filtered and sorted list of [Transaction] objects.
     */
    operator fun invoke(): Flow<List<Transaction>> = repository.getTransactions(
        filter = TransactionFilter.monthlyRecurringIncome()
    ).map { transactions ->
        transactions.sortedByDescending { it.amount }
    }
}
