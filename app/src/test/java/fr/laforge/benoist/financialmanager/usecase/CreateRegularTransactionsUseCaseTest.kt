package fr.laforge.benoist.financialmanager.usecase

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionPeriod
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import fr.laforge.benoist.financialmanager.domain.usecase.CreateRegularTransactionsUseCaseImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.LocalDateTime
import java.time.YearMonth

class CreateRegularTransactionsUseCaseTest {

    private val repository = mockk<FinancialRepository>()
    private val useCase = CreateRegularTransactionsUseCaseImpl(repository)

    @Test
    fun `execute should create child instance for each template within date range`() = runTest {
        // --- Arrange ---
        every { repository.getAllPeriodicTransactions() } returns flowOf(getPeriodicTransactions())
        every { repository.getChildrenTransactions(any(), any(), any()) } returns emptyList()
        every { repository.createTransaction(any()) } returns 1

        // --- Act ---
        useCase.execute(
            startDate = START_DATE_1,
            endDate = END_DATE_1,
            currentDate = CURRENT_DATE_1
        )

        // --- Assert ---
        // Template 1 (day 20): within [start, end], lands on day 20 of the current month
        val expectedDate1 = LocalDateTime.of(
            CURRENT_DATE_1.year, CURRENT_DATE_1.month, TEMPLATE_DAY_1, 0, 0
        )
        // Template 2 (day 27): 27 > 24 (end-date day), so falls back to the previous month
        val expectedDate2 = LocalDateTime.of(
            START_DATE_1.year, START_DATE_1.month, TEMPLATE_DAY_2, 0, 0
        )
        verify(exactly = 1) {
            repository.createTransaction(
                Transaction(
                    dateTime = expectedDate1,
                    amount = 1f,
                    description = "A periodic income",
                    type = TransactionType.Income,
                    isPeriodic = false,
                    period = TransactionPeriod.None,
                    parent = 1
                )
            )
        }
        verify(exactly = 1) {
            repository.createTransaction(
                Transaction(
                    dateTime = expectedDate2,
                    amount = 1f,
                    description = "A periodic income",
                    type = TransactionType.Income,
                    isPeriodic = false,
                    period = TransactionPeriod.None,
                    parent = 2
                )
            )
        }
    }

    @Test
    fun `execute should handle december-to-january transition correctly`() = runTest {
        // --- Arrange ---
        every { repository.getAllPeriodicTransactions() } returns flowOf(getPeriodicTransactions())
        every { repository.getChildrenTransactions(any(), any(), any()) } returns emptyList()
        every { repository.createTransaction(any()) } returns 1

        // --- Act ---
        useCase.execute(
            startDate = START_DATE_2,
            endDate = END_DATE_2,
            currentDate = CURRENT_DATE_2
        )

        // --- Assert ---
        // Template 1 (day 20): within [Dec 24, Jan 24], lands on day 20 of January
        val expectedDate1 = LocalDateTime.of(
            CURRENT_DATE_2.year, CURRENT_DATE_2.month, TEMPLATE_DAY_1, 0, 0
        )
        // Template 2 (day 27): 27 > 24 (Jan 24 end-date day), falls back to December
        val expectedDate2 = LocalDateTime.of(
            START_DATE_2.year, START_DATE_2.month, TEMPLATE_DAY_2, 0, 0
        )
        verify(exactly = 1) {
            repository.createTransaction(
                Transaction(
                    dateTime = expectedDate1,
                    amount = 1f,
                    description = "A periodic income",
                    type = TransactionType.Income,
                    isPeriodic = false,
                    period = TransactionPeriod.None,
                    parent = 1
                )
            )
        }
        verify(exactly = 1) {
            repository.createTransaction(
                Transaction(
                    dateTime = expectedDate2,
                    amount = 1f,
                    description = "A periodic income",
                    type = TransactionType.Income,
                    isPeriodic = false,
                    period = TransactionPeriod.None,
                    parent = 2
                )
            )
        }
    }

    @Test
    fun `execute should skip template when child already exists in range`() = runTest {
        // --- Arrange ---
        val existingChild = Transaction(uid = 99, parent = 1, isPeriodic = false)
        every { repository.getAllPeriodicTransactions() } returns flowOf(
            listOf(getPeriodicTransactions().first())
        )
        every { repository.getChildrenTransactions(1, any(), any()) } returns listOf(existingChild)
        every { repository.createTransaction(any()) } returns 1

        // --- Act ---
        useCase.execute(
            startDate = START_DATE_1,
            endDate = END_DATE_1,
            currentDate = CURRENT_DATE_1
        )

        // --- Assert ---
        verify(exactly = 0) { repository.createTransaction(any()) }
    }

    private fun getPeriodicTransactions() = listOf(
        Transaction(
            uid = 1,
            dateTime = CREATION_DATE,
            amount = 1f,
            description = "A periodic income",
            type = TransactionType.Income,
            isPeriodic = true,
            period = TransactionPeriod.Monthly,
        ),
        Transaction(
            uid = 2,
            dateTime = CREATION_DATE_2,
            amount = 1f,
            description = "A periodic income",
            type = TransactionType.Income,
            isPeriodic = true,
            period = TransactionPeriod.Monthly,
        ),
        Transaction(
            uid = 3,
            dateTime = CREATION_DATE_3,
            amount = 1f,
            description = "A periodic income",
            type = TransactionType.Income,
            isPeriodic = true,
            period = TransactionPeriod.Monthly,
        )
    )

    companion object {
        // Day-of-month values for the three periodic transaction templates.
        // Only the day matters — the use case extracts dayOfMonth from the template's dateTime.
        private const val TEMPLATE_DAY_1 = 20
        private const val TEMPLATE_DAY_2 = 27
        private const val TEMPLATE_DAY_3 = 5

        // Base month for test suite 1 (any month works; month-arithmetic stays relative)
        private val CURRENT_MONTH = YearMonth.now()
        private val PREV_MONTH = CURRENT_MONTH.minusMonths(1)

        // Template creation dates — only the day-of-month is used by the use case.
        val CREATION_DATE: LocalDateTime = PREV_MONTH.minusMonths(3).atDay(TEMPLATE_DAY_1).atStartOfDay()
        val CREATION_DATE_2: LocalDateTime = PREV_MONTH.minusMonths(3).atDay(TEMPLATE_DAY_2).atStartOfDay()
        val CREATION_DATE_3: LocalDateTime = PREV_MONTH.minusMonths(3).atDay(TEMPLATE_DAY_3).atStartOfDay()

        // Test 1 — standard monthly range
        val START_DATE_1: LocalDateTime = PREV_MONTH.atDay(24).atStartOfDay()
        val END_DATE_1: LocalDateTime = CURRENT_MONTH.atDay(24).atStartOfDay()
        val CURRENT_DATE_1: LocalDateTime = CURRENT_MONTH.atDay(10).atStartOfDay()

        // Test 2 — forces the December→January year-boundary code path.
        // CURRENT_DATE_2 is always January of the following year so month-1 == December.
        private val NEXT_JANUARY = YearMonth.of(CURRENT_MONTH.year + 1, 1)
        private val PREV_DECEMBER = YearMonth.of(CURRENT_MONTH.year, 12)

        val START_DATE_2: LocalDateTime = PREV_DECEMBER.atDay(24).atStartOfDay()
        val END_DATE_2: LocalDateTime = NEXT_JANUARY.atDay(24).atStartOfDay()
        val CURRENT_DATE_2: LocalDateTime = NEXT_JANUARY.atDay(10).atStartOfDay()
    }
}
