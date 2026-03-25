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

class GetAllRecurringTransactionsUseCaseTest {

    private val repository = mockk<FinancialRepository>()
    private val useCase = GetAllRecurringTransactionsUseCase(repository)

    private val expectedFilter = TransactionFilter(isPeriodic = true, parentId = 0)

    @Test
    fun `invoke should return all recurring templates regardless of type`() = runTest {
        // --- Arrange ---
        val recurringExpense = Transaction(
            uid = 1,
            description = "Netflix",
            amount = 15f,
            type = TransactionType.Expense,
            isPeriodic = true,
            parent = 0
        )
        val recurringIncome = Transaction(
            uid = 2,
            description = "Salary",
            amount = 3000f,
            type = TransactionType.Income,
            isPeriodic = true,
            parent = 0
        )

        every { repository.getTransactions(expectedFilter) } returns flowOf(
            listOf(recurringExpense, recurringIncome)
        )

        // --- Act ---
        val result = useCase().first()

        // --- Assert ---
        assertEquals("Should return both recurring templates", 2, result.size)
        assertEquals(recurringExpense, result[0])
        assertEquals(recurringIncome, result[1])
    }

    @Test
    fun `invoke should return empty list when no recurring templates exist`() = runTest {
        // --- Arrange ---
        every { repository.getTransactions(expectedFilter) } returns flowOf(emptyList())

        // --- Act ---
        val result = useCase().first()

        // --- Assert ---
        assertEquals("Should return empty list when no recurring templates exist", 0, result.size)
    }

    @Test
    fun `invoke should delegate to repository with isPeriodic=true and parentId=0 filter`() = runTest {
        // --- Arrange ---
        every { repository.getTransactions(expectedFilter) } returns flowOf(emptyList())

        // --- Act ---
        useCase().first()

        // --- Assert ---
        verify(exactly = 1) {
            repository.getTransactions(TransactionFilter(isPeriodic = true, parentId = 0))
        }
    }
}
