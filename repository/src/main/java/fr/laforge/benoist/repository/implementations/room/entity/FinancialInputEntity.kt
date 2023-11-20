package fr.laforge.benoist.repository.implementations.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.model.InputType
import java.time.LocalDateTime

@Entity
data class FinancialInputEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name="date_time") val dateTime: LocalDateTime = LocalDateTime.now(),
    @ColumnInfo(name="amount") val amount: Float,
    @ColumnInfo(name="type") val type: InputType = InputType.Expense,
    @ColumnInfo(name="description") val description: String
) {
    fun toModel(): Transaction {
        return Transaction(
            uid = uid,
            dateTime = dateTime,
            amount = amount,
            type = type,
            description = description
        )
    }
}

fun fromModel(transaction: Transaction): FinancialInputEntity {
    return FinancialInputEntity(
        uid = transaction.uid,
        dateTime = transaction.dateTime,
        amount = transaction.amount,
        type = transaction.type,
        description = transaction.description
    )
}
