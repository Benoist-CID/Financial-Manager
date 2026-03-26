package fr.laforge.benoist.financialmanager.presentation.ui.transaction.update

import androidx.lifecycle.SavedStateHandle
import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionCategory
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import fr.laforge.benoist.financialmanager.domain.usecase.TransactionInteractor
import fr.laforge.benoist.financialmanager.domain.usecase.transaction.GetTransactionByIdUseCase
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
class UpdateTransactionViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val savedStateHandle = mockk<SavedStateHandle>()
    private val getTransactionByIdUseCase = mockk<GetTransactionByIdUseCase>()
    private val transactionInteractor = mockk<TransactionInteractor>(relaxed = true)

    private val existingTransaction = Transaction(
        uid = 42,
        amount = 100f,
        description = "Initial",
        type = TransactionType.Expense
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { savedStateHandle.get<Int>("transactionId") } returns 42
        every { savedStateHandle["transactionId"] } returns 42
        every { getTransactionByIdUseCase(42) } returns flowOf(existingTransaction)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = UpdateTransactionViewModel(
        savedStateHandle = savedStateHandle,
        getTransactionByIdUseCase = getTransactionByIdUseCase,
        transactionInteractor = transactionInteractor,
    )

    // --- updateAmount ---

    @Test
    fun `updateAmount parses string and updates uiState transaction amount`() = runTest(testDispatcher) {
        // --- Arrange ---
        val vm = createViewModel()
        advanceUntilIdle() // allow init block to load transaction

        // --- Act ---
        vm.updateAmount("250.0")

        // --- Assert ---
        vm.uiState.transaction.amount shouldBeEqualTo 250f
    }

    @Test
    fun `updateAmount silently ignores non-numeric input`() = runTest(testDispatcher) {
        // --- Arrange ---
        val vm = createViewModel()
        advanceUntilIdle()
        val amountBefore = vm.uiState.transaction.amount

        // --- Act ---
        vm.updateAmount("not-a-number")

        // --- Assert ---
        vm.uiState.transaction.amount shouldBeEqualTo amountBefore
    }

    // --- updateDescription ---

    @Test
    fun `updateDescription updates uiState transaction description`() = runTest(testDispatcher) {
        // --- Arrange ---
        val vm = createViewModel()
        advanceUntilIdle()

        // --- Act ---
        vm.updateDescription("New description")

        // --- Assert ---
        vm.uiState.transaction.description shouldBeEqualTo "New description"
    }

    // --- updateInputType ---

    @Test
    fun `updateInputType updates uiState transaction type`() = runTest(testDispatcher) {
        // --- Arrange ---
        val vm = createViewModel()
        advanceUntilIdle()

        // --- Act ---
        vm.updateInputType(TransactionType.Income)

        // --- Assert ---
        vm.uiState.transaction.type shouldBeEqualTo TransactionType.Income
    }

    // --- updateTransactionCategory ---

    @Test
    fun `updateTransactionCategory updates uiState transaction category`() = runTest(testDispatcher) {
        // --- Arrange ---
        val vm = createViewModel()
        advanceUntilIdle()

        // --- Act ---
        vm.updateTransactionCategory(TransactionCategory.Food)

        // --- Assert ---
        vm.uiState.transaction.category shouldBeEqualTo TransactionCategory.Food
    }

    // --- isPeriodicTransaction ---

    @Test
    fun `isPeriodicTransaction delegates to TransactionInteractor`() = runTest(testDispatcher) {
        // --- Arrange ---
        val vm = createViewModel()
        val periodicTransaction = Transaction(uid = 1, parent = 5)
        every { transactionInteractor.isPeriodicTransaction(periodicTransaction) } returns true

        // --- Act ---
        val result = vm.isPeriodicTransaction(periodicTransaction)

        // --- Assert ---
        result shouldBeEqualTo true
    }

    // --- updateTransaction(dispatcher) ---

    @Test
    fun `updateTransaction delegates uiState transaction to interactor with shouldUpdateParent=false`() = runTest(testDispatcher) {
        // --- Arrange ---
        val vm = createViewModel()
        advanceUntilIdle()
        vm.updateDescription("Updated")

        // --- Act ---
        vm.updateTransaction(dispatcher = testDispatcher)
        advanceUntilIdle()

        // --- Assert ---
        coVerify(exactly = 1) {
            transactionInteractor.updateTransaction(
                transaction = match { it.description == "Updated" },
                shouldUpdateParent = false
            )
        }
    }
}
