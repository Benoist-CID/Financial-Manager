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
        verify(exactly = 1) {
            repository.createTransaction(
                Transaction(
                    dateTime = LocalDateTime.parse("2023-12-20T00:00:00"),
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
                    dateTime = LocalDateTime.parse("2023-11-27T00:00:00"),
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
        verify(exactly = 1) {
            repository.createTransaction(
                Transaction(
                    dateTime = LocalDateTime.parse("2024-01-20T00:00:00"),
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
                    dateTime = LocalDateTime.parse("2023-12-27T00:00:00"),
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
        val CREATION_DATE: LocalDateTime = LocalDateTime.parse("2022-12-20T00:00:00")
        val CREATION_DATE_2: LocalDateTime = LocalDateTime.parse("2022-11-27T00:00:00")
        val CREATION_DATE_3: LocalDateTime = LocalDateTime.parse("2022-11-05T00:00:00")
        val START_DATE_1: LocalDateTime = LocalDateTime.parse("2023-11-24T00:00:00")
        val END_DATE_1: LocalDateTime = LocalDateTime.parse("2023-12-24T00:00:00")
        val CURRENT_DATE_1: LocalDateTime = LocalDateTime.parse("2023-12-10T00:00:00")
        val START_DATE_2: LocalDateTime = LocalDateTime.parse("2023-12-24T00:00:00")
        val END_DATE_2: LocalDateTime = LocalDateTime.parse("2024-01-24T00:00:00")
        val CURRENT_DATE_2: LocalDateTime = LocalDateTime.parse("2024-01-10T00:00:00")
    }
}
