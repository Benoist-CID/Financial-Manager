package fr.laforge.benoist.financialmanager.domain.usecase.notification

import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.usecase.CreateTransactionUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class CreateTransactionFromNotificationUseCaseTest {

    private val createTransactionUseCase = mockk<CreateTransactionUseCase>(relaxed = true)
    private val notificationHelper = mockk<NotificationHelper>()
    private val testDispatcher = StandardTestDispatcher()

    private val useCase = CreateTransactionFromNotificationUseCase(
        createTransactionUseCase = createTransactionUseCase,
        notificationHelper = notificationHelper,
        dispatcher = testDispatcher
    )

    private val dummyTransaction = Transaction(uid = 1, amount = 50f, description = "Test")

    @Test
    fun `invoke creates transaction and returns true when notification is valid`() = runTest(testDispatcher) {
        // --- Arrange ---
        every {
            notificationHelper.isTransaction("€ 50.00 - Merchant")
        } returns Result.success(true)
        every {
            notificationHelper.parseNotificationMessage("Merchant", "€ 50.00 - Merchant")
        } returns Result.success(dummyTransaction)
        coEvery { createTransactionUseCase(dummyTransaction) } returns true

        // --- Act ---
        val result = useCase(
            notificationTitle = "Merchant",
            notificationMessage = "€ 50.00 - Merchant"
        )

        // --- Assert ---
        result shouldBeEqualTo true
        coVerify(exactly = 1) { createTransactionUseCase(dummyTransaction) }
    }

    @Test
    fun `invoke returns false and skips creation when notification is not a transaction`() = runTest(testDispatcher) {
        // --- Arrange ---
        every {
            notificationHelper.isTransaction(any())
        } returns Result.success(false)

        // --- Act ---
        val result = useCase(
            notificationTitle = "News App",
            notificationMessage = "Breaking news: markets hit record"
        )

        // --- Assert ---
        result shouldBeEqualTo false
        coVerify(exactly = 0) { createTransactionUseCase(any()) }
    }

    @Test
    fun `invoke returns false and skips creation when parsing fails`() = runTest(testDispatcher) {
        // --- Arrange ---
        every {
            notificationHelper.isTransaction(any())
        } returns Result.success(true)
        every {
            notificationHelper.parseNotificationMessage(any(), any())
        } returns Result.failure(IllegalArgumentException("Cannot parse amount"))

        // --- Act ---
        val result = useCase(
            notificationTitle = "Bank",
            notificationMessage = "Malformed message"
        )

        // --- Assert ---
        result shouldBeEqualTo false
        coVerify(exactly = 0) { createTransactionUseCase(any()) }
    }
}
