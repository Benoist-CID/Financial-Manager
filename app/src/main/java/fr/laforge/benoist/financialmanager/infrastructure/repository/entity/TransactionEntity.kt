package fr.laforge.benoist.financialmanager.infrastructure.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import fr.laforge.benoist.financialmanager.domain.model.sync.SyncStatus
import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionCategory
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionPeriod
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import java.time.LocalDateTime

@Entity
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name="date_time") var dateTime: LocalDateTime = LocalDateTime.now(),
    @ColumnInfo(name="amount") var amount: Float,
    @ColumnInfo(name="type") var type: TransactionType = TransactionType.Expense,
    @ColumnInfo(name="description") var description: String,
    @ColumnInfo(name="is_periodic") var isPeriodic: Boolean,
    @ColumnInfo(name="period") var period: TransactionPeriod,
    @ColumnInfo(name="parent") var parentId: Int = 0,
    @ColumnInfo(name="category", defaultValue = "None") var category: TransactionCategory = TransactionCategory.None,
    @ColumnInfo(name="sync_status", defaultValue = "PENDING") var syncStatus: SyncStatus = SyncStatus.PENDING,
) {
    fun toModel(): Transaction {
        return Transaction(
            uid = uid,
            dateTime = dateTime,
            amount = amount,
            type = type,
            description = description,
            isPeriodic = isPeriodic,
            period = period,
            parent = parentId,
            category = category,
            syncStatus = syncStatus,
        )
    }
}

fun fromModel(transaction: Transaction): TransactionEntity {
    return TransactionEntity(
        uid = transaction.uid,
        dateTime = transaction.dateTime,
        amount = transaction.amount,
        type = transaction.type,
        description = transaction.description,
        isPeriodic = transaction.isPeriodic,
        period = transaction.period,
        parentId = transaction.parent,
        category = transaction.category,
        syncStatus = transaction.syncStatus,
    )
}
