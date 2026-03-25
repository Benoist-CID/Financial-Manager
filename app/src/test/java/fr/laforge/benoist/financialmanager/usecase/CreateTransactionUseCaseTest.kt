package fr.laforge.benoist.financialmanager.usecase

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import fr.laforge.benoist.financialmanager.domain.usecase.CreateTransactionUseCaseImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class CreateTransactionUseCaseTest {

    private val repository = mockk<FinancialRepository>()
    private val useCase = CreateTransactionUseCaseImpl(repository)

    @Test
    fun `execute should persist only the template when transaction is not periodic`() = runTest {
        // --- Arrange ---
        val transaction = Transaction(
            dateTime = LocalDateTime.now(),
            type = TransactionType.Expense,
            amount = 150f,
            description = "A simple non-periodic transaction"
        )
        every { repository.createTransaction(any()) } returns 1

        // --- Act ---
        val result = useCase.execute(transaction).first()

        // --- Assert ---
        assertTrue(result)
        verify(exactly = 1) { repository.createTransaction(transaction) }
        verify(exactly = 0) { repository.createTransaction(transaction.copy(isPeriodic = false, parent = 1)) }
    }

    @Test
    fun `execute should persist template and child instance when transaction is periodic`() = runTest {
        // --- Arrange ---
        val transaction = Transaction(
            dateTime = LocalDateTime.now(),
            type = TransactionType.Expense,
            amount = 150f,
            description = "A simple periodic transaction",
            isPeriodic = true
        )
        every { repository.createTransaction(any()) } returns 1

        // --- Act ---
        val result = useCase.execute(transaction).first()

        // --- Assert ---
        assertTrue(result)
        verify(exactly = 1) { repository.createTransaction(transaction) }
        verify(exactly = 1) { repository.createTransaction(transaction.copy(isPeriodic = false, parent = 1)) }
    }

    @Test
    fun `invoke should persist only the template when transaction is not periodic`() {
        // --- Arrange ---
        val transaction = Transaction(
            dateTime = LocalDateTime.now(),
            type = TransactionType.Expense,
            amount = 50f,
            description = "One-shot expense"
        )
        every { repository.createTransaction(any()) } returns 1

        // --- Act ---
        val result = useCase.invoke(transaction)

        // --- Assert ---
        assertTrue(result)
        verify(exactly = 1) { repository.createTransaction(transaction) }
        verify(exactly = 0) { repository.createTransaction(transaction.copy(isPeriodic = false, parent = 1)) }
    }
}
