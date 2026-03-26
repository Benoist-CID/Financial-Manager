package fr.laforge.benoist.financialmanager.views.home

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import fr.laforge.benoist.financialmanager.domain.usecase.DeleteTransactionType
import fr.laforge.benoist.financialmanager.domain.usecase.TransactionInteractor
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.GetMonthStartingBalanceUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.GetNonRecurringIncomeUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.GetRecurringIncomeUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.transaction.GetMonthlyTransactionsUseCase
import fr.laforge.benoist.financialmanager.presentation.ui.home.HomeScreenViewModel
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
class HomeScreenViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val transactionInteractor = mockk<TransactionInteractor>(relaxed = true)
    private val getMonthStartingBalanceUseCase = mockk<GetMonthStartingBalanceUseCase>()
    private val getNonRecurringIncomeUseCase = mockk<GetNonRecurringIncomeUseCase>()
    private val getRecurringIncomeUseCase = mockk<GetRecurringIncomeUseCase>()
    private val getMonthlyTransactionsUseCase = mockk<GetMonthlyTransactionsUseCase>()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // Provide default no-op flows so that the ViewModel can be constructed without errors
        every { getMonthStartingBalanceUseCase(any()) } returns flowOf(0f)
        every { getNonRecurringIncomeUseCase(any()) } returns flowOf(0f)
        every { getRecurringIncomeUseCase() } returns flowOf(0f)
        every { getMonthlyTransactionsUseCase(any(), any(), any()) } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = HomeScreenViewModel(
        transactionInteractor = transactionInteractor,
        getMonthStartingBalanceUseCase = getMonthStartingBalanceUseCase,
        getNonRecurringIncomeUseCase = getNonRecurringIncomeUseCase,
        getRecurringIncomeUseCase = getRecurringIncomeUseCase,
        getMonthlyTransactionsUseCase = getMonthlyTransactionsUseCase,
    )

    // --- updateSearch ---

    @Test
    fun `updateSearch updates uiState query`() = runTest(testDispatcher) {
        // --- Arrange ---
        val vm = createViewModel()

        // --- Act ---
        vm.updateSearch("coffee")
        advanceUntilIdle()

        // --- Assert ---
        vm.uiState.value.query shouldBeEqualTo "coffee"
    }

    @Test
    fun `updateSearch with empty string resets query`() = runTest(testDispatcher) {
        // --- Arrange ---
        val vm = createViewModel()
        vm.updateSearch("old query")
        advanceUntilIdle()

        // --- Act ---
        vm.updateSearch("")
        advanceUntilIdle()

        // --- Assert ---
        vm.uiState.value.query shouldBeEqualTo ""
    }

    // --- isPeriodicTransaction ---

    @Test
    fun `isPeriodicTransaction delegates to transactionInteractor`() {
        // --- Arrange ---
        val vm = createViewModel()
        val transaction = Transaction(uid = 1, parent = 5)
        every { transactionInteractor.isPeriodicTransaction(transaction) } returns true

        // --- Act ---
        val result = vm.isPeriodicTransaction(transaction)

        // --- Assert ---
        result shouldBeEqualTo true
    }

    @Test
    fun `isPeriodicTransaction returns false for non-periodic transaction`() {
        // --- Arrange ---
        val vm = createViewModel()
        val transaction = Transaction(uid = 1, parent = 0)
        every { transactionInteractor.isPeriodicTransaction(transaction) } returns false

        // --- Act ---
        val result = vm.isPeriodicTransaction(transaction)

        // --- Assert ---
        result shouldBeEqualTo false
    }

    // --- deleteTransaction ---

    @Test
    fun `deleteTransaction with shouldDeleteParent=false uses ThisOccurrenceOnly`() = runTest(testDispatcher) {
        // --- Arrange ---
        val vm = createViewModel()
        val transaction = Transaction(uid = 1)

        // --- Act ---
        vm.deleteTransaction(
            transaction = transaction,
            shouldDeleteParent = false,
            dispatcher = testDispatcher
        )
        advanceUntilIdle()

        // --- Assert ---
        coVerify(exactly = 1) {
            transactionInteractor.deleteTransaction(
                transaction = transaction,
                deleteTransactionType = DeleteTransactionType.ThisOccurrenceOnly
            )
        }
    }

    @Test
    fun `deleteTransaction with shouldDeleteParent=true uses AllOccurrences`() = runTest(testDispatcher) {
        // --- Arrange ---
        val vm = createViewModel()
        val transaction = Transaction(uid = 1, parent = 2)

        // --- Act ---
        vm.deleteTransaction(
            transaction = transaction,
            shouldDeleteParent = true,
            dispatcher = testDispatcher
        )
        advanceUntilIdle()

        // --- Assert ---
        coVerify(exactly = 1) {
            transactionInteractor.deleteTransaction(
                transaction = transaction,
                deleteTransactionType = DeleteTransactionType.AllOccurrences
            )
        }
    }
}
