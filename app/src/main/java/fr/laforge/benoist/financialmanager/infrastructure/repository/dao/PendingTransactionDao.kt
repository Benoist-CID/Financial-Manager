package fr.laforge.benoist.financialmanager.infrastructure.repository.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import fr.laforge.benoist.financialmanager.infrastructure.repository.entity.PendingTransactionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for [PendingTransactionEntity] persistence.
 *
 * All mutating functions are intentionally non-suspend to avoid a Room KSP code-generation
 * variance bug (Continuation<T> vs Continuation<? super T>).  Callers in
 * [fr.laforge.benoist.financialmanager.infrastructure.repository.RoomPendingTransactionRepository]
 * run them inside a coroutine dispatcher, so no blocking occurs on the main thread.
 */
@Dao
interface PendingTransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: PendingTransactionEntity): Long

    @Update
    fun update(entity: PendingTransactionEntity): Int

    @Query("SELECT * FROM pending_transaction WHERE status = 'PENDING' ORDER BY detected_at DESC")
    fun getAllPending(): Flow<List<PendingTransactionEntity>>

    /**
     * Finds a PENDING transaction with the given [amount] whose
     * [detectedAt][PendingTransactionEntity.detectedAt] falls within [[fromEpochMs], [toEpochMs]].
     *
     * Parameters are epoch-milliseconds (UTC) to avoid binding [java.time.LocalDateTime] directly
     * in a @Query — TypeConverters apply to entity columns, not query bind parameters, in this
     * version of Room KSP.
     */
    @Query("""
        SELECT * FROM pending_transaction
        WHERE status = 'PENDING'
        AND amount = :amount
        AND detected_at >= :fromEpochMs
        AND detected_at <= :toEpochMs
        LIMIT 1
    """)
    fun findPendingByAmountInWindow(
        amount: Float,
        fromEpochMs: Long,
        toEpochMs: Long,
    ): PendingTransactionEntity?

    @Query("UPDATE pending_transaction SET status = :status WHERE id = :id")
    fun updateStatus(id: String, status: String): Int
}
