package fr.laforge.benoist.financialmanager.interactors

import fr.laforge.benoist.financialmanager.domain.usecases.CheckIfTransactionIsPeriodicUseCase
import fr.laforge.benoist.financialmanager.domain.usecases.DeleteTransactionType
import fr.laforge.benoist.financialmanager.domain.usecases.DeleteTransactionUseCase
import fr.laforge.benoist.financialmanager.domain.usecases.UpdateTransactionUseCase
import fr.laforge.benoist.model.Transaction

interface TransactionInteractor {
    /**
     * Checks if a transaction is periodic.
     *
     * @param transaction The transaction to check.
     * @return True if the transaction is periodic, false otherwise.
     */
    fun isPeriodicTransaction(transaction: Transaction): Boolean

    /**
     * Deletes a transaction.
     *
     * @param transaction The transaction to delete.
     * @param deleteTransactionType The type of deletion to perform.
     * @return A [Result] indicating the success or failure of the deletion.
     */
    suspend fun deleteTransaction(
        transaction: Transaction,
        deleteTransactionType: DeleteTransactionType = DeleteTransactionType.ThisOccurrenceOnly,
    ): Result<Boolean>

    /**
     * Updates a transaction.
     *
     * @param transaction The transaction to update.
     * @param shouldUpdateParent Whether to update the parent transaction if it exists.
     * @return A [Result] indicating the success or failure of the deletion.
     */
    suspend fun updateTransaction(
        transaction: Transaction,
        shouldUpdateParent: Boolean = false
    ): Result<Boolean>
}

class TransactionInteractorImpl(
    private val checkIfTransactionIsPeriodicUseCase: CheckIfTransactionIsPeriodicUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val updateTransactionUseCase: UpdateTransactionUseCase,
) :
    TransactionInteractor {
    override fun isPeriodicTransaction(transaction: Transaction) =
        checkIfTransactionIsPeriodicUseCase(transaction = transaction)

    override suspend fun deleteTransaction(
        transaction: Transaction,
        deleteTransactionType: DeleteTransactionType,
    ): Result<Boolean> =
        deleteTransactionUseCase(
            transaction = transaction,
            deleteTransactionType = deleteTransactionType
        )

    override suspend fun updateTransaction(
        transaction: Transaction,
        shouldUpdateParent: Boolean
    ): Result<Boolean> = updateTransactionUseCase(
        transaction = transaction,
        shouldUpdateParent = shouldUpdateParent
    )
}
