package fr.laforge.benoist.financialmanager.domain.usecase.transaction

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class GetTransactionByIdUseCaseTest {

    private val repository = mockk<FinancialRepository>()
    private val useCase = GetTransactionByIdUseCase(repository)

    // --- Happy Path ---

    @Test
    fun `invoke should return the transaction matching the given id`() = runTest {
        // --- Arrange ---
        val expected = Transaction(
            uid = 42,
            description = "Grocery shopping",
            amount = 75f,
            type = TransactionType.Expense,
            isPeriodic = false
        )
        every { repository.get(uid = 42) } returns flowOf(expected)

        // --- Act ---
        val result = useCase(42).first()

        // --- Assert ---
        result shouldBeEqualTo expected
    }

    // --- Edge Cases ---

    @Test
    fun `invoke should delegate to repository with the exact id provided`() = runTest {
        // --- Arrange ---
        val transaction = Transaction(uid = 7, description = "Bus pass", amount = 30f)
        every { repository.get(uid = 7) } returns flowOf(transaction)

        // --- Act ---
        useCase(7).first()

        // --- Assert ---
        verify(exactly = 1) { repository.get(uid = 7) }
    }

    @Test
    fun `invoke with id zero should still delegate to repository without short-circuiting`() = runTest {
        // --- Arrange ---
        val transaction = Transaction(uid = 0, description = "Placeholder", amount = 0f)
        every { repository.get(uid = 0) } returns flowOf(transaction)

        // --- Act ---
        val result = useCase(0).first()

        // --- Assert ---
        result shouldBeEqualTo transaction
        verify(exactly = 1) { repository.get(uid = 0) }
    }
}
