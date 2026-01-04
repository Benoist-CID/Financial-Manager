package fr.laforge.benoist.financialmanager.domain.usecase.transaction

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionFilter
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import fr.laforge.benoist.financialmanager.infrastructure.repository.dao.FinancialInputDao
import fr.laforge.benoist.financialmanager.infrastructure.repository.database.AppDatabase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import java.io.IOException
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class GetNonRecurringExpenseTransactionsUseCaseTest {

    private val repository = mockk<FinancialRepository>()
    private val useCase = GetNonRecurringExpenseTransactionsUseCase(repository)

    // Fixed date for testing: 15th May 2025
    private val fixedDate = LocalDateTime.of(2025, 5, 15, 12, 0, 0)

    @Test
    fun `invoke should fetch data for specific month range`() = runTest {
        // --- Arrange ---
        every { repository.getTransactions(filter = any()) } returns flowOf(emptyList())

        // --- Act ---
        useCase(date = fixedDate).first()

        // --- Assert ---
        verify {
            repository.getTransactions(
                filter = TransactionFilter(
                    type = TransactionType.Expense,
                    startDate = LocalDateTime.of(2025, 5, 1, 0, 0),
                    endDate = LocalDateTime.of(2025, 6, 1, 0, 0),
                    descriptionQuery = "",
                    isPeriodic = false,
                    parentId = 0
                )
            )
        }
    }

    @Test
    fun `invoke should filter ONLY manual non-periodic expenses`() = runTest {
        // --- Arrange ---
        // 1. Valid: Manual Expense (Burger King) -> KEEP
        val burger = Transaction(
            uid = 1,
            description = "Burger King",
            amount = 15f,
            type = TransactionType.Expense,
            isPeriodic = false,
            parent = 0
        )

        // 2. Invalid: Periodic Template (Rent Definition) -> IGNORE
        val rentTemplate = Transaction(
            uid = 2,
            description = "Rent Template",
            amount = 800f,
            type = TransactionType.Expense,
            isPeriodic = true,
            parent = 0
        )

        // 3. Invalid: Generated Recurring Expense (This month's Rent) -> IGNORE
        val rentPayment = Transaction(
            uid = 3,
            description = "Rent January",
            amount = 800f,
            type = TransactionType.Expense,
            isPeriodic = false,
            parent = 2 // Has parent = Generated
        )

        every {
            repository.getTransactions(filter = any())
        } returns flowOf(listOf(burger, rentTemplate, rentPayment))

        // --- Act ---
        val result = useCase(fixedDate).first()

        // --- Assert ---
        assertEquals(1, result.size)
        assertEquals(burger, result.first())
    }

    @Test
    fun `invoke should sort expenses by amount DESCENDING`() = runTest {
        // --- Arrange ---
        val smallCoffee = Transaction(
            description = "Coffee",
            amount = 5f,
            type = TransactionType.Expense,
            isPeriodic = false,
            parent = 0
        )
        val bigShopping = Transaction(
            description = "IKEA",
            amount = 200f,
            type = TransactionType.Expense,
            isPeriodic = false,
            parent = 0
        )

        every {
            repository.getAllInDateRange(any(), any())
        } returns flowOf(listOf(smallCoffee, bigShopping))

        // --- Act ---
        val result = useCase(fixedDate).first()

        // --- Assert ---
        assertEquals(bigShopping, result[0])
        assertEquals(smallCoffee, result[1])
    }
}
