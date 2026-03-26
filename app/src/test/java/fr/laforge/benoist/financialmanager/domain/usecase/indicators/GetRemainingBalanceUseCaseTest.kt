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
import java.time.LocalDateTime

class GetRemainingBalanceUseCaseTest {

    private val repository = mockk<FinancialRepository>(relaxed = true)
    private val useCase = GetRemainingBalanceUseCase(repository)

    private val fixedDate = LocalDateTime.of(2026, 3, 15, 12, 0, 0)

    @Test
    fun `invoke returns income minus expenses for real transactions`() = runTest {
        // --- Arrange ---
        val salary = Transaction(uid = 1, amount = 2000f, type = TransactionType.Income, isPeriodic = false)
        val rent = Transaction(uid = 2, amount = 800f, type = TransactionType.Expense, isPeriodic = false)
        val groceries = Transaction(uid = 3, amount = 200f, type = TransactionType.Expense, isPeriodic = false)

        every { repository.getAllInDateRange(any(), any()) } returns flowOf(
            listOf(salary, rent, groceries)
        )

        // --- Act ---
        val result = useCase(date = fixedDate).first()

        // --- Assert ---
        // 2000 (income) - 800 (rent) - 200 (groceries) = 1000
        result shouldBeEqualTo 1000f
    }

    @Test
    fun `invoke returns zero when no transactions exist for the month`() = runTest {
        // --- Arrange ---
        every { repository.getAllInDateRange(any(), any()) } returns flowOf(emptyList())

        // --- Act ---
        val result = useCase(date = fixedDate).first()

        // --- Assert ---
        result shouldBeEqualTo 0f
    }

    @Test
    fun `invoke excludes periodic template transactions from the balance`() = runTest {
        // --- Arrange ---
        val realIncome = Transaction(uid = 1, amount = 1000f, type = TransactionType.Income, isPeriodic = false)
        // A periodic template — should be filtered out and not affect the balance
        val periodicTemplate = Transaction(uid = 2, amount = 500f, type = TransactionType.Income, isPeriodic = true)

        every { repository.getAllInDateRange(any(), any()) } returns flowOf(
            listOf(realIncome, periodicTemplate)
        )

        // --- Act ---
        val result = useCase(date = fixedDate).first()

        // --- Assert ---
        // Only realIncome should count — periodicTemplate is excluded
        result shouldBeEqualTo 1000f
    }
}
