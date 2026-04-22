package fr.laforge.benoist.financialmanager.presentation.ui.home.upcoming

import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionCategory
import fr.laforge.benoist.financialmanager.domain.model.transaction.UpcomingExpense
import fr.laforge.benoist.financialmanager.domain.usecase.transaction.GetUpcomingExpensesUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldHaveSize
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class UpcomingExpensesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val useCase = mockk<GetUpcomingExpensesUseCase>(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `upcomingExpenses emits empty list when use case returns no data`() = runTest(testDispatcher) {
        // --- Arrange ---
        every { useCase(any(), any()) } returns flowOf(emptyList())
        val viewModel = UpcomingExpensesViewModel(useCase)

        // --- Act --- WhileSubscribed requires an active collector to start the upstream flow
        val collectJob = launch { viewModel.upcomingExpenses.collect {} }
        advanceUntilIdle()

        // --- Assert ---
        viewModel.upcomingExpenses.value shouldHaveSize 0
        collectJob.cancel()
    }

    @Test
    fun `upcomingExpenses emits list returned by use case`() = runTest(testDispatcher) {
        // --- Arrange ---
        val expenses = listOf(
            UpcomingExpense(uid = 1, date = LocalDateTime.now(), amount = 30f, description = "Netflix", category = TransactionCategory.None, isRecurring = false),
            UpcomingExpense(uid = 2, date = LocalDateTime.now().plusDays(1), amount = 70f, description = "Electricity", category = TransactionCategory.None, isRecurring = true),
        )
        every { useCase(any(), any()) } returns flowOf(expenses)
        val viewModel = UpcomingExpensesViewModel(useCase)

        // --- Act ---
        val collectJob = launch { viewModel.upcomingExpenses.collect {} }
        advanceUntilIdle()

        // --- Assert ---
        viewModel.upcomingExpenses.value shouldHaveSize 2
        viewModel.upcomingExpenses.value[0].description shouldBeEqualTo "Netflix"
        collectJob.cancel()
    }

    @Test
    fun `totalUpcomingAmount emits sum of all upcoming expense amounts`() = runTest(testDispatcher) {
        // --- Arrange ---
        val expenses = listOf(
            UpcomingExpense(uid = 1, date = LocalDateTime.now(), amount = 30f, description = "Netflix", category = TransactionCategory.None, isRecurring = false),
            UpcomingExpense(uid = 2, date = LocalDateTime.now().plusDays(1), amount = 70f, description = "Electricity", category = TransactionCategory.None, isRecurring = true),
        )
        every { useCase(any(), any()) } returns flowOf(expenses)
        val viewModel = UpcomingExpensesViewModel(useCase)

        // --- Act ---
        val collectJob = launch { viewModel.totalUpcomingAmount.collect {} }
        advanceUntilIdle()

        // --- Assert ---
        viewModel.totalUpcomingAmount.value shouldBeEqualTo 100f
        collectJob.cancel()
    }
}
