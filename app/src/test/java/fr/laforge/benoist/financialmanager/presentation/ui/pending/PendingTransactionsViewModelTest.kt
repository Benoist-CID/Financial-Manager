package fr.laforge.benoist.financialmanager.presentation.ui.pending

import fr.laforge.benoist.financialmanager.domain.model.notification.NotificationSource
import fr.laforge.benoist.financialmanager.domain.model.notification.PendingTransaction
import fr.laforge.benoist.financialmanager.domain.model.notification.PendingTransactionConfidence
import fr.laforge.benoist.financialmanager.domain.model.notification.PendingTransactionStatus
import fr.laforge.benoist.financialmanager.domain.repository.PendingTransactionRepository
import fr.laforge.benoist.financialmanager.domain.usecase.notification.ConfirmPendingTransactionUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.notification.DismissPendingTransactionUseCase
import io.mockk.coJustRun
import io.mockk.coVerify
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
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PendingTransactionsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val repository = mockk<PendingTransactionRepository>()
    private val confirmPendingTransactionUseCase = mockk<ConfirmPendingTransactionUseCase>()
    private val dismissPendingTransactionUseCase = mockk<DismissPendingTransactionUseCase>()

    private val dummyTransaction = PendingTransaction(
        amount = 42f,
        description = "Test Shop",
        sources = listOf(NotificationSource.GOOGLE_PAY),
        confidence = PendingTransactionConfidence.LOW,
        status = PendingTransactionStatus.PENDING,
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { repository.getAllPending() } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildVm() = PendingTransactionsViewModel(
        repository = repository,
        confirmPendingTransactionUseCase = confirmPendingTransactionUseCase,
        dismissPendingTransactionUseCase = dismissPendingTransactionUseCase,
    )

    // --- Happy path ---

    @Test
    fun `uiState reflects pending transactions emitted by repository`() = runTest(testDispatcher) {
        // --- Arrange ---
        every { repository.getAllPending() } returns flowOf(listOf(dummyTransaction))
        val vm = buildVm()
        val collectJob = launch { vm.uiState.collect {} }

        // --- Act ---
        advanceUntilIdle()

        // --- Assert ---
        vm.uiState.value.transactions shouldBeEqualTo listOf(dummyTransaction)
        collectJob.cancel()
    }

    @Test
    fun `uiState is empty when repository emits no pending transactions`() = runTest(testDispatcher) {
        // --- Arrange ---
        val vm = buildVm()
        val collectJob = launch { vm.uiState.collect {} }

        // --- Act ---
        advanceUntilIdle()

        // --- Assert ---
        vm.uiState.value.transactions shouldBeEqualTo emptyList()
        collectJob.cancel()
    }

    // --- confirm ---

    @Test
    fun `confirm delegates to ConfirmPendingTransactionUseCase`() = runTest(testDispatcher) {
        // --- Arrange ---
        coJustRun { confirmPendingTransactionUseCase(dummyTransaction) }
        val vm = buildVm()

        // --- Act ---
        vm.confirm(dummyTransaction)
        advanceUntilIdle()

        // --- Assert ---
        coVerify(exactly = 1) { confirmPendingTransactionUseCase(dummyTransaction) }
    }

    // --- dismiss ---

    @Test
    fun `dismiss delegates to DismissPendingTransactionUseCase`() = runTest(testDispatcher) {
        // --- Arrange ---
        coJustRun { dismissPendingTransactionUseCase(dummyTransaction) }
        val vm = buildVm()

        // --- Act ---
        vm.dismiss(dummyTransaction)
        advanceUntilIdle()

        // --- Assert ---
        coVerify(exactly = 1) { dismissPendingTransactionUseCase(dummyTransaction) }
    }
}
