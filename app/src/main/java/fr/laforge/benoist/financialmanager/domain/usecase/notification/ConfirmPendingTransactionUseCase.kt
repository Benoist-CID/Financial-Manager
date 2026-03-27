package fr.laforge.benoist.financialmanager.domain.usecase.notification

import fr.laforge.benoist.financialmanager.domain.model.notification.PendingTransaction
import fr.laforge.benoist.financialmanager.domain.model.notification.PendingTransactionStatus
import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import fr.laforge.benoist.financialmanager.domain.repository.PendingTransactionRepository
import fr.laforge.benoist.financialmanager.domain.usecase.CreateTransactionUseCase

/**
 * Confirms a [PendingTransaction], promoting it to the main transaction history.
 *
 * Steps:
 * 1. Mark the pending entry as [PendingTransactionStatus.CONFIRMED].
 * 2. Delegate creation of the real [Transaction] to [CreateTransactionUseCase].
 *
 * @property pendingRepository Repository for updating the pending entry status.
 * @property createTransactionUseCase Use case for persisting the confirmed transaction.
 */
class ConfirmPendingTransactionUseCase(
    private val pendingRepository: PendingTransactionRepository,
    private val createTransactionUseCase: CreateTransactionUseCase,
) {
    /**
     * Confirms [pendingTransaction] and creates the corresponding [Transaction].
     *
     * @param pendingTransaction The pending entry to confirm.
     */
    suspend operator fun invoke(pendingTransaction: PendingTransaction) {
        pendingRepository.updateStatus(
            id = pendingTransaction.id,
            status = PendingTransactionStatus.CONFIRMED,
        )
        createTransactionUseCase(
            Transaction(
                amount = pendingTransaction.amount,
                description = pendingTransaction.description,
                type = TransactionType.Expense,
            )
        )
    }
}
