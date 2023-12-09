package fr.laforge.benoist.financialmanager.util

import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.model.TransactionType
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
}