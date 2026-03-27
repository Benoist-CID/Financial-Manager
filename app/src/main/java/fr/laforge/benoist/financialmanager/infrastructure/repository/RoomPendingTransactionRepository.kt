package fr.laforge.benoist.financialmanager.infrastructure.repository

import fr.laforge.benoist.financialmanager.domain.model.notification.PendingTransaction
import fr.laforge.benoist.financialmanager.domain.model.notification.PendingTransactionStatus
import fr.laforge.benoist.financialmanager.domain.repository.PendingTransactionRepository
import fr.laforge.benoist.financialmanager.domain.util.toMilliseconds
import fr.laforge.benoist.financialmanager.infrastructure.repository.dao.PendingTransactionDao
import fr.laforge.benoist.financialmanager.infrastructure.repository.entity.PendingTransactionEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.UUID

/**
 * Room-backed implementation of [PendingTransactionRepository].
 *
 * All mapping between domain models and Room entities is performed here,
 * keeping the domain layer free of persistence concerns.
 *
 * The [PendingTransactionDao] uses non-suspend methods to work around a Room KSP 2.3.4
 * code-generation bug with `suspend` + `Unit`/`Long` return types. All mutating calls are
 * therefore wrapped in `withContext(ioDispatcher)` so they never block the calling thread.
 *
 * @property dao          The [PendingTransactionDao] used for all database operations.
 * @property ioDispatcher Dispatcher for blocking I/O. Defaults to [Dispatchers.IO]; override
 *   in tests to keep them synchronous.
 */
class RoomPendingTransactionRepository(
    private val dao: PendingTransactionDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : PendingTransactionRepository {

    override suspend fun add(pendingTransaction: PendingTransaction) {
        withContext(ioDispatcher) { dao.insert(PendingTransactionEntity.fromModel(pendingTransaction)) }
    }

    override suspend fun update(pendingTransaction: PendingTransaction) {
        withContext(ioDispatcher) { dao.update(PendingTransactionEntity.fromModel(pendingTransaction)) }
    }

    override fun getAllPending(): Flow<List<PendingTransaction>> =
        dao.getAllPending().map { entities -> entities.map { it.toModel() } }

    override suspend fun findPendingByAmountInWindow(
        amount: Float,
        from: LocalDateTime,
        to: LocalDateTime,
    ): PendingTransaction? = withContext(ioDispatcher) {
        dao.findPendingByAmountInWindow(amount, from.toMilliseconds(), to.toMilliseconds())?.toModel()
    }

    override suspend fun updateStatus(id: UUID, status: PendingTransactionStatus) {
        withContext(ioDispatcher) { dao.updateStatus(id.toString(), status.name) }
    }
}
