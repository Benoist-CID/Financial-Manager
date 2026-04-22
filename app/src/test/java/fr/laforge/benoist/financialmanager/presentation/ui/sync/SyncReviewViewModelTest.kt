package fr.laforge.benoist.financialmanager.presentation.ui.sync

import fr.laforge.benoist.financialmanager.domain.model.sync.BankTransaction
import fr.laforge.benoist.financialmanager.domain.model.sync.MatchConfidence
import fr.laforge.benoist.financialmanager.domain.model.sync.SyncStatus
import fr.laforge.benoist.financialmanager.domain.model.sync.TransactionMatchResult
import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import fr.laforge.benoist.financialmanager.domain.usecase.sync.ApplySyncMatchUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.sync.CreateTransactionFromBankUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.sync.ParseCsvBankTransactionsUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.sync.RunSyncUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should be empty`
import org.amshove.kluent.shouldHaveSize
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class SyncReviewViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val runSyncUseCase: RunSyncUseCase = mockk()
    private val applySyncMatchUseCase: ApplySyncMatchUseCase = mockk(relaxed = true)
    private val createTransactionFromBankUseCase: CreateTransactionFromBankUseCase = mockk(relaxed = true)
    private val parseCsvBankTransactionsUseCase: ParseCsvBankTransactionsUseCase = mockk()

    private lateinit var vm: SyncReviewViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        vm = SyncReviewViewModel(
            runSyncUseCase = runSyncUseCase,
            applySyncMatchUseCase = applySyncMatchUseCase,
            createTransactionFromBankUseCase = createTransactionFromBankUseCase,
            parseCsvBankTransactionsUseCase = parseCsvBankTransactionsUseCase,
            dispatcher = testDispatcher,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- Helpers ---

    private val sampleBankTx = BankTransaction(
        bankId = "b1",
        amount = 10f,
        description = "NETFLIX*123",
        valueDate = LocalDate.of(2024, 6, 15),
        bookingDate = LocalDate.of(2024, 6, 15),
        type = TransactionType.Expense,
    )

    private fun sampleMatchResult() = TransactionMatchResult(
        appTransaction = Transaction(
            uid = 1,
            amount = 10f,
            description = "Netflix",
            dateTime = LocalDateTime.of(2024, 6, 15, 0, 0),
            type = TransactionType.Expense,
            syncStatus = SyncStatus.PENDING,
        ),
        bankTransaction = sampleBankTx,
        confidence = MatchConfidence.HIGH,
    )

    /** Stubs the full importCsv pipeline to return [matches]. */
    private fun stubSuccessfulImport(matches: List<TransactionMatchResult>) {
        every { parseCsvBankTransactionsUseCase(any()) } returns Result.success(listOf(sampleBankTx))
        coEvery { runSyncUseCase(any(), any(), any()) } returns Result.success(matches)
    }

    // =========================================================================
    // Initial state
    // =========================================================================

    @Test
    fun `initial state is idle with empty matches`() {
        vm.uiState.value.isIdle `should be` true
        vm.uiState.value.matches.`should be empty`()
        vm.uiState.value.isLoading `should be` false
        vm.uiState.value.error `should be` null
    }

    // =========================================================================
    // Happy path — importCsv populates matches
    // =========================================================================

    @Test
    fun `importCsv populates matches on success`() = runTest {
        // Arrange
        val match = sampleMatchResult()
        stubSuccessfulImport(listOf(match))

        // Act
        vm.importCsv("csv-content")

        // Assert
        vm.uiState.value.isIdle `should be` false
        vm.uiState.value.isLoading `should be` false
        vm.uiState.value.matches shouldHaveSize 1
        vm.uiState.value.error `should be` null
    }

    // =========================================================================
    // Edge case — CSV parse failure sets error state
    // =========================================================================

    @Test
    fun `importCsv sets error when CSV parsing fails`() = runTest {
        // Arrange
        every { parseCsvBankTransactionsUseCase(any()) } returns
            Result.failure(IllegalArgumentException("Unrecognised CSV format"))

        // Act
        vm.importCsv("bad-csv")

        // Assert
        vm.uiState.value.isLoading `should be` false
        vm.uiState.value.error `should be` "Unrecognised CSV format"
        vm.uiState.value.matches.`should be empty`()
    }

    // =========================================================================
    // Edge case — sync matching failure sets error state
    // =========================================================================

    @Test
    fun `importCsv sets error when sync matching fails`() = runTest {
        // Arrange
        every { parseCsvBankTransactionsUseCase(any()) } returns Result.success(listOf(sampleBankTx))
        coEvery { runSyncUseCase(any(), any(), any()) } returns
            Result.failure(RuntimeException("DB error"))

        // Act
        vm.importCsv("csv-content")

        // Assert
        vm.uiState.value.isLoading `should be` false
        vm.uiState.value.error `should be` "DB error"
        vm.uiState.value.matches.`should be empty`()
    }

    // =========================================================================
    // resetToIdle returns to initial state
    // =========================================================================

    @Test
    fun `resetToIdle restores the initial idle state after an error`() = runTest {
        // Arrange — drive into error state first
        every { parseCsvBankTransactionsUseCase(any()) } returns
            Result.failure(RuntimeException("fail"))
        vm.importCsv("csv")

        // Act
        vm.resetToIdle()

        // Assert
        vm.uiState.value.isIdle `should be` true
        vm.uiState.value.error `should be` null
        vm.uiState.value.matches.`should be empty`()
    }

    // =========================================================================
    // confirmMatch removes the result from the list
    // =========================================================================

    @Test
    fun `confirmMatch removes the match from the list after applying it`() = runTest {
        // Arrange
        val match = sampleMatchResult()
        stubSuccessfulImport(listOf(match))
        vm.importCsv("csv-content")

        // Act
        vm.confirmMatch(match)

        // Assert
        vm.uiState.value.matches.`should be empty`()
        verify { applySyncMatchUseCase(match) }
    }

    // =========================================================================
    // skipMatch removes the result without persisting
    // =========================================================================

    @Test
    fun `skipMatch removes the match without calling applySyncMatchUseCase`() = runTest {
        // Arrange
        val match = sampleMatchResult()
        stubSuccessfulImport(listOf(match))
        vm.importCsv("csv-content")

        // Act
        vm.skipMatch(match)

        // Assert
        vm.uiState.value.matches.`should be empty`()
        verify(exactly = 0) { applySyncMatchUseCase(any()) }
    }

    // =========================================================================
    // createFromBank removes all matches for that bank transaction
    // =========================================================================

    @Test
    fun `createFromBank removes all candidates for the bank transaction`() = runTest {
        // Arrange — two app candidates for the same bank transaction
        val match1 = sampleMatchResult()
        val match2 = sampleMatchResult().copy(
            appTransaction = sampleMatchResult().appTransaction.copy(uid = 2),
        )
        stubSuccessfulImport(listOf(match1, match2))
        vm.importCsv("csv-content")

        // Act
        vm.createFromBank(sampleBankTx)

        // Assert — both matches removed since they share the same bankTx
        vm.uiState.value.matches.`should be empty`()
        verify { createTransactionFromBankUseCase(sampleBankTx) }
    }
}
