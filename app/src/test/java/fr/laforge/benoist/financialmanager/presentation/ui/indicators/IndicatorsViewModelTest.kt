package fr.laforge.benoist.financialmanager.presentation.ui.indicators

import fr.laforge.benoist.financialmanager.domain.usecase.indicators.GetDailyBalanceUseCase
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
class IndicatorsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val getRecurringIncomeUseCase = mockk<GetRecurringIncomeUseCase>()
    private val getRecurringExpensesUseCase = mockk<GetRecurringExpensesUseCase>()
    private val getRegularExpensesUseCase = mockk<GetRegularExpensesUseCase>()
    private val getDailyBalanceUseCase = mockk<GetDailyBalanceUseCase>()
    private val getNonRecurringIncomeUseCase = mockk<GetNonRecurringIncomeUseCase>()
    private val getMonthStartingBalanceUseCase = mockk<GetMonthStartingBalanceUseCase>()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { getRecurringIncomeUseCase() } returns flowOf(0f)
        every { getRecurringExpensesUseCase() } returns flowOf(0f)
        every { getRegularExpensesUseCase(any()) } returns flowOf(0f)
        every { getDailyBalanceUseCase() } returns flowOf(emptyList())
        every { getNonRecurringIncomeUseCase(any()) } returns flowOf(0f)
        every { getMonthStartingBalanceUseCase(any()) } returns flowOf(0f)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = IndicatorsViewModel(
        getRecurringIncomeUseCase = getRecurringIncomeUseCase,
        getRecurringExpensesUseCase = getRecurringExpensesUseCase,
        getRegularExpensesUseCase = getRegularExpensesUseCase,
        getDailyBalanceUseCase = getDailyBalanceUseCase,
        getNonRecurringIncomeUseCase = getNonRecurringIncomeUseCase,
        getMonthStartingBalanceUseCase = getMonthStartingBalanceUseCase,
    )

    @Test
    fun `ViewModel initialises with default zero values for all indicators`() = runTest(testDispatcher) {
        // --- Arrange & Act ---
        val vm = createViewModel()
        advanceUntilIdle()

        // --- Assert ---
        vm.recurringIncome.value shouldBeEqualTo 0f
        vm.recurringExpenses.value shouldBeEqualTo 0f
        vm.regularExpenses.value shouldBeEqualTo 0f
        vm.nonRecurringIncome.value shouldBeEqualTo 0f
        vm.startBalanceFlow.value shouldBeEqualTo 0f
    }

    @Test
    fun `recurringIncome StateFlow reflects value emitted by use case`() = runTest(testDispatcher) {
        // --- Arrange ---
        every { getRecurringIncomeUseCase() } returns flowOf(2500f)
        val vm = createViewModel()

        // --- Act ---
        advanceUntilIdle()

        // --- Assert ---
        vm.recurringIncome.value shouldBeEqualTo 2500f
    }

    @Test
    fun `recurringExpenses StateFlow reflects value emitted by use case`() = runTest(testDispatcher) {
        // --- Arrange ---
        every { getRecurringExpensesUseCase() } returns flowOf(800f)
        val vm = createViewModel()

        // --- Act ---
        advanceUntilIdle()

        // --- Assert ---
        vm.recurringExpenses.value shouldBeEqualTo 800f
    }
}
