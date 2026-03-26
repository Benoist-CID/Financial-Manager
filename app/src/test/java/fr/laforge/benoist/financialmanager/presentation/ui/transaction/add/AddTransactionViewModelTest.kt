package fr.laforge.benoist.financialmanager.presentation.ui.transaction.add

import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionCategory
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionPeriod
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import fr.laforge.benoist.financialmanager.domain.usecase.CreateTransactionUseCase
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class AddTransactionViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val createTransactionUseCase = mockk<CreateTransactionUseCase>(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = AddTransactionViewModel(createTransactionUseCase)

    // --- updateAmount ---

    @Test
    fun `updateAmount updates uiState amount`() {
        // --- Arrange ---
        val vm = createViewModel()

        // --- Act ---
        vm.updateAmount("42.5")

        // --- Assert ---
        vm.uiState.value.amount shouldBeEqualTo "42.5"
    }

    @Test
    fun `updateAmount with empty string keeps uiState amount`() {
        // --- Arrange ---
        val vm = createViewModel()

        // --- Act ---
        vm.updateAmount("")

        // --- Assert ---
        vm.uiState.value.amount shouldBeEqualTo ""
    }

    // --- updateDescription ---

    @Test
    fun `updateDescription updates uiState description`() {
        // --- Arrange ---
        val vm = createViewModel()

        // --- Act ---
        vm.updateDescription("Coffee")

        // --- Assert ---
        vm.uiState.value.description shouldBeEqualTo "Coffee"
    }

    // --- updateInputType ---

    @Test
    fun `updateInputType updates uiState transactionType`() {
        // --- Arrange ---
        val vm = createViewModel()

        // --- Act ---
        vm.updateInputType(TransactionType.Income)

        // --- Assert ---
        vm.uiState.value.transactionType shouldBeEqualTo TransactionType.Income
    }

    // --- updateTransactionCategory ---

    @Test
    fun `updateTransactionCategory updates uiState transactionCategory`() {
        // --- Arrange ---
        val vm = createViewModel()

        // --- Act ---
        vm.updateTransactionCategory(TransactionCategory.Food)

        // --- Assert ---
        vm.uiState.value.transactionCategory shouldBeEqualTo TransactionCategory.Food
    }

    // --- createTransaction ---

    @Test
    fun `createTransaction delegates to CreateTransactionUseCase with current state`() = runTest(testDispatcher) {
        // --- Arrange ---
        val vm = createViewModel()
        vm.updateAmount("100.0")
        vm.updateDescription("Groceries")
        vm.updateInputType(TransactionType.Expense)
        vm.updateTransactionCategory(TransactionCategory.Food)
        vm.updateIsPeriodic(false)
        vm.updatePeriod(TransactionPeriod.Monthly)

        // --- Act ---
        vm.createTransaction()
        advanceUntilIdle()

        // --- Assert ---
        coVerify(exactly = 1) {
            createTransactionUseCase(
                match { t ->
                    t.amount == 100f &&
                    t.description == "Groceries" &&
                    t.type == TransactionType.Expense &&
                    t.category == TransactionCategory.Food &&
                    !t.isPeriodic
                }
            )
        }
    }

    @Test
    fun `createTransaction with periodic=true sets isPeriodic and period on transaction`() = runTest(testDispatcher) {
        // --- Arrange ---
        val vm = createViewModel()
        vm.updateAmount("50.0")
        vm.updateIsPeriodic(true)
        vm.updatePeriod(TransactionPeriod.Monthly)

        // --- Act ---
        vm.createTransaction()
        advanceUntilIdle()

        // --- Assert ---
        coVerify(exactly = 1) {
            createTransactionUseCase(
                match { t ->
                    t.isPeriodic &&
                    t.period == TransactionPeriod.Monthly
                }
            )
        }
    }
}
