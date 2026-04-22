package fr.laforge.benoist.financialmanager.domain.usecase.sync

import fr.laforge.benoist.financialmanager.domain.model.sync.BankTransaction
import fr.laforge.benoist.financialmanager.domain.model.sync.MatchConfidence
import fr.laforge.benoist.financialmanager.domain.model.sync.SyncStatus
import fr.laforge.benoist.financialmanager.domain.model.sync.TransactionMatchResult
import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.amshove.kluent.`should be equal to`
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class ApplySyncMatchUseCaseTest {

    private val financialRepository: FinancialRepository = mockk(relaxed = true)
    private val useCase = ApplySyncMatchUseCase(financialRepository)

    private fun matchResult(
        appTxSyncStatus: SyncStatus = SyncStatus.PENDING,
        confidence: MatchConfidence = MatchConfidence.HIGH,
    ) = TransactionMatchResult(
        appTransaction = Transaction(
            uid = 1,
            amount = 10f,
            description = "Netflix",
            dateTime = LocalDateTime.of(2024, 6, 15, 0, 0),
            type = TransactionType.Expense,
            syncStatus = appTxSyncStatus,
        ),
        bankTransaction = BankTransaction(
            bankId = "b1",
            amount = 10f,
            description = "NETFLIX*123",
            valueDate = LocalDate.of(2024, 6, 15),
            bookingDate = LocalDate.of(2024, 6, 15),
            type = TransactionType.Expense,
        ),
        confidence = confidence,
    )

    // =========================================================================
    // Happy path — PENDING → IN_SYNC
    // =========================================================================

    @Test
    fun `invoke updates the app transaction syncStatus to IN_SYNC`() {
        // Arrange
        val result = matchResult(appTxSyncStatus = SyncStatus.PENDING)
        val capturedTx = slot<Transaction>()

        // Act
        useCase(result)

        // Assert
        verify { financialRepository.updateTransaction(capture(capturedTx)) }
        capturedTx.captured.syncStatus `should be equal to` SyncStatus.IN_SYNC
    }

    // =========================================================================
    // Edge case — other fields are preserved after status update
    // =========================================================================

    @Test
    fun `invoke preserves all other transaction fields`() {
        // Arrange
        val result = matchResult()
        val capturedTx = slot<Transaction>()

        // Act
        useCase(result)

        // Assert
        verify { financialRepository.updateTransaction(capture(capturedTx)) }
        val updated = capturedTx.captured
        val original = result.appTransaction
        updated.uid `should be equal to` original.uid
        updated.amount `should be equal to` original.amount
        updated.description `should be equal to` original.description
        updated.type `should be equal to` original.type
    }

    // =========================================================================
    // Edge case — updateTransaction is called exactly once
    // =========================================================================

    @Test
    fun `invoke calls updateTransaction exactly once`() {
        // Arrange
        val result = matchResult()

        // Act
        useCase(result)

        // Assert
        verify(exactly = 1) { financialRepository.updateTransaction(any()) }
    }
}
