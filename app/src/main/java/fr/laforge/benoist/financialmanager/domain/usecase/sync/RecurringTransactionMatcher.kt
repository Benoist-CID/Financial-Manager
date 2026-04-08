package fr.laforge.benoist.financialmanager.domain.usecase.sync

import fr.laforge.benoist.financialmanager.domain.model.sync.BankTransaction
import fr.laforge.benoist.financialmanager.domain.model.sync.MatchConfidence
import fr.laforge.benoist.financialmanager.domain.model.sync.SyncSettings
import fr.laforge.benoist.financialmanager.domain.model.sync.TransactionMatchResult
import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import kotlin.math.abs

/** Number of days on either side of the app transaction date to consider a date match. */
private const val DATE_WINDOW_DAYS = 15L

/**
 * Matches generated children of recurring app transactions against a single [BankTransaction]
 * using fuzzy criteria (configurable amount tolerance, ±15-day date window).
 *
 * This matcher is designed for subscriptions and periodic payments where:
 * - The amount may vary slightly between cycles (e.g. pro-rated billing).
 * - The payment date may shift significantly (e.g. salary credited on the 25th vs the 1st).
 *
 * This matcher deliberately excludes manually-entered transactions (`parent == 0`) and
 * periodic templates (`isPeriodic == true`). Those are handled by [StandardTransactionMatcher].
 *
 * Matching rules:
 * - Only considers recurring children: `isPeriodic == false` and `parent != 0`.
 * - Transaction type (income vs expense) must match exactly.
 * - Amount must be within [SyncSettings.recurringAmountTolerancePercent] of the bank amount.
 * - The app transaction's date must be within ±[DATE_WINDOW_DAYS] of the bank's value date.
 *
 * Confidence assignment:
 * - [MatchConfidence.HIGH] when fuzzy-amount + date + description all match.
 * - [MatchConfidence.MEDIUM] when fuzzy-amount + date match but description does not.
 *
 * @note This class is stateless and has no external dependencies. It can be
 *   instantiated directly in use cases without DI.
 */
class RecurringTransactionMatcher {

    /**
     * Returns all recurring-child transactions from [appTransactions] that match [bankTransaction].
     *
     * Multiple results are possible when several recurring transactions share a similar amount
     * and overlapping date windows. The caller is responsible for resolving ambiguity.
     *
     * @param appTransactions The pool of app-side transactions to search for candidates.
     * @param bankTransaction The bank-side transaction to reconcile.
     * @param settings        User-configurable sync settings, primarily used for the
     *                        amount-tolerance percentage.
     * @return A (possibly empty) list of [TransactionMatchResult], one per matching
     *   app transaction, sorted by descending confidence.
     */
    fun match(
        appTransactions: List<Transaction>,
        bankTransaction: BankTransaction,
        settings: SyncSettings,
    ): List<TransactionMatchResult> =
        appTransactions
            .filter { isRecurringChild(it) }
            .filter { it.type == bankTransaction.type }
            .filter { amountMatches(it.amount, bankTransaction.amount, settings.recurringAmountTolerancePercent) }
            .filter { dateMatches(it, bankTransaction) }
            .map { appTx ->
                TransactionMatchResult(
                    appTransaction = appTx,
                    bankTransaction = bankTransaction,
                    confidence = if (descriptionMatches(appTx.description, bankTransaction.description))
                        MatchConfidence.HIGH
                    else
                        MatchConfidence.MEDIUM,
                )
            }
            .sortedByDescending { it.confidence }

    // --- Private helpers ---

    /**
     * Returns `true` when [tx] is a generated instance of a recurring template.
     *
     * @note Periodic templates themselves (`isPeriodic == true`) are never matched
     *   directly — only their generated children carry actual transaction data.
     */
    private fun isRecurringChild(tx: Transaction): Boolean = !tx.isPeriodic && tx.parent != 0

    /**
     * Returns `true` when the absolute difference between [appAmount] and [bankAmount]
     * does not exceed [tolerancePercent]% of [bankAmount].
     *
     * @param tolerancePercent Percentage expressed as a plain number (e.g. `1f` means 1%).
     *
     * @note Division is guarded against zero: when [bankAmount] is 0, only an [appAmount]
     *   of exactly 0 is considered a match to avoid division-by-zero and false positives.
     */
    private fun amountMatches(appAmount: Float, bankAmount: Float, tolerancePercent: Float): Boolean {
        if (bankAmount == 0f) return appAmount == 0f
        val maxAllowedDiff = bankAmount * (tolerancePercent / 100f)
        return abs(appAmount - bankAmount) <= maxAllowedDiff
    }

    /**
     * Returns `true` when the app transaction's date falls within ±[DATE_WINDOW_DAYS]
     * of the bank transaction's value date.
     *
     * @note The wide ±15-day window is intentional: recurring payments (e.g. salary,
     *   rent) can arrive several days earlier or later than the nominal day of the month.
     */
    private fun dateMatches(appTx: Transaction, bankTx: BankTransaction): Boolean {
        val appDate = appTx.dateTime.toLocalDate()
        val diff = abs(appDate.toEpochDay() - bankTx.valueDate.toEpochDay())
        return diff <= DATE_WINDOW_DAYS
    }

    /**
     * Returns `true` when either description contains the other (case-insensitive).
     *
     * @note Bidirectional containment handles cases where the bank appends noise to the
     *   merchant name (e.g. "NETFLIX*123456789 PARIS" contains "netflix") and cases
     *   where the app description is more verbose than the bank's terse label.
     */
    private fun descriptionMatches(appDesc: String, bankDesc: String): Boolean {
        val appLower = appDesc.lowercase()
        val bankLower = bankDesc.lowercase()
        return bankLower.contains(appLower) || appLower.contains(bankLower)
    }
}
