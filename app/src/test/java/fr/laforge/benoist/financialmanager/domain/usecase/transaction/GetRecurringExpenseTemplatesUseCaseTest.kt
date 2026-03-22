package fr.laforge.benoist.financialmanager.domain.usecase.transaction

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetRecurringExpenseTemplatesUseCaseTest {

    private val repository = mockk<FinancialRepository>()
    private val useCase = GetRecurringExpenseTemplatesUseCase(repository)

    @Test
    fun `invoke should filter OUT income and non-periodic expenses`() = runTest {
        // --- Arrange ---
        // 1. Valid: Periodic Expense (Should be kept)
        val validExpense = Transaction(
            uid = 1,
            description = "Netflix",
            amount = 15f,
            type = TransactionType.Expense,
            isPeriodic = true
        )

        // 2. Invalid: One-shot Expense (Should be removed)
        val oneShotExpense = Transaction(
            uid = 2,
            description = "Burger King",
            amount = 20f,
            type = TransactionType.Expense,
            isPeriodic = false
        )

        // 3. Invalid: Periodic Income (Should be removed)
        val salary = Transaction(
            uid = 3,
            description = "Salary",
            amount = 2000f,
            type = TransactionType.Income,
            isPeriodic = true
        )

        every { repository.getTransactions(any()) } returns flowOf(
            listOf(validExpense)
        )

        // --- Act ---
        val result = useCase().first()

        // --- Assert ---
        assertEquals("Should return exactly 1 item", 1, result.size)
        assertEquals("Should return the periodic expense", validExpense, result.first())
    }

    @Test
    fun `invoke should sort expenses by amount DESCENDING`() = runTest {
        // --- Arrange ---
        val smallExpense = Transaction(
            description = "Spotify",
            amount = 10f,
            type = TransactionType.Expense,
            isPeriodic = true
        )
        val mediumExpense = Transaction(
            description = "Gym",
            amount = 50f,
            type = TransactionType.Expense,
            isPeriodic = true
        )
        val largeExpense = Transaction(
            description = "Rent",
            amount = 800f,
            type = TransactionType.Expense,
            isPeriodic = true
        )

        // Return them in random order to prove sorting works
        every { repository.getTransactions(any()) } returns flowOf(
            listOf(mediumExpense, smallExpense, largeExpense)
        )

        // --- Act ---
        val result = useCase().first()

        // --- Assert ---
        assertEquals("First item should be the largest (Rent)", largeExpense, result[0])
        assertEquals("Second item should be the medium (Gym)", mediumExpense, result[1])
        assertEquals("Third item should be the smallest (Spotify)", smallExpense, result[2])
    }

    @Test
    fun `invoke should return empty list when no periodic expenses exist`() = runTest {
        // --- Arrange ---
        every { repository.getTransactions(any()) } returns flowOf(emptyList())

        // --- Act ---
        val result = useCase().first()

        // --- Assert ---
        assertEquals(0, result.size)
    }
}
