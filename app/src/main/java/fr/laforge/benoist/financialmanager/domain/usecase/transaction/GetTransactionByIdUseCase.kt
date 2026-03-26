package fr.laforge.benoist.financialmanager.domain.usecase.transaction

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case responsible for retrieving a single [Transaction] by its unique identifier.
 *
 * Centralising this lookup in a use case ensures that ViewModels and other callers
 * never hold a direct reference to [FinancialRepository], preserving the Clean
 * Architecture dependency rule (presentation → domain, never → data).
 *
 * @property repository The [FinancialRepository] that is the source of truth for transactions.
 */
class GetTransactionByIdUseCase(private val repository: FinancialRepository) {

    /**
     * Returns a [Flow] that emits the [Transaction] matching [id].
     *
     * The flow is backed by the repository's reactive stream, so any downstream
     * update to that row will automatically re-emit to collectors.
     *
     * @param id The unique identifier of the transaction to retrieve.
     * @return A [Flow] emitting the matching [Transaction].
     *
     * @note The repository contract returns a hot-ish Flow (Room LiveData-backed).
     * Callers are expected to apply [kotlinx.coroutines.flow.filterNotNull] if the
     * row may be absent, rather than performing null-safety checks here — keeping
     * this use case a pure delegation without defensive logic for absent records.
     */
    operator fun invoke(id: Int): Flow<Transaction> = repository.get(uid = id)
}
