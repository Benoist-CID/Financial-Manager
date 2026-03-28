package fr.laforge.benoist.financialmanager.domain.usecase.notification

import fr.laforge.benoist.financialmanager.domain.model.notification.NotificationSource
import fr.laforge.benoist.financialmanager.domain.model.notification.PendingTransaction
import fr.laforge.benoist.financialmanager.domain.model.notification.PendingTransactionConfidence
import fr.laforge.benoist.financialmanager.domain.model.notification.PendingTransactionStatus
import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.repository.PendingTransactionRepository
import fr.laforge.benoist.financialmanager.domain.usecase.CreateTransactionUseCase
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import io.mockk.coEvery
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test
import java.util.UUID

class ConfirmPendingTransactionUseCaseTest {

    private val repository = mockk<PendingTransactionRepository>(relaxed = true)
    private val createTransactionUseCase = mockk<CreateTransactionUseCase>(relaxed = true)
    private val useCase = ConfirmPendingTransactionUseCase(repository, createTransactionUseCase)

    private val pending = PendingTransaction(
        id = UUID.randomUUID(),
        amount = 25.00f,
        description = "Supermarket",
        sources = listOf(NotificationSource.BANK),
        confidence = PendingTransactionConfidence.LOW,
    )

    // --- Happy path ---

    @Test
    fun `updates status to CONFIRMED and creates transaction`() = runTest {
        // --- Act ---
        useCase(pending)

        // --- Assert ---
        coVerify(exactly = 1) {
            repository.updateStatus(pending.id, PendingTransactionStatus.CONFIRMED)
        }
        coVerify(exactly = 1) { createTransactionUseCase(any()) }
    }

    @Test
    fun `created transaction carries amount and description from pending entry`() = runTest {
        // --- Arrange ---
        val slot = slot<Transaction>()
        coEvery { createTransactionUseCase(capture(slot)) } returns true

        // --- Act ---
        useCase(pending)

        // --- Assert ---
        slot.captured.amount shouldBeEqualTo 25.00f
        slot.captured.description shouldBeEqualTo "Supermarket"
    }

    // --- Edge case ---

    @Test
    fun `does not create transaction when repository update throws`() = runTest {
        // --- Arrange ---
        coEvery { repository.updateStatus(any(), any()) } throws RuntimeException("DB error")

        // --- Act & Assert ---
        try { useCase(pending) } catch (_: RuntimeException) {}
        coVerify(exactly = 0) { createTransactionUseCase(any()) }
    }
}
