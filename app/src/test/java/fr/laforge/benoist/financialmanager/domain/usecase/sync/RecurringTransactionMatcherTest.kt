package fr.laforge.benoist.financialmanager.domain.usecase.sync

import fr.laforge.benoist.financialmanager.domain.model.sync.BankTransaction
import fr.laforge.benoist.financialmanager.domain.model.sync.MatchConfidence
import fr.laforge.benoist.financialmanager.domain.model.sync.SyncSettings
import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import org.amshove.kluent.`should be empty`
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.shouldHaveSize
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class RecurringTransactionMatcherTest {

    private val matcher = RecurringTransactionMatcher()
    private val defaultSettings = SyncSettings(recurringAmountTolerancePercent = 1f)

    // --- Helper builders ---

    private fun bankTx(
        amount: Float = 100f,
        description: String = "Netflix",
        valueDate: LocalDate = LocalDate.of(2024, 6, 15),
        type: TransactionType = TransactionType.Expense,
    ) = BankTransaction(
        bankId = "bank-1",
        amount = amount,
        description = description,
        valueDate = valueDate,
        bookingDate = valueDate,
        type = type,
    )

    /** Recurring child: isPeriodic = false, parent != 0. */
    private fun recurringChild(
        uid: Int = 1,
        amount: Float = 100f,
        description: String = "Netflix",
        dateTime: LocalDateTime = LocalDateTime.of(2024, 6, 15, 0, 0),
        type: TransactionType = TransactionType.Expense,
    ) = Transaction(
        uid = uid,
        amount = amount,
        description = description,
        dateTime = dateTime,
        type = type,
        isPeriodic = false,
        parent = 10, // non-zero → recurring child
    )

    // =========================================================================
    // Happy path — all criteria match → HIGH confidence
    // =========================================================================

    @Test
    fun `match returns HIGH confidence when fuzzy-amount, date and description all match`() {
        // Arrange — bank amount is exactly 1% more than app amount (at the tolerance boundary)
        val bank = bankTx(amount = 101f, description = "NETFLIX*12345 PARIS")
        val app = recurringChild(amount = 100f, description = "Netflix")

        // Act
        val results = matcher.match(listOf(app), bank, defaultSettings)

        // Assert
        results shouldHaveSize 1
        results[0].confidence `should be equal to` MatchConfidence.HIGH
        results[0].appTransaction `should be equal to` app
        results[0].bankTransaction `should be equal to` bank
    }

    // =========================================================================
    // Happy path — amount + date match, description does not → MEDIUM confidence
    // =========================================================================

    @Test
    fun `match returns MEDIUM confidence when fuzzy-amount and date match but description does not`() {
        // Arrange
        val bank = bankTx(amount = 100f, description = "VIR SEPA EMPLOYER")
        val app = recurringChild(amount = 100f, description = "Salary")

        // Act
        val results = matcher.match(listOf(app), bank, defaultSettings)

        // Assert
        results shouldHaveSize 1
        results[0].confidence `should be equal to` MatchConfidence.MEDIUM
    }

    // =========================================================================
    // Edge case — amount exceeds tolerance → no match
    // =========================================================================

    @Test
    fun `match returns empty when amount difference exceeds tolerance`() {
        // Arrange — app is 100, bank is 102 → 2% diff, tolerance is 1%
        val bank = bankTx(amount = 102f)
        val app = recurringChild(amount = 100f)

        // Act
        val results = matcher.match(listOf(app), bank, defaultSettings)

        // Assert
        results.`should be empty`()
    }

    @Test
    fun `match respects custom tolerance from settings`() {
        // Arrange — 5% tolerance; bank is 105, app is 100 → 5% diff, exactly at boundary
        val settings = SyncSettings(recurringAmountTolerancePercent = 5f)
        val bank = bankTx(amount = 105f)
        val app = recurringChild(amount = 100f)

        // Act
        val results = matcher.match(listOf(app), bank, settings)

        // Assert
        results shouldHaveSize 1
    }

    // =========================================================================
    // Edge case — date outside ±15-day window → no match
    // =========================================================================

    @Test
    fun `match returns empty when app date is 16 days from bank value date`() {
        // Arrange
        val bankDate = LocalDate.of(2024, 6, 15)
        val bank = bankTx(valueDate = bankDate)
        val app = recurringChild(dateTime = LocalDateTime.of(2024, 5, 30, 0, 0)) // 16 days before

        // Act
        val results = matcher.match(listOf(app), bank, defaultSettings)

        // Assert
        results.`should be empty`()
    }

    @Test
    fun `match returns result when app date is exactly 15 days from bank value date`() {
        // Arrange
        val bankDate = LocalDate.of(2024, 6, 15)
        val bank = bankTx(valueDate = bankDate)
        val app = recurringChild(dateTime = LocalDateTime.of(2024, 5, 31, 0, 0)) // exactly 15 days before

        // Act
        val results = matcher.match(listOf(app), bank, defaultSettings)

        // Assert
        results shouldHaveSize 1
    }

    // =========================================================================
    // Edge case — non-recurring (manual) transaction excluded
    // =========================================================================

    @Test
    fun `match excludes manual transactions with parent 0`() {
        // Arrange
        val bank = bankTx(amount = 100f)
        val manual = Transaction(uid = 1, amount = 100f, isPeriodic = false, parent = 0)

        // Act
        val results = matcher.match(listOf(manual), bank, defaultSettings)

        // Assert
        results.`should be empty`()
    }

    // =========================================================================
    // Edge case — periodic template excluded
    // =========================================================================

    @Test
    fun `match excludes periodic template transactions`() {
        // Arrange
        val bank = bankTx(amount = 100f)
        val template = Transaction(uid = 1, amount = 100f, isPeriodic = true, parent = 0)

        // Act
        val results = matcher.match(listOf(template), bank, defaultSettings)

        // Assert
        results.`should be empty`()
    }

    // =========================================================================
    // Edge case — type mismatch → no match
    // =========================================================================

    @Test
    fun `match returns empty when transaction type does not match bank type`() {
        // Arrange
        val bank = bankTx(type = TransactionType.Income)
        val app = recurringChild(type = TransactionType.Expense)

        // Act
        val results = matcher.match(listOf(app), bank, defaultSettings)

        // Assert
        results.`should be empty`()
    }

    // =========================================================================
    // Edge case — bank amount is zero → only exact zero match accepted
    // =========================================================================

    @Test
    fun `match handles zero bank amount by requiring exact zero app amount`() {
        // Arrange
        val bank = bankTx(amount = 0f)
        val nonZeroApp = recurringChild(amount = 0.5f)
        val zeroApp = recurringChild(uid = 2, amount = 0f)

        // Act
        val results = matcher.match(listOf(nonZeroApp, zeroApp), bank, defaultSettings)

        // Assert — only zeroApp should match
        results shouldHaveSize 1
        results[0].appTransaction `should be equal to` zeroApp
    }
}
