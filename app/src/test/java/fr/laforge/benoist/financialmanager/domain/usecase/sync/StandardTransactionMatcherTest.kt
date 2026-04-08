package fr.laforge.benoist.financialmanager.domain.usecase.sync

import fr.laforge.benoist.financialmanager.domain.model.sync.BankTransaction
import fr.laforge.benoist.financialmanager.domain.model.sync.MatchConfidence
import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import fr.laforge.benoist.financialmanager.domain.model.transaction.TransactionType
import org.amshove.kluent.`should be empty`
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.shouldHaveSize
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class StandardTransactionMatcherTest {

    private val matcher = StandardTransactionMatcher()

    // --- Helper builders ---

    private fun bankTx(
        amount: Float = 10f,
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

    private fun appTx(
        amount: Float = 10f,
        description: String = "Netflix",
        dateTime: LocalDateTime = LocalDateTime.of(2024, 6, 15, 0, 0),
        type: TransactionType = TransactionType.Expense,
        isPeriodic: Boolean = false,
        parent: Int = 0,
    ) = Transaction(
        uid = 1,
        amount = amount,
        description = description,
        dateTime = dateTime,
        type = type,
        isPeriodic = isPeriodic,
        parent = parent,
    )

    // =========================================================================
    // Happy path — description matches → HIGH confidence
    // =========================================================================

    @Test
    fun `match returns HIGH confidence when amount, date and description all match`() {
        // Arrange
        val bank = bankTx(amount = 10f, description = "NETFLIX*123456 PARIS")
        val app = appTx(amount = 10f, description = "Netflix")

        // Act
        val results = matcher.match(listOf(app), bank)

        // Assert
        results shouldHaveSize 1
        results[0].confidence `should be equal to` MatchConfidence.HIGH
        results[0].appTransaction `should be equal to` app
        results[0].bankTransaction `should be equal to` bank
    }

    // =========================================================================
    // Happy path — description doesn't match → MEDIUM confidence
    // =========================================================================

    @Test
    fun `match returns MEDIUM confidence when amount and date match but description does not`() {
        // Arrange
        val bank = bankTx(amount = 10f, description = "VIR SEPA XXXX123")
        val app = appTx(amount = 10f, description = "Netflix")

        // Act
        val results = matcher.match(listOf(app), bank)

        // Assert
        results shouldHaveSize 1
        results[0].confidence `should be equal to` MatchConfidence.MEDIUM
    }

    // =========================================================================
    // Edge case — date outside ±3-day window → no match
    // =========================================================================

    @Test
    fun `match returns empty when app date is 4 days before bank value date`() {
        // Arrange
        val bankDate = LocalDate.of(2024, 6, 15)
        val bank = bankTx(valueDate = bankDate)
        val app = appTx(dateTime = LocalDateTime.of(2024, 6, 11, 0, 0)) // 4 days before

        // Act
        val results = matcher.match(listOf(app), bank)

        // Assert
        results.`should be empty`()
    }

    @Test
    fun `match returns result when app date is exactly 3 days from bank value date`() {
        // Arrange
        val bankDate = LocalDate.of(2024, 6, 15)
        val bank = bankTx(valueDate = bankDate)
        val app = appTx(dateTime = LocalDateTime.of(2024, 6, 12, 0, 0)) // exactly 3 days before

        // Act
        val results = matcher.match(listOf(app), bank)

        // Assert
        results shouldHaveSize 1
    }

    // =========================================================================
    // Edge case — amount mismatch → no match
    // =========================================================================

    @Test
    fun `match returns empty when amounts differ by more than epsilon`() {
        // Arrange
        val bank = bankTx(amount = 10f)
        val app = appTx(amount = 10.02f) // 2 cents off — outside 0.01 epsilon

        // Act
        val results = matcher.match(listOf(app), bank)

        // Assert
        results.`should be empty`()
    }

    // =========================================================================
    // Edge case — periodic template excluded
    // =========================================================================

    @Test
    fun `match excludes periodic template transactions`() {
        // Arrange
        val bank = bankTx(amount = 10f)
        val periodicTemplate = appTx(amount = 10f, isPeriodic = true, parent = 0)

        // Act
        val results = matcher.match(listOf(periodicTemplate), bank)

        // Assert
        results.`should be empty`()
    }

    // =========================================================================
    // Edge case — recurring child excluded (handled by RecurringTransactionMatcher)
    // =========================================================================

    @Test
    fun `match excludes recurring child transactions`() {
        // Arrange
        val bank = bankTx(amount = 10f)
        val recurringChild = appTx(amount = 10f, isPeriodic = false, parent = 42)

        // Act
        val results = matcher.match(listOf(recurringChild), bank)

        // Assert
        results.`should be empty`()
    }

    // =========================================================================
    // Edge case — type mismatch → no match
    // =========================================================================

    @Test
    fun `match returns empty when transaction type does not match bank type`() {
        // Arrange
        val bank = bankTx(type = TransactionType.Expense)
        val app = appTx(type = TransactionType.Income)

        // Act
        val results = matcher.match(listOf(app), bank)

        // Assert
        results.`should be empty`()
    }

    // =========================================================================
    // Edge case — multiple candidates all returned, sorted by confidence desc
    // =========================================================================

    @Test
    fun `match returns multiple candidates sorted by descending confidence`() {
        // Arrange
        val bank = bankTx(amount = 10f, description = "Netflix")
        val highCandidate = appTx(uid = 1, amount = 10f, description = "Netflix") // HIGH
        val mediumCandidate = appTx(uid = 2, amount = 10f, description = "Hulu")   // MEDIUM

        // Act
        val results = matcher.match(listOf(mediumCandidate, highCandidate), bank)

        // Assert
        results shouldHaveSize 2
        results[0].confidence `should be equal to` MatchConfidence.HIGH
        results[1].confidence `should be equal to` MatchConfidence.MEDIUM
    }
}

// Extension to allow uid override in test helper
private fun appTx(
    uid: Int,
    amount: Float = 10f,
    description: String = "Netflix",
    dateTime: LocalDateTime = LocalDateTime.of(2024, 6, 15, 0, 0),
    type: TransactionType = TransactionType.Expense,
    isPeriodic: Boolean = false,
    parent: Int = 0,
) = Transaction(
    uid = uid,
    amount = amount,
    description = description,
    dateTime = dateTime,
    type = type,
    isPeriodic = isPeriodic,
    parent = parent,
)
