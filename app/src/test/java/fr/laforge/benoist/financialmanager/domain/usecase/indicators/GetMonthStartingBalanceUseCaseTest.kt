package fr.laforge.benoist.financialmanager.domain.usecase.indicators

import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test
import java.time.LocalDateTime

class GetMonthStartingBalanceUseCaseTest {

    private val repository = mockk<FinancialRepository>(relaxed = true)
    private val useCase = GetMonthStartingBalanceUseCase(repository)

    @Test
    fun `invoke calls getBalanceBeforeDate with the first instant of the given month`() = runTest {
        // --- Arrange ---
        val input = LocalDateTime.of(2026, 3, 15, 14, 30, 0)
        val expectedStartOfMonth = LocalDateTime.of(2026, 3, 1, 0, 0, 0)

        val dateSlot = slot<LocalDateTime>()
        every { repository.getBalanceBeforeDate(capture(dateSlot)) } returns flowOf(500f)

        // --- Act ---
        useCase(month = input).first()

        // --- Assert ---
        dateSlot.captured shouldBeEqualTo expectedStartOfMonth
    }

    @Test
    fun `invoke returns the balance emitted by the repository`() = runTest {
        // --- Arrange ---
        val expectedBalance = 1234.56f
        every { repository.getBalanceBeforeDate(any()) } returns flowOf(expectedBalance)

        // --- Act ---
        val result = useCase(month = LocalDateTime.of(2026, 1, 1, 0, 0, 0)).first()

        // --- Assert ---
        result shouldBeEqualTo expectedBalance
    }

    @Test
    fun `invoke on the last day of month still computes start-of-month correctly`() = runTest {
        // --- Arrange ---
        val lastDayOfMarch = LocalDateTime.of(2026, 3, 31, 23, 59, 59)
        val expectedStartOfMonth = LocalDateTime.of(2026, 3, 1, 0, 0, 0)

        val dateSlot = slot<LocalDateTime>()
        every { repository.getBalanceBeforeDate(capture(dateSlot)) } returns flowOf(0f)

        // --- Act ---
        useCase(month = lastDayOfMarch).first()

        // --- Assert ---
        dateSlot.captured shouldBeEqualTo expectedStartOfMonth
    }
}
