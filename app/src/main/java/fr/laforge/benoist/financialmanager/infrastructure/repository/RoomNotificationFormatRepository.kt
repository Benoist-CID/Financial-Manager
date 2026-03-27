package fr.laforge.benoist.financialmanager.infrastructure.repository

import fr.laforge.benoist.financialmanager.domain.model.notification.NotificationFormat
import fr.laforge.benoist.financialmanager.domain.repository.NotificationFormatRepository
import fr.laforge.benoist.financialmanager.infrastructure.repository.dao.NotificationFormatDao
import fr.laforge.benoist.financialmanager.infrastructure.repository.entity.NotificationFormatEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

/**
 * Room-backed implementation of [NotificationFormatRepository].
 *
 * @property dao The [NotificationFormatDao] used for all database operations.
 */
class RoomNotificationFormatRepository(
    private val dao: NotificationFormatDao,
) : NotificationFormatRepository {

    override suspend fun add(format: NotificationFormat) {
        dao.insert(NotificationFormatEntity.fromModel(format))
    }

    override suspend fun delete(id: UUID) {
        dao.deleteById(id.toString())
    }

    override fun getAll(): Flow<List<NotificationFormat>> =
        dao.getAll().map { entities -> entities.map { it.toModel() } }
}
