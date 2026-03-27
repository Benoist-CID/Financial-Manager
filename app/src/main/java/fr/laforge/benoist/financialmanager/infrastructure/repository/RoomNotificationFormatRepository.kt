package fr.laforge.benoist.financialmanager.infrastructure.repository

import fr.laforge.benoist.financialmanager.domain.model.notification.NotificationFormat
import fr.laforge.benoist.financialmanager.domain.repository.NotificationFormatRepository
import fr.laforge.benoist.financialmanager.infrastructure.repository.dao.NotificationFormatDao
import fr.laforge.benoist.financialmanager.infrastructure.repository.entity.NotificationFormatEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * Room-backed implementation of [NotificationFormatRepository].
 *
 * The [NotificationFormatDao] uses non-suspend methods to work around a Room KSP 2.3.4
 * code-generation bug with `suspend` + `Unit`/`Long` return types. All mutating calls are
 * therefore wrapped in `withContext(ioDispatcher)` so they never block the calling thread.
 *
 * @property dao          The [NotificationFormatDao] used for all database operations.
 * @property ioDispatcher Dispatcher for blocking I/O. Defaults to [Dispatchers.IO]; override
 *   in tests to keep them synchronous.
 */
class RoomNotificationFormatRepository(
    private val dao: NotificationFormatDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : NotificationFormatRepository {

    override suspend fun add(format: NotificationFormat) {
        withContext(ioDispatcher) { dao.insert(NotificationFormatEntity.fromModel(format)) }
    }

    override suspend fun delete(id: UUID) {
        withContext(ioDispatcher) { dao.deleteById(id.toString()) }
    }

    override fun getAll(): Flow<List<NotificationFormat>> =
        dao.getAll().map { entities -> entities.map { it.toModel() } }
}
