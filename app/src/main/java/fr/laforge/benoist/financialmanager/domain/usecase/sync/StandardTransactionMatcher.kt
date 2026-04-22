package fr.laforge.benoist.financialmanager.domain.usecase.sync

import fr.laforge.benoist.financialmanager.domain.model.sync.BankTransaction
import fr.laforge.benoist.financialmanager.domain.model.sync.MatchConfidence
import fr.laforge.benoist.financialmanager.domain.model.sync.TransactionMatchResult
import fr.laforge.benoist.financialmanager.domain.model.transaction.Transaction
import kotlin.math.abs

/** Maximum allowed amount difference (in currency units) for an exact-amount match. */
private const val AMOUNT_EPSILON = 0.01f

/** Number of days on either side of the app transaction date to consider a date match. */
private const val DATE_WINDOW_DAYS = 3L

/**
 * Matches manually-entered app transactions against a single [BankTransaction] using
 * strict (exact-amount, ±3-day) criteria.
 *
 * This matcher is intended for one-off, non-recurring transactions. It deliberately
 * excludes periodic templates and generated recurring children so that the
 * [RecurringTransactionMatcher] can handle those with the appropriate fuzzy logic.
 *
 * Matching rules:
 * - Only considers manual entries: `isPeriodic == false` and `parent == 0`.
 * - Transaction type (income vs expense) must match exactly.
 * - Amount must be within [AMOUNT_EPSILON] (0.01 currency unit) of the bank amount.
 * - The app transaction's date must be within ±[DATE_WINDOW_DAYS] of the bank's value date.
 *
 * Confidence assignment:
 * - [MatchConfidence.HIGH] when amount + date + description all match.
 * - [MatchConfidence.MEDIUM] when amount + date match but description does not.
 *
 * @note This class is stateless and has no external dependencies. It can be
 *   instantiated directly in use cases without DI.
 */
class StandardTransactionMatcher {

    /**
     * Returns all app transactions from [appTransactions] that match [bankTransaction].
     *
     * Multiple results are possible when several app transactions share the same amount
     * and date. The caller is responsible for resolving ambiguity (e.g. by surfacing
     * all candidates for user selection).
     *
     * @param appTransactions The pool of app-side transactions to search for candidates.
     * @param bankTransaction The bank-side transaction to reconcile.
     * @return A (possibly empty) list of [TransactionMatchResult], one per matching
     *   app transaction, sorted by descending confidence.
     */
    fun match(
        appTransactions: List<Transaction>,
        bankTransaction: BankTransaction,
    ): List<TransactionMatchResult> =
        appTransactions
            .filter { isManualEntry(it) }
            .filter { it.type == bankTransaction.type }
            .filter { amountMatches(it.amount, bankTransaction.amount) }
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
     * Returns `true` when [tx] was entered manually (not generated from a periodic template).
     *
     * @note Periodic templates (`isPeriodic == true`) and their generated children
     *   (`parent != 0`) are handled by [RecurringTransactionMatcher].
     */
    private fun isManualEntry(tx: Transaction): Boolean = !tx.isPeriodic && tx.parent == 0

    /**
     * Returns `true` when the absolute difference between [appAmount] and [bankAmount]
     * is within [AMOUNT_EPSILON].
     *
     * @note Float comparison with an epsilon is used instead of direct equality to
     *   absorb rounding artefacts from currency formatting (e.g. 9.999... vs 10.00).
     */
    private fun amountMatches(appAmount: Float, bankAmount: Float): Boolean =
        abs(appAmount - bankAmount) < AMOUNT_EPSILON

    /**
     * Returns `true` when the app transaction's date falls within ±[DATE_WINDOW_DAYS]
     * of the bank transaction's value date.
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
