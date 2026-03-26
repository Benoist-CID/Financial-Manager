package fr.laforge.benoist.financialmanager.domain.usecase.transaction

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
import org.amshove.kluent.shouldBeNull
import org.junit.Test
import java.time.LocalDateTime
import java.time.YearMonth

class GetMonthlyTransactionsUseCaseTest {

    private val repository = mockk<FinancialRepository>(relaxed = true)
    private val useCase = GetMonthlyTransactionsUseCase(repository)

    @Test
    fun `invoke passes correct date boundaries and non-periodic flag to the repository`() = runTest {
        // --- Arrange ---
        val testMonth = YearMonth.of(2026, 2)  // February — non-leap year, ends on 28th
        val expectedStart = LocalDateTime.of(2026, 2, 1, 0, 0, 0)
        val expectedEnd = LocalDateTime.of(2026, 2, 28, 23, 59, 59)

        val filterSlot = slot<TransactionFilter>()
        every { repository.getTransactions(capture(filterSlot)) } returns flowOf(emptyList())

        // --- Act ---
        useCase(month = testMonth).first()

        // --- Assert ---
        val captured = filterSlot.captured
        captured.startDate shouldBeEqualTo expectedStart
        captured.endDate shouldBeEqualTo expectedEnd
        captured.isPeriodic shouldBeEqualTo false   // must not return periodic templates
        captured.parentId.shouldBeNull()             // both manual and child transactions
    }

    @Test
    fun `invoke returns transactions sorted by dateTime descending`() = runTest {
        // --- Arrange ---
        val older = Transaction(uid = 1, dateTime = LocalDateTime.of(2026, 3, 5, 10, 0))
        val newer = Transaction(uid = 2, dateTime = LocalDateTime.of(2026, 3, 20, 10, 0))

        every { repository.getTransactions(any()) } returns flowOf(listOf(older, newer))

        // --- Act ---
        val result = useCase(month = YearMonth.of(2026, 3)).first()

        // --- Assert ---
        result[0].uid shouldBeEqualTo newer.uid
        result[1].uid shouldBeEqualTo older.uid
    }

    @Test
    fun `invoke returns empty list when repository has no matching transactions`() = runTest {
        // --- Arrange ---
        every { repository.getTransactions(any()) } returns flowOf(emptyList())

        // --- Act ---
        val result = useCase(month = YearMonth.of(2026, 1)).first()

        // --- Assert ---
        result shouldBeEqualTo emptyList()
    }

    @Test
    fun `invoke passes filterType to repository when provided`() = runTest {
        // --- Arrange ---
        val filterSlot = slot<TransactionFilter>()
        every { repository.getTransactions(capture(filterSlot)) } returns flowOf(emptyList())

        // --- Act ---
        useCase(month = YearMonth.of(2026, 3), filterType = TransactionType.Expense).first()

        // --- Assert ---
        filterSlot.captured.type shouldBeEqualTo TransactionType.Expense
    }
}
