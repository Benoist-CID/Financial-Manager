package fr.laforge.benoist.financialmanager.usecase

import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.model.TransactionType
import org.amshove.kluent.`should be equal to`
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar

class CreateTransactionUseCaseTest {
    @Test
    fun `Tests CreateTransactionUseCase with a non periodic command`() {
        val testDate = LocalDateTime.now()
        val transactions = CreateTransactionUseCaseImpl().execute(
            date = testDate,
            transactionType = TransactionType.Expense,
            amount = 150F,
            description = "A simple non-periodic transaction"
        )

        transactions.`should be equal to`(
            listOf(
                Transaction(
                    dateTime = testDate,
                    type = TransactionType.Expense,
                    amount = 150F,
                    description = "A simple non-periodic transaction"
                )
            )
        )
    }

    @Test
    fun `Tests CreateTransactionUseCase with a periodic command`() {
        val testDate = LocalDateTime.parse("2024-01-01T00:00:00")
        val transactions = CreateTransactionUseCaseImpl().execute(
            date = testDate,
            transactionType = TransactionType.Expense,
            amount = 150F,
            description = "A periodic transaction",
            isPeriodic = true,
            startDate = testDate,
            endDate = testDate.plusMonths(11)
        )

        transactions.size.`should be equal to`(12)

        transactions.`should be equal to`(
            listOf(
                Transaction(
                    dateTime = testDate.plusMonths(11),
                    type = TransactionType.Expense,
                    amount = 150F,
                    description = "A periodic transaction"
                ),
                Transaction(
                    dateTime = testDate.plusMonths(10),
                    type = TransactionType.Expense,
                    amount = 150F,
                    description = "A periodic transaction"
                ),Transaction(
                    dateTime = testDate.plusMonths(9),
                    type = TransactionType.Expense,
                    amount = 150F,
                    description = "A periodic transaction"
                ), Transaction(
                    dateTime = testDate.plusMonths(8),
                    type = TransactionType.Expense,
                    amount = 150F,
                    description = "A periodic transaction"
                ),
                Transaction(
                    dateTime = testDate.plusMonths(7),
                    type = TransactionType.Expense,
                    amount = 150F,
                    description = "A periodic transaction"
                ),
                Transaction(
                    dateTime = testDate.plusMonths(6),
                    type = TransactionType.Expense,
                    amount = 150F,
                    description = "A periodic transaction"
                ),
                Transaction(
                    dateTime = testDate.plusMonths(5),
                    type = TransactionType.Expense,
                    amount = 150F,
                    description = "A periodic transaction"
                ),
                Transaction(
                    dateTime = testDate.plusMonths(4),
                    type = TransactionType.Expense,
                    amount = 150F,
                    description = "A periodic transaction"
                ),
                Transaction(
                    dateTime = testDate.plusMonths(3),
                    type = TransactionType.Expense,
                    amount = 150F,
                    description = "A periodic transaction"
                ),
                Transaction(
                    dateTime = testDate.plusMonths(2),
                    type = TransactionType.Expense,
                    amount = 150F,
                    description = "A periodic transaction"
                ),
                Transaction(
                    dateTime = testDate.plusMonths(1),
                    type = TransactionType.Expense,
                    amount = 150F,
                    description = "A periodic transaction"
                ),
                Transaction(
                    dateTime = testDate,
                    type = TransactionType.Expense,
                    amount = 150F,
                    description = "A periodic transaction"
                )
            )
        )
    }
}