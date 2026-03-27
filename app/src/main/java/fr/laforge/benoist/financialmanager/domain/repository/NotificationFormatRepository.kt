package fr.laforge.benoist.financialmanager.domain.repository

import fr.laforge.benoist.financialmanager.domain.model.notification.NotificationFormat
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Persistence port for user-defined [NotificationFormat] entries.
 *
 * Implementations must be infrastructure-layer classes (e.g. Room-backed).
 * The domain never depends on any persistence framework directly.
 */
interface NotificationFormatRepository {

    /**
     * Persists a new [format].
     *
     * @param format The [NotificationFormat] to store.
     */
    suspend fun add(format: NotificationFormat)

    /**
     * Removes the format identified by [id].
     *
     * Silently succeeds if no matching format exists.
     *
     * @param id The [UUID] of the format to delete.
     */
    suspend fun delete(id: UUID)

    /**
     * Returns a [Flow] that emits the full list of stored formats whenever it changes.
     */
    fun getAll(): Flow<List<NotificationFormat>>
}
