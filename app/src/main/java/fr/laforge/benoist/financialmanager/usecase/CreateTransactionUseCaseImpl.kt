package fr.laforge.benoist.financialmanager.usecase

import fr.laforge.benoist.financialmanager.util.next
import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.model.TransactionPeriod
import fr.laforge.benoist.model.TransactionType
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

class CreateTransactionUseCaseImpl: CreateTransactionUseCase {
    override fun execute(
        date: LocalDateTime,
        transactionType: TransactionType,
        amount: Float,
        description: String,
        isPeriodic: Boolean,
        period: TransactionPeriod,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction> {
        val transactions = mutableListOf<Transaction>()

        transactions.add(
            Transaction(
                dateTime = date,
                amount = amount,
                description = description,
                type = transactionType
            )
        )

        return transactions
    }
}
