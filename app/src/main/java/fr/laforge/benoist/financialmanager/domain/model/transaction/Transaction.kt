package fr.laforge.benoist.financialmanager.domain.model.transaction

import java.time.LocalDateTime

data class Transaction(
    val uid: Int = 0,
    val dateTime: LocalDateTime = LocalDateTime.now(),
    val amount: Float = 0F,
    val description: String = "",
    val type: TransactionType = TransactionType.Expense,
    val isPeriodic: Boolean = false,
    val period: TransactionPeriod = TransactionPeriod.None,
    val parent: Int = 0,
    val category: TransactionCategory = TransactionCategory.None
)
