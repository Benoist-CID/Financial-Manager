package fr.laforge.benoist.financialmanager.domain.usecase.indicators

import fr.laforge.benoist.financialmanager.domain.model.Transaction
import fr.laforge.benoist.financialmanager.domain.model.TransactionType
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.`should be equal to`
import org.junit.Test

class GetRecurringExpensesUseCaseTest {
    // 1. Create the Mock
    private val financialRepository = mockk<FinancialRepository>()

    // 2. Instantiate the UseCase with the mock
    private val useCase = GetRecurringExpensesUseCase(financialRepository)

    @Test
    fun `invoke should return correct summed income`() = runTest {
        // --- Arrange ---
        // Create dummy transactions
        val transaction1 = Transaction(amount = 1000f, type = TransactionType.Expense)
        val transaction2 = Transaction(amount = 500f, type = TransactionType.Expense)
        val expectedSum = 1500f

        // Mock the repository behavior to return a Flow of our list
        every {
            financialRepository.getAllPeriodicTransactionsByType(TransactionType.Expense)
        } returns flowOf(listOf(transaction1, transaction2))

        // --- Act ---
        // We use .first() to grab the emitted value from the Flow
        val result = useCase().first()

        // --- Assert ---
        result `should be equal to` expectedSum
    }

    @Test
    fun `invoke should return 0 when no transactions found`() = runTest {
        // --- Arrange ---
        every {
            financialRepository.getAllPeriodicTransactionsByType(TransactionType.Expense)
        } returns flowOf(emptyList())

        // --- Act ---
        val result = useCase().first()

        // --- Assert ---
        result `should be equal to`  0f
    }
}
