package fr.laforge.benoist.financialmanager.domain.repository

import fr.laforge.benoist.financialmanager.domain.model.notification.PendingTransaction
import fr.laforge.benoist.financialmanager.domain.model.notification.PendingTransactionStatus
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.util.UUID

/**
 * Domain contract for persisting and querying [PendingTransaction] entries.
 *
 * @note Implementations live in the infrastructure layer and must not leak
 *   Android or Room types through this interface.
 */
interface PendingTransactionRepository {

    /**
     * Persists a new [PendingTransaction].
     *
     * @param pendingTransaction The entry to store.
     */
    suspend fun add(pendingTransaction: PendingTransaction)

    /**
     * Updates an existing [PendingTransaction].
     *
     * @param pendingTransaction The updated entry (matched by [PendingTransaction.id]).
     */
    suspend fun update(pendingTransaction: PendingTransaction)

    /**
     * Returns a [Flow] emitting all [PendingTransaction] entries whose status
     * is [PendingTransactionStatus.PENDING], ordered by [PendingTransaction.detectedAt] descending.
     */
    fun getAllPending(): Flow<List<PendingTransaction>>

    /**
     * Returns all [PendingTransactionStatus.PENDING] entries whose
     * [PendingTransaction.detectedAt] falls within [from]..[to].
     *
     * Used by the deduplication logic to find matching entries within the
     * configured time window.
     *
     * @param amount The exact amount to match.
     * @param from   Start of the time window (inclusive).
     * @param to     End of the time window (inclusive).
     */
    suspend fun findPendingByAmountInWindow(
        amount: Float,
        from: LocalDateTime,
        to: LocalDateTime,
    ): PendingTransaction?

    /**
     * Updates the [PendingTransactionStatus] of the entry identified by [id].
     *
     * @param id     The unique identifier of the entry.
     * @param status The new status to apply.
     */
    suspend fun updateStatus(id: UUID, status: PendingTransactionStatus)
}
