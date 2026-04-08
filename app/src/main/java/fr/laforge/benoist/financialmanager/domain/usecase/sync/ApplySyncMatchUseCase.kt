package fr.laforge.benoist.financialmanager.domain.usecase.sync

import fr.laforge.benoist.financialmanager.domain.model.sync.SyncStatus
import fr.laforge.benoist.financialmanager.domain.model.sync.TransactionMatchResult
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository

/**
 * Confirms a match between an app transaction and a bank transaction, updating the
 * app transaction's [SyncStatus] to [SyncStatus.IN_SYNC].
 *
 * This is the only way a transaction transitions from [SyncStatus.PENDING] to
 * [SyncStatus.IN_SYNC] — it always requires a [TransactionMatchResult] produced by a
 * matcher, ensuring the change is grounded in an actual bank statement entry.
 *
 * @property financialRepository Repository used to persist the updated transaction.
 */
class ApplySyncMatchUseCase(private val financialRepository: FinancialRepository) {

    /**
     * Applies [matchResult] by updating the matched app transaction's sync status to
     * [SyncStatus.IN_SYNC].
     *
     * @param matchResult The confirmed pairing produced by a [StandardTransactionMatcher]
     *   or [RecurringTransactionMatcher].
     */
    operator fun invoke(matchResult: TransactionMatchResult) {
        val updated = matchResult.appTransaction.copy(syncStatus = SyncStatus.IN_SYNC)
        financialRepository.updateTransaction(updated)
    }
}
