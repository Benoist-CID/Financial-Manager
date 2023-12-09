package fr.laforge.benoist.model

import org.amshove.kluent.`should be equal to`
import org.junit.Test
import java.time.LocalDateTime


class TransactionTest {
    @Test
    fun `Tests exportToCsv method`() {
        val transaction = Transaction(
            uid = 0,
            dateTime = LocalDateTime.parse("2023-12-06T08:15:00"),
            amount = 150F,
            description = "A simple transaction",
            type = TransactionType.Income,
            isPeriodic = true,
            period = TransactionPeriod.Monthly
        )

        transaction.exportToCsvFormat().`should be equal to`("0;2023-12-06T08:15;150.0;A simple transaction;Income;true;Monthly")
    }

    @Test
    fun `Tests transactionFromCsv function`() {
        val transaction = transactionFromCsv("0;2023-12-06T08:15;150.0;A simple transaction;Income;true;Monthly")

        transaction.`should be equal to`(Transaction(
                uid = 0,
                dateTime = LocalDateTime.parse("2023-12-06T08:15:00"),
                amount = 150F,
                description = "A simple transaction",
                type = TransactionType.Income,
                isPeriodic = true,
                period = TransactionPeriod.Monthly
            ))
    }
}