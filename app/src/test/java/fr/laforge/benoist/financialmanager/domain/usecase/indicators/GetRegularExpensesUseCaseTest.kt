package fr.laforge.benoist.financialmanager.domain.usecase.indicators

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class GetRecurringExpensesUseCaseTest {

    private val financialRepository = mockk<FinancialRepository>()
    private val useCase = GetRecurringExpensesUseCase(financialRepository)

    @Test
    fun `invoke should return only expenses with a parent`() = runTest {
        // --- Arrange ---
        // 1. A valid recurring expense instance (Generated from parent ID 10)
        val rentInstance = Transaction(
            amount = 800f,
            type = TransactionType.Expense,
            parent = 10, // Has a parent -> Should be counted
            isPeriodic = false
        )

        // 2. A regular variable expense (Manual entry)
        val restaurant = Transaction(
            amount = 50f,
            type = TransactionType.Expense,
            parent = 0, // No parent -> Should be IGNORED
            isPeriodic = false
        )

        // 3. An income (Should be IGNORED)
        val salary = Transaction(
            amount = 2000f,
            type = TransactionType.Income,
            parent = 5
        )

        // Mock repository to return only what matches the filter
        every {
            financialRepository.getTransactions(any())
        } returns flowOf(listOf(rentInstance))

        // --- Act ---
        val result = useCase().first()

        // --- Assert ---
        // Should only sum the rent (800)
        result shouldBeEqualTo 800f
    }

    @Test
    fun `invoke should return 0 when no generated expenses exist`() = runTest {
        // --- Arrange ---
        every {
            financialRepository.getTransactions(any())
        } returns flowOf(emptyList())

        // --- Act ---
        val result = useCase().first()

        // --- Assert ---
        result shouldBeEqualTo 0f
    }
}
