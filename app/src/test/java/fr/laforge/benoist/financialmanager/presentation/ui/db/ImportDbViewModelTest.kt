package fr.laforge.benoist.financialmanager.presentation.ui.db

import fr.laforge.benoist.financialmanager.domain.usecase.transaction.ImportTransactionsUseCase
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
class ImportDbViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val importTransactionsUseCase = mockk<ImportTransactionsUseCase>(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = ImportDbViewModel(
        importTransactionsUseCase = importTransactionsUseCase,
        dispatcher = testDispatcher
    )

    // --- updateToBeImported ---

    @Test
    fun `updateToBeImported updates toBeImported state`() {
        // --- Arrange ---
        val vm = createViewModel()
        val csv = "1;12345;50.0;Coffee;Expense;false;None;0;Food"

        // --- Act ---
        vm.updateToBeImported(csv)

        // --- Assert ---
        vm.toBeImported shouldBeEqualTo csv
    }

    @Test
    fun `updateToBeImported with empty string resets state`() {
        // --- Arrange ---
        val vm = createViewModel()
        vm.updateToBeImported("some content")

        // --- Act ---
        vm.updateToBeImported("")

        // --- Assert ---
        vm.toBeImported shouldBeEqualTo ""
    }

    // --- importDb ---

    @Test
    fun `importDb delegates toBeImported content to ImportTransactionsUseCase`() = runTest(testDispatcher) {
        // --- Arrange ---
        val vm = createViewModel()
        val csv = "1;12345;50.0;Coffee;Expense;false;None;0;Food"
        vm.updateToBeImported(csv)

        // --- Act ---
        vm.importDb()
        advanceUntilIdle()

        // --- Assert ---
        coVerify(exactly = 1) { importTransactionsUseCase(csv) }
    }

    @Test
    fun `importDb with empty string still delegates to ImportTransactionsUseCase`() = runTest(testDispatcher) {
        // --- Arrange ---
        val vm = createViewModel()
        vm.updateToBeImported("")

        // --- Act ---
        vm.importDb()
        advanceUntilIdle()

        // --- Assert ---
        coVerify(exactly = 1) { importTransactionsUseCase("") }
    }
}
