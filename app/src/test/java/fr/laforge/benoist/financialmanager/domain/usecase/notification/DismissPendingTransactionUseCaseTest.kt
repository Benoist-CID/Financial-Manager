package fr.laforge.benoist.financialmanager.domain.usecase.notification

import fr.laforge.benoist.financialmanager.domain.model.notification.NotificationSource
import fr.laforge.benoist.financialmanager.domain.model.notification.PendingTransaction
import fr.laforge.benoist.financialmanager.domain.model.notification.PendingTransactionConfidence
import fr.laforge.benoist.financialmanager.domain.model.notification.PendingTransactionStatus
import fr.laforge.benoist.financialmanager.domain.repository.PendingTransactionRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.util.UUID

class DismissPendingTransactionUseCaseTest {

    private val repository = mockk<PendingTransactionRepository>(relaxed = true)
    private val useCase = DismissPendingTransactionUseCase(repository)

    private val pending = PendingTransaction(
        id = UUID.randomUUID(),
        amount = 8.50f,
        description = "Coffee",
        sources = listOf(NotificationSource.GOOGLE_PAY),
        confidence = PendingTransactionConfidence.HIGH,
    )

    // --- Happy path ---

    @Test
    fun `updates status to DISMISSED`() = runTest {
        // --- Act ---
        useCase(pending)

        // --- Assert ---
        coVerify(exactly = 1) {
            repository.updateStatus(pending.id, PendingTransactionStatus.DISMISSED)
        }
    }

    // --- Edge cases ---

    @Test
    fun `does not call updateStatus with CONFIRMED`() = runTest {
        // --- Act ---
        useCase(pending)

        // --- Assert ---
        coVerify(exactly = 0) {
            repository.updateStatus(any(), PendingTransactionStatus.CONFIRMED)
        }
    }

    @Test
    fun `calls updateStatus exactly once even for HIGH confidence entry`() = runTest {
        // --- Act ---
        useCase(pending.copy(confidence = PendingTransactionConfidence.HIGH))

        // --- Assert ---
        coVerify(exactly = 1) { repository.updateStatus(any(), any()) }
    }
}
