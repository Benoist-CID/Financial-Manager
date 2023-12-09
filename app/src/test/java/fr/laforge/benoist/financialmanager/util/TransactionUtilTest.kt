package fr.laforge.benoist.financialmanager.util

import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.model.TransactionPeriod
import fr.laforge.benoist.model.TransactionType
import fr.laforge.benoist.util.toLocalDateTime
import org.amshove.kluent.`should be equal to`
import org.junit.Test

class TransactionUtilTest {
    @Test
    fun sumTest() {
        val transactionLists = listOf(
            Transaction(amount = 1.0F, type = TransactionType.Income),
            Transaction(amount = 1.0F, type = TransactionType.Income),
            Transaction(amount = 1.0F, type = TransactionType.Income),
            Transaction(amount = 1.0F, type = TransactionType.Expense),
            Transaction(amount = 1.0F, type = TransactionType.Income),
            Transaction(amount = 1.0F, type = TransactionType.Expense),
            Transaction(amount = 1.0F, type = TransactionType.Income),
            Transaction(amount = 1.0F, type = TransactionType.Income),
            Transaction(amount = 1.0F, type = TransactionType.Expense),
            Transaction(amount = 1.0F, type = TransactionType.Income),
        )

        transactionLists.sum().`should be equal to`(4.0F)
    }

    @Test
    fun `Tests exportToCsv method`() {
        val transaction = Transaction(
            uid = 0,
            dateTime = toLocalDateTime(1702118940000),
            amount = 150F,
            description = "A simple transaction",
            type = TransactionType.Income,
            isPeriodic = true,
            period = TransactionPeriod.Monthly
        )

        transaction.exportToCsvFormat().`should be equal to`("0;1702118940000;150.0;A simple transaction;Income;true;Monthly")
    }

    @Test
    fun `Tests transactionFromCsv function`() {
        val transaction = transactionFromCsv("0;1702118940000;150.0;A simple transaction;Income;true;Monthly")

        transaction.`should be equal to`(Transaction(
            uid = 0,
            dateTime = toLocalDateTime(1702118940000),
            amount = 150F,
            description = "A simple transaction",
            type = TransactionType.Income,
            isPeriodic = true,
            period = TransactionPeriod.Monthly
        ))
    }
}