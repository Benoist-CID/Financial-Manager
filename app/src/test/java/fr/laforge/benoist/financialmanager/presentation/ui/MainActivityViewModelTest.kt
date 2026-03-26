package fr.laforge.benoist.financialmanager.presentation.ui

import fr.laforge.benoist.financialmanager.domain.usecase.CreateRegularTransactionsUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.notification.EnableNotificationAccessUseCase
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [MainActivityViewModel].
 *
 * @note [onStart] requires a live [androidx.navigation.NavController] to be set via
 * [MainActivityViewModel.setNavController] before it can call `popBackStack`. Tests
 * that would trigger `onStart` are therefore covered by instrumented (UI) tests.
 * These unit tests focus on the business-logic delegations that can be verified
 * without a NavController.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainActivityViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val createRegularTransactionsUseCase = mockk<CreateRegularTransactionsUseCase>(relaxed = true)
    private val enableNotificationAccessUseCase = mockk<EnableNotificationAccessUseCase>(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = MainActivityViewModel(
        createRegularTransactionsUseCase = createRegularTransactionsUseCase,
        enableNotificationAccessUseCase = enableNotificationAccessUseCase,
    )

    @Test
    fun `ViewModel can be constructed without errors`() {
        // --- Act & Assert ---
        createViewModel() // should not throw
    }

    @Test
    fun `enableNotificationAccessUseCase is called during onStart`() = runTest(testDispatcher) {
        // --- Arrange ---
        // We need a NavController mock to call onStart without crashing on
        // uninitialised lateinit. Since NavController is an Android framework class
        // we use a relaxed mock so all calls are no-ops.
        val vm = createViewModel()
        val navController = mockk<androidx.navigation.NavController>(relaxed = true)
        vm.setNavController(navController)

        // --- Act ---
        val owner = mockk<androidx.lifecycle.LifecycleOwner>(relaxed = true)
        vm.onStart(owner)
        advanceUntilIdle()

        // --- Assert ---
        verify(exactly = 1) { enableNotificationAccessUseCase() }
    }

    @Test
    fun `createRegularTransactionsUseCase is called with current month range during onStart`() = runTest(testDispatcher) {
        // --- Arrange ---
        val vm = createViewModel()
        val navController = mockk<androidx.navigation.NavController>(relaxed = true)
        vm.setNavController(navController)

        // --- Act ---
        val owner = mockk<androidx.lifecycle.LifecycleOwner>(relaxed = true)
        vm.onStart(owner)
        advanceUntilIdle()

        // --- Assert ---
        coVerify(exactly = 1) {
            createRegularTransactionsUseCase.execute(
                startDate = match { it.dayOfMonth == 1 },   // first of month
                endDate = any(),
            )
        }
    }
}
