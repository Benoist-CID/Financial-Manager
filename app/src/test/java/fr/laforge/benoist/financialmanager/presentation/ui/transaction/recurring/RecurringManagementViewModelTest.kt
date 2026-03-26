package fr.laforge.benoist.financialmanager.presentation.ui.transaction.recurring

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.usecase.DeleteTransactionUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.transaction.GetRecurringExpenseTemplatesUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.transaction.GetRecurringIncomeTransactionsUseCase
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.amshove.kluent.shouldBeEqualTo
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecurringManagementViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val getRecurringExpenseTemplatesUseCase = mockk<GetRecurringExpenseTemplatesUseCase>()
    private val getRecurringIncomeTransactionsUseCase = mockk<GetRecurringIncomeTransactionsUseCase>()
    private val deleteTransactionUseCase = mockk<DeleteTransactionUseCase>(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { getRecurringExpenseTemplatesUseCase() } returns flowOf(emptyList())
        every { getRecurringIncomeTransactionsUseCase() } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = RecurringManagementViewModel(
        getRecurringExpenseTemplatesUseCase = getRecurringExpenseTemplatesUseCase,
        getRecurringIncomeTransactionsUseCase = getRecurringIncomeTransactionsUseCase,
        deleteTransactionUseCase = deleteTransactionUseCase,
    )

    // --- updateSearch ---

    @Test
    fun `updateSearch updates query state`() {
        // --- Arrange ---
        val vm = createViewModel()

        // --- Act ---
        vm.updateSearch("Netflix")

        // --- Assert ---
        vm.query.value shouldBeEqualTo "Netflix"
    }

    @Test
    fun `updateSearch with empty string resets query`() {
        // --- Arrange ---
        val vm = createViewModel()
        vm.updateSearch("old")

        // --- Act ---
        vm.updateSearch("")

        // --- Assert ---
        vm.query.value shouldBeEqualTo ""
    }

    // --- deleteTransaction ---

    @Test
    fun `deleteTransaction delegates to DeleteTransactionUseCase`() = runTest(testDispatcher) {
        // --- Arrange ---
        val vm = createViewModel()
        val transaction = Transaction(uid = 5, description = "Netflix")

        // --- Act ---
        vm.deleteTransaction(transaction)
        advanceUntilIdle()

        // --- Assert ---
        coVerify(exactly = 1) { deleteTransactionUseCase(transaction) }
    }
}
