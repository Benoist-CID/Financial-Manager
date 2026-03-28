package fr.laforge.benoist.financialmanager.domain.usecase.notification

import fr.laforge.benoist.financialmanager.domain.model.notification.NotificationSource
import fr.laforge.benoist.financialmanager.domain.model.notification.ParsedNotification
import fr.laforge.benoist.financialmanager.domain.model.notification.PendingTransaction
import fr.laforge.benoist.financialmanager.domain.model.notification.PendingTransactionConfidence
import fr.laforge.benoist.financialmanager.domain.model.notification.PendingTransactionStatus
import fr.laforge.benoist.financialmanager.domain.repository.PendingTransactionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContain
import org.junit.Test
import java.time.LocalDateTime

class ProcessIncomingNotificationUseCaseTest {

    private val repository = mockk<PendingTransactionRepository>(relaxed = true)
    private val useCase = ProcessIncomingNotificationUseCase(repository)

    private val baseTime = LocalDateTime.of(2026, 3, 1, 10, 0, 0)

    // --- Happy path: new entry ---

    @Test
    fun `adds new PendingTransaction when no match exists in window`() = runTest {
        // --- Arrange ---
        val parsed = ParsedNotification(
            amount = 12.50f,
            description = "Starbucks",
            source = NotificationSource.BANK,
            receivedAt = baseTime,
        )
        coEvery { repository.findPendingByAmountInWindow(any(), any(), any()) } returns null
        val slot = slot<PendingTransaction>()
        coEvery { repository.add(capture(slot)) } returns Unit

        // --- Act ---
        useCase(parsed)

        // --- Assert ---
        coVerify(exactly = 1) { repository.add(any()) }
        slot.captured.amount shouldBeEqualTo 12.50f
        slot.captured.description shouldBeEqualTo "Starbucks"
        slot.captured.sources shouldBeEqualTo listOf(NotificationSource.BANK)
        slot.captured.confidence shouldBeEqualTo PendingTransactionConfidence.LOW
        slot.captured.status shouldBeEqualTo PendingTransactionStatus.PENDING
    }

    // --- Deduplication: merge ---

    @Test
    fun `merges into existing entry and upgrades confidence to HIGH when match found`() = runTest {
        // --- Arrange ---
        val existing = PendingTransaction(
            amount = 12.50f,
            description = "Starbucks",
            detectedAt = baseTime.minusMinutes(1),
            sources = listOf(NotificationSource.GOOGLE_PAY),
            confidence = PendingTransactionConfidence.LOW,
        )
        val parsed = ParsedNotification(
            amount = 12.50f,
            description = "Starbucks",
            source = NotificationSource.BANK,
            receivedAt = baseTime,
        )
        coEvery { repository.findPendingByAmountInWindow(any(), any(), any()) } returns existing
        val slot = slot<PendingTransaction>()
        coEvery { repository.update(capture(slot)) } returns Unit

        // --- Act ---
        useCase(parsed)

        // --- Assert ---
        coVerify(exactly = 0) { repository.add(any()) }
        coVerify(exactly = 1) { repository.update(any()) }
        slot.captured.sources shouldContain NotificationSource.GOOGLE_PAY
        slot.captured.sources shouldContain NotificationSource.BANK
        slot.captured.confidence shouldBeEqualTo PendingTransactionConfidence.HIGH
    }

    // --- Edge case: same source does not duplicate ---

    @Test
    fun `does not duplicate source when same source matches within window`() = runTest {
        // --- Arrange ---
        val existing = PendingTransaction(
            amount = 5.00f,
            description = "Coffee",
            detectedAt = baseTime,
            sources = listOf(NotificationSource.BANK),
            confidence = PendingTransactionConfidence.LOW,
        )
        val parsed = ParsedNotification(
            amount = 5.00f,
            description = "Coffee",
            source = NotificationSource.BANK,
            receivedAt = baseTime.plusSeconds(30),
        )
        coEvery { repository.findPendingByAmountInWindow(any(), any(), any()) } returns existing
        val slot = slot<PendingTransaction>()
        coEvery { repository.update(capture(slot)) } returns Unit

        // --- Act ---
        useCase(parsed)

        // --- Assert ---
        slot.captured.sources.size shouldBeEqualTo 1
        slot.captured.sources shouldBeEqualTo listOf(NotificationSource.BANK)
    }

    // --- Edge case: outside window creates new entry ---

    @Test
    fun `creates new entry when matching amount is outside deduplication window`() = runTest {
        // --- Arrange ---
        val parsed = ParsedNotification(
            amount = 20.00f,
            description = "Restaurant",
            source = NotificationSource.GOOGLE_PAY,
            receivedAt = baseTime,
        )
        // Repository returns null because no match in window
        coEvery { repository.findPendingByAmountInWindow(any(), any(), any()) } returns null

        // --- Act ---
        useCase(parsed)

        // --- Assert ---
        coVerify(exactly = 1) { repository.add(any()) }
        coVerify(exactly = 0) { repository.update(any()) }
    }
}
