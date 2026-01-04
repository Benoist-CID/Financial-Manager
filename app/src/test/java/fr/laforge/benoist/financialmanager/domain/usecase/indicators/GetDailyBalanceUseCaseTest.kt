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
import org.amshove.kluent.shouldHaveSize
import org.junit.Test
import java.time.LocalDateTime

class GetDailyBalanceUseCaseTest {
    private val repository = mockk<FinancialRepository>()
    private val getRecurringIncome = mockk<GetRecurringIncomeUseCase>()
    private val getRecurringExpenses = mockk<GetRecurringExpensesUseCase>()
    private val getMonthStartingBalanceUseCase = mockk<GetMonthStartingBalanceUseCase>()


    private val useCase = GetDailyBalanceUseCase(
        repository,
        getRecurringIncome,
        getRecurringExpenses,
        getMonthStartingBalanceUseCase,
    )

    // A fixed date: 15th of Jan 2025
    private val fixedDate = LocalDateTime.of(2025, 1, 15, 12, 0)

    @Test
    fun `invoke should calculate correct daily drop based on regular expenses`() = runTest {
        // --- Arrange ---
        every { getRecurringIncome() } returns flowOf(3000f)
        every { getRecurringExpenses() } returns flowOf(1000f)
        every { getMonthStartingBalanceUseCase(any()) } returns flowOf(-1000f)
        // Starting Disposable = 2000

        // Day 5: Spent 100
        val exp1 = Transaction(
            dateTime = fixedDate.withDayOfMonth(5),
            amount = 100f,
            type = TransactionType.Expense,
            parent = 0,
            isPeriodic = false
        )
        // Day 10: Spent 50
        val exp2 = Transaction(
            dateTime = fixedDate.withDayOfMonth(10),
            amount = 50f,
            type = TransactionType.Expense,
            parent = 0,
            isPeriodic = false
        )

        every { repository.getAllInDateRange(any(), any()) } returns flowOf(listOf(exp1, exp2))

        // --- Act ---
        // Pass the fixed date explicitly
        val points = useCase(date = fixedDate).first()

        // --- Assert ---
        points shouldHaveSize 15 // Only up to the 15th

        // Day 1 (2000)
        points.find { it.dayOfMonth == 1 }?.balance shouldBeEqualTo 1000f

        // Day 5 (2000 - 100 = 1900)
        points.find { it.dayOfMonth == 5 }?.balance shouldBeEqualTo 900f

        // Day 10 (1900 - 50 = 1850)
        points.find { it.dayOfMonth == 10 }?.balance shouldBeEqualTo 850f
    }

    @Test
    fun `invoke should ignore generated recurring bills and income`() = runTest {
        // --- Arrange ---
        every { getRecurringIncome() } returns flowOf(2000f)
        every { getRecurringExpenses() } returns flowOf(500f)
        // Starting = 1500

        val validExpense = Transaction(
            amount = 100f,
            dateTime = fixedDate.withDayOfMonth(5),
            type = TransactionType.Expense,
            parent = 0
        )

        // Should be ignored
        val generatedBill = Transaction(
            amount = 500f,
            dateTime = fixedDate.withDayOfMonth(5),
            type = TransactionType.Expense,
            parent = 99
        )

        every {
            repository.getAllInDateRange(any(), any())
        } returns flowOf(listOf(validExpense, generatedBill))

        // --- Act ---
        val points = useCase(date = fixedDate).first()

        // --- Assert ---
        // 1500 - 100 = 1400. (If bill was counted, it would be 900)
        points.find { it.dayOfMonth == 5 }?.balance shouldBeEqualTo 1400f
    }
}
