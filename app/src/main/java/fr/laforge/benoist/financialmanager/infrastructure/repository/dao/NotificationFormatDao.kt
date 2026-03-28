package fr.laforge.benoist.financialmanager.infrastructure.repository.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fr.laforge.benoist.financialmanager.infrastructure.repository.entity.NotificationFormatEntity
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for [NotificationFormatEntity] persistence.
 *
 * Mutating functions are intentionally non-suspend to avoid the Room KSP
 * Continuation variance bug present in Room 2.6.0 + KSP 2.3.4.
 */
@Dao
interface NotificationFormatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: NotificationFormatEntity): Long

    @Query("DELETE FROM notification_format WHERE id = :id")
    fun deleteById(id: String): Int

    @Query("SELECT * FROM notification_format ORDER BY rowid ASC")
    fun getAll(): Flow<List<NotificationFormatEntity>>
}
