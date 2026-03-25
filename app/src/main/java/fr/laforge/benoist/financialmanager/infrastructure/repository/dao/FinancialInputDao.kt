package fr.laforge.benoist.financialmanager.infrastructure.repository.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import fr.laforge.benoist.financialmanager.infrastructure.repository.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface FinancialInputDao {
    @Insert
    fun insertAll(financialInputs: List<TransactionEntity>): List<Long>

    @Query("SELECT * FROM transactionentity WHERE (date_time >= :startDate AND date_time <= :endDate) AND is_periodic = false ORDER BY date_time DESC")
    fun getAllInDateRange(startDate:LocalDateTime, endDate: LocalDateTime): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactionentity WHERE (date_time >= :startDate AND date_time <= :endDate) AND is_periodic = false AND description LIKE :description || '%' ORDER BY date_time DESC")
    fun getAllInDateRangeByDescription(
        startDate:LocalDateTime,
        endDate: LocalDateTime,
        description: String
    ): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactionentity WHERE (is_periodic = true AND type=:type) ORDER BY date_time DESC")
    fun getAllPeriodicByType(type: TransactionType = TransactionType.Expense): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactionentity WHERE is_periodic = true ORDER BY date_time DESC")
    fun getAllPeriodic(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactionentity WHERE uid=:uid")
    fun getById(uid: Int): Flow<TransactionEntity>

    @Query("SELECT * FROM transactionentity WHERE type=:inputType")
    fun getByInputType(inputType: TransactionType): Flow<List<TransactionEntity>>

    @Delete
    fun delete(financialInputs: List<TransactionEntity>)

    @Query("SELECT * FROM transactionentity WHERE (date_time >= :startDate AND date_time <= :endDate) AND is_periodic = false AND parent=:parentId ORDER BY date_time DESC")
    fun getChildrenTransactions(parentId: Int, startDate:LocalDateTime, endDate: LocalDateTime): List<TransactionEntity>

    @Update
    fun update(transactions: List<TransactionEntity>)

    @Query("""
        SELECT COALESCE(SUM(
            CASE 
                WHEN type = 'Income' THEN amount 
                WHEN type = 'Expense' THEN -amount 
                ELSE 0 
            END
        ), 0.0) 
        FROM TransactionEntity 
        WHERE date_time < :date 
        AND is_periodic = 0 
    """)
    fun getBalanceBefore(date: LocalDateTime): Flow<Float>

    @Query("""
        SELECT *
        FROM TransactionEntity 
        WHERE date_time < :date 
        AND is_periodic = 0 
    """)
    fun getTransactionsBefore(date: LocalDateTime): Flow<TransactionEntity>

    @Query("""
    SELECT * FROM transactionEntity
    WHERE (:type IS NULL OR type = :type)
    AND (:startDate IS NULL OR date_time >= :startDate)
    AND (:endDate IS NULL OR date_time <= :endDate)
    AND (:search IS NULL OR description LIKE '%' || :search || '%')
    AND (:isPeriodic IS NULL OR is_periodic = :isPeriodic)
    AND (:parentId IS NULL OR parent = :parentId)
    """)
    fun getTransactions(
        type: String?,
        startDate: LocalDateTime?,
        endDate: LocalDateTime?,
        search: String?,
        isPeriodic: Boolean?,
        parentId: Int?,
    ): Flow<List<TransactionEntity>>
}
