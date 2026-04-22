package fr.laforge.benoist.financialmanager.domain.usecase.transaction

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionCategory
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
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldHaveSize
import org.junit.Test
import java.time.LocalDateTime
import java.time.YearMonth

class GetUpcomingExpensesUseCaseTest {

    private val repository = mockk<FinancialRepository>(relaxed = true)
    private val useCase = GetUpcomingExpensesUseCase(repository)

    private val fixedNow = LocalDateTime.of(2026, 4, 22, 10, 0, 0)
    private val fixedMonth = YearMonth.of(2026, 4)

    @Test
    fun `invoke should query repository with correct date bounds and expense filter`() = runTest {
        // --- Arrange ---
        val filterSlot = slot<TransactionFilter>()
        every { repository.getTransactions(capture(filterSlot)) } returns flowOf(emptyList())

        // --- Act ---
        useCase(now = fixedNow, currentMonth = fixedMonth).first()

        // --- Assert ---
        val captured = filterSlot.captured
        captured.type shouldBeEqualTo TransactionType.Expense
        captured.startDate shouldBeEqualTo fixedNow
        captured.endDate shouldBeEqualTo LocalDateTime.of(2026, 4, 30, 23, 59, 59)
        captured.isPeriodic shouldBeEqualTo false
        captured.parentId.shouldBeNull() // null = skip parent filter
    }

    @Test
    fun `invoke should return empty list when repository returns no transactions`() = runTest {
        // --- Arrange ---
        every { repository.getTransactions(any()) } returns flowOf(emptyList())

        // --- Act ---
        val result = useCase(now = fixedNow, currentMonth = fixedMonth).first()

        // --- Assert ---
        result shouldHaveSize 0
    }

    @Test
    fun `invoke should map transactions to UpcomingExpense sorted by date ascending`() = runTest {
        // --- Arrange ---
        val later = fixedNow.plusDays(5)
        val sooner = fixedNow.plusDays(1)
        val standalone = Transaction(
            uid = 1,
            dateTime = later,
            amount = 50f,
            description = "Netflix",
            category = TransactionCategory.None,
            type = TransactionType.Expense,
            parent = 0,
        )
        val recurringChild = Transaction(
            uid = 2,
            dateTime = sooner,
            amount = 100f,
            description = "Electricity",
            category = TransactionCategory.None,
            type = TransactionType.Expense,
            parent = 3,
        )
        every { repository.getTransactions(any()) } returns flowOf(listOf(standalone, recurringChild))

        // --- Act ---
        val result = useCase(now = fixedNow, currentMonth = fixedMonth).first()

        // --- Assert ---
        result shouldHaveSize 2
        // sooner date comes first
        result[0].description shouldBeEqualTo "Electricity"
        result[0].amount shouldBeEqualTo 100f
        result[0].isRecurring.shouldBeTrue()
        result[1].description shouldBeEqualTo "Netflix"
        result[1].isRecurring.shouldBeFalse()
    }
}
