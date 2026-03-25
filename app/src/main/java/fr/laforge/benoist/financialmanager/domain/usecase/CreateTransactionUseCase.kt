package fr.laforge.benoist.financialmanager.domain.usecase

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction

interface CreateTransactionUseCase {
    /**
     * Persists a new transaction in the repository.
     *
     * @param transaction The transaction to create.
     * @return True if the transaction was successfully created.
     */
    suspend operator fun invoke(transaction: Transaction): Boolean
}
