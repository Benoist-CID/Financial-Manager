package fr.laforge.benoist.financialmanager.domain.usecase.sync

import fr.laforge.benoist.financialmanager.domain.model.sync.BankTransaction
import fr.laforge.benoist.financialmanager.domain.model.sync.SyncStatus
import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionCategory
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.amshove.kluent.`should be equal to`
import org.junit.Test
import java.time.LocalDate

class CreateTransactionFromBankUseCaseTest {

    private val financialRepository: FinancialRepository = mockk {
        every { createTransaction(any()) } returns 42L
    }
    private val useCase = CreateTransactionFromBankUseCase(financialRepository)

    private fun bankTx(
        amount: Float = 25f,
        description: String = "CARREFOUR PARIS",
        valueDate: LocalDate = LocalDate.of(2024, 6, 10),
        type: TransactionType = TransactionType.Expense,
    ) = BankTransaction(
        bankId = "b1",
        amount = amount,
        description = description,
        valueDate = valueDate,
        bookingDate = valueDate,
        type = type,
    )

    // =========================================================================
    // Happy path — created transaction has correct fields
    // =========================================================================

    @Test
    fun `invoke creates transaction with NEW_FROM_BANK status and None category`() {
        // Arrange
        val bank = bankTx()
        val capturedTx = slot<Transaction>()

        // Act
        useCase(bank)

        // Assert
        verify { financialRepository.createTransaction(capture(capturedTx)) }
        val created = capturedTx.captured
        created.syncStatus `should be equal to` SyncStatus.NEW_FROM_BANK
        created.category `should be equal to` TransactionCategory.None
    }

    @Test
    fun `invoke maps amount, description and type from bank transaction`() {
        // Arrange
        val bank = bankTx(amount = 99.5f, description = "CARREFOUR PARIS", type = TransactionType.Expense)
        val capturedTx = slot<Transaction>()

        // Act
        useCase(bank)

        // Assert
        verify { financialRepository.createTransaction(capture(capturedTx)) }
        val created = capturedTx.captured
        created.amount `should be equal to` 99.5f
        created.description `should be equal to` "CARREFOUR PARIS"
        created.type `should be equal to` TransactionType.Expense
    }

    @Test
    fun `invoke uses valueDate at start of day as the transaction dateTime`() {
        // Arrange
        val valueDate = LocalDate.of(2024, 6, 10)
        val bank = bankTx(valueDate = valueDate)
        val capturedTx = slot<Transaction>()

        // Act
        useCase(bank)

        // Assert
        verify { financialRepository.createTransaction(capture(capturedTx)) }
        capturedTx.captured.dateTime `should be equal to` valueDate.atStartOfDay()
    }

    // =========================================================================
    // Edge case — return value is the row ID from the repository
    // =========================================================================

    @Test
    fun `invoke returns the row ID produced by the repository`() {
        // Arrange
        every { financialRepository.createTransaction(any()) } returns 99L

        // Act
        val rowId = useCase(bankTx())

        // Assert
        rowId `should be equal to` 99L
    }

    // =========================================================================
    // Edge case — income bank transaction is mapped to income app transaction
    // =========================================================================

    @Test
    fun `invoke preserves income type for salary-like bank transactions`() {
        // Arrange
        val bank = bankTx(type = TransactionType.Income)
        val capturedTx = slot<Transaction>()

        // Act
        useCase(bank)

        // Assert
        verify { financialRepository.createTransaction(capture(capturedTx)) }
        capturedTx.captured.type `should be equal to` TransactionType.Income
    }
}
