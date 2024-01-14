package fr.laforge.benoist.repository.implementations.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import fr.laforge.benoist.model.TransactionType
import fr.laforge.benoist.repository.implementations.room.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface FinancialInputDao {
    @Insert
    fun insertAll(vararg financialInputs: TransactionEntity): List<Long>

    @Query("SELECT * FROM transactionentity")
    fun getAll(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactionentity WHERE (date_time >= :startDate AND date_time <= :endDate) AND is_periodic = false ORDER BY date_time DESC")
    fun getAllInDateRange(startDate:LocalDateTime, endDate: LocalDateTime): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactionentity WHERE (date_time >= :startDate AND date_time <= :endDate) AND is_periodic = false AND description LIKE :description || '%' ORDER BY date_time DESC")
    fun getAllInDateRangeByDescription(
        startDate:LocalDateTime,
        endDate: LocalDateTime,
        description: String
    ): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactionentity WHERE (is_periodic = true AND type=:type) ORDER BY date_time DESC")
    fun getAllPeriodicByType(type: TransactionType= TransactionType.Expense): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactionentity WHERE is_periodic = true ORDER BY date_time DESC")
    fun getAllPeriodic(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactionentity WHERE uid=:uid")
    fun getById(uid: Int): Flow<TransactionEntity>

    @Query("SELECT * FROM transactionentity WHERE type=:inputType")
    fun getByInputType(inputType: TransactionType): Flow<List<TransactionEntity>>

    @Delete
    fun delete(vararg financialInputs: TransactionEntity)

    @Query("SELECT * FROM transactionentity WHERE (date_time >= :startDate AND date_time <= :endDate) AND is_periodic = false AND parent=:parentId ORDER BY date_time DESC")
    fun getChildrenTransactions(parentId: Int, startDate:LocalDateTime, endDate: LocalDateTime): List<TransactionEntity>
}
