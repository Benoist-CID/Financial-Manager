package fr.laforge.benoist.financialmanager.util

import fr.laforge.benoist.model.Transaction
import fr.laforge.benoist.model.TransactionPeriod
import fr.laforge.benoist.model.TransactionType
import fr.laforge.benoist.util.toLocalDateTime
import org.amshove.kluent.`should be equal to`
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

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
    fun `isChildTransaction returns true when parent is not 0`() {
        // Arrange
        val transaction = Transaction(parent = 1) // Assuming Transaction has a 'parent' property

        // Act
        val result = transaction.isChildTransaction()

        // Assert
        assertTrue(result)
    }

    @Test
    fun `isChildTransaction returns false when parent is 0`() {
        // Arrange
        val transaction = Transaction(parent = 0)

        // Act
        val result = transaction.isChildTransaction()

        // Assert
        assertFalse(result)
    }
}
