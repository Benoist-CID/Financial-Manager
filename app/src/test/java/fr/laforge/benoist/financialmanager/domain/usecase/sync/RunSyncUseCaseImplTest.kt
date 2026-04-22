package fr.laforge.benoist.financialmanager.domain.usecase.sync

import fr.laforge.benoist.financialmanager.domain.model.sync.BankTransaction
import fr.laforge.benoist.financialmanager.domain.model.sync.MatchConfidence
import fr.laforge.benoist.financialmanager.domain.model.sync.SyncSettings
import fr.laforge.benoist.financialmanager.domain.model.sync.SyncStatus
import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import fr.laforge.benoist.financialmanager.domain.repository.FinancialRepository
import fr.laforge.benoist.financialmanager.domain.repository.SyncSettingsRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should be empty`
import org.amshove.kluent.shouldHaveSize
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class RunSyncUseCaseImplTest {

    private val financialRepo: FinancialRepository = mockk()
    private val syncSettingsRepo: SyncSettingsRepository = mockk {
        every { get() } returns flowOf(SyncSettings())
    }
    private val standardMatcher = StandardTransactionMatcher()
    private val recurringMatcher = RecurringTransactionMatcher()

    private val useCase = RunSyncUseCaseImpl(
        financialRepository = financialRepo,
        standardMatcher = standardMatcher,
        recurringMatcher = recurringMatcher,
        syncSettingsRepository = syncSettingsRepo,
    )

    private val dateFrom = LocalDate.of(2024, 6, 1)
    private val dateTo = LocalDate.of(2024, 6, 30)

    private fun bankTx(
        bankId: String = "b1",
        amount: Float = 10f,
        description: String = "Netflix",
        date: LocalDate = LocalDate.of(2024, 6, 15),
        type: TransactionType = TransactionType.Expense,
    ) = BankTransaction(bankId = bankId, amount = amount, description = description,
        valueDate = date, bookingDate = date, type = type)

    private fun appTx(
        uid: Int = 1,
        amount: Float = 10f,
        description: String = "Netflix",
        date: LocalDate = LocalDate.of(2024, 6, 15),
        type: TransactionType = TransactionType.Expense,
        syncStatus: SyncStatus = SyncStatus.PENDING,
    ) = Transaction(uid = uid, amount = amount, description = description,
        dateTime = date.atStartOfDay(), type = type, syncStatus = syncStatus)

    // =========================================================================
    // Happy path — one bank tx matched against one pending app tx
    // =========================================================================

    @Test
    fun `invoke returns match result when bank and pending app transaction align`() = runTest {
        // Arrange
        val bank = bankTx()
        val app = appTx()
        every { financialRepo.getAllInDateRange(any(), any()) } returns flowOf(listOf(app))

        // Act
        val result = useCase(listOf(bank), dateFrom, dateTo)

        // Assert
        result.isSuccess `should be` true
        result.getOrNull()!! shouldHaveSize 1
        result.getOrNull()!![0].confidence `should be` MatchConfidence.HIGH
    }

    // =========================================================================
    // Happy path — already-synced transactions are excluded from matching
    // =========================================================================

    @Test
    fun `invoke skips transactions that are already IN_SYNC`() = runTest {
        // Arrange
        val bank = bankTx()
        val app = appTx(syncStatus = SyncStatus.IN_SYNC)
        every { financialRepo.getAllInDateRange(any(), any()) } returns flowOf(listOf(app))

        // Act
        val result = useCase(listOf(bank), dateFrom, dateTo)

        // Assert
        result.isSuccess `should be` true
        result.getOrNull()!!.`should be empty`()
    }

    // =========================================================================
    // Happy path — empty bank list → empty result
    // =========================================================================

    @Test
    fun `invoke returns empty list when bankTransactions is empty`() = runTest {
        // Arrange
        every { financialRepo.getAllInDateRange(any(), any()) } returns flowOf(emptyList())

        // Act
        val result = useCase(emptyList(), dateFrom, dateTo)

        // Assert
        result.isSuccess `should be` true
        result.getOrNull()!!.`should be empty`()
    }

    // =========================================================================
    // Edge case — bank transactions outside the date range are filtered out
    // =========================================================================

    @Test
    fun `invoke filters out bank transactions outside the requested date range`() = runTest {
        // Arrange — bank tx is in July, but the range is June
        val bankOutOfRange = bankTx(date = LocalDate.of(2024, 7, 1))
        val app = appTx()
        every { financialRepo.getAllInDateRange(any(), any()) } returns flowOf(listOf(app))

        // Act
        val result = useCase(listOf(bankOutOfRange), dateFrom, dateTo)

        // Assert
        result.isSuccess `should be` true
        result.getOrNull()!!.`should be empty`()
    }

    // =========================================================================
    // Edge case — bank transactions on range boundary dates are included
    // =========================================================================

    @Test
    fun `invoke includes bank transactions on the dateFrom and dateTo boundary`() = runTest {
        // Arrange
        val bankFirst = bankTx(bankId = "first", date = dateFrom)
        val bankLast = bankTx(bankId = "last", date = dateTo)
        val appFirst = appTx(uid = 1, date = dateFrom)
        val appLast = appTx(uid = 2, date = dateTo)
        every { financialRepo.getAllInDateRange(any(), any()) } returns
            flowOf(listOf(appFirst, appLast))

        // Act
        val result = useCase(listOf(bankFirst, bankLast), dateFrom, dateTo)

        // Assert
        result.isSuccess `should be` true
        result.getOrNull()!! shouldHaveSize 2
    }
}
