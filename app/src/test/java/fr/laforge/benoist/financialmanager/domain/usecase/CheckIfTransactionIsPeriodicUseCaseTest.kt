package fr.laforge.benoist.financialmanager.domain.usecase

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class CheckIfTransactionIsPeriodicUseCaseTest {

    private val checkIfTransactionIsPeriodicUseCase: CheckIfTransactionIsPeriodicUseCase =
        CheckIfTransactionIsPeriodicUseCaseImpl()

    @Test
    fun `invoke should return true when transaction has parent`() {
        // Arrange
        val transaction = Transaction(uid = 1, parent = 2) // Assuming Transaction has a 'parent' property

        // Act
        val result = checkIfTransactionIsPeriodicUseCase(transaction)

        // Assert
        assertTrue(result)
    }

    @Test
    fun `invoke should return false when transaction has no parent`() {
        // Arrange
        val transaction = Transaction(uid = 1, parent = 0)

        // Act
        val result = checkIfTransactionIsPeriodicUseCase(transaction)

        // Assert
        assertFalse(result)
    }
}
