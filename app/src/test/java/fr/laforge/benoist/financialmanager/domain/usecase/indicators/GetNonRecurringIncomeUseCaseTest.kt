package fr.laforge.benoist.financialmanager.domain.usecase.indicators

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
import java.time.LocalDateTime

class GetNonRecurringIncomeUseCaseTest {

    private val repository = mockk<FinancialRepository>()
    private val useCase = GetNonRecurringIncomeUseCase(repository)

    // Fixed date for testing: 15th May 2025
    private val fixedDate = LocalDateTime.of(2025, 5, 15, 12, 0, 0)

    @Test
    fun `invoke should sum ONLY manual non-periodic income`() = runTest {
        // --- Arrange ---
        // 1. Valid Income (Should be counted)
        val gift = Transaction(
            uid = 1,
            description = "Birthday Gift",
            amount = 100f,
            type = TransactionType.Income,
            dateTime = fixedDate,
            isPeriodic = false,
            parent = 0
        )

        val soldItem = Transaction(
            uid = 2,
            description = "Vinted Sale",
            amount = 50f,
            type = TransactionType.Income,
            dateTime = fixedDate,
            isPeriodic = false,
            parent = 0
        )

        // 2. Invalid: Expense (Should be ignored)
        val expense = Transaction(
            uid = 3,
            description = "Groceries",
            amount = 200f,
            type = TransactionType.Expense,
            dateTime = fixedDate,
            isPeriodic = false,
            parent = 0
        )

        // 3. Invalid: Recurring Income (Salary) (Should be ignored)
        val salaryTemplate = Transaction(
            uid = 4,
            description = "Salary Template",
            amount = 2000f,
            type = TransactionType.Income,
            dateTime = fixedDate,
            isPeriodic = true, // Periodic = True -> Ignore
            parent = 0
        )

        // 4. Invalid: Generated/Child Transaction (Should be ignored)
        // (Even if isPeriodic is false, if it has a parent, it's not a manual "extra")
        val salaryGenerated = Transaction(
            uid = 5,
            description = "Salary May",
            amount = 2000f,
            type = TransactionType.Income,
            dateTime = fixedDate,
            isPeriodic = false,
            parent = 99 // Has parent -> Ignore
        )

        every {
            repository.getAllInDateRange(any(), any())
        } returns flowOf(listOf(gift, soldItem, expense, salaryTemplate, salaryGenerated))

        // --- Act ---
        val result = useCase(date = fixedDate).first()

        // --- Assert ---
        // Expected: 100 (Gift) + 50 (Sale) = 150
        result shouldBeEqualTo 150f

        // Optional: Verify the repository was called with the correct date range (1st to 1st)
        verify {
            repository.getAllInDateRange(
                startDate = LocalDateTime.of(2025, 5, 1, 0, 0),
                endDate = LocalDateTime.of(2025, 6, 1, 0, 0)
            )
        }
    }

    @Test
    fun `invoke should return 0 when no non-recurring income exists`() = runTest {
        // --- Arrange ---
        // Only expenses and recurring income
        val expense = Transaction(amount = 50f, type = TransactionType.Expense)
        val salary = Transaction(amount = 2000f, type = TransactionType.Income, isPeriodic = true)

        every {
            repository.getAllInDateRange(any(), any())
        } returns flowOf(listOf(expense, salary))

        // --- Act ---
        val result = useCase(date = fixedDate).first()

        // --- Assert ---
        result shouldBeEqualTo 0f
    }
}
