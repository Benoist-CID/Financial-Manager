package fr.laforge.benoist.financialmanager.domain.usecase

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Deletes a transaction and, optionally, all associated occurrences.
 *
 * Supports two deletion modes controlled by [DeleteTransactionType]:
 * - [DeleteTransactionType.ThisOccurrenceOnly]: removes only the given instance.
 * - [DeleteTransactionType.AllOccurrences]: also fetches and deletes the parent
 *   recurring template, which stops future instances from being generated.
 */
interface DeleteTransactionUseCase {
    /**
     * Deletes [transaction] according to [deleteTransactionType].
     *
     * @param transaction          The transaction to remove.
     * @param deleteTransactionType Whether to remove just this occurrence or also the
     *   parent template (and thereby all future instances).
     * @return [Result.success] `true` on completion. Never returns failure in the current
     *   implementation; errors propagate as exceptions.
     */
    suspend operator fun invoke(
        transaction: Transaction,
        deleteTransactionType: DeleteTransactionType = DeleteTransactionType.ThisOccurrenceOnly,
    ): Result<Boolean>
}

/**
 * Controls the scope of a [DeleteTransactionUseCase] invocation.
 */
enum class DeleteTransactionType {
    /** Remove only the selected transaction instance. */
    ThisOccurrenceOnly,

    /** Remove the selected instance **and** its recurring parent template. */
    AllOccurrences,
}

/**
 * Default implementation of [DeleteTransactionUseCase].
 *
 * @property financialRepository The [FinancialRepository] used to perform the deletions.
 */
class DeleteTransactionUseCaseImpl(private val financialRepository: FinancialRepository) :
    DeleteTransactionUseCase {
    override suspend fun invoke(
        transaction: Transaction,
        deleteTransactionType: DeleteTransactionType,
    ): Result<Boolean> {
        financialRepository.deleteTransaction(transaction)

        if (deleteTransactionType == DeleteTransactionType.AllOccurrences) {
            deleteParentTransaction(transaction)
        }

        return Result.success(true)
    }

    private suspend fun deleteParentTransaction(transaction: Transaction) {
        financialRepository.get(uid = transaction.parent).map { parent ->
            financialRepository.deleteTransaction(parent)
        }.first()
    }
}
