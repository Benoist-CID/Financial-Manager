package fr.laforge.benoist.financialmanager.domain.usecase.transaction

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionFilter
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetAllTransactionsUseCaseTest {

    private val repository = mockk<FinancialRepository>()
    private val useCase = GetAllTransactionsUseCase(repository)

    @Test
    fun `invoke should return all transactions unfiltered`() = runTest {
        // --- Arrange ---
        val expense = Transaction(
            uid = 1,
            description = "Netflix",
            amount = 15f,
            type = TransactionType.Expense,
            isPeriodic = true
        )
        val income = Transaction(
            uid = 2,
            description = "Salary",
            amount = 3000f,
            type = TransactionType.Income,
            isPeriodic = true
        )
        val oneShot = Transaction(
            uid = 3,
            description = "Coffee",
            amount = 3f,
            type = TransactionType.Expense,
            isPeriodic = false
        )

        every { repository.getTransactions(TransactionFilter.all()) } returns flowOf(
            listOf(expense, income, oneShot)
        )

        // --- Act ---
        val result = useCase().first()

        // --- Assert ---
        assertEquals("Should return all 3 transactions without filtering", 3, result.size)
        assertEquals(expense, result[0])
        assertEquals(income, result[1])
        assertEquals(oneShot, result[2])
    }

    @Test
    fun `invoke should return empty list when repository has no transactions`() = runTest {
        // --- Arrange ---
        every { repository.getTransactions(TransactionFilter.all()) } returns flowOf(emptyList())

        // --- Act ---
        val result = useCase().first()

        // --- Assert ---
        assertEquals("Should return empty list when repository is empty", 0, result.size)
    }

    @Test
    fun `invoke should delegate to repository with all filter`() = runTest {
        // --- Arrange ---
        every { repository.getTransactions(TransactionFilter.all()) } returns flowOf(emptyList())

        // --- Act ---
        useCase().first()

        // --- Assert ---
        verify(exactly = 1) { repository.getTransactions(TransactionFilter.all()) }
    }
}
