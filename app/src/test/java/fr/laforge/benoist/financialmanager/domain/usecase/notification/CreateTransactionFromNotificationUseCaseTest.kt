package fr.laforge.benoist.financialmanager.domain.usecase.notification

import fr.laforge.benoist.financialmanager.domain.model.notification.NotificationSource
import fr.laforge.benoist.financialmanager.domain.model.notification.ParsedNotification
import fr.laforge.benoist.financialmanager.domain.repository.PendingTransactionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class CreateTransactionFromNotificationUseCaseTest {

    private val pendingTransactionRepository = mockk<PendingTransactionRepository>(relaxed = true)
    private val notificationHelper = mockk<NotificationHelper>()
    private val testDispatcher = StandardTestDispatcher()

    private val processIncomingNotificationUseCase = ProcessIncomingNotificationUseCase(
        repository = pendingTransactionRepository,
    )

    private val useCase = CreateTransactionFromNotificationUseCase(
        processIncomingNotificationUseCase = processIncomingNotificationUseCase,
        notificationHelper = notificationHelper,
        dispatcher = testDispatcher,
    )

    private val dummyParsed = ParsedNotification(
        amount = 50f,
        description = "Test Merchant",
        source = NotificationSource.GOOGLE_PAY,
    )

    @Test
    fun `invoke stages pending transaction and returns true when notification is valid`() = runTest(testDispatcher) {
        // --- Arrange ---
        every { notificationHelper.isTransaction("€ 50.00 - Merchant") } returns Result.success(true)
        every {
            notificationHelper.parseToPending("Merchant", "€ 50.00 - Merchant")
        } returns Result.success(dummyParsed)
        coEvery { pendingTransactionRepository.findPendingByAmountInWindow(any(), any(), any()) } returns null

        // --- Act ---
        val result = useCase(
            notificationTitle = "Merchant",
            notificationMessage = "€ 50.00 - Merchant",
        )

        // --- Assert ---
        result shouldBeEqualTo true
        coVerify(exactly = 1) { pendingTransactionRepository.add(any()) }
    }

    @Test
    fun `invoke returns false and skips staging when notification is not a transaction`() = runTest(testDispatcher) {
        // --- Arrange ---
        every { notificationHelper.isTransaction(any()) } returns Result.success(false)

        // --- Act ---
        val result = useCase(
            notificationTitle = "News App",
            notificationMessage = "Breaking news: markets hit record",
        )

        // --- Assert ---
        result shouldBeEqualTo false
        coVerify(exactly = 0) { pendingTransactionRepository.add(any()) }
    }

    @Test
    fun `invoke returns false and skips staging when parsing fails`() = runTest(testDispatcher) {
        // --- Arrange ---
        every { notificationHelper.isTransaction(any()) } returns Result.success(true)
        every {
            notificationHelper.parseToPending(any(), any())
        } returns Result.failure(IllegalArgumentException("No parser matched"))

        // --- Act ---
        val result = useCase(
            notificationTitle = "Bank",
            notificationMessage = "Malformed message",
        )

        // --- Assert ---
        result shouldBeEqualTo false
        coVerify(exactly = 0) { pendingTransactionRepository.add(any()) }
    }
}
