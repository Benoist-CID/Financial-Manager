package fr.laforge.benoist.financialmanager.domain.util

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import org.amshove.kluent.`should be equal to`
import org.junit.Test

class TransactionUtilTest {

    @Test
    fun `sum returns net balance for mixed income and expense list`() {
        val transactions = listOf(
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

        transactions.sum().`should be equal to`(4.0F)
    }

    @Test
    fun `sum returns zero for empty list`() {
        emptyList<Transaction>().sum().`should be equal to`(0.0F)
    }

    @Test
    fun `sum returns positive total for all-income list`() {
        val transactions = listOf(
            Transaction(amount = 100F, type = TransactionType.Income),
            Transaction(amount = 200F, type = TransactionType.Income),
            Transaction(amount = 50F, type = TransactionType.Income),
        )

        transactions.sum().`should be equal to`(350F)
    }

    @Test
    fun `sum returns negative total for all-expense list`() {
        val transactions = listOf(
            Transaction(amount = 100F, type = TransactionType.Expense),
            Transaction(amount = 50F, type = TransactionType.Expense),
        )

        transactions.sum().`should be equal to`(-150F)
    }

    @Test
    fun `sum returns zero for zero-amount transactions`() {
        val transactions = listOf(
            Transaction(amount = 0F, type = TransactionType.Income),
            Transaction(amount = 0F, type = TransactionType.Expense),
        )

        transactions.sum().`should be equal to`(0F)
    }
}
