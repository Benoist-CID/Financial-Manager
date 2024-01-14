package fr.laforge.benoist.repository.implementations.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.model.TransactionCategory
import fr.laforge.benoist.model.TransactionPeriod
import fr.laforge.benoist.model.TransactionType
import java.time.LocalDateTime

@Entity
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name="date_time") val dateTime: LocalDateTime = LocalDateTime.now(),
    @ColumnInfo(name="amount") val amount: Float,
    @ColumnInfo(name="type") val type: TransactionType = TransactionType.Expense,
    @ColumnInfo(name="description") val description: String,
    @ColumnInfo(name="is_periodic") val isPeriodic: Boolean,
    @ColumnInfo(name="period") val period: TransactionPeriod,
    @ColumnInfo(name="parent") val parentId: Int = 0,
    @ColumnInfo(name="category", defaultValue = "None") val category: TransactionCategory = TransactionCategory.None
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
            category = category
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
        category = transaction.category
    )
}
