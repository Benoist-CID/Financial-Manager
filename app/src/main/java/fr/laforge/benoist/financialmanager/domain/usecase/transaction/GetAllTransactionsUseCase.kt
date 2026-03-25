package fr.laforge.benoist.financialmanager.domain.usecase.transaction

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionFilter
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case responsible for retrieving every transaction stored in the repository,
 * without any filtering or pagination.
 *
 * This is intentionally a "dump all" query and should only be used for operations
 * that genuinely require the full dataset — such as a database export/backup.
 * For any display or analytical purpose, prefer a more scoped use case.
 *
 * @property repository The [FinancialRepository] source of truth for all transactions.
 */
class GetAllTransactionsUseCase(private val repository: FinancialRepository) {

    /**
     * Retrieves a stream of all transactions with no filter applied.
     *
     * @return A [Flow] emitting the complete, unfiltered list of [Transaction] objects.
     *
     * @note [TransactionFilter.all] is used here to explicitly signal intent.
     * Passing a null filter would be ambiguous; a named factory method makes the
     * "no constraint" semantic clear to future readers.
     */
    operator fun invoke(): Flow<List<Transaction>> =
        repository.getTransactions(filter = TransactionFilter.all())
}
