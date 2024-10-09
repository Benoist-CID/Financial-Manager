package fr.laforge.benoist.financialmanager.domain.usecases

import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.repository.FinancialRepository
import kotlinx.coroutines.flow.firstOrNull

interface UpdateTransactionUseCase {
    /**
     * Updates a transaction in the repository.
     *
     * @param transaction The transaction to update.
     * @param shouldUpdateParent Whether to update the parent transaction if it exists.
     *
     * @return A [Result] indicating the success or failure of the update.
     */
    suspend operator fun invoke(transaction: Transaction, shouldUpdateParent: Boolean = false): Result<Boolean>
}

class UpdateTransactionUseCaseImpl(private val financialRepository: FinancialRepository) : UpdateTransactionUseCase {

    override suspend fun invoke(transaction: Transaction, shouldUpdateParent: Boolean): Result<Boolean> {
        return try {
            financialRepository.updateTransaction(transaction)

            if (shouldUpdateParent && transaction.parent != 0) {
                updateParentTransaction(transaction = transaction)
            }

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Updates the parent transaction if it exists.
     *
     * @param transaction The transaction to update.
     */
    private suspend fun updateParentTransaction(transaction: Transaction) {
        financialRepository.get(uid = transaction.parent).firstOrNull()?.let { parentTransaction ->
            parentTransaction.type = transaction.type
            parentTransaction.category = transaction.category
            parentTransaction.amount = transaction.amount
            parentTransaction.description = transaction.description
            financialRepository.updateTransaction(parentTransaction)
        }
    }
}
