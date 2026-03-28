package fr.laforge.benoist.financialmanager.domain.usecase.notification

import fr.laforge.benoist.financialmanager.domain.model.notification.PendingTransaction
import fr.laforge.benoist.financialmanager.domain.model.notification.PendingTransactionStatus
import fr.laforge.benoist.financialmanager.domain.repository.PendingTransactionRepository

/**
 * Dismisses a [PendingTransaction], marking it as a false positive.
 *
 * The entry is retained in the repository with status [PendingTransactionStatus.DISMISSED]
 * for audit purposes but will no longer appear in the pending queue.
 *
 * @property repository Repository for updating the pending entry status.
 */
class DismissPendingTransactionUseCase(
    private val repository: PendingTransactionRepository,
) {
    /**
     * Marks [pendingTransaction] as [PendingTransactionStatus.DISMISSED].
     *
     * @param pendingTransaction The pending entry to dismiss.
     */
    suspend operator fun invoke(pendingTransaction: PendingTransaction) {
        repository.updateStatus(
            id = pendingTransaction.id,
            status = PendingTransactionStatus.DISMISSED,
        )
    }
}
