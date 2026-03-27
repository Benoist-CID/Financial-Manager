package fr.laforge.benoist.financialmanager.infrastructure.repository

import fr.laforge.benoist.financialmanager.domain.model.notification.PendingTransaction
import fr.laforge.benoist.financialmanager.domain.model.notification.PendingTransactionStatus
import fr.laforge.benoist.financialmanager.domain.repository.PendingTransactionRepository
import fr.laforge.benoist.financialmanager.domain.util.toMilliseconds
import fr.laforge.benoist.financialmanager.infrastructure.repository.dao.PendingTransactionDao
import fr.laforge.benoist.financialmanager.infrastructure.repository.entity.PendingTransactionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.util.UUID

/**
 * Room-backed implementation of [PendingTransactionRepository].
 *
 * All mapping between domain models and Room entities is performed here,
 * keeping the domain layer free of persistence concerns.
 *
 * @property dao The [PendingTransactionDao] used for all database operations.
 */
class RoomPendingTransactionRepository(
    private val dao: PendingTransactionDao,
) : PendingTransactionRepository {

    override suspend fun add(pendingTransaction: PendingTransaction) {
        dao.insert(PendingTransactionEntity.fromModel(pendingTransaction))
    }

    override suspend fun update(pendingTransaction: PendingTransaction) {
        dao.update(PendingTransactionEntity.fromModel(pendingTransaction))
    }

    override fun getAllPending(): Flow<List<PendingTransaction>> =
        dao.getAllPending().map { entities -> entities.map { it.toModel() } }

    override suspend fun findPendingByAmountInWindow(
        amount: Float,
        from: LocalDateTime,
        to: LocalDateTime,
    ): PendingTransaction? =
        dao.findPendingByAmountInWindow(amount, from.toMilliseconds(), to.toMilliseconds())?.toModel()

    override suspend fun updateStatus(id: UUID, status: PendingTransactionStatus) {
        dao.updateStatus(id.toString(), status.name)
    }
}
