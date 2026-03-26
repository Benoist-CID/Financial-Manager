package fr.laforge.benoist.financialmanager.presentation.ui.transaction.detail

import androidx.lifecycle.SavedStateHandle
import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import fr.laforge.benoist.financialmanager.domain.usecase.transaction.GetTransactionByIdUseCase
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
class TransactionDetailsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val savedStateHandle = mockk<SavedStateHandle>()
    private val getTransactionByIdUseCase = mockk<GetTransactionByIdUseCase>()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = TransactionDetailsViewModel(
        savedStateHandle = savedStateHandle,
        getTransactionByIdUseCase = getTransactionByIdUseCase,
    )

    @Test
    fun `uiState initialises with empty TransactionUiState before use case emits`() = runTest(testDispatcher) {
        // --- Arrange ---
        every { savedStateHandle.get<Int>("transactionId") } returns null
        every { getTransactionByIdUseCase(0) } returns flowOf(Transaction())

        // --- Act ---
        val vm = createViewModel()

        // --- Assert ---
        // initial value is empty TransactionUiState before coroutine starts
        vm.uiState.value.transaction shouldBeEqualTo Transaction()
    }

    @Test
    fun `uiState reflects transaction loaded by use case`() = runTest(testDispatcher) {
        // --- Arrange ---
        val expected = Transaction(uid = 7, description = "Salary", amount = 2000f, type = TransactionType.Income)
        every { savedStateHandle.get<Int>("transactionId") } returns 7
        every { getTransactionByIdUseCase(7) } returns flowOf(expected)

        // --- Act ---
        val vm = createViewModel()
        advanceUntilIdle()

        // --- Assert ---
        vm.uiState.value.transaction shouldBeEqualTo expected
    }

    @Test
    fun `uiState uses transactionId 0 when savedStateHandle returns null`() = runTest(testDispatcher) {
        // --- Arrange ---
        every { savedStateHandle.get<Int>("transactionId") } returns null
        val fallbackTransaction = Transaction(uid = 0)
        every { getTransactionByIdUseCase(0) } returns flowOf(fallbackTransaction)

        // --- Act ---
        val vm = createViewModel()
        advanceUntilIdle()

        // --- Assert ---
        vm.uiState.value.transaction.uid shouldBeEqualTo 0
    }
}
