package fr.laforge.benoist.financialmanager.infrastructure.repository.converters

import androidx.room.TypeConverter
import fr.laforge.benoist.financialmanager.domain.model.sync.SyncStatus

/**
 * Room [TypeConverter] for persisting [SyncStatus] enum values as TEXT.
 *
 * Storing the enum [name] (e.g. `"PENDING"`) rather than the ordinal makes the schema
 * resilient to reordering of enum constants — ordinal-based storage breaks silently if
 * enum members are reordered or inserted between existing ones.
 */
class SyncStatusConverter {

    /**
     * Converts a database TEXT value to a [SyncStatus] enum constant.
     *
     * @param value The raw string stored in the database column.
     * @return The matching [SyncStatus], or [SyncStatus.PENDING] as a safe fallback if
     *   [value] does not correspond to a known constant.
     *
     * @note The fallback to [SyncStatus.PENDING] protects against rows written by a future
     *   schema version that contains a status the current build doesn't know about yet.
     */
    @TypeConverter
    fun toSyncStatus(value: String): SyncStatus =
        runCatching { SyncStatus.valueOf(value) }.getOrDefault(SyncStatus.PENDING)

    /**
     * Converts a [SyncStatus] enum constant to its TEXT representation for storage.
     *
     * @param status The status to persist.
     * @return The [name] of [status] (e.g. `"PENDING"`, `"IN_SYNC"`, `"NEW_FROM_BANK"`).
     */
    @TypeConverter
    fun fromSyncStatus(status: SyncStatus): String = status.name
}
