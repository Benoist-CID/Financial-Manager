package fr.laforge.benoist.financialmanager.interactors

import fr.laforge.benoist.financialmanager.domain.usecases.CheckIfTransactionIsPeriodicUseCase
import fr.laforge.benoist.financialmanager.domain.usecases.DeleteTransactionType
import fr.laforge.benoist.financialmanager.domain.usecases.DeleteTransactionUseCase
import fr.laforge.benoist.model.Transaction

interface DeleteTransactionInteractor {
    /**
     * Checks if a transaction is a Periodic one
     */
    fun isPeriodicTransaction(transaction: Transaction): Boolean

    suspend fun deleteTransaction(
        transaction: Transaction,
        deleteTransactionType: DeleteTransactionType = DeleteTransactionType.ThisOccurrenceOnly,
        ): Result<Boolean>
}

class DeleteTransactionInteractorImpl(
    private val checkIfTransactionIsPeriodicUseCase: CheckIfTransactionIsPeriodicUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
) :
    DeleteTransactionInteractor {
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
}
