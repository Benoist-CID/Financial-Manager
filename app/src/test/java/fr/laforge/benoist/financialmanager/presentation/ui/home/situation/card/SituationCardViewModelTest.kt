package fr.laforge.benoist.financialmanager.presentation.ui.home.situation.card

import fr.laforge.benoist.financialmanager.domain.repository.PreferencesRepository
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.CalculateSituationProportionsUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.GetMonthStartingBalanceUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.GetNonRecurringIncomeUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.GetRecurringExpensesUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.GetRecurringIncomeUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.indicators.GetRegularExpensesUseCase
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
class SituationCardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val preferencesRepository = mockk<PreferencesRepository>()
    private val getRecurringExpensesUseCase = mockk<GetRecurringExpensesUseCase>()
    private val getRegularExpensesUseCase = mockk<GetRegularExpensesUseCase>()
    private val getRecurringIncomeUseCase = mockk<GetRecurringIncomeUseCase>()
    private val getNonRecurringIncomeUseCase = mockk<GetNonRecurringIncomeUseCase>()
    private val getMonthStartingBalanceUseCase = mockk<GetMonthStartingBalanceUseCase>()
    private val calculateSituationProportionsUseCase = CalculateSituationProportionsUseCase()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { preferencesRepository.getSavingTarget() } returns flowOf(0f)
        every { getRecurringExpensesUseCase() } returns flowOf(0f)
        every { getRegularExpensesUseCase(any()) } returns flowOf(0f)
        every { getRecurringIncomeUseCase() } returns flowOf(0f)
        every { getNonRecurringIncomeUseCase(any()) } returns flowOf(0f)
        every { getMonthStartingBalanceUseCase(any()) } returns flowOf(0f)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = SituationCardViewModel(
        preferencesRepository = preferencesRepository,
        getRecurringExpensesUseCase = getRecurringExpensesUseCase,
        getNonRecurringExpensesUseCase = getRegularExpensesUseCase,
        getRecurringIncomeUseCase = getRecurringIncomeUseCase,
        getNonRecurringIncomeUseCase = getNonRecurringIncomeUseCase,
        getMonthStartingBalanceUseCase = getMonthStartingBalanceUseCase,
        calculateSituationProportionsUseCase = calculateSituationProportionsUseCase,
    )

    @Test
    fun `uiState initialises with default zero values`() = runTest(testDispatcher) {
        // --- Arrange & Act ---
        val vm = createViewModel()
        advanceUntilIdle()

        // --- Assert ---
        vm.uiState.value.income shouldBeEqualTo 0f
        vm.uiState.value.savingsTarget shouldBeEqualTo 0f
    }

    @Test
    fun `uiState reflects income from use cases`() = runTest(testDispatcher) {
        // --- Arrange ---
        every { getRecurringIncomeUseCase() } returns flowOf(2000f)
        every { getNonRecurringIncomeUseCase(any()) } returns flowOf(500f)

        val vm = createViewModel()

        // --- Act ---
        advanceUntilIdle()

        // --- Assert ---
        vm.uiState.value.income shouldBeEqualTo 2500f
    }

    @Test
    fun `uiState reflects savings target from PreferencesRepository`() = runTest(testDispatcher) {
        // --- Arrange ---
        every { preferencesRepository.getSavingTarget() } returns flowOf(300f)
        val vm = createViewModel()

        // --- Act ---
        advanceUntilIdle()

        // --- Assert ---
        vm.uiState.value.savingsTarget shouldBeEqualTo 300f
    }
}
