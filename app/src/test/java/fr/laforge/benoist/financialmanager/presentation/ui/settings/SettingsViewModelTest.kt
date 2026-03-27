package fr.laforge.benoist.financialmanager.presentation.ui.settings

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.repository.PreferencesRepository
import fr.laforge.benoist.financialmanager.domain.usecase.transaction.ExportTransactionsListUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.transaction.GetAllRecurringTransactionsUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.transaction.GetAllTransactionsUseCase
import fr.laforge.benoist.financialmanager.infrastructure.service.AppVersionProvider
import fr.laforge.benoist.financialmanager.presentation.util.ExportService
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val preferencesRepository = mockk<PreferencesRepository>(relaxed = true)
    private val getAllTransactionsUseCase = mockk<GetAllTransactionsUseCase>()
    private val getAllRecurringTransactionsUseCase = mockk<GetAllRecurringTransactionsUseCase>()
    private val exportTransactionsListUseCase = mockk<ExportTransactionsListUseCase>(relaxed = true)
    private val exportService = mockk<ExportService>(relaxed = true)
    private val appVersionProvider = mockk<AppVersionProvider>()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { preferencesRepository.getSavingTarget() } returns flowOf(0f)
        every { appVersionProvider.getVersionName() } returns "1.0"
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = SettingsViewModel(
        preferencesRepository = preferencesRepository,
        getAllTransactionsUseCase = getAllTransactionsUseCase,
        getAllRecurringTransactionsUseCase = getAllRecurringTransactionsUseCase,
        exportTransactionsListUseCase = exportTransactionsListUseCase,
        exportService = exportService,
        appVersionProvider = appVersionProvider,
    )

    // --- uiState.versionName ---

    @Test
    fun `uiState exposes version name from AppVersionProvider`() {
        // --- Arrange ---
        every { appVersionProvider.getVersionName() } returns "2.5"

        // --- Act ---
        val vm = createViewModel()

        // --- Assert ---
        vm.uiState.value.versionName shouldBeEqualTo "2.5"
    }

    @Test
    fun `uiState exposes fallback when AppVersionProvider returns empty`() {
        // --- Arrange ---
        every { appVersionProvider.getVersionName() } returns ""

        // --- Act ---
        val vm = createViewModel()

        // --- Assert ---
        vm.uiState.value.versionName shouldBeEqualTo ""
    }

    // --- setSavingsTarget ---

    @Test
    fun `setSavingsTarget delegates to PreferencesRepository`() = runTest(testDispatcher) {
        // --- Arrange ---
        val vm = createViewModel()

        // --- Act ---
        vm.setSavingsTarget(500f)
        advanceUntilIdle()

        // --- Assert ---
        coVerify(exactly = 1) { preferencesRepository.setSavingsTarget(500f) }
    }

    @Test
    fun `setSavingsTarget with zero is forwarded to PreferencesRepository`() = runTest(testDispatcher) {
        // --- Arrange ---
        val vm = createViewModel()

        // --- Act ---
        vm.setSavingsTarget(0f)
        advanceUntilIdle()

        // --- Assert ---
        coVerify(exactly = 1) { preferencesRepository.setSavingsTarget(0f) }
    }

    // --- saveDb ---

    @Test
    fun `saveDb fetches all transactions and triggers export service`() = runTest(testDispatcher) {
        // --- Arrange ---
        val transactions = listOf(
            Transaction(uid = 1, description = "Salary"),
            Transaction(uid = 2, description = "Rent")
        )
        every { getAllTransactionsUseCase() } returns flowOf(transactions)
        every { exportTransactionsListUseCase(transactions) } returns Result.success("csv content")

        val vm = createViewModel()

        // --- Act ---
        vm.saveDb()
        advanceUntilIdle()

        // --- Assert ---
        verify(exactly = 1) { exportService.export("csv content", any()) }
    }

    @Test
    fun `saveDb does not call export service when export use case fails`() = runTest(testDispatcher) {
        // --- Arrange ---
        every { getAllTransactionsUseCase() } returns flowOf(emptyList())
        every { exportTransactionsListUseCase(emptyList()) } returns Result.failure(
            IllegalArgumentException("No transactions")
        )

        val vm = createViewModel()

        // --- Act ---
        vm.saveDb()
        advanceUntilIdle()

        // --- Assert ---
        verify(exactly = 0) { exportService.export(any(), any()) }
    }
}
