package fr.laforge.benoist.financialmanager.presentation.ui.transaction.non.recurring

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.usecase.DeleteTransactionUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.transaction.GetNonRecurringExpenseTransactionsUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.transaction.GetNonRecurringIncomeTransactionsUseCase
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
class NonRecurringManagementViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val getNonRecurringExpenseTransactionsUseCase = mockk<GetNonRecurringExpenseTransactionsUseCase>()
    private val getNonRecurringIncomeTransactionsUseCase = mockk<GetNonRecurringIncomeTransactionsUseCase>()
    private val deleteTransactionUseCase = mockk<DeleteTransactionUseCase>(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { getNonRecurringExpenseTransactionsUseCase() } returns flowOf(emptyList())
        every { getNonRecurringIncomeTransactionsUseCase() } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = NonRecurringManagementViewModel(
        getNonRecurringExpenseTransactionsUseCase = getNonRecurringExpenseTransactionsUseCase,
        getNonRecurringIncomeTransactionsUseCase = getNonRecurringIncomeTransactionsUseCase,
        deleteTransactionUseCase = deleteTransactionUseCase,
    )

    // --- updateSearch ---

    @Test
    fun `updateSearch updates query state`() {
        // --- Arrange ---
        val vm = createViewModel()

        // --- Act ---
        vm.updateSearch("Coffee")

        // --- Assert ---
        vm.query.value shouldBeEqualTo "Coffee"
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
        val transaction = Transaction(uid = 3, description = "Coffee")

        // --- Act ---
        vm.deleteTransaction(transaction)
        advanceUntilIdle()

        // --- Assert ---
        coVerify(exactly = 1) { deleteTransactionUseCase(transaction) }
    }
}
