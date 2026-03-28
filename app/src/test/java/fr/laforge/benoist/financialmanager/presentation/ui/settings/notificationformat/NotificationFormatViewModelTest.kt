package fr.laforge.benoist.financialmanager.presentation.ui.settings.notificationformat

import fr.laforge.benoist.financialmanager.domain.model.notification.NotificationFormat
import fr.laforge.benoist.financialmanager.domain.usecase.notification.AddNotificationFormatUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.notification.DeleteNotificationFormatUseCase
import fr.laforge.benoist.financialmanager.domain.usecase.notification.GetAllNotificationFormatsUseCase
import io.mockk.coEvery
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
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationFormatViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val getAllNotificationFormatsUseCase = mockk<GetAllNotificationFormatsUseCase>()
    private val addNotificationFormatUseCase = mockk<AddNotificationFormatUseCase>()
    private val deleteNotificationFormatUseCase = mockk<DeleteNotificationFormatUseCase>()

    private val dummyFormat = NotificationFormat(
        id = UUID.fromString("00000000-0000-0000-0000-000000000001"),
        description = "My Bank",
        pattern = "Debited {amount} for {description}",
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { getAllNotificationFormatsUseCase() } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildVm() = NotificationFormatViewModel(
        getAllNotificationFormatsUseCase = getAllNotificationFormatsUseCase,
        addNotificationFormatUseCase = addNotificationFormatUseCase,
        deleteNotificationFormatUseCase = deleteNotificationFormatUseCase,
    )

    // --- Happy path ---

    @Test
    fun `uiState reflects formats emitted by GetAllNotificationFormatsUseCase`() = runTest(testDispatcher) {
        // --- Arrange ---
        every { getAllNotificationFormatsUseCase() } returns flowOf(listOf(dummyFormat))
        val vm = buildVm()
        val collectJob = launch { vm.uiState.collect {} }

        // --- Act ---
        advanceUntilIdle()

        // --- Assert ---
        vm.uiState.value.formats shouldBeEqualTo listOf(dummyFormat)
        collectJob.cancel()
    }

    @Test
    fun `uiState is empty when repository emits no formats`() = runTest(testDispatcher) {
        // --- Arrange ---
        val vm = buildVm()
        val collectJob = launch { vm.uiState.collect {} }

        // --- Act ---
        advanceUntilIdle()

        // --- Assert ---
        vm.uiState.value.formats shouldBeEqualTo emptyList()
        collectJob.cancel()
    }

    @Test
    fun `addFormat delegates to AddNotificationFormatUseCase with correct model`() = runTest(testDispatcher) {
        // --- Arrange ---
        coJustRun { addNotificationFormatUseCase(any()) }
        val vm = buildVm()

        // --- Act ---
        vm.addFormat(description = "My Bank", pattern = "Spent {amount} at {description}")
        advanceUntilIdle()

        // --- Assert ---
        coVerify(exactly = 1) {
            addNotificationFormatUseCase(
                match { it.description == "My Bank" && it.pattern == "Spent {amount} at {description}" }
            )
        }
    }

    @Test
    fun `deleteFormat delegates to DeleteNotificationFormatUseCase with correct id`() = runTest(testDispatcher) {
        // --- Arrange ---
        coJustRun { deleteNotificationFormatUseCase(dummyFormat.id) }
        val vm = buildVm()

        // --- Act ---
        vm.deleteFormat(dummyFormat.id)
        advanceUntilIdle()

        // --- Assert ---
        coVerify(exactly = 1) { deleteNotificationFormatUseCase(dummyFormat.id) }
    }

    // --- Edge cases ---

    @Test
    fun `addFormat does not crash when use case throws IllegalArgumentException`() = runTest(testDispatcher) {
        // --- Arrange --- use case rejects pattern missing {amount}
        coEvery { addNotificationFormatUseCase(any()) } throws IllegalArgumentException("missing placeholder")
        val vm = buildVm()

        // --- Act & Assert --- no exception propagates to the caller
        vm.addFormat(description = "Bad", pattern = "no placeholders here")
        advanceUntilIdle()
    }

    @Test
    fun `uiState starts with empty formats before repository emits`() = runTest(testDispatcher) {
        // --- Arrange ---
        every { getAllNotificationFormatsUseCase() } returns flowOf()
        val vm = buildVm()

        // --- Assert --- initial state before any emission
        vm.uiState.value.formats shouldBeEqualTo emptyList()
    }
}
