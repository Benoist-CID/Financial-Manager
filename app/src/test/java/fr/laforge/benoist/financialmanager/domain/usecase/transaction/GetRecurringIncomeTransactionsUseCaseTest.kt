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

class GetRecurringIncomeTransactionsUseCaseTest {

    private val repository = mockk<FinancialRepository>()
    private val useCase = GetRecurringIncomeTransactionsUseCase(repository)

    @Test
    fun `invoke should filter ONLY recurring income`() = runTest {
        // --- Arrange ---
        // 1. Valid: Periodic Income (Salary) -> KEEP
        val salary = Transaction(
            uid = 1,
            description = "Main Salary",
            amount = 3000f,
            type = TransactionType.Income,
            isPeriodic = true
        )

        // 2. Invalid: Non-Periodic Income (One-shot gift) -> IGNORE
        val gift = Transaction(
            uid = 2,
            description = "Birthday Gift",
            amount = 100f,
            type = TransactionType.Income,
            isPeriodic = false
        )

        // 3. Invalid: Periodic Expense (Rent) -> IGNORE
        val rent = Transaction(
            uid = 3,
            description = "Rent",
            amount = 800f,
            type = TransactionType.Expense,
            isPeriodic = true
        )

        every { repository.getAll() } returns flowOf(
            listOf(salary, gift, rent)
        )

        // --- Act ---
        val result = useCase().first()

        // --- Assert ---
        assertEquals("Should return exactly 1 item", 1, result.size)
        assertEquals("Should return the salary", salary, result.first())
    }

    @Test
    fun `invoke should sort income by amount DESCENDING`() = runTest {
        // --- Arrange ---
        val smallSideHustle = Transaction(
            description = "Etsy Sales",
            amount = 150f,
            type = TransactionType.Income,
            isPeriodic = true
        )
        val bigSalary = Transaction(
            description = "Corporate Salary",
            amount = 3000f,
            type = TransactionType.Income,
            isPeriodic = true
        )
        val mediumRentIncome = Transaction(
            description = "Garage Rental",
            amount = 500f,
            type = TransactionType.Income,
            isPeriodic = true
        )

        // Provide them in random order to test sorting
        every { repository.getAll() } returns flowOf(
            listOf(mediumRentIncome, smallSideHustle, bigSalary)
        )

        // --- Act ---
        val result = useCase().first()

        // --- Assert ---
        assertEquals("First should be Salary (Largest)", bigSalary, result[0])
        assertEquals("Second should be Rental (Medium)", mediumRentIncome, result[1])
        assertEquals("Third should be Etsy (Smallest)", smallSideHustle, result[2])
    }

    @Test
    fun `invoke should return empty list if no periodic income exists`() = runTest {
        // --- Arrange ---
        val expense = Transaction(amount = 50f, type = TransactionType.Expense, isPeriodic = true)

        every { repository.getAll() } returns flowOf(listOf(expense))

        // --- Act ---
        val result = useCase().first()

        // --- Assert ---
        assertEquals(0, result.size)
    }
}
