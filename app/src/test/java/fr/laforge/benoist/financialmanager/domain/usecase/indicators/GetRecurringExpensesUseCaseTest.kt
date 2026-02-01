package fr.laforge.benoist.financialmanager.domain.usecase.indicators

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionFilter
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.YearMonth.*

class GetRegularExpensesUseCaseTest {

    private val financialRepository = mockk<FinancialRepository>(relaxed = true)
    private val useCase = GetRegularExpensesUseCase(financialRepository)

    @Test
    fun `invoke should call repository with correct Date Boundaries and Filter`() = runTest {
        // --- Arrange ---
        // 1. Use a fixed date to make the test deterministic (Non-Leap Year)
        val testMonth = of(2026, 2)
        val expectedStart = LocalDateTime.of(2026, 2, 1, 0, 0, 0)
        val expectedEnd = LocalDateTime.of(2026, 2, 28, 23, 59, 59)

        // 2. Capture the filter passed to the repo
        val filterSlot = slot<TransactionFilter>()

        // 3. Mock empty return (we just want to check the filter)
        every {
            financialRepository.getTransactions(capture(filterSlot))
        } returns flowOf(emptyList())

        // --- Act ---
        useCase(currentMonth = testMonth).first()

        // --- Assert ---
        val captured = filterSlot.captured

        // Verify Dates
        captured.startDate shouldBeEqualTo expectedStart
        captured.endDate shouldBeEqualTo expectedEnd

        // Verify Logic Flags (The "Business Logic" of this UseCase)
        captured.type shouldBeEqualTo TransactionType.Expense
        captured.isPeriodic shouldBeEqualTo false
        captured.parentId shouldBeEqualTo 0 // This ensures recurring instances are ignored
    }

    @Test
    fun `invoke should correctly sum the values returned by repository`() = runTest {
        // --- Arrange ---
        // We assume the Repo respects the filter and returns valid items
        val item1 = Transaction(amount = 150f, type = TransactionType.Expense)
        val item2 = Transaction(amount = 50f, type = TransactionType.Expense)

        every {
            financialRepository.getTransactions(any())
        } returns flowOf(listOf(item1, item2))

        // --- Act ---
        val result = useCase(now()).first()

        // --- Assert ---
        result shouldBeEqualTo 200f
    }

    @Test
    fun `invoke should return 0 if repository returns empty list`() = runTest {
        // --- Arrange ---
        every { financialRepository.getTransactions(any()) } returns flowOf(emptyList())

        // --- Act ---
        val result = useCase(now()).first()

        // --- Assert ---
        result shouldBeEqualTo 0f
    }
}
