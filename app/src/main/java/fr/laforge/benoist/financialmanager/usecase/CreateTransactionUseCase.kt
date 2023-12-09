package fr.laforge.benoist.financialmanager.usecase

import fr.laforge.benoist.financialmanager.util.next
import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.model.TransactionPeriod
import fr.laforge.benoist.model.TransactionType
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date

interface CreateTransactionUseCase {
    fun execute(
        date: LocalDateTime = LocalDateTime.now(),
        transactionType: TransactionType = TransactionType.Expense,
        amount: Float = 0F,
        description: String = "",
        isPeriodic: Boolean = false,
        period: TransactionPeriod = TransactionPeriod.Monthly,
        startDate: LocalDateTime = LocalDateTime.now(),
        endDate: LocalDateTime = LocalDateTime.now().next(TransactionPeriod.Monthly)
    ): List<Transaction>
}
