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

class GetRegularExpensesUseCaseTest {

    private val financialRepository = mockk<FinancialRepository>(relaxed = true)
    private val useCase = GetRegularExpensesUseCase(financialRepository)

    @Test
    fun `invoke should return only regular expenses (no parent, not periodic)`() = runTest {
        // --- Arrange ---

        // 1. A Valid Regular Expense (Manual entry like Groceries)
        val groceries = Transaction(
            amount = 150f,
            type = TransactionType.Expense,
            isPeriodic = false,
            parent = 0 // Key criteria: No parent
        )

        // 2. A Generated Recurring Bill (Should be IGNORED)
        // e.g., This month's Rent, generated from a parent ID 55
        val rentInstance = Transaction(
            amount = 800f,
            type = TransactionType.Expense,
            isPeriodic = false,
            parent = 55 // Has parent -> Ignore
        )

        // 3. The Periodic Template itself (Should be IGNORED)
        // Even if it appears in the date range (unlikely but possible)
        val rentTemplate = Transaction(
            amount = 800f,
            type = TransactionType.Expense,
            isPeriodic = true, // Is Periodic -> Ignore
            parent = 0
        )

        // 4. An Income (Should be IGNORED)
        val salary = Transaction(
            amount = 2000f,
            type = TransactionType.Income,
            isPeriodic = false,
            parent = 0
        )

        every {
            financialRepository.getTransactions(
                filter = any()
            )
        } returns flowOf(listOf(groceries, rentInstance, rentTemplate, salary))

        // --- Act ---
        val result = useCase().first()

        // --- Assert ---
        // Should only sum 'groceries' (150f)
        result shouldBeEqualTo 150f
    }

    @Test
    fun `invoke should return 0 if only recurring expenses exist`() = runTest {
        // --- Arrange ---
        val rentInstance = Transaction(
            amount = 800f,
            type = TransactionType.Expense,
            parent = 55
        )

        every {
            financialRepository.getTransactions(any())
        } returns flowOf(listOf(rentInstance))

        // --- Act ---
        val result = useCase().first()

        // --- Assert ---
        result shouldBeEqualTo 0f
    }
}
